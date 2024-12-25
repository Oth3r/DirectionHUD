package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.Assets.symbols.arrows;
import one.oth3r.directionhud.common.Hud.Setting.ModuleAngleDisplay;
import one.oth3r.directionhud.common.Hud.Setting.ModuleTrackingTarget;
import one.oth3r.directionhud.common.Hud.Setting.*;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.ModuleText;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.*;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.Time.*;
import one.oth3r.directionhud.common.files.playerdata.PDHud;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Helper.Num;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Hud {

    public enum Setting {
        state,
        type,
        bossbar__color,
        bossbar__distance,
        bossbar__distance_max,
        module__time_24hr,
        module__tracking_target,
        module__tracking_type,
        module__tracking_hybrid,
        module__speed_pattern,
        module__speed_3d,
        module__angle_display,
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
            list.removeAll(moduleSettings());
            return list;
        }
        public static ArrayList<Setting> moduleSettings() {
            ArrayList<Setting> list = new ArrayList<>();
            for (Setting setting : values())
                if (setting.toString().startsWith("module.")) list.add(setting);
            return list;
        }
        public static ArrayList<Setting> boolSettings() {
            ArrayList<Setting> list = new ArrayList<>();
            list.add(state);
            list.add(bossbar__distance);
            list.add(module__time_24hr);
            list.add(module__speed_3d);
            list.add(module__tracking_hybrid);
            return list;
        }
        /**
         * @return a list of modules that use booleans but have custom names for the boolean states
         */
        public static ArrayList<Setting> customBool() {
            ArrayList<Setting> list = new ArrayList<>();
            list.add(module__time_24hr);
            list.add(module__speed_3d);
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
        public enum ModuleTrackingTarget {
            player,
            dest
        }
        public enum ModuleTrackingType {
            simple,
            compact
        }
        public enum ModuleAngleDisplay {
            yaw,
            pitch,
            both
        }
    }

    public enum Module {
        coordinates,
        distance,
        tracking,
        destination,
        direction,
        time,
        weather,
        speed,
        angle,
        unknown;
        public static Module get(String s) {
            try {
                return Module.valueOf(s);
            } catch (IllegalArgumentException e) {
                return unknown;
            }
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
            for (Module module: modules.getEnabled(player)) {
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
            filledModules.put(Module.coordinates, getCoordinatesModule(player));
            filledModules.put(Module.destination, getDestinationModule(player));
            filledModules.put(Module.distance, getDistanceModule(player));
            filledModules.put(Module.tracking, getTrackingModule(player));
            filledModules.put(Module.direction, getDirectionModule(player));
            filledModules.put(Module.weather, getWeatherModule(player));
            filledModules.put(Module.time, getTimeModule(player));
            filledModules.put(Module.angle, getAngleModule(player));
            filledModules.put(Module.speed, getSpeedModule(player));
            return filledModules;
        }

        public static String getCoordinatesModule(Player player) {
            return String.format(FileData.getModuleText().getCoordinates().getXyz(),
                    player.getBlockX(), player.getBlockY(), player.getBlockZ());

        }
        public static String getDestinationModule(Player player) {
            Dest dest = Destination.dest.get(player);
            // if no destination is set, empty
            if (!dest.hasXYZ()) return "";
            // get the destination assets
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
            int distance = Destination.dest.getDist(player);
            // if no destination is set, return empty
            if (distance == -1) return "";

            return String.format(FileData.getModuleText().getDistance().getNumber(),
                    distance);
        }
        public static String getTrackingModule(Player player) {
            // if the module isn't enabled, return empty
            if (!player.getPCache().getHud().getModule(Module.tracking)) return "";

            // pointer target
            Loc pointLoc = null;
            // player tracking mode
            ModuleTrackingTarget trackingTarget = Enums.get(player.getPCache().getHud().getSetting(Setting.module__tracking_target),ModuleTrackingTarget.class);
            boolean hybrid = (boolean) player.getPCache().getHud().getSetting(Setting.module__tracking_hybrid);

            // if PLAYER or HYBRID (TRACKING CHECK)
            if (trackingTarget.equals(ModuleTrackingTarget.player) || hybrid) {
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
            if (trackingTarget.equals(ModuleTrackingTarget.dest) || (hybrid && pointLoc == null)) {
                // make sure theres a dest
                if (Destination.dest.get(player).hasXYZ())
                    pointLoc = Destination.dest.get(player);
            }
            // check if there's a point set, otherwise return nothing
            if (pointLoc == null) return "";

            ModuleTrackingType trackingType = Enums.get(String.valueOf(player.getPCache().getHud().getSetting(Setting.module__tracking_type)),ModuleTrackingType.class);
            boolean simple = trackingType.equals(ModuleTrackingType.simple);

            // pointer logic
            int x = pointLoc.getX()-player.getBlockX();
            int z = (pointLoc.getZ()-player.getBlockZ())*-1;
            double target = Math.toDegrees(Math.atan2(x, z));
            double rotation = (player.getYaw() - 180) % 360;
            // make sure 0 - 360
            if (rotation < 0) rotation += 360;
            if (target < 0) target += 360;

            String data;
            ModuleText.ModuleTracking.Assets assets = FileData.getModuleText().getTracking().getAssets();
            ModuleText.ModuleTracking.Assets.Simple simpleArrows = assets.getSimple();
            ModuleText.ModuleTracking.Assets.Compact compactArrows = assets.getCompact();
            ModuleText.ModuleTracking.Assets.Elevation elevationArrows = assets.getElevation();

            // NORTH
            if (Num.inBetween(rotation, Num.wSubtract(target,15,360), Num.wAdd(target,15,360)))
                data = simple?simpleArrows.getNorth():compactArrows.getNorth();
            // NORTH WEST
            else if (Num.inBetween(rotation, target, Num.wAdd(target,65,360)))
                data = simple?simpleArrows.getNorthWest():compactArrows.getNorthWest();
            // WEST
            else if (Num.inBetween(rotation, target, Num.wAdd(target,115,360)))
                data = simple?simpleArrows.getWest():compactArrows.getWest();
            // SOUTH WEST
            else if (Num.inBetween(rotation, target, Num.wAdd(target,165,360)))
                data = simple?simpleArrows.getSouthWest():compactArrows.getSouthWest();
            // NORTH EAST
            else if (Num.inBetween(rotation, Num.wSubtract(target, 65, 360), target))
                data = simple?simpleArrows.getNorthEast():compactArrows.getNorthEast();
            // EAST
            else if (Num.inBetween(rotation, Num.wSubtract(target, 115, 360), target))
                data = simple?simpleArrows.getEast():compactArrows.getEast();
            // SOUTH EAST
            else if (Num.inBetween(rotation, Num.wSubtract(target, 165, 360), target))
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
            double rotation = player.getYaw()+180;
            ModuleText.ModuleDirection.Assets.Cardinal cardinals = FileData.getModuleText().getDirection().getAssets().getCardinal();
            String data;

            if (Num.inBetween(rotation,22.5,67.5)) data = cardinals.getNorthEast();
            else if (Num.inBetween(rotation,67.5,112.5)) data = cardinals.getEast();
            else if (Num.inBetween(rotation,112.5,157.5)) data = cardinals.getSouthEast();
            else if (Num.inBetween(rotation,157.5,202.5)) data = cardinals.getSouth();
            else if (Num.inBetween(rotation,202.5,247.5)) data = cardinals.getSouthWest();
            else if (Num.inBetween(rotation,247.5,292.5)) data = cardinals.getWest();
            else if (Num.inBetween(rotation,292.5,337.5)) data = cardinals.getNorthWest();
            else data = cardinals.getNorth();

            return String.format(FileData.getModuleText().getDirection().getFacing(),data);
        }
        public static String getWeatherModule(Player player) {
            Time timeSettings = Dimension.getTimeSettings(player.getDimension());
            Weather weatherSettings = timeSettings.getWeather();

            // if not enabled, return empty
            if (!timeSettings.getEnabled() || !weatherSettings.getEnabled()) return "";

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

            // if no extra icons, single weather
            if (extraIcons == null) return String.format(FileData.getModuleText().getWeather().getWeatherSingle(), weatherIcon);
            // if not, dual weather module
            return String.format(FileData.getModuleText().getWeather().getWeather(), weatherIcon, extraIcons);
        }
        public static String getTimeModule(Player player) {
            Time timeSettings = Dimension.getTimeSettings(player.getDimension());

            // if not enabled, return empty
            if (!timeSettings.getEnabled()) return "";

            // assume that a day ANYWHERE is 24000 ticks (PEOPLE please don't make a mod that changes that)
            int timeTicks = player.getTimeOfDay();

            // (add 6 to account for the day starting at 6 am);
            int hour = (timeTicks / 1000 + 6) % 24;
            int minute = ((timeTicks % 1000) * 60 / 1000);

            // add 0 to the start, then set the string to the last two numbers to always have a 2-digit number
            String min = "0"+minute;
            min = min.substring(min.length()-2);

            // assets
            ModuleText.ModuleTime.Assets assets = FileData.getModuleText().getTime().getAssets();
            boolean time12 = !(boolean)player.getPCache().getHud().getSetting(Setting.module__time_24hr);

            // if 12 hr, fix the hour mark
            if (time12) {
                int hr = hour % 12;
                // if hr % 12 = 0, its 12 am/pm
                if (hr == 0) hour = 12;
            }
            // get the time as a string
            String timeString = hour + assets.getTimeSeparator() + min;

            // return based on 12 or 24 hour
            if (time12) return String.format(FileData.getModuleText().getTime().getHour12(), timeString,
                    hour>=12?assets.getPM():assets.getAM());
            return String.format(FileData.getModuleText().getTime().getHour24(),timeString);
        }
        public static String getAngleModule(Player player) {
            // if the module isnt enabled, return empty
            if (!player.getPCache().getHud().getModule(Module.angle)) return "";
            // assets
            DecimalFormat df = new DecimalFormat("0.0");
            ModuleAngleDisplay playerType = Enums.get(player.getPCache().getHud().getSetting(Setting.module__angle_display),ModuleAngleDisplay.class);
            String yaw = df.format(player.getYaw()), pitch = df.format(player.getPitch());

            return switch (playerType) {
                case yaw -> String.format(FileData.getModuleText().getAngle().getYaw(), yaw);
                case pitch -> String.format(FileData.getModuleText().getAngle().getPitch(), pitch);
                case both -> String.format(FileData.getModuleText().getAngle().getBoth(), yaw, pitch);
            };
        }
        public static String getSpeedModule(Player player) {
            // if the module isnt enabled, return empty
            if (!player.getPCache().getHud().getModule(Module.speed)) return "";

            // assets
            boolean speed3D = (boolean)player.getPCache().getHud().getSetting(Setting.module__speed_3d);
            DecimalFormat df = new DecimalFormat((String) player.getPCache().getHud().getSetting(Setting.module__speed_pattern));
            String speed = df.format(player.getPCache().getSpeedData().getSpeed());

            if (speed3D) String.format(FileData.getModuleText().getSpeed().getXyzSpeed(), speed);
            return String.format(FileData.getModuleText().getSpeed().getXzSpeed(), speed);
        }
    }

    public static class modules {
        private static final int PER_PAGE = 5;

        public static final Lang LANG = new Lang("hud.module.");

        /**
         * creates the button for the modules UI
         * @return the button created
         */
        public static CTxT button() {
            return LANG.btn().btn(true).color(Assets.mainColors.edit).click(1,"/hud modules")
                    .hover(CTxT.of(Assets.cmdUsage.hudModules).color(Assets.mainColors.edit).append("\n").append(LANG.hover()));
        }

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
                if (args.length == 1) reset(player,Module.unknown,Return);
                // reset (module) - per module
                else reset(player,Module.get(args[1]),Return);
            }
            // TOGGLE
            if (args[0].equals("toggle")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else toggle(player, Module.valueOf(args[1]),null,Return);
            }
            // ORDER
            if (args[0].equals("order")) {
                // send error if cmd length isn't long enough or an order number isn't entered
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else if (args.length == 3 && Num.isInt(args[2])) move(player, Module.get(args[1]), Integer.parseInt(args[2]), Return);
                else player.sendMessage(CUtl.error("number"));
            }
        }
        public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // modules
            if (pos == 0) {
                suggester.add("order");
                suggester.add("toggle");
                suggester.add("reset");
                return suggester;
            }
            // if -r is attached, remove it and continue with the suggester
            args[0] = args[0].replaceAll("-r", "");
            // modules (order/toggle/reset) [module]
            if (pos == 1) suggester.addAll(Enums.toStringList(getDefaultOrder()));
            // modules order (module) [order]
            if (pos == 2 && args[0].equalsIgnoreCase("order"))
                suggester.add(String.valueOf(player.getPCache().getHud().getOrder().indexOf(Module.get(args[1]))+1));
            return suggester;
        }

        public static ArrayList<Module> getDefaultOrder() {
            ArrayList<Module> list = new ArrayList<>();
            list.add(Module.coordinates);
            list.add(Module.distance);
            list.add(Module.tracking);
            list.add(Module.destination);
            list.add(Module.direction);
            list.add(Module.time);
            list.add(Module.weather);
            list.add(Module.speed);
            list.add(Module.angle);
            return list;
        }

        /**
         * gets the default module enabled state from the given module
         * @return default state
         */
        public static boolean getDefaultState(Module module) {
            return PlayerData.getDefaults().getHud().getModule(module);
        }

        /**
         * reset module(s) to their default config state
         * @param module module to reset, unknown to reset all
         * @param Return to return to the modules UI
         */
        public static void reset(Player player, Module module, boolean Return) {
            CTxT msg = CUtl.tag();
            if (module.equals(Module.unknown)) {
                // reset order
                player.getPData().getHud().setOrder(PlayerData.getDefaults().getHud().getOrder());
                // reset module settings
                for (Setting s : Setting.moduleSettings())
                    player.getPData().getHud().setSetting(s,settings.getConfig(s));
                // reset module states
                player.getPData().getHud().setModule(new PDHud.Modules());
                msg.append(LANG.msg("reset_all", CUtl.LANG.btn("all").color('c')));
            } else {
                // reset order
                ArrayList<Module> order = player.getPData().getHud().getOrder();
                order.remove(module);
                order.add(getDefaultOrder().indexOf(module),module);
                player.getPData().getHud().setOrder(order);
                // reset settings dealing with the module being reset
                for (Setting s : Setting.moduleSettings()) {
                    if (s.toString().startsWith("module."+module))
                        player.getPData().getHud().setSetting(s, settings.getConfig(s));
                }
                // reset state
                player.getPData().getHud().setModule(module,getDefaultState(module));
                // reset message
                msg.append(LANG.msg("reset",CUtl.LANG.btn("reset").color('c'),CTxT.of(module.toString()).color(CUtl.s())));
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
            if (module.equals(Module.unknown)) {
                player.sendMessage(CUtl.error("hud.module"));
                return;
            }
            ArrayList<Module> order = player.getPData().getHud().getOrder();
            order.remove(module);
            // sub because indexes start at 0, make sure it's not out of bounds;
            pos--;
            pos = Math.max(0,Math.min(pos,order.size()));
            order.add(pos,module);
            player.getPData().getHud().setOrder(order);

            Helper.ListPage<Module> listPage = new Helper.ListPage<>(order,PER_PAGE);
            CTxT msg = CUtl.tag().append(LANG.msg("order",CTxT.of(module.toString()).color(CUtl.s()),CTxT.of(String.valueOf(pos+1)).color(CUtl.s())));
            if (Return) UI(player, msg, listPage.getPageOf(module));
            else player.sendMessage(msg);
        }

        /**
         * sets the enabled state of a module
         * @param module module to edit
         * @param toggle state to change to - null to flip
         * @param Return to return to the modules UI
         */
        public static void toggle(Player player, Module module, Boolean toggle, boolean Return) {
            if (module.equals(Module.unknown)) {
                player.sendMessage(CUtl.error("hud.module"));
                return;
            }
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(player.getPData().getHud().getOrder(),PER_PAGE);
            // if toggle null, flip current
            if (toggle == null) toggle = !player.getPData().getHud().getModule(module);
            player.getPData().getHud().setModule(module,toggle);

            CTxT msg = CUtl.tag().append(LANG.msg("toggle",CUtl.LANG.btn(toggle?"on":"off").color(toggle?'a':'c'),CTxT.of(module.toString()).color(CUtl.s())));
            if (Return) UI(player, msg, listPage.getPageOf(module));
            else player.sendMessage(msg);
        }

        /**
         * module order fixer, removes unknown modules, and fills in the gaps if modules are missing
         * @param list the list that needs to be fixed
         * @return the fixed list
         */
        public static ArrayList<Module> fixOrder(ArrayList<Module> list) {
            ArrayList<Module> allModules = getDefaultOrder();
            // if the module isn't valid, remove
            list.removeIf(s -> s.equals(Module.unknown));
            // if there is more than one of the same module, remove it
            for (Module a: allModules) if (Collections.frequency(list, a) > 1) list.remove(a);
            // remove all duplicates from the default list
            allModules.removeAll(list);
            // then add the missing modules (if there is any)
            list.addAll(allModules);
            return list;
        }

        /**
         * gets the module page # from the HUD setting that is associated with a module
         * @param setting the module setting
         * @return page #
         */
        public static int getPageFromSetting(Player player, Setting setting) {
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(player.getPData().getHud().getOrder(),PER_PAGE);
            Module module = Module.unknown;
            switch (setting) {
                case module__time_24hr -> module = Module.time;
                case module__tracking_hybrid, module__tracking_target, module__tracking_type -> module = Module.tracking;
                case module__speed_3d, module__speed_pattern -> module = Module.speed;
                case module__angle_display -> module = Module.angle;
            }
            return listPage.getPageOf(module);
        }

        /**
         * @return a list of enabled modules in the HUD order
         */
        public static ArrayList<Module> getEnabled(Player player) {
            ArrayList<Module> enabled = new ArrayList<>();
            for (Module module: player.getPCache().getHud().getOrder())
                if (player.getPCache().getHud().getModule(module)) enabled.add(module);
            return enabled;
        }

        /**
         * get setting buttons for the provided module
         * @return CTxT with the buttons
         */
        public static CTxT getButtons(Player player, Module module) {
            CTxT button = CTxT.of("");
            if (module.equals(Module.time)) {
                Setting type = Setting.module__time_24hr;
                boolean state = (boolean) player.getPCache().getHud().getSetting(type);
                button.append(settings.LANG.get(type+"."+(state?"on":"off")).btn(true).color(CUtl.s())
                        .hover(CTxT.of("")
                                .append(settings.LANG.get(type+".ui").color('e')).append("\n")
                                .append(settings.LANG.hover("info",settings.LANG.get(type.toString())).color('7')).append("\n\n")
                                .append(settings.LANG.hover("set",settings.LANG.get(type.toString()),settings.LANG.get(type+"."+(state?"off":"on")).color(CUtl.s()))))
                        .click(1,"/hud settings set-m "+type+" "+(state?"off":"on")));
            }
            if (module.equals(Module.tracking)) {
                Setting type = Setting.module__tracking_hybrid;
                boolean state = (boolean) player.getPCache().getHud().getSetting(type);
                button.append(settings.LANG.get(type+".icon").btn(true).color(state?'a':'c')
                        .hover(CTxT.of("")
                                .append(settings.LANG.get(type+".ui").color('e')).append("\n")
                                .append(settings.LANG.get(type+".info").color('7')).append("\n\n")
                                .append(settings.LANG.hover("set",settings.LANG.get(type.toString()),CUtl.toggleTxT(!state))))
                        .click(1,"/hud settings set-m "+type+" "+(state?"off":"on")));
                type = Setting.module__tracking_target;
                ModuleTrackingTarget currentTarget = Enums.get(player.getPCache().getHud().getSetting(type),ModuleTrackingTarget.class);
                ModuleTrackingTarget nextTarget = Enums.next(currentTarget, ModuleTrackingTarget.class);
                button.append(settings.LANG.get(type+"."+currentTarget).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                .append(settings.LANG.get(type+".ui").color('e')).append("\n")
                                .append(settings.LANG.hover("info",settings.LANG.get(type.toString())).color('7')).append("\n\n")
                                .append(settings.LANG.hover("set",settings.LANG.get(type.toString()),settings.LANG.get(type+"."+nextTarget).color(CUtl.s()))))
                        .click(1,"/hud settings set-m "+type+" "+nextTarget));
                type = Setting.module__tracking_type;
                ModuleTrackingType currentType = Enums.get(player.getPCache().getHud().getSetting(type),ModuleTrackingType.class);
                ModuleTrackingType nextType = Enums.next(currentType, ModuleTrackingType.class);
                button.append(CTxT.of(currentType.equals(ModuleTrackingType.simple)?arrows.up:arrows.north).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                .append(settings.LANG.get(type+".ui").color('e')).append(" - ")
                                .append(moduleInfo(player,Module.tracking,true).color(CUtl.s())).append("\n")
                                .append(settings.LANG.get(type+"."+currentType+".info").color('7')).append("\n\n")
                                .append(settings.LANG.hover("set",settings.LANG.get(type.toString()),settings.LANG.get(type+"."+nextType).color(CUtl.s()))))
                        .click(1,"/hud settings set-m "+type+" "+nextType));
            }
            if (module.equals(Module.speed)) {
                Setting type = Setting.module__speed_3d;
                boolean state = (boolean) player.getPCache().getHud().getSetting(type);
                button.append(settings.LANG.get(type+"."+(state?"on":"off")).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                        .append(settings.LANG.get(type+".ui").color(CUtl.s())).append("\n")
                                        .append(settings.LANG.get(type+"."+(state?"on":"off")+".info").color('7')).append("\n\n")
                                        .append(settings.LANG.hover("set",settings.LANG.get(type.toString()),settings.LANG.get(type+"."+(state?"off":"on")).color(CUtl.s()))))
                        .click(1,"/hud settings set-m "+type+" "+(state?"off":"on")));
                type = Setting.module__speed_pattern;
                button.append(CTxT.of((String) player.getPCache().getHud().getSetting(type)).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                .append(settings.LANG.get(type+".ui").color(CUtl.s())).append(" - ")
                                .append(moduleInfo(player,Module.speed,true).color(CUtl.s())).append("\n")
                                .append(settings.LANG.get(type+".info").color('7')).append("\n")
                                .append(settings.LANG.get(type+".info.2").color('7').italic(true)).append("\n\n")
                                .append(settings.LANG.hover("set.custom",settings.LANG.get(type.toString()))))
                        .click(2,"/hud settings set-m "+type+" "));
            }
            if (module.equals(Module.angle)) {
                Setting type = Setting.module__angle_display;
                ModuleAngleDisplay currentType = Enums.get(player.getPCache().getHud().getSetting(type),ModuleAngleDisplay.class);
                ModuleAngleDisplay nextType = Enums.next(currentType, ModuleAngleDisplay.class);
                String buttonIcon = arrows.leftRight;
                if (currentType.equals(ModuleAngleDisplay.both)) buttonIcon += arrows.upDown;
                else if (currentType.equals(ModuleAngleDisplay.pitch)) buttonIcon = arrows.upDown;
                button.append(CTxT.of(buttonIcon).btn(true).color(CUtl.s()).hover(CTxT.of("")
                                .append(settings.LANG.get(type+".ui").color('e')).append(" - ")
                                .append(settings.LANG.get(type+"."+currentType)).append("\n")
                                .append(settings.LANG.get(type+"."+currentType+".info").color('7')).append("\n\n")
                                .append(settings.LANG.hover("set",settings.LANG.get(type.toString()),settings.LANG.get(type+"."+nextType).color(CUtl.s()))))
                        .click(1,"/hud settings set-m "+type+" "+nextType));
            }
            return button;
        }

        /**
         * gets the sample of the given module as a CTxT todo make data driven
         * @param onlyExample to return only the example
         * @return returns the sample of the HUD module
         */
        public static CTxT moduleInfo(Player player, Module module, boolean... onlyExample) {
            // get the hover info for each module
            CTxT info = CTxT.of("");
            if (module.equals(Module.coordinates))
                info.append(color.addColor(player,"XYZ: ",1,new Rainbow(15,20)))
                        .append(color.addColor(player,"0 0 0",2,new Rainbow(95,20)));
            if (module.equals(Module.distance))
                info.append(color.addColor(player,"[",1,new Rainbow(15,20)))
                        .append(color.addColor(player,"0",2,new Rainbow(35,20)))
                        .append(color.addColor(player,"]",1,new Rainbow(55,20)));
            if (module.equals(Module.destination))
                info.append(color.addColor(player,"[",1,new Rainbow(15,20)))
                        .append(color.addColor(player,"0 0 0",2,new Rainbow(35,20)))
                        .append(color.addColor(player,"]",1,new Rainbow(95,20)));
            if (module.equals(Module.direction))
                info.append(color.addColor(player,"N",1,new Rainbow(15,20)));
            if (module.equals(Module.tracking)) {
                if (Enums.get(player.getPCache().getHud().getSetting(Setting.module__tracking_type),ModuleTrackingType.class)
                        .equals(ModuleTrackingType.simple))
                    info.append(color.addColor(player,"[",1,new Rainbow(15,20)).strikethrough(true))
                            .append(color.addColor(player,"-"+ arrows.up+ arrows.right,2,new Rainbow(35,20)))
                            .append(color.addColor(player,"]",1,new Rainbow(55,20)).strikethrough(true));
                else info.append(color.addColor(player,"[",1,new Rainbow(15,20)).strikethrough(true))
                        .append(color.addColor(player,arrows.north,2,new Rainbow(35,20)))
                        .append(color.addColor(player,"|",1,new Rainbow(55,20)).strikethrough(true))
                        .append(color.addColor(player,arrows.south,2,new Rainbow(75,20)))
                        .append(color.addColor(player,"]",1,new Rainbow(95,20)).strikethrough(true));
            }
            if (module.equals(Module.time)) {
                if ((boolean) player.getPCache().getHud().getSetting(Setting.module__time_24hr))
                    info.append(color.addColor(player,"22:22",1,new Rainbow(15,20)));
                else info.append(color.addColor(player,"11:11 ",2,new Rainbow(15,20)))
                            .append(color.addColor(player,"AM",1,new Rainbow(115,20)));
            }
            if (module.equals(Module.weather))
                info.append(color.addColor(player,Assets.symbols.sun,1,new Rainbow(15,20)));
            DecimalFormat f = new DecimalFormat((String) player.getPCache().getHud().getSetting(Setting.module__speed_pattern));
            String speed = f.format(12.3456789);
            if (module.equals(Module.speed))
                info.append(color.addColor(player,speed,2,new Rainbow(15,20)))
                        .append(color.addColor(player," B/S",1,new Rainbow((speed.length()*20)+15,20)));
            if (module.equals(Module.angle)) {
                info.append(color.addColor(player,"-15.1",2,new Rainbow(15,20)));
                if (Enums.get(player.getPCache().getHud().getSetting(Setting.module__angle_display),ModuleAngleDisplay.class).equals(ModuleAngleDisplay.both))
                    info.append(color.addColor(player,"/",1,new Rainbow(135,20))).append(color.addColor(player,"55.1",2,new Rainbow(155,20)));
            }
            if (onlyExample.length == 0) info.append("\n").append(LANG.get("info."+module).color('7'));
            return info;
        }

        /**
         * returns the color of the module -
         * grey if off
         * yellow if on but can't display
         * green if on and displaying
         * @return the HEX code of the color
         */
        public static String stateColor(Player player, Module module) {
            if (!player.getPCache().getHud().getModule( module)) return Assets.mainColors.gray;
            boolean yellow = false;
            if (!Destination.dest.get(player).hasXYZ()) {
                if (module.equals(Module.destination) || module.equals(Module.distance) || (module.equals(Module.tracking) &&
                        Enums.get(player.getPCache().getHud().getSetting(Setting.module__tracking_target),ModuleTrackingTarget.class).equals(ModuleTrackingTarget.dest)))
                    yellow = true;
            }
            if (module.equals(Module.tracking) && !Destination.social.track.getTarget(player).isValid() && Enums.get(player.getPCache().getHud().getSetting(Setting.module__tracking_target),ModuleTrackingTarget.class).equals(ModuleTrackingTarget.player))
                yellow = true;
            if (yellow) return "#fff419";
            return "#19ff21";
        }

        /**
         * the HUD Modules chat UI
         * @param aboveTxT a messages that displays above the UI
         * @param pg the module page to display
         */
        public static void UI(Player player, CTxT aboveTxT, int pg) {
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(player.getPCache().getHud().getOrder(),PER_PAGE);

            CTxT msg = CTxT.of(""), line = CUtl.makeLine(37);

            // add the text above if available
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            // make the top bar
            msg.append(" ").append(LANG.ui().color(Assets.mainColors.edit)).append(line);
            //MAKE THE TEXT
            for (Module module: listPage.getPage(pg)) {
                boolean state = player.getPCache().getHud().getModule(module);
                msg.append("\n ")
                        //ORDER
                        .append(CTxT.of(String.valueOf(listPage.getIndexOf(module)+1)).btn(true).color(CUtl.p())
                                .click(2,"/hud modules order-r "+module+" ")
                                .hover(CUtl.LANG.hover("order").color(CUtl.p())))
                        //TOGGLE
                        .append(CTxT.of(Assets.symbols.toggle).btn(true).color(CUtl.toggleColor(state))
                                .click(1,"/hud modules toggle-r "+module)
                                .hover(LANG.hover("toggle",
                                        CTxT.of(module.toString()).color(CUtl.s()),
                                        CUtl.LANG.btn(!state?"on":"off").color(!state?'a':'c')))).append(" ")
                        //NAME
                        .append(CTxT.of(module.toString()).color(stateColor(player,module))
                                .hover(moduleInfo(player,module))).append(" ");
                //EXTRA BUTTONS
                msg.append(getButtons(player,module));
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
            // if there is -m, remove it and enable module settings
            boolean module = args[0].contains("-m");
            args[0] = args[0].replace("-m","");

            // RESET
            if (args[0].equals("reset")) {
                if (args.length == 1) reset(player,Setting.none,Return);
                // reset (module) - per module
                else reset(player, Setting.get(args[1]),Return);
            }
            // SET
            if (args[0].equals("set")) {
                if (args.length != 3) player.sendMessage(CUtl.error("args"));
                // return to module UI if -m
                else if (module) {
                    change(player, Setting.get(args[1]),args[2],false);
                    modules.UI(player,null,modules.getPageFromSetting(player,Setting.get(args[1])));
                }
                // else normal return handling
                else change(player, Setting.get(args[1]),args[2],Return);
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
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(DisplayType.values())));
                // bossbar.color
                if (args[1].equalsIgnoreCase(Setting.bossbar__color.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(BarColor.values())));
                // bossbar.distance_max
                if (args[1].equalsIgnoreCase(Setting.bossbar__distance_max.toString()))
                    suggester.add("0");
                // module.tracking_target
                if (args[1].equalsIgnoreCase(Setting.module__tracking_target.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(ModuleTrackingTarget.values())));
                // module.tracking_type
                if (args[1].equalsIgnoreCase(Setting.module__tracking_type.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(ModuleTrackingType.values())));
                // module.speed_pattern
                if (args[1].equalsIgnoreCase(Setting.module__speed_pattern.toString()))
                    suggester.add("\"0.0#\"");
                // module.angle_display
                if (args[1].equalsIgnoreCase(Setting.module__angle_display.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(ModuleAngleDisplay.values())));
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
            if (setting.equals(Setting.bossbar__distance) || setting.equals(Setting.state) || setting.equals(Setting.module__tracking_hybrid)) {
                player.getPData().getHud().setSetting(setting,bool);
                setTxT.append(CUtl.toggleTxT(bool));
            }
            // ON/OFF with custom name for the states
            if (setting.equals(Setting.module__time_24hr) || setting.equals(Setting.module__speed_3d)) {
                player.getPData().getHud().setSetting(setting,bool);
                setTxT.append(LANG.get(setting+"."+(bool?"on":"off")).color(CUtl.s()));
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
            if (setting.equals(Setting.module__tracking_target)) {
                ModuleTrackingTarget moduleTrackingTarget = Enums.get(state,ModuleTrackingTarget.class);
                player.getPData().getHud().setSetting(setting, moduleTrackingTarget);
                setTxT.append(LANG.get(setting+"."+moduleTrackingTarget).color(CUtl.s()));
            }
            if (setting.equals(Setting.module__tracking_type)) {
                ModuleTrackingType moduleTrackingType = Enums.get(state,ModuleTrackingType.class);
                player.getPData().getHud().setSetting(setting, moduleTrackingType);
                setTxT.append(LANG.get(setting+"."+moduleTrackingType).color(CUtl.s()));
            }
            if (setting.equals(Setting.module__speed_pattern)) {
                // try to make the decimal format, if error don't do anything
                try {
                    new DecimalFormat(state);
                    player.getPData().getHud().setSetting(setting,state);
                } catch (IllegalArgumentException ignored) {}
                setTxT.append(CTxT.of(String.valueOf(player.getPData().getHud().getSetting(setting))).color(CUtl.s()));
            }
            if (setting.equals(Setting.module__angle_display)) {
                ModuleAngleDisplay moduleAngleDisplay = Enums.get(state,ModuleAngleDisplay.class);
                player.getPData().getHud().setSetting(setting, moduleAngleDisplay);
                setTxT.append(LANG.get(setting+"."+moduleAngleDisplay).color(CUtl.s()));
            }
            // update the hud
            player.updateHUD();
            // make the message
            CTxT msg = CUtl.tag(), typeTxT = LANG.get(setting.toString()).color(CUtl.s());
            String extra = "";
            // if apart of boolSettings, make it a toggle message
            if (Setting.boolSettings().contains(setting)) extra = ".toggle";
            // if custom boolean, use the normal set message
            if (Setting.customBool().contains(setting)) extra = "";
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
