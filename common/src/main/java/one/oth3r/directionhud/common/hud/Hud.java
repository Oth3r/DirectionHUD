package one.oth3r.directionhud.common.hud;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Assets.symbols.arrows;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.hud.Hud.Setting.*;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.ModuleText;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.*;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.Time.*;
import one.oth3r.directionhud.common.files.playerdata.PDHud;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.module.*;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Helper.Num;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Hud {

    public enum Setting {
        state,
        type,
        bossbar__color,
        bossbar__distance,
        bossbar__distance_max,
        none;
        @Override
        public String toString() {
            return name().replace("__",".");
        }
        // . = _ and _ == __
        public static Setting get(String s) {
            try {
                return Setting.valueOf(s.replace(".","__"));

            } catch (IllegalArgumentException e) {
                return none;
            }
        }
        public static ArrayList<Setting> baseSettings() {
            // without module settings & none
            ArrayList<Setting> list = new ArrayList<>(Arrays.asList(values()));
            list.remove(none);
            return list;
        }

        public static ArrayList<Setting> boolSettings() {
            ArrayList<Setting> list = new ArrayList<>();
            list.add(state);
            list.add(bossbar__distance);
            return list;
        }

        public enum DisplayType {
            actionbar,
            bossbar;
            private static final DisplayType[] values = values();
            public DisplayType next() {
                return values[(ordinal() + 1) % values.length];
            }
            public static DisplayType get(String s) {
                try {
                    return DisplayType.valueOf(s);
                } catch (IllegalArgumentException e) {
                    return DisplayType.valueOf(PlayerData.getDefaults().getHud().getSetting().getType());
                }
            }
        }
        public enum BarColor {
            pink,
            blue,
            red,
            green,
            yellow,
            purple,
            white
        }
    }

    public static final Lang LANG = new Lang("hud.");

    public static void CMDExecutor(Player player, String[] args) {
        if (!Helper.checkEnabled(player).hud()) return;
        if (args.length == 0) {
            UI(player,null);
            return;
        }
        String type = args[0].toLowerCase();
        String[] trimmedArgs = Helper.trimStart(args, 1);
        switch (type) {
            case "modules" -> modules.CMDExecutor(player, trimmedArgs);
            case "settings" -> settings.CMDExecutor(player,trimmedArgs);
            case "color" -> color.cmdExecutor(player, trimmedArgs);
            case "toggle" -> settings.change(player,Setting.state,(boolean) player.getPCache().getHud().getSetting(Setting.state)?"off":"on",false);
            default -> player.sendMessage(CUtl.error("command"));
        }
    }

    public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
        ArrayList<String> suggester = new ArrayList<>();
        if (!Helper.checkEnabled(player).hud()) return suggester;
        if (pos == 1) {
            suggester.add("modules");
            suggester.add("color");
            suggester.add("toggle");
            suggester.add("settings");
        }
        if (pos > 1) {
            String command = args[0].toLowerCase();
            String[] trimmedArgs = Helper.trimStart(args, 1);
            int fixedPos = pos - 2;
            switch (command) {
                case "modules" -> suggester.addAll(modules.CMDSuggester(player,fixedPos,trimmedArgs));
                case "settings" -> suggester.addAll(settings.CMDSuggester(fixedPos,trimmedArgs));
                case "color" -> suggester.addAll(color.cmdSuggester(player,fixedPos,trimmedArgs));
            }
        }
        return suggester;
    }

    public static class build {

        /**
         * builds the HUD for actionBar & BossBar use
         * @param moduleInstructions a HashMap with the instructions for building the HUD
         * @return a CTxT with the fully built HUD
         */
        public static CTxT compile(Player player, HashMap<Module, String> moduleInstructions) {
            // returns a CTxT with the fully built HUD
            player.getPCache().getRainbow(1).setPosition(LoopManager.rainbowF);
            CTxT msg = CTxT.of("");
            // loop for all enabled modules
            int count = 0;
            for (BaseModule mod: modules.getEnabled(player)) {
                Module module = mod.getModuleType();
                count++;
                // if module is empty, skip
                if (moduleInstructions.get(module).isEmpty()) continue;
                // append the parsed module text
                msg.append(CUtl.parse(player,moduleInstructions.get(module)));
                // if there's another module after the current one, add a space
                if (count < modules.getEnabled(player).size()) msg.append(" ");
            }
            // if the built string is empty, just return
            if (msg.isEmpty()) return msg;

            //make the click event unique for detecting if an actionbar is from DirectionHUD or not
            msg.click(3,"https://modrinth.com/mod/directionhud");

            return msg;
        }

        /**
         * puts all HUD building instructions into a HashMap
         * @return HUD building instructions
         */
        public static HashMap<Module, String> getHUDInstructions(Player player) {
            HashMap<Module, String> filledModules = new HashMap<>();
            filledModules.put(Module.COORDINATES, getCoordinatesModule(player));
            filledModules.put(Module.DESTINATION, getDestinationModule(player));
            filledModules.put(Module.DISTANCE, getDistanceModule(player));
            filledModules.put(Module.TRACKING, getTrackingModule(player));
            filledModules.put(Module.DIRECTION, getDirectionModule(player));
            filledModules.put(Module.WEATHER, getWeatherModule(player));
            filledModules.put(Module.TIME, getTimeModule(player));
            filledModules.put(Module.ANGLE, getAngleModule(player));
            filledModules.put(Module.SPEED, getSpeedModule(player));
            return filledModules;
        }

        public static String getCoordinatesModule(Player player) {
            ModuleCoordinates module = player.getPCache().getHud().getModule(Module.COORDINATES);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            return getCoordinatesModule(module, player.getLoc());
        }
        public static String getCoordinatesModule(ModuleCoordinates coordinatesModule, Loc loc) {
            return String.format(FileData.getModuleText().getCoordinates().getXyz(),
                    loc.getX(), loc.getY(), loc.getZ());
        }

        public static String getDestinationModule(Player player) {
            ModuleDestination module = player.getPCache().getHud().getModule(Module.DESTINATION);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            Dest dest = Destination.dest.get(player);
            // if no destination is set, empty
            if (!dest.hasXYZ()) return "";

            return getDestinationModule(module, dest);
        }
        public static String getDestinationModule(ModuleDestination destinationModule, Dest dest) {
            // assets
            ModuleText.ModuleDestination moduleDestination = FileData.getModuleText().getDestination();
            // return based on the destination
            if (dest.hasDestRequirements()) {
                return String.format(moduleDestination.getName(), dest.getName());
            }
            else if (dest.hasY()) {
                return String.format(moduleDestination.getXyz(), dest.getX(), dest.getY(), dest.getZ());
            }
            else {
                return String.format(moduleDestination.getXz(), dest.getX(), dest.getZ());
            }
        }

        public static String getDistanceModule(Player player) {
            ModuleDistance module = player.getPCache().getHud().getModule(Module.DISTANCE);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            int distance = Destination.dest.getDist(player);
            // if no destination is set, return empty
            if (distance == -1) return "";

            return getDistanceModule(module, distance);
        }
        public static String getDistanceModule(ModuleDistance distanceModule, int distance) {
            return String.format(FileData.getModuleText().getDistance().getNumber(),
                    distance);
        }

        public static String getTrackingModule(Player player) {
            ModuleTracking module = player.getPCache().getHud().getModule(Module.TRACKING);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            // pointer target
            Loc pointLoc = null;
            // player tracking mode
            ModuleTracking tracking = player.getPCache().getHud().getModule(Module.TRACKING);
            ModuleTracking.Target trackingTarget = tracking.getTarget();
            boolean hybrid = tracking.isHybrid();

            // if PLAYER or HYBRID (TRACKING CHECK)
            if (trackingTarget.equals(ModuleTracking.Target.player) || hybrid) {
                // get the target
                Player target = Destination.social.track.getTarget(player);
                // make sure the player is real
                if (target.isValid()) {
                    Loc plLoc = new Loc(target);
                    // not in the same dimension
                    if (!player.getDimension().equals(target.getDimension())) {
                        // can convert and autoconvert is on
                        if (Dimension.canConvert(player.getDimension(),target.getDimension()) &&
                                player.getPCache().getDEST().getDestSettings().getAutoconvert()) {
                            plLoc.convertTo(player.getDimension());
                        }
                        // else no tracking
                        else plLoc = null;
                    }
                    // set the loc
                    pointLoc = plLoc;
                }
            }
            // DEST or (HYBRID & NULL TRACKER)
            if (trackingTarget.equals(ModuleTracking.Target.dest) || (hybrid && pointLoc == null)) {
                // make sure theres a dest
                if (Destination.dest.get(player).hasXYZ())
                    pointLoc = Destination.dest.get(player);
            }
            // check if there's a point set, otherwise return nothing
            if (pointLoc == null) return "";

            // add 180 to the player's yaw (-180 - 180) to get the rotation
            double rotation = player.getYaw()+180;

            return getTrackingModule(module, rotation, player.getLoc(), pointLoc);
        }
        public static String getTrackingModule(ModuleTracking trackingModule, double originRotation, Loc originLoc, Loc targetLoc) {
            // assets
            boolean simple = trackingModule.getType().equals(ModuleTracking.Type.simple);
            double target;
            String data;
            ModuleText.ModuleTracking.Assets assets = FileData.getModuleText().getTracking().getAssets();
            ModuleText.ModuleTracking.Assets.Simple simpleArrows = assets.getSimple();
            ModuleText.ModuleTracking.Assets.Compact compactArrows = assets.getCompact();
            ModuleText.ModuleTracking.Assets.Elevation elevationArrows = assets.getElevation();


            // find the rotation needed for the originloc to 'face' the targetloc
            int x = targetLoc.getX()-originLoc.getX();
            int z = (targetLoc.getZ()-originLoc.getZ())*-1;
            target = Math.toDegrees(Math.atan2(x, z));
            if (target < 0) target += 360;

            // NORTH
            if (Num.inBetween(originRotation, Num.wSubtract(target,15,360), Num.wAdd(target,15,360)))
                data = simple?simpleArrows.getNorth():compactArrows.getNorth();
                // NORTH WEST
            else if (Num.inBetween(originRotation, target, Num.wAdd(target,65,360)))
                data = simple?simpleArrows.getNorthWest():compactArrows.getNorthWest();
                // WEST
            else if (Num.inBetween(originRotation, target, Num.wAdd(target,115,360)))
                data = simple?simpleArrows.getWest():compactArrows.getWest();
                // SOUTH WEST
            else if (Num.inBetween(originRotation, target, Num.wAdd(target,165,360)))
                data = simple?simpleArrows.getSouthWest():compactArrows.getSouthWest();
                // NORTH EAST
            else if (Num.inBetween(originRotation, Num.wSubtract(target, 65, 360), target))
                data = simple?simpleArrows.getNorthEast():compactArrows.getNorthEast();
                // EAST
            else if (Num.inBetween(originRotation, Num.wSubtract(target, 115, 360), target))
                data = simple?simpleArrows.getEast():compactArrows.getEast();
                // SOUTH EAST
            else if (Num.inBetween(originRotation, Num.wSubtract(target, 165, 360), target))
                data = simple?simpleArrows.getSouthEast():compactArrows.getSouthEast();
                // SOUTH
            else data = simple?simpleArrows.getSouth():compactArrows.getSouth();

            // todo add elevation toggle
//            // if compact and the ylevel is different & there's a y level on the loc
//            if (!simple && !(boolean) player.getPCache().getDEST().getDestSettings().getYlevel() && pointLoc.getY() != null) {
//                tracking.add("p|");
//                int playerY = player.getLoc().getY(), targetY = pointLoc.getY();
//                // dash if in Y range
//                if (playerY-2 < targetY && targetY < playerY+2)
//                    tracking.add("s-");
//                    // down if higher
//                else if (player.getLoc().getY() > pointLoc.getY())
//                    tracking.add("s"+arrows.south);
//                    // up if lower
//                else tracking.add("s"+arrows.north);
//            }

            return String.format(FileData.getModuleText().getTracking().getTracking(), data);
        }

        public static String getDirectionModule(Player player) {
            ModuleDirection module = player.getPCache().getHud().getModule(Module.DIRECTION);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";
            // add 180 to the player's yaw (-180 - 180) to get the rotation
            double rotation = player.getYaw()+180;

            return getDirectionModule(module,rotation);
        }
        public static String getDirectionModule(ModuleDirection directionModule, double rotation) {
            ModuleText.ModuleDirection.Assets.Cardinal cardinals = FileData.getModuleText().getDirection().getAssets().getCardinal();
            String cardinal;

            if (Num.inBetween(rotation,22.5,67.5)) cardinal = cardinals.getNorthEast();
            else if (Num.inBetween(rotation,67.5,112.5)) cardinal = cardinals.getEast();
            else if (Num.inBetween(rotation,112.5,157.5)) cardinal = cardinals.getSouthEast();
            else if (Num.inBetween(rotation,157.5,202.5)) cardinal = cardinals.getSouth();
            else if (Num.inBetween(rotation,202.5,247.5)) cardinal = cardinals.getSouthWest();
            else if (Num.inBetween(rotation,247.5,292.5)) cardinal = cardinals.getWest();
            else if (Num.inBetween(rotation,292.5,337.5)) cardinal = cardinals.getNorthWest();
            else cardinal = cardinals.getNorth();

            return String.format(FileData.getModuleText().getDirection().getFacing(),cardinal);
        }

        public static String getWeatherModule(Player player) {
            ModuleWeather module = player.getPCache().getHud().getModule(Module.WEATHER);
            Time timeSettings = Dimension.getTimeSettings(player.getDimension());
            Weather weatherSettings = timeSettings.getWeather();

            // if not enabled, return empty
            if (!module.isEnabled() || !timeSettings.getEnabled() || !weatherSettings.getEnabled()) return "";

            // get the variables
            int timeTicks = player.getTimeOfDay();
            Weather.NightTicks nightTicks = weatherSettings.getNightTicks();
            Weather.Icons weatherIcons = weatherSettings.getIcons();
            String extraIcons = null;
            boolean night;

            // get the extra icons and if It's night or not
            if (player.hasThunderstorm()) {
                extraIcons = weatherIcons.thunderstorm();
                night = Num.inBetween(timeTicks,nightTicks.thunderstorm().startTick(),nightTicks.thunderstorm().endTick());
            }
            else if (player.hasStorm()) {
                extraIcons = weatherIcons.storm();
                night = Num.inBetween(timeTicks,nightTicks.storm().startTick(),nightTicks.storm().endTick());
            }
            else {
                night = Num.inBetween(timeTicks,nightTicks.normal().startTick(),nightTicks.normal().endTick());
            }
            // get the weather time of day icon
            String weatherIcon = night?weatherIcons.night():weatherIcons.day();

            return getWeatherModule(module,weatherIcon,extraIcons);
        }
        public static String getWeatherModule(ModuleWeather weatherModule, String weatherIcon, String extraIcons) {
            // if no extra icons, single weather
            if (extraIcons == null) return String.format(FileData.getModuleText().getWeather().getWeatherSingle(), weatherIcon);
            // if not, dual weather module
            return String.format(FileData.getModuleText().getWeather().getWeather(), weatherIcon, extraIcons);
        }

        public static String getTimeModule(Player player) {
            ModuleTime module = player.getPCache().getHud().getModule(Module.TIME);
            Time timeSettings = Dimension.getTimeSettings(player.getDimension());
            // if not enabled, return empty
            if (!module.isEnabled() || !timeSettings.getEnabled()) return "";

            int timeTicks = player.getTimeOfDay();

            // (add 6 to account for the day starting at 6 am);
            int hour = (timeTicks / 1000 + 6) % 24;
            int minute = ((timeTicks % 1000) * 60 / 1000);

            return getTimeModule(module, hour, minute);
        }
        public static String getTimeModule(ModuleTime timeModule, int hour, int minute) {
            // assets
            ModuleText.ModuleTime.Assets assets = FileData.getModuleText().getTime().getAssets();
            boolean time12 = !timeModule.isHour24();

            String hr;
            // if 12 hr, fix the hour mark
            if (time12) {
                int hourMod = hour % 12;
                // if hr % 12 = 0, its 12 am/pm
                if (hourMod == 0) hr = String.valueOf(12);
                else hr = String.valueOf(hourMod);
            } else {
                hr = Num.formatToTwoDigits(hour);
            }

            // add 0 to the start, then set the string to the last two numbers to always have a 2-digit number
            String min = Num.formatToTwoDigits(minute);

            // get the time as a string
            String timeString = hr + assets.getTimeSeparator() + min;

            // return based on 12 or 24 hour
            if (!timeModule.isHour24()) return String.format(FileData.getModuleText().getTime().getHour12(), timeString,
                    hour >= 12 ? assets.getPM():assets.getAM());
            return String.format(FileData.getModuleText().getTime().getHour24(),timeString);
        }

        public static String getAngleModule(Player player) {
            ModuleAngle module = player.getPCache().getHud().getModule(Module.ANGLE);
            // if the module isnt enabled, return empty
            if (!module.isEnabled()) return "";

            // assets
            return getAngleModule(module, player.getYaw(), player.getPitch());
        }
        public static String getAngleModule(ModuleAngle angleModule, float yaw, float pitch) {
            // assets
            DecimalFormat df = new DecimalFormat("0.0");
            String y = df.format(yaw), p = df.format(pitch);
            return switch (angleModule.getDisplay()) {
                case yaw -> String.format(FileData.getModuleText().getAngle().getYaw(), y);
                case pitch -> String.format(FileData.getModuleText().getAngle().getPitch(), p);
                case both -> String.format(FileData.getModuleText().getAngle().getBoth(), y, p);
            };
        }

        public static String getSpeedModule(Player player) {
            ModuleSpeed module = player.getPCache().getHud().getModule(Module.SPEED);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            // assets
            return getSpeedModule(module, player.getPCache().getSpeedData().getSpeed());
        }
        public static String getSpeedModule(ModuleSpeed speedModule, double speed) {
            // assets
            boolean speed2D = speedModule.isCalculation2D();
            DecimalFormat df = new DecimalFormat(speedModule.getDisplayPattern());
            String data = df.format(speed);

            if (speed2D) return String.format(FileData.getModuleText().getSpeed().getXzSpeed(), data);
            return String.format(FileData.getModuleText().getSpeed().getXyzSpeed(), data);
        }
    }

    public static class modules {
        private static final int PER_PAGE = 5;

        public static final String SETTING_ON = "on";
        public static final String SETTING_OFF = "off";
        public static final List<String> SUGGESTER_ON_OFF = List.of(SETTING_ON, SETTING_OFF);

        public static final Lang LANG = new Lang("hud.module.");

        /**
         * creates the button for the modules UI
         * @return the button created
         */
        public static CTxT button() {
            return LANG.btn().btn(true).color(Assets.mainColors.edit).click(1,"/hud modules")
                    .hover(CTxT.of(Assets.cmdUsage.hudModules).color(Assets.mainColors.edit).append("\n").append(LANG.hover()));
        }

        /**
         * Executes commands related to managing modules via the provided player input.
         * Supported commands include viewing, resetting, toggling, editing, and ordering modules.
         *
         * @param player the player executing the command
         * @param args the command arguments provided by the player
         */
        public static void CMDExecutor(Player player, String[] args) {
            // UI
            if (args.length == 0) {
                UI(player, null, 1);
                return;
            }
            // UI (page)
            if (Num.isInt(args[0])) UI(player,null,Integer.parseInt(args[0]));
            // if there is -r, remove it and enable returning
            boolean Return = args[0].contains("-r");
            args[0] = args[0].replace("-r","");
            // RESET
            if (args[0].equals("reset")) {
                // reset - all
                if (args.length == 1) reset(player,Module.UNKNOWN,Return);
                // reset (module) - per module
                else reset(player,Module.fromString(args[1]),Return);
            }
            // TOGGLE
            if (args[0].equals("toggle")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else toggle(player, Module.fromString(args[1]),null,Return);
            }
            // TOGGLE
            if (args[0].equals("edit")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else Edit.UI(player, null, Module.fromString(args[1]));
            }
            // TOGGLE
            if (args[0].equals("setting")) {
                // send error if cmd length isn't long enough
                if (args.length < 4) player.sendMessage(CUtl.error("args"));
                else Setting.setSetting(player, Module.fromString(args[1]), args[2], args[3], Return);
            }
            // ORDER
            if (args[0].equals("order")) {
                // send error if cmd length isn't long enough or an order number isn't entered
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else if (args.length == 3 && Num.isInt(args[2])) move(player, Module.fromString(args[1]), Integer.parseInt(args[2]), Return);
                else player.sendMessage(CUtl.error("number"));
            }
        }
        public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // /modules [subcommand]
            if (pos == 0) {
                suggester.add("order");
                suggester.add("toggle");
                suggester.add("reset");
                suggester.add("edit");
                suggester.add("setting");
                return suggester;
            }

            // if -r is attached, remove it and continue with the suggester
            args[0] = args[0].replaceAll("-r", "");

            // modules (order/toggle/reset/edit/setting) [module]
            if (pos == 1) suggester.addAll(
                    PlayerData.getDefaults().getHud().getModules().stream().map(mod -> mod.getModuleType().getName()).toList());

            // modules order (module) [order]
            if (pos == 2 && args[0].equalsIgnoreCase("order")) {
                suggester.add(String.valueOf(player.getPCache().getHud().getModule(Module.fromString(args[1])).getOrder()));
            }

            // /module order setting
            if (args[0].equalsIgnoreCase("setting")) {
                // [setting-id]
                if (pos == 3) {
                    // get the selected module
                    Module module = Module.fromString(args[1]);

                    // if unknown, return
                    if (module.equals(Module.UNKNOWN)) return suggester;

                    // get the module & module settings
                    BaseModule mod = player.getPCache().getHud().getModule(module);
                    String[] settingIDs = mod.getSettingIDs();

                    // if the module doesn't have any settings, return
                    if (settingIDs.length == 0) return suggester;

                    // add all the setting ids to the suggester
                    suggester.addAll(Arrays.asList(settingIDs));
                }
                // setting-id [value]
                if (pos == 4) {
                    // get the setting ID
                    String settingID = args[2];
                    // add items to the suggester based on the ID
                    switch (settingID) {
                        // ANGLE
                        case ModuleAngle.displayID -> suggester.addAll(Enums.toStringList(ModuleAngle.Display.values()));
                        // TIME
                        case ModuleTime.hour24ID -> suggester.addAll(SUGGESTER_ON_OFF);
                        // SPEED
                        case ModuleSpeed.calculation2DID -> suggester.addAll(SUGGESTER_ON_OFF);
                        case ModuleSpeed.displayPatternID -> suggester.addAll(List.of("0.00", "0.##", "0.00##"));
                        // TRACKING
                        case ModuleTracking.hybridID -> suggester.addAll(SUGGESTER_ON_OFF);
                        case ModuleTracking.typeID -> suggester.addAll(Enums.toStringList(ModuleTracking.Type.values()));
                        case ModuleTracking.targetID -> suggester.addAll(Enums.toStringList(ModuleTracking.Target.values()));
                    }
                }
            }
            return suggester;
        }

        /**
         * reset module(s) to their default config state
         * @param module module to reset, unknown to reset all
         * @param Return to return to the modules UI
         */
        public static void reset(Player player, Module module, boolean Return) {
            CTxT msg = CUtl.tag();
            if (module.equals(Module.UNKNOWN)) {
                // reset everything
                player.getPData().getHud().setModules(PlayerData.getDefaults().getHud().getModules());

                msg.append(LANG.msg("reset_all", CUtl.LANG.btn("all").color('c')));
            } else {
                // the module to reset to
                BaseModule resetModule = PlayerData.getDefaults().getHud().getModule(module);
                // the original module
                BaseModule mod = player.getPData().getHud().getModule(module);

                // get the order
                ArrayList<BaseModule> list = player.getPData().getHud().getModules();
                // move the module in the list (-1 because module order starts from 1)
                Helper.moveTo(list, mod.getOrder()-1, resetModule.getOrder()-1);
                // update the other orders
                setOrder(list);
                // set the module to the reset one (saves)
                player.getPData().getHud().setModule(resetModule);

                // reset message
                msg.append(LANG.msg("reset",CUtl.LANG.btn("reset").color('c'),CTxT.of(module.toString()).color(CUtl.s())));

                // if returning
                if (Return) {
                    Edit.UI(player, msg, module);
                    return;
                }
            }
            if (Return) UI(player, msg, 1);
            else player.sendMessage(msg);
        }

        /**
         * move a module's position in the HUD
         * @param module module to move
         * @param pos position in the list, starts at 1
         * @param Return to return to the modules UI
         */
        public static void move(Player player, Module module, int pos, boolean Return) {
            if (module.equals(Module.UNKNOWN)) {
                player.sendMessage(CUtl.error("hud.module"));
                return;
            }
            // -1 to pos because it starts at 1
            pos--;

            ArrayList<BaseModule> list = player.getPData().getHud().getModules();
            // move the module to the new index
            Helper.moveTo(list, player.getPData().getHud().getModule(module).getOrder()-1, pos);
            // set the new order numbers
            setOrder(list);
            // make sure everything is saved
            player.getPData().queueSave();
            // get the new order number (formatted for user)
            int order = BaseModule.findInArrayList(list,module).orElse(
                    player.getPData().getHud().getModule(module)).getOrder();


            // set the order of module to <>
            CTxT msg = CUtl.tag().append(LANG.msg("order",
                    CTxT.of(module.toString()).color(CUtl.s()),
                    CTxT.of(String.valueOf(order)).color(CUtl.s())));

            if (Return) Edit.UI(player, msg, module);
            else player.sendMessage(msg);
        }

        /**
         * sets the enabled state of a module
         * @param module module to edit
         * @param toggle state to change to - null to flip
         * @param Return to return to the modules UI
         */
        public static void toggle(Player player, Module module, Boolean toggle, boolean Return) {
            // bad data
            if (module.equals(Module.UNKNOWN)) {
                player.sendMessage(CUtl.error("hud.module"));
                return;
            }
            // get the module
            BaseModule mod = player.getPData().getHud().getModule(module);
            // if toggle null, flip current
            if (toggle == null) toggle = !mod.isEnabled();
            // set the new state
            mod.setState(toggle);
            // save the changes
            player.getPData().queueSave();

            // turned <toggle> <model>
            CTxT msg = CUtl.tag().append(LANG.msg("toggle",
                    CUtl.toggleTxT(toggle),
                    CTxT.of(module.toString()).color(CUtl.s())));

            if (Return) Edit.UI(player, msg, module);
            else player.sendMessage(msg);
        }

        public static void fixOrder(ArrayList<BaseModule> list) {
            fixOrder(list, false);
        }

        /**
         * module order fixer, removes unknown modules, and fills in the gaps if modules are missing
         *
         * @param list              the list that needs to be fixed
         * @param getFactoryDefault if the list to check against should be the factory default or not
         */
        public static void fixOrder(ArrayList<BaseModule> list, boolean getFactoryDefault) {
            /*
            1. removes duplicates and invalid entries
            2. sorts based on module.getOrder()
            3. adds any missing modules
             */

            // the default list of modules
            ArrayList<BaseModule> defaultList;
            if (getFactoryDefault) defaultList = new PDHud().getModules();
            else defaultList = PlayerData.getDefaults().getHud().getModules();

            // if the module isn't valid or there's a duplicate module, remove
            Helper.removeDuplicateSubclasses(list);

            // if the size of the list is still bigger than the default list, return the default
            if (list.size() > defaultList.size()) {
                list.clear();
                list.addAll(defaultList);
                return;
            }

            // sort the list
            list.sort(Comparator.comparingInt(BaseModule::getOrder));

            // set each order in the list from 1 - max
            setOrder(list);

            // add missing modules to the list
            defaultList.stream()
                    .filter(mod -> list.stream().noneMatch(module -> module.getClass().equals(mod.getClass())))
                    .forEach(list::add);
        }

        /**
         * sets the order to how the modules are in the array list
         */
        public static void setOrder(ArrayList<BaseModule> list) {
            // set the order of the module to the order in the arraylist
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setOrder(i+1);
            }
        }

        /**
         * @return a list of enabled modules in the HUD order
         */
        public static ArrayList<BaseModule> getEnabled(Player player) {
            return player.getPCache().getHud().getModules().stream().filter(BaseModule::isEnabled).collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * generates an example with random data for the given module as a CTxT
         */
        public static CTxT moduleExample(Player player, Module module) {
            // assets
            BaseModule mod = player.getPData().getHud().getModule(module);
            Random random = new Random();
            Loc randomLoc = new Loc(
                    random.nextInt(5000),random.nextInt(-64,200),random.nextInt(5000));
            float randomRotation = random.nextFloat(0,360);
            Weather.Icons defaultIcons = Weather.Icons.defaultIcons();

            return CUtl.parse(player,switch (module) {
                case DISTANCE -> build.getDistanceModule((ModuleDistance) mod,random.nextInt(50,250));
                case DESTINATION -> build.getDestinationModule((ModuleDestination) mod, new Dest(randomLoc,"a","#ffffff"));
                case DIRECTION -> build.getDirectionModule((ModuleDirection) mod,randomRotation);
                case TRACKING -> build.getTrackingModule((ModuleTracking) mod,randomRotation,player.getLoc(),randomLoc);
                case TIME -> build.getTimeModule((ModuleTime) mod,random.nextInt(1,25), random.nextInt(0,60));
                case WEATHER -> build.getWeatherModule((ModuleWeather) mod,random.nextBoolean() ? defaultIcons.day():defaultIcons.night(),
                        random.nextBoolean() ? null : random.nextBoolean() ? defaultIcons.storm() : defaultIcons.thunderstorm());
                case SPEED -> build.getSpeedModule((ModuleSpeed) mod,random.nextFloat(1,12));
                case ANGLE -> build.getAngleModule((ModuleAngle) mod,random.nextFloat(-180,180.1f),random.nextFloat(-90,90.1f));
                // default is coordinates module
                default -> build.getCoordinatesModule((ModuleCoordinates) mod,randomLoc);
            });
        }

        /**
         * returns the info about the module provided
         */
        public static CTxT moduleInfo(Module module) {
            return LANG.get("info."+module.getName());
        }

        /**
         * the colors for each state a module can be in
         */
        public static final String STATE_GREEN = "#19ff21";
        public static final String STATE_YELLOW = "#fff419";
        public static final String STATE_GRAY = Assets.mainColors.gray;

        /**
         * returns the color of the module
         * <br>{@link #STATE_GRAY} when off
         * <br>{@link #STATE_YELLOW} when on but can't display
         * <br>{@link #STATE_GREEN} when on and displaying
         * @return the HEX code of the color
         */
        public static String stateColor(Player player, Module module) {
            // bad data
            if (module.equals(Module.UNKNOWN)) return Assets.mainColors.error;
            // get the module
            BaseModule mod = player.getPCache().getHud().getModule(module);

            // if off return gray
            if (!mod.isEnabled()) return STATE_GRAY;

            // see if the text should be yellow
            boolean yellow = false;
            // if no destination
            if (!Destination.dest.get(player).hasXYZ()) {
                // if destination or distance
                if (module.equals(Module.DESTINATION) || module.equals(Module.DISTANCE)) yellow = true;
            }
            // if tracking
            if (module.equals(Module.TRACKING)) {
                boolean hasPlayer = Destination.social.track.getTarget(player).isValid();
                boolean hasDest = Destination.dest.get(player).hasXYZ();
                ModuleTracking.Target target = ((ModuleTracking)mod).getTarget();

                // if player tracking and no player, yellow
                if (!hasPlayer && target.equals(ModuleTracking.Target.player)) yellow = true;
                    // if dest tracking and no dest, yellow
                else if (hasDest && target.equals(ModuleTracking.Target.dest)) yellow = true;
                    // if hybrid tracking and nether, yellow
                else if (!hasDest || !hasPlayer && (((ModuleTracking)mod).isHybrid())) yellow = true;
            }

            // return based on yellow, if not green
            if (yellow) return STATE_YELLOW;
            return STATE_GREEN;
        }

        /**
         * checks if a module has been edited
         * @return true if the module has been edited, as it can reset
         */
        public static boolean canReset(BaseModule module) {
            BaseModule defaultModule = PlayerData.getDefaults().getHud().getModule(module.getModuleType());
            return !defaultModule.equals(module);
        }

        public static class Setting {
            public static final Lang LANG = new Lang("hud.module.setting.");

            public static void setSetting(Player player, Module module, String settingID, String value, boolean Return) {
                BaseModule mod = player.getPData().getHud().getModule(module);
                // if the module doesn't have any extra settings to edit
                if (!mod.hasExtraSettings()) {
                    player.sendMessage(LANG.error("no_settings", CTxT.of(module.getName()).color(CUtl.s())));
                    return;
                }
                // if the module setting doesn't exist
                if (Arrays.stream(mod.getSettingIDs()).noneMatch(settingID::equals)) {
                    player.sendMessage(LANG.error("invalid", CTxT.of(module.getName()).color(CUtl.s())));
                    return;
                }

                // start building the message
                CTxT msg = CUtl.tag(), invalidSettingValue = LANG.error("invalid.setting_value", LANG.get(module.getName()+"."+settingID).color(CUtl.s()));

                boolean state = value.equalsIgnoreCase("on");
                switch (module) {
                    case TIME -> {
                        ModuleTime timeModule = (ModuleTime) mod;
                        // update the module
                        timeModule.setHour24(state);
                        player.getPData().getHud().setModule(timeModule);

                        // build the set message
                        msg.append(SetMSG.customToggle(module, settingID, state));
                    }
                    case TRACKING -> {
                        ModuleTracking trackingModule = (ModuleTracking) mod;
                        switch (settingID) {
                            case ModuleTracking.hybridID -> {
                                // update the module
                                trackingModule.setHybrid(state);
                                // get the correct set message
                                msg.append(SetMSG.toggle(module, settingID, state));
                            }
                            case ModuleTracking.targetID -> {
                                ModuleTracking.Target newTarget = Enums.search(value, ModuleTracking.Target.class).orElse(null);

                                // if an enum can't be found
                                if (newTarget == null) {
                                    player.sendMessage(invalidSettingValue);
                                    return;
                                }

                                // update the module
                                trackingModule.setTarget(newTarget);
                                // get the correct set message
                                msg.append(SetMSG.enumString(module, settingID, newTarget.name()));
                            }
                            case ModuleTracking.typeID -> {
                                ModuleTracking.Type newType = Enums.search(value, ModuleTracking.Type.class).orElse(null);

                                // if an enum can't be found
                                if (newType == null) {
                                    player.sendMessage(invalidSettingValue);
                                    return;
                                }

                                // update the module
                                trackingModule.setType(newType);
                                // get the correct set message
                                msg.append(SetMSG.enumString(module, settingID, newType.name()));
                            }
                            default -> {
                                player.sendMessage(LANG.error("invalid", CTxT.of(module.getName()).color(CUtl.s())));
                                return;
                            }
                        }

                        // save & set the updated module after logic is done
                        player.getPData().getHud().setModule(trackingModule);
                    }
                    case SPEED -> {
                        ModuleSpeed speedModule = (ModuleSpeed) mod;

                        switch (settingID) {
                            case ModuleSpeed.calculation2DID -> {
                                // update the module
                                speedModule.setCalculation2D(state);

                                // build the set message
                                msg.append(SetMSG.customToggle(module, settingID, state));
                            }
                            case ModuleSpeed.displayPatternID -> {
                                try {
                                    new DecimalFormat(value);
                                    speedModule.setDisplayPattern(value);
                                }
                                // invalid pattern, error message
                                catch (IllegalArgumentException ignored) {
                                    player.sendMessage(invalidSettingValue);
                                    return;
                                }

                                // build the set message
                                msg.append(SetMSG.custom(module,settingID,CTxT.of(value)));
                            }
                            default -> {
                                player.sendMessage(LANG.error("invalid", CTxT.of(module.getName()).color(CUtl.s())));
                                return;
                            }
                        }

                        // set the updated module after logic is done
                        player.getPData().getHud().setModule(speedModule);
                    }
                    case ANGLE -> {
                        ModuleAngle angleModule = (ModuleAngle) mod;
                        ModuleAngle.Display newDisplay = Enums.search(value,ModuleAngle.Display.class).orElse(null);

                        // if an enum can't be found
                        if (newDisplay == null) {
                            player.sendMessage(invalidSettingValue);
                            return;
                        }

                        // update the module
                        angleModule.setDisplay(newDisplay);

                        // get the correct set message
                        msg.append(SetMSG.enumString(module,settingID,newDisplay.name()));

                        // set the updated module after logic is done
                        player.getPData().getHud().setModule(angleModule);
                    }
                }

                if (Return) Edit.UI(player,msg,module);
                else player.sendMessage(msg);
            }

            /**
             * get setting buttons for the provided module
             * @return CTxT with the buttons
             */
            public static CTxT getSettingButtons(Player player, Module module) {
                // assets
                Lang lang = new Lang("hud.module.setting.");
                Lang moduleLang = new Lang("hud.module.setting."+module.getName()+".");
                BaseModule mod = player.getPCache().getHud().getModule(module);
                String setCMD = "/hud modules setting-r "+module.getName()+" ";

                CTxT button = CTxT.of("");

                if (module.equals(Module.TIME)) {
                    ModuleTime timeModule = (ModuleTime) mod;
                    String settingID = ModuleTime.hour24ID;

                    boolean state = timeModule.isHour24();
                    button.append(moduleLang.get(settingID+"."+(state?"on":"off")).btn(true).color(CUtl.s())
                            .hover(CTxT.of("")
                                    .append(moduleLang.get(settingID+".ui").color('e')).append("\n")
                                    .append(lang.hover("info",moduleLang.get(settingID).color('7')).color('7')).append("\n\n")
                                    .append(lang.hover("set",moduleLang.get(settingID),moduleLang.get(settingID+"."+(state?"off":"on")).color(CUtl.s()))))
                            .click(1,setCMD+settingID+" "+(state?"off":"on")));
                }

                if (module.equals(Module.TRACKING)) {
                    ModuleTracking trackingModule = (ModuleTracking) mod;
                    String hybridID = ModuleTracking.hybridID;

                    boolean hybrid = trackingModule.isHybrid();
                    button.append(CTxT.of(arrows.shuffle).btn(true).color(hybrid?'a':'c')
                            .hover(CTxT.of("")
                                    .append(moduleLang.get(hybridID+".ui").color('e')).append("\n")
                                    .append(moduleLang.get(hybridID+".info").color('7')).append("\n\n")
                                    .append(lang.hover("set.toggle",moduleLang.get(hybridID),CUtl.toggleTxT(!hybrid))))
                            .click(1,setCMD+hybridID+" "+(hybrid?"off":"on")));

                    button.append(" ");

                    String targetID = ModuleTracking.targetID;
                    ModuleTracking.Target currentTarget = trackingModule.getTarget();
                    ModuleTracking.Target nextTarget = Enums.next(currentTarget, ModuleTracking.Target.class);
                    button.append(moduleLang.get(targetID+"."+currentTarget).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                    .append(moduleLang.get(targetID+".ui").color('e')).append("\n")
                                    .append(moduleLang.get(targetID+".info").color('7')).append("\n\n")
                                    .append(lang.hover("set",moduleLang.get(targetID),moduleLang.get(targetID+"."+nextTarget).color(CUtl.s()))))
                            .click(1,setCMD+targetID+" "+nextTarget));

                    button.append(" ");

                    String typeID = ModuleTracking.typeID;
                    ModuleTracking.Type currentType = trackingModule.getType();
                    ModuleTracking.Type nextType = Enums.next(currentType, ModuleTracking.Type.class);
                    button.append(CTxT.of(currentType.equals(ModuleTracking.Type.simple)?arrows.up:arrows.north).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                    .append(moduleLang.get(typeID+".ui").color('e')).append(" - ")
                                    .append(moduleExample(player,Module.TRACKING).color(CUtl.s())).append("\n")
                                    .append(moduleLang.get(typeID+"."+currentType+".info").color('7')).append("\n\n")
                                    .append(lang.hover("set",moduleLang.get(typeID),moduleLang.get(typeID+"."+nextType).color(CUtl.s()))))
                            .click(1,setCMD+typeID+" "+nextType));
                }

                if (module.equals(Module.SPEED)) {
                    ModuleSpeed speedModule = (ModuleSpeed) mod;
                    String calculation2DID = ModuleSpeed.calculation2DID;
                    boolean is2D = speedModule.isCalculation2D();
                    button.append(moduleLang.get(calculation2DID+"."+(is2D?"on":"off")).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                    .append(moduleLang.get(calculation2DID+".ui").color(CUtl.s())).append("\n")
                                    .append(moduleLang.get(calculation2DID+"."+(is2D?"on":"off")+".info").color('7')).append("\n\n")
                                    .append(lang.hover("set",moduleLang.get(calculation2DID),moduleLang.get(calculation2DID+"."+(is2D?"off":"on")).color(CUtl.s()))))
                            .click(1,setCMD+calculation2DID+" "+(is2D?"off":"on")));

                    button.append(" ");

                    String patternID = ModuleSpeed.displayPatternID;
                    button.append(CTxT.of(speedModule.getDisplayPattern()).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                    .append(moduleLang.get(patternID+".ui").color(CUtl.s())).append(" - ")
                                    .append(moduleExample(player,Module.SPEED).color(CUtl.s())).append("\n")
                                    .append(moduleLang.get(patternID+".info").color('7')).append("\n")
                                    .append(moduleLang.get(patternID+".example").color('7').italic(true)).append("\n\n")
                                    .append(lang.hover("set.custom",moduleLang.get(patternID))))
                            .click(2,setCMD+patternID+" "));
                }

                if (module.equals(Module.ANGLE)) {
                    ModuleAngle angleModule = (ModuleAngle) mod;

                    String displayID = ModuleAngle.displayID;
                    ModuleAngle.Display currentType = angleModule.getDisplay();
                    ModuleAngle.Display nextType = Enums.next(currentType, ModuleAngle.Display.class);

                    String buttonIcon = switch (currentType) {
                        case yaw -> arrows.leftRight;
                        case pitch -> arrows.upDown;
                        default -> arrows.leftRight+arrows.upDown;
                    };

                    button.append(CTxT.of(buttonIcon).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                    .append(moduleLang.get(displayID+".ui").color('e')).append(" - ")
                                    .append(moduleLang.get(displayID+"."+currentType)).append("\n")
                                    .append(moduleLang.get(displayID+"."+currentType+".info").color('7')).append("\n\n")
                                    .append(lang.hover("set",moduleLang.get(displayID),moduleLang.get(displayID+"."+nextType).color(CUtl.s()))))
                            .click(1,setCMD+displayID+" "+nextType));
                }

                return button;
            }

            /**
             * contains helper methods to generate a set message for module setting editing
             */
            public static class SetMSG {

                public static CTxT customToggle(Module module, String settingID, boolean state) {
                    Lang settingsLang = new Lang(LANG,module.getName()+"."+settingID+".");
                    return setMSGBuilder("set", module, settingID, settingsLang.get(state?"on":"off").color(CUtl.s()));
                }

                public static CTxT toggle(Module module, String settingID, boolean state) {
                    return setMSGBuilder("set.toggle", module, settingID, CUtl.toggleTxT(state));
                }

                public static CTxT enumString(Module module, String settingID, String enumString) {
                    Lang settingsLang = new Lang(LANG,module.getName()+"."+settingID+".");
                    return setMSGBuilder("set", module, settingID, settingsLang.get(enumString).color(CUtl.s()));
                }

                public static CTxT custom(Module module, String settingID, CTxT customSetMSG) {
                    return setMSGBuilder("set", module, settingID, customSetMSG.color(CUtl.s()));
                }

                private static CTxT setMSGBuilder(String setLang, Module module, String settingID, CTxT setMSG) {
                    return LANG.msg(setLang,
                            LANG.get(module.getName()+"."+settingID).color(CUtl.s()),
                            setMSG);
                }
            }
        }

        public static class Edit {
            public static final Lang LANG = new Lang("hud.module.edit.");

            /**
             * UI for editing a specific module
             * @param aboveTxT the text to show above the UI
             * @param module the module to edit
             */
            public static void UI(Player player, CTxT aboveTxT, Module module) {
                // data
                CTxT msg = CTxT.of(""), line = CUtl.makeLine(37);

                // add the text above if available
                if (aboveTxT != null) msg.append(aboveTxT).append("\n");

                BaseModule mod = player.getPCache().getHud().getModule(module);
                //state
                boolean state = mod.isEnabled();
                CTxT toggle = CTxT.of(Assets.symbols.toggle).btn(true).color(CUtl.toggleColor(state))
                        .click(1,"/hud modules toggle-r "+module)
                        .hover(LANG.hover("toggle").color(Assets.mainColors.edit).append("\n").append(LANG.hover("toggle.click",
                                CTxT.of(module.getName()).color(CUtl.s()),
                                CUtl.LANG.btn(!state?"on":"off").color(!state?'a':'c'))));

                // make the top bar
                msg.append(" ").append(LANG.ui().color(Assets.mainColors.edit));
                msg.append(line);
                msg.append("\n ").append(createPreviewBar(player, module));
                msg.append(line);

                // ORDER
                msg.append("\n ").append(toggle).append(" ").append(createModuleOrderUI(player, module)).append(" ");

                // extra settings section
                CTxT extraSettings = Setting.getSettingButtons(player, module);
                if (!extraSettings.isEmpty()) msg.append("\n\n ").append(extraSettings);

                // toggle and module switcher row
                msg.append("\n\n ").append(createModuleSwitcher(player, module));

                // reset button
                CTxT reset = CUtl.LANG.btn("reset").btn(true).color('7');
                // enable if the module can be reset
                if (canReset(mod)) {
                    reset.color('c')
                            .click(1,"/hud modules reset-r "+module.getName())
                            .hover(new CTxT(LANG.hover("reset").color('c'))
                                    .append("\n").append(LANG.hover("reset.click",
                                            LANG.hover("reset.click.fill").color('c'),
                                            new CTxT(module.getName()).color(CUtl.s()))));
                }

                // bottom buttons
                msg.append("\n\n ").append(reset);
                msg.append("  ").append(CUtl.CButton.back("/hud modules"));

                // bottom line
                msg.append(line);

                player.sendMessage(msg);
            }

            /**
             * creates the preview bar for the module edit UI
             */
            private static CTxT createPreviewBar(Player player, Module module) {
                CTxT refresh = new CTxT(arrows.repeat).btn(true).color('a')
                        .click(1, "/hud modules edit "+module)
                        .hover(LANG.hover("refresh").color('a').append("\n")
                                .append(LANG.hover("refresh.click",LANG.hover("refresh.click.fill").color('a'))));

                CTxT preview = moduleExample(player, module).hover(LANG.hover("preview").color(Assets.mainColors.edit).append("\n")
                        .append(LANG.hover("preview.info").color('7')));

                return new CTxT().append(refresh).append(" ").append(preview);
            }

            /**
             * creates the buttons to switch which module to edit
             */
            private static CTxT createModuleSwitcher(Player player, Module module) {
                ArrayList<BaseModule> modules = player.getPCache().getHud().getModules();
                BaseModule mod = player.getPCache().getHud().getModule(module);
                // the module index starts at 1, so adjust accordingly
                int moduleIndex = mod.getOrder() - 1;
                boolean leftEnabled = moduleIndex > 0, rightEnabled = moduleIndex < modules.size()-1;

                // arrows to cycle the module to edit
                CTxT leftArrow = CTxT.of(arrows.left).btn(true).color(Assets.mainColors.gray),
                        rightArrow = CTxT.of(arrows.right).btn(true).color(Assets.mainColors.gray);

                CTxT hover = LANG.hover().color(Assets.mainColors.edit).append("\n");

                // add functionality if the button is enabled
                if (leftEnabled) leftArrow.color(Assets.mainColors.edit)
                        .hover(CTxT.of(hover).append(LANG.hover("cycle",LANG.hover("cycle.previous").color(CUtl.s()))))
                        .click(1,"/hud modules edit "+modules.get(moduleIndex-1).getModuleType().getName());

                if (rightEnabled) rightArrow.color(Assets.mainColors.edit)
                        .hover(CTxT.of(hover).append(LANG.hover("cycle",LANG.hover("cycle.next").color(CUtl.s()))))
                        .click(1,"/hud modules edit "+modules.get(moduleIndex+1).getModuleType().getName());

                // get the module state color
                String stateColor = stateColor(player, module);
                // get the correct language entry based on the state color
                CTxT status = LANG.hover("status."+ switch (stateColor) {
                    case STATE_GREEN -> "enabled";
                    case STATE_YELLOW -> "no_data";
                    default -> "disabled";
                });
                status.color(stateColor);

                // the middle button
                CTxT moduleTxT = CTxT.of(module.getName()).btn(true)
                        .color(stateColor(player, module))
                        .hover(new CTxT().append(CTxT.of(module.getName()).color(Assets.mainColors.edit))
                                .append("\n").append(LANG.hover("status",status))
                                .append("\n").append(Hud.modules.LANG.get("info."+module.getName()).color('7'))

                                .append("\n\n").append(LANG.hover("change")))
                        .click(2, "/hud modules edit ");

                // build and return the module switcher
                return new CTxT().append(leftArrow).append(" ").append(moduleTxT).append(" ").append(rightArrow);
            }

            /**
             * helper method to create the arrow buttons to change the module's order
             * @param text the text in the button
             * @param enabled if the button is enabled or not
             * @param hoverKey the language key for the hover
             * @param hoverKeySecondary the language key for the hover fill
             * @param setCMD the command to move the module
             * @return the built button
             */
            private static CTxT createOrderMoveButton(String text, boolean enabled, String hoverKey, String hoverKeySecondary, String setCMD) {
                // get the colors for enabled and disabled
                final String enabledColor = CUtl.s(), disabledColor = Assets.mainColors.gray;

                // build the button
                CTxT button = LANG.btn().text(text).btn(true).color(enabled ? enabledColor : disabledColor);

                // if not enabled, return now
                if (!enabled) return button;

                // add click and hover
                button
                        .click(1, setCMD)
                        // key would be something like "most" and the secondary would be like "first"
                        // when building, put "order.move." in front of the key
                        // move.most = Click to move the module to the ___ pos
                        // move.most.first = first
                        .hover(LANG.hover("order").color(Assets.mainColors.edit).append("\n").append(
                                LANG.hover("order.move."+hoverKey, LANG.hover("order.move."+hoverKey+"."+hoverKeySecondary).color(CUtl.s()))));

                return button;
            }

            /**
             * creates the module order navigation buttons
             */
            private static CTxT createModuleOrderUI(Player player, Module module) {
                // order starts at 1, index starts at 0
                int currentOrder = player.getPCache().getHud().getModule(module).getOrder(), moduleIndex = currentOrder - 1;
                boolean leftEnabled = moduleIndex > 0, rightEnabled = moduleIndex < player.getPCache().getHud().getModules().size()-1;


                // the start of the order command
                final String cmd = "/hud modules order-r "+module.getName()+" ";

                // Create each button
                CTxT moveLeftMost = createOrderMoveButton(
                        arrows.leftEnd, leftEnabled,
                        "most", "first",
                        cmd + "1"
                );

                CTxT moveLeft = createOrderMoveButton(
                        arrows.left, leftEnabled,
                        "one", "up",
                        cmd + (currentOrder - 1)
                );

                CTxT moveRight = createOrderMoveButton(
                        arrows.right, rightEnabled,
                        "one", "down",
                        cmd + (currentOrder + 1)
                );

                CTxT moveRightMost = createOrderMoveButton(
                        arrows.rightEnd, rightEnabled,
                        "most", "last",
                        cmd + "100"
                );

                // the middle button
                CTxT currentPosition = new CTxT(String.valueOf(currentOrder)).btn(true)
                        .color(Assets.mainColors.edit)
                        .hover(LANG.hover("order").color(Assets.mainColors.edit).append("\n")
                                .append(LANG.hover("order.info").color('7')).append("\n\n")
                                .append(LANG.hover("order.set")))
                        .click(2, cmd);

                // Combine all buttons into the UI
                return new CTxT()
                        .append(moveLeftMost).append(moveLeft)
                        .append(currentPosition)
                        .append(moveRight).append(moveRightMost);
            }
        }

        /**
         * the HUD Modules chat UI
         * @param aboveTxT a messages that displays above the UI
         * @param pg the module page to display
         */
        public static void UI(Player player, CTxT aboveTxT, int pg) {
            Helper.ListPage<BaseModule> listPage = new Helper.ListPage<>(player.getPCache().getHud().getModules(),PER_PAGE);

            CTxT msg = CTxT.of(""), line = CUtl.makeLine(37);

            // add the text above if available
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            // make the top bar
            msg.append(" ").append(LANG.ui().color(Assets.mainColors.edit)).append(line);


            //MAKE THE TEXT
            for (BaseModule mod : listPage.getPage(pg)) {
                String name = mod.getModuleType().getName();
                msg.append("\n ")
                        // EDIT
                        .append(CTxT.of(Assets.symbols.pencil).btn(true).color(CUtl.p())
                                .click(1, "/hud modules edit "+name)
                                .hover(CTxT.of("click to edit "+name+ " module"))) //todo
                        .append(" ")
                        // NAME
                        .append(CTxT.of(name).color(stateColor(player,mod.getModuleType()))
                                .hover(moduleExample(player,mod.getModuleType()).append("\n").append(moduleInfo(mod.getModuleType()).color('7')))).append(" ");
            }

            //BOTTOM ROW
            msg.append("\n\n ").append(CUtl.LANG.btn("reset").btn(true).color('c').click(1,"/hud modules reset-r")
                            .hover(CUtl.LANG.hover("reset",CUtl.LANG.btn("all").color('c'),LANG.hover("reset_fill"))))
                    .append(" ").append(listPage.getNavButtons(pg,"/hud modules ")).append(" ").append(CUtl.CButton.back("/hud"))
                    .append(line);

            player.sendMessage(msg);
        }
    }
    public static class color {
        /**
         * enum w/the different color toggles
         */
        public enum ColorToggle {
            bold,italics,rainbow,unknown;
            public static ColorToggle get(String s) {
                try {
                    return ColorToggle.valueOf(s);
                } catch (IllegalArgumentException e) {
                    return unknown;
                }
            }
        }
        public static final Lang LANG = new Lang("hud.color.");
        /**
         * creates the button for the color UI
         * @return the button created
         */
        public static CTxT button() {
            return LANG.btn().btn(true).rainbow(new Rainbow(15f,45f)).click(1,"/hud color")
                    .hover(CTxT.of(Assets.cmdUsage.hudColor).rainbow(new Rainbow(15f,45f)).append("\n").append(LANG.hover()));
        }
        public static void cmdExecutor(Player player, String[] args) {
            if (args.length == 0) {
                UI(player, null);
                return;
            }
            // if there is -r, remove it and enable returning
            boolean Return = args[0].contains("-r");
            args[0] = args[0].replace("-r","");
            //RESET
            if (args[0].equals("reset")) {
                // color reset (type)
                if (args.length == 2) reset(player, null, args[1], Return);
                // color reset (type) (settings)
                if (args.length == 3) reset(player,args[2],args[1],Return);
            }
            // CHANGE STYLE
            if (args.length < 2) return;
            if (args[0].equals("primary") || args[0].equals("secondary")) {
                // color (type) edit (settings)
                if (args[1].equals("edit")) {
                    changeUI(player, args.length==3?args[2]: DHud.preset.DEFAULT_UI_SETTINGS, args[0], null);
                    return;
                }
                if (args.length < 3) return;
                // color (type) set (color)
                if (args[1].equals("set")) setColor(player,null,args[0],args[2],false);
                    // color (type) (bold/italics/rainbow) (on/off) (settings)
                else setToggle(player,args.length==4?args[3]:"normal",args[0],ColorToggle.get(args[1]),args[2].equals("on"),Return);
            }
        }
        public static ArrayList<String> cmdSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // color
            if (pos == 0) {
                suggester.add("reset");
                suggester.add("primary");
                suggester.add("secondary");
                return suggester;
            }
            // remove -r and continue with the suggester
            args[0] = args[0].replaceAll("-r", "");

            if (pos == 1) {
                // reset (all/primary/secondary)
                if (args[0].equals("reset")) {
                    suggester.add("all");
                    suggester.add("primary");
                    suggester.add("secondary");
                } else {
                    // (type) (set/bold/italics/rainbow)
                    suggester.add("set");
                    suggester.add("bold");
                    suggester.add("italics");
                    suggester.add("rainbow");
                }
            }
            if (pos == 2) {
                // (type) set (color)
                if (args[1].equals("set")) return Suggester.colors(player,Suggester.getCurrent(args,pos),true);
                else {
                    // (type) (subType) (on/off)
                    suggester.add("on");
                    suggester.add("off");
                }
            }
            return suggester;
        }
        /**
         * gets the default PlayerData entry for each color typ
         * @param typ default HUD color type to get
         * @return the default PlayerData entry for the HUD color type
         */
        public static PDHud.Color defaultEntry(Player player, int typ) {
            PDHud.Color color;
            if (typ==1) color = PlayerData.getDefaults().getHud().getPrimary();
            else color = PlayerData.getDefaults().getHud().getSecondary();
            // make sure the color is bound to the player
            color.setPlayer(player);
            return color;
        }
        /**
         * gets the color pData entry for the typ
         * @param typ the color typ
         * @return the color pData
         */
        public static PDHud.Color getEntry(Player player, int typ) {
            if (typ==1) return player.getPCache().getHud().getPrimary();
            else return player.getPCache().getHud().getSecondary();
        }
        /**
         * sets the pData with the color entry from typ
         * @param typ typ
         * @param entry entry to set
         */
        public static void setEntry(Player player, int typ, PDHud.Color entry) {
            if (typ==1) player.getPData().getHud().setPrimary(entry);
            else player.getPData().getHud().setSecondary(entry);
        }
        /**
         * resets the specified color(s)
         * @param UISettings color UI settings
         * @param type the HUD color type
         * @param Return to return back to the color UI or not
         */
        public static void reset(Player player, String UISettings, String type, boolean Return) {
            switch (type) {
                case "all" -> {
                    player.getPData().getHud().setPrimary(defaultEntry(player,1));
                    player.getPData().getHud().setSecondary(defaultEntry(player,2));
                }
                case "primary" -> player.getPData().getHud().setPrimary(defaultEntry(player,1));
                case "secondary" -> player.getPData().getHud().setSecondary(defaultEntry(player,2));
                default -> {
                    player.sendMessage(CUtl.error("args"));
                    return;
                }
            }
            CTxT msg = CUtl.tag().append(LANG.msg("reset",LANG.get(type).color('c')));
            if (Return && type.equals("all")) UI(player,msg);
            else if (Return) changeUI(player,UISettings,type,msg);
            else player.sendMessage(msg);
        }
        /**
         * sets the color of the HUD type specified
         * @param UISettings color UI settings
         * @param type the HUD color type
         * @param color the color to set
         * @param Return to return back to the color UI or not
         */
        public static void setColor(Player player, String UISettings, String type, String color, boolean Return) {
            int typ = type.equals("primary")?1:2;
            PDHud.Color colorEntry = getEntry(player,typ);
            colorEntry.setColor(CUtl.color.colorHandler(player,color,defaultEntry(player,typ).getColor()));
            setEntry(player,typ,colorEntry);

            if (Return) changeUI(player,UISettings,type,null);
            else player.sendMessage(CUtl.tag().append(LANG.msg("set",LANG.get(type),CUtl.color.getBadge(colorEntry.getColor()))));
        }
        /**
         * sets the toggle state of the specified color setting
         * @param UISettings color UI settings
         * @param colorType the type of color to set the toggle in
         * @param colorToggle the toggle to set
         * @param state the state to set the toggle to
         * @param Return to return back to the color UI or not
         */
        public static void setToggle(Player player, String UISettings, String colorType, ColorToggle colorToggle, boolean state, boolean Return) {
            int typ = colorType.equals("primary")?1:2;
            PDHud.Color colorEntry = getEntry(player,typ);
            // edit the correct toggle
            switch (colorToggle) {
                case bold -> colorEntry.setBold(state);
                case italics -> colorEntry.setItalics(state);
                case rainbow -> colorEntry.setRainbow(state);
                case unknown -> {
                    player.sendMessage(CUtl.error("args"));
                    return;
                }
            }
            // save the new color settings
            setEntry(player,typ,colorEntry);
            // generate the message
            CTxT msg = CUtl.tag().append(LANG.msg("toggle",CUtl.toggleTxT(state),LANG.get(colorType),LANG.get(colorToggle.toString())));
            if (Return) changeUI(player,UISettings,colorType,msg);
            else player.sendMessage(msg);
        }

        /**
         * formats the given text with the hud colors selected
         * @param txt text to be formatted
         * @param typ primary/secondary
         */
        public static CTxT addColor(Player player, String txt, int typ, Rainbow rainbow) {
            CTxT output = CTxT.of(txt).italic(getEntry(player,typ).getItalics()).bold(getEntry(player,typ).getBold());
            if (getEntry(player,typ).getRainbow()) return output.rainbow(rainbow);
            return output.color(getEntry(player,typ).getColor());
        }
        public static CTxT addColor(Player player, CTxT txt, int typ, Rainbow rainbow) {
            return addColor(player,txt.toString(),typ,rainbow);
        }

        /**
         * add the color to the text, using the hud color
         */
        public static CTxT addColor(Player player, CTxT txt, int typ) {
            Rainbow rainbow = player.getPCache().getRainbow(typ);
            return addColor(player, txt, typ, rainbow);
        }

        /**
         * the chat UI for editing a HUD color
         * @param setting the color UI settings
         * @param type HUD color type
         * @param aboveTxT text that gets placed above the UI
         */
        public static void changeUI(Player player, String setting, String type, CTxT aboveTxT) {
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                               ").strikethrough(true);
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            int typ = type.equals("primary")?1:2;
            // if not primary or secondary
            if (typ == 2 && !type.equals("secondary")) {
                player.sendMessage(CUtl.error("args"));
                return;
            }
            // message header
            msg.append(" ").append(addColor(player,LANG.btn(type),typ,new Rainbow(15,20))).append(line).append("\n");
            // make the buttons
            CTxT reset = CUtl.LANG.btn("reset").btn(true).color('c').click(1, "/hud color reset-r "+type+" "+setting)
                    .hover(LANG.hover("reset",addColor(player,LANG.get(type),typ,new Rainbow(15,20))));
            // bold
            CTxT boldButton = LANG.btn("bold").btn(true).color(CUtl.toggleColor(getEntry(player,typ).getBold()))
                    .click(1,String.format("/hud color %s-r bold %s %s",type,(getEntry(player,typ).getBold()?"off":"on"),setting))
                    .hover(LANG.hover("toggle",CUtl.toggleTxT(!getEntry(player,typ).getBold()),LANG.get("bold").bold(true)));
            // italics
            CTxT italicsButton = LANG.btn("italics").btn(true).color(CUtl.toggleColor(getEntry(player,typ).getItalics()))
                    .click(1,String.format("/hud color %s-r italics %s %s",type,(getEntry(player,typ).getItalics()?"off":"on"),setting))
                    .hover(LANG.hover("toggle",CUtl.toggleTxT(!getEntry(player,typ).getItalics()),LANG.get("italics").italic(true)));
            // rainbow
            CTxT rgbButton = LANG.btn("rgb").btn(true).color(CUtl.toggleColor(getEntry(player,typ).getRainbow()))
                    .click(1,String.format("/hud color %s-r rainbow %s %s",type,(getEntry(player,typ).getRainbow()?"off":"on"),setting))
                    .hover(LANG.hover("toggle",CUtl.toggleTxT(!getEntry(player,typ).getRainbow()),LANG.get("rainbow").rainbow(new Rainbow(15f,20f))));
            // build the message
            msg.append(DHud.preset.colorEditor(getEntry(player,typ).getColor(),setting, DHud.preset.Type.hud,type,"/hud color "+type+" edit %s"))
                    .append("\n\n ").append(boldButton).append(" ").append(italicsButton).append(" ").append(rgbButton)
                    .append("\n\n     ").append(reset).append(" ").append(CUtl.CButton.back("/hud color")).append(line);
            player.sendMessage(msg);
        }
        /**
         * main UI for HUD colors
         * @param aboveTxT text that gets placed above the UI
         */
        public static void UI(Player player, CTxT aboveTxT) {
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                                ").strikethrough(true);
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            msg.append(" ").append(LANG.ui().rainbow(new Rainbow(15f,45f))).append(line).append("\n ")
                    //PRIMARY
                    .append(addColor(player,LANG.btn("primary"),1,new Rainbow(15,20)).btn(true).click(1,"/hud color primary edit")
                            .hover(LANG.hover("edit",addColor(player,LANG.get("primary"),1,new Rainbow(15,20))))).append(" ")
                    //SECONDARY
                    .append(addColor(player,LANG.btn("secondary"),2,new Rainbow(15,20)).btn(true).click(1,"/hud color secondary edit")
                            .hover(LANG.hover("edit",addColor(player,LANG.get("secondary"),2,new Rainbow(15,20))))).append("\n\n      ")
                    //RESET
                    .append(CUtl.LANG.btn("reset").btn(true).color('c').click(1,"/hud color reset-r all")
                            .hover(LANG.hover("reset",LANG.get("all").color('c')))).append("  ")
                    .append(CUtl.CButton.back("/hud")).append(line);
            player.sendMessage(msg);
        }
    }
    public static class settings {
        public static final Lang LANG = new Lang("hud.setting.");
        /**
         * creates the button for the settings UI
         * @return the button created
         */
        public static CTxT button() {
            return CUtl.LANG.btn("settings").btn(true).color(Assets.mainColors.setting).click(1,"/hud settings")
                    .hover(CTxT.of(Assets.cmdUsage.hudSettings).color(Assets.mainColors.setting).append("\n").append(CUtl.LANG.hover("settings",CUtl.LANG.get("hud"))));
        }
        public static void CMDExecutor(Player player, String[] args) {
            //UI
            if (args.length == 0) {
                UI(player, null);
                return;
            }
            // if there is -r, remove it and enable returning
            boolean Return = args[0].contains("-r");
            args[0] = args[0].replace("-r","");

            // RESET
            if (args[0].equals("reset")) {
                if (args.length == 1) reset(player,Setting.none,Return);
                // reset (module) - per module
                else reset(player, Setting.get(args[1]),Return);
            }
            // SET
            if (args[0].equals("set")) {
                if (args.length != 3) player.sendMessage(CUtl.error("args"));
                change(player, Setting.get(args[1]),args[2],Return);
            }
        }
        public static ArrayList<String> CMDSuggester(int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // base
            if (pos == 0) {
                suggester.add("set");
                suggester.add("reset");
                return suggester;
            }
            // if -r or -m is attached, remove it and continue with the suggester
            args[0] = args[0].replaceAll("-[rm]", "");
            // settings (set, reset)
            if (pos == 1) {
                // add all settings
                for (Setting s:Setting.values())
                    suggester.add(s.toString());
                // remove none as it was added when adding all settings
                suggester.remove(Setting.none.toString());
                // cant reset distance max, remove
                if (args[0].equals("reset")) suggester.remove(Setting.bossbar__distance_max.toString());
            }
            // settings set (type)
            if (pos == 2 && args[0].equalsIgnoreCase("set")) {
                // boolean settings
                if (Enums.toStringList(Setting.boolSettings()).contains(args[1])) {
                    suggester.add("on");
                    suggester.add("off");
                }
                // type
                if (args[1].equalsIgnoreCase(Setting.type.toString()))
                    suggester.addAll(Enums.toStringList(DisplayType.values()));
                // bossbar.color
                if (args[1].equalsIgnoreCase(Setting.bossbar__color.toString()))
                    suggester.addAll(Enums.toStringList(BarColor.values()));
                // bossbar.distance_max
                if (args[1].equalsIgnoreCase(Setting.bossbar__distance_max.toString()))
                    suggester.add("0");
            }
            return suggester;
        }
        /**
         * gets the config state from the Setting
         * @param setting setting to get
         * @return the current config value for the setting
         */
        public static Object getConfig(Setting setting) {
            return PlayerData.getDefaults().getHud().getSetting(setting);
        }
        /**
         * resets the setting to the config state
         * @param setting setting to reset
         * @param Return to return back to the settings UI
         */
        public static void reset(Player player, Setting setting, boolean Return) {
            // non resettable settings
            if (setting.equals(Setting.bossbar__distance_max)) return;
            // reset all
            if (setting.equals(Setting.none)) {
                // reset all main settings, excluding module settings
                for (Setting s : Setting.baseSettings()) player.getPData().getHud().setSetting(s, getConfig(s));
            } else {
                // reset the selected setting
                player.getPData().getHud().setSetting(setting,getConfig(setting));
            }
            // if bossbar distance, reset max alongside it
            if (setting.equals(Setting.bossbar__distance))
                player.getPData().getHud().setSetting(Setting.get(setting+"_max"),getConfig(Setting.get(setting+"_max")));
            // update the HUD
            player.updateHUD();
            // make the reset message
            CTxT msg = CUtl.tag().append(LANG.msg("reset",LANG.get("category",
                    LANG.get("category."+(setting.toString().startsWith("bossbar")?"bossbar":"hud")),
                    LANG.get(setting.toString()).color(CUtl.s()))));
            if (setting.equals(Setting.none)) msg = CUtl.tag().append(LANG.msg("reset_all",CUtl.LANG.btn("all").color('c')));

            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        /**
         * code for changing HUD settings
         * @param setting the setting to change
         * @param state the new state for the setting
         * @param Return to return back to the settings UI
         */
        public static void change(Player player, Setting setting, String state, boolean Return) {
            boolean bool = state.equals("on");
            CTxT setTxT = CTxT.of("");
            // ON/OFF simple on off toggle
            if (setting.equals(Setting.bossbar__distance) || setting.equals(Setting.state)) {
                player.getPData().getHud().setSetting(setting,bool);
                setTxT.append(CUtl.toggleTxT(bool));
            }
            // ---- CUSTOM HANDLING ----
            if (setting.equals(Setting.type)) {
                DisplayType displayType = DisplayType.get(state);
                player.getPData().getHud().setSetting(setting,displayType);
                setTxT.append(LANG.get(setting+"."+displayType).color(CUtl.s()));
            }
            if (setting.equals(Setting.bossbar__color)) {
                BarColor barColor = Enums.get(state,BarColor.class);
                player.getPData().getHud().setSetting(setting,barColor);
                setTxT.append(LANG.get(setting+"."+barColor).color(Assets.barColor(barColor)));
            }
            if (setting.equals(Setting.bossbar__distance_max)) {
                // make sure the number is greater than 0
                int i = Math.max(Num.toInt(state),0);
                player.getPData().getHud().setSetting(setting,i);
                setTxT.append(CTxT.of(String.valueOf(i)).color((boolean)player.getPData().getHud().getSetting(Setting.bossbar__distance)?'a':'c'));
            }

            // update the hud
            player.updateHUD();
            // make the message
            CTxT msg = CUtl.tag(), typeTxT = LANG.get(setting.toString()).color(CUtl.s());
            String extra = "";
            // if apart of boolSettings, make it a toggle message
            if (Setting.boolSettings().contains(setting)) extra = ".toggle";
            if (setting.toString().startsWith("bossbar.")) { // if bossbar, bossbar category
                typeTxT = LANG.get("category",LANG.get("category.bossbar"),typeTxT);
            } else if (!setting.toString().startsWith("module.")) { // else not module, HUD category
                typeTxT = LANG.get("category",LANG.get("category.hud"),typeTxT);
            }

            msg.append(LANG.msg("set"+extra,typeTxT,setTxT));
            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        /**
         * checks if a setting can be reset by comparing the current state to the config state
         */
        public static boolean canBeReset(Player player, Setting setting) {
            boolean output = false;
            // bossbar max isn't a base, so skip
            if (setting.equals(Setting.none) || setting.equals(Setting.bossbar__distance_max)) return false;
            // flip if the default and current settings doesn't match
            if (!player.getPCache().getHud().getSetting(setting).equals(getConfig(setting))) output = true;
            // if bossbar.distance, check the child setting for the same thing
            if (setting.equals(Setting.bossbar__distance))
                if ((int) player.getPCache().getHud().getSetting(Setting.bossbar__distance_max) != (int)getConfig(Setting.bossbar__distance_max)) output = true;
            return output;
        }
        /**
         * creates an X button for resetting a setting, only enabled if the setting can be reset
         * @param setting setting for the button
         * @return the CTxT with the button
         */
        public static CTxT resetBtn(Player player, Setting setting) {
            CTxT msg = CTxT.of(Assets.symbols.x).btn(true).color('7');
            if (canBeReset(player,setting)) {
                msg.color('c').click(1, "/hud settings reset-r " + setting)
                        .hover(CUtl.LANG.hover("reset",
                                LANG.get("category",
                                        LANG.get("category." + (setting.toString().startsWith("bossbar") ? "bossbar" : "hud")),
                                        LANG.get(setting.toString())).color('c'),
                                CUtl.LANG.hover("reset.settings")));
            }
            return msg;
        }
        /**
         * creates the editing buttons for each setting entry
         * @param setting the setting for the buttons
         * @return the CTxT with the button
         */
        public static CTxT getButtons(Player player, Setting setting) {
            // if there's something in module the command 'end's in module, to return to the module command instead of the settings command
            CTxT button = CTxT.of("");
            if (setting.equals(Setting.state)) {
                button.append(CUtl.toggleBtn((boolean) player.getPCache().getHud().getSetting(setting),"/hud settings set-r "+setting+" ")).append(" ");
            }
            if (setting.equals(Setting.type)) {
                DisplayType nextType = DisplayType.valueOf((String) player.getPCache().getHud().getSetting(setting)).next();
                button.append(LANG.get(setting+"."+ player.getPCache().getHud().getSetting(setting)).btn(true).color(CUtl.s())
                        .click(1,"/hud settings set-r "+setting+" "+nextType)
                        .hover(LANG.hover("set",LANG.get("category",
                                        LANG.get("category.hud"),LANG.get(setting.toString())),
                                LANG.get(setting+"."+nextType).color(CUtl.s()))));
            }
            if (setting.equals(Setting.bossbar__color)) {
                button.append(LANG.get(setting+"."+player.getPCache().getHud().getSetting(setting)).btn(true)
                        .color(Assets.barColor((BarColor.valueOf((String) player.getPCache().getHud().getSetting(setting)))))
                        .click(2,"/hud settings set-r "+setting+" ")
                        .hover(LANG.hover("set.custom",LANG.get("category",
                                LANG.get("category.bossbar"),LANG.get(setting.toString())))));
            }
            if (setting.equals(Setting.bossbar__distance)) {
                boolean state = (boolean) player.getPCache().getHud().getSetting(setting);
                button.append(CUtl.toggleBtn(state,"/hud settings set-r "+setting+" ")).append(" ");
                button.append(CTxT.of(String.valueOf((int)player.getPCache().getHud().getSetting(Setting.bossbar__distance_max))).btn(true).color((boolean) player.getPCache().getHud().getSetting(setting)?'a':'c')
                        .click(2,"/hud settings set-r "+ Setting.bossbar__distance_max+" ")
                        .hover(LANG.hover("set.custom",LANG.get("category",
                                        LANG.get("category.bossbar"),LANG.get(Setting.bossbar__distance_max.toString())))
                                .append("\n").append(LANG.get(setting+"_max.hover").italic(true).color('7'))));
            }
            return button;
        }

        /**
         * the settings UI
         * @param aboveTxT the TxT that appears above the UI, can be null
         */
        public static void UI(Player player, CTxT aboveTxT) {
            CTxT msg = CTxT.of("");
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            msg.append(" ").append(LANG.ui().color(Assets.mainColors.setting)).append(CTxT.of("\n                              \n").strikethrough(true));
            //HUD
            msg.append(" ").append(LANG.get("category.hud").color(CUtl.p())).append(":\n  ");
            msg     //STATE
                    .append(resetBtn(player, Setting.state)).append(" ")
                    .append(LANG.get(Setting.state+".ui").hover(CTxT.of(LANG.get(Setting.state+".ui"))
                                    .append("\n").append(LANG.hover("info.toggle",LANG.get("category.hud"),LANG.get(Setting.state.toString())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.state))
                    .append("\n  ");
            msg     //TYPE
                    .append(resetBtn(player, Setting.type)).append(" ")
                    .append(LANG.get(Setting.type+".ui").hover(CTxT.of(LANG.get(Setting.type+".ui"))
                                    .append("\n").append(LANG.hover("info",LANG.get("category.hud"),LANG.get(Setting.type.toString())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.type))
                    .append("\n");
            //BOSSBAR
            msg.append(" ").append(LANG.get("category.bossbar").color(CUtl.p())).append(":\n  ");
            msg     //COLOR
                    .append(resetBtn(player, Setting.bossbar__color)).append(" ")
                    .append(LANG.get(Setting.bossbar__color+".ui").hover(CTxT.of(LANG.get(Setting.bossbar__color+".ui"))
                            .append("\n").append(LANG.hover("info",LANG.get("category.bossbar"),LANG.get(Setting.bossbar__color.toString())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.bossbar__color))
                    .append("\n  ");
            msg     //DISTANCE
                    .append(resetBtn(player, Setting.bossbar__distance)).append(" ")
                    .append(LANG.get(Setting.bossbar__distance+".ui").hover(CTxT.of(LANG.get(Setting.bossbar__distance+".ui"))
                            .append("\n").append(LANG.get(Setting.bossbar__distance+".info").color('7'))
                            .append("\n").append(LANG.get(Setting.bossbar__distance_max+".ui"))
                            .append("\n").append(LANG.get(Setting.bossbar__distance+".info.2").color('7')))).append(": ")
                    .append(getButtons(player, Setting.bossbar__distance))
                    .append("\n");
            CTxT reset = CUtl.LANG.btn("reset").btn(true).color('7');
            boolean resetOn = false;
            // see if a setting can be reset, then flip the switch
            for (Setting t: Setting.baseSettings()) {
                if (resetOn) break;
                resetOn = canBeReset(player,t);
            }
            if (resetOn) reset.color('c').click(1,"/hud settings reset-r all")
                    .hover(CUtl.LANG.hover("reset",CUtl.LANG.btn("all").color('c'),CUtl.LANG.hover("reset.settings")));
            msg.append("\n    ").append(reset).append("  ").append(CUtl.CButton.back("/hud")).append("\n")
                    .append(CTxT.of("                              ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    /**
     * creates the button for the main HUD UI
     * @return the button created
     */
    public static CTxT BUTTON = LANG.btn().btn(true).color(Assets.mainColors.hud).click(1,"/hud").hover(
                CTxT.of(Assets.cmdUsage.hud).color(Assets.mainColors.hud).append("\n").append(LANG.hover()));
    /**
     * the main UI for the HUD
     * @param aboveTxT TxT that shows up before the UI
     */
    public static void UI(Player player, CTxT aboveTxT) {
        CTxT msg = CTxT.of(""), line = CTxT.of("\n                            ").strikethrough(true);
        if (aboveTxT != null) msg.append(aboveTxT).append("\n");
        msg.append(" ").append(LANG.ui("commands").color(CUtl.p())).append(line).append("\n ");
        //COLOR
        msg.append(color.button()).append(" ");
        //SETTINGS
        msg.append(settings.button()).append("\n\n ");
        //MODULES
        msg.append(modules.button()).append(" ");
        //BACK
        msg.append(CUtl.CButton.back("/dhud"));
        msg.append(line);
        player.sendMessage(msg);
    }
}
