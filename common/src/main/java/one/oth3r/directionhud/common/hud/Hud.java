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
import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Helper.Num;
import one.oth3r.directionhud.common.utils.Helper.ListPage;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

import java.text.DecimalFormat;
import java.util.*;

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
         * @param instructions instructions for building the HUD
         * @return a CTxT with the fully built HUD
         */
        public static CTxT compile(Player player, ModuleInstructions instructions) {
            // returns a CTxT with the fully built HUD
            player.getPCache().getRainbow(HudColor.PRIMARY).setPosition(LoopManager.rainbowF);
            CTxT msg = CTxT.of("");
            // loop for all enabled modules
            int count = 0;
            for (BaseModule mod: ModuleManager.State.getEnabled(player)) {
                Module module = mod.getModuleType();
                count++;
                // if module is empty, skip
                if (instructions.get(module).isEmpty()) continue;
                // append the parsed module text
                msg.append(CUtl.parse(player,instructions.get(module)));
                // if there's another module after the current one, add a space
                if (count < ModuleManager.State.getEnabled(player).size()) msg.append(" ");
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
        public static ModuleInstructions getModuleInstructions(Player player) {
            ModuleInstructions instructions = new ModuleInstructions();
            instructions.put(Module.COORDINATES, getCoordinatesModule(player));
            instructions.put(Module.DESTINATION, getDestinationModule(player));
            instructions.put(Module.DISTANCE, getDistanceModule(player));
            instructions.put(Module.TRACKING, getTrackingModule(player));
            instructions.put(Module.DIRECTION, getDirectionModule(player));
            instructions.put(Module.WEATHER, getWeatherModule(player));
            instructions.put(Module.TIME, getTimeModule(player));
            instructions.put(Module.ANGLE, getAngleModule(player));
            instructions.put(Module.SPEED, getSpeedModule(player));
            return instructions;
        }

        public static String getCoordinatesModule(Player player) {
            ModuleCoordinates module = player.getPCache().getHud().getModule(Module.COORDINATES);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            return module.getDisplayString(player.getLoc());
        }

        public static String getDestinationModule(Player player) {
            ModuleDestination module = player.getPCache().getHud().getModule(Module.DESTINATION);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            Dest dest = Destination.dest.get(player);
            // if no destination is set, empty
            if (!dest.hasXYZ()) return "";

            return module.getDisplayString(dest);
        }

        public static String getDistanceModule(Player player) {
            ModuleDistance module = player.getPCache().getHud().getModule(Module.DISTANCE);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            int distance = Destination.dest.getDist(player);
            // if no destination is set, return empty
            if (distance == -1) return "";

            return module.getDisplayString(distance);
        }

        public static String getTrackingModule(Player player) {
            ModuleTracking module = player.getPCache().getHud().getModule(Module.TRACKING);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            // pointer target
            Loc pointLoc = null;
            // player tracking mode
            ModuleTracking.Target trackingTarget = module.getSetting(ModuleTracking.targetID);
            boolean hybrid = module.getSetting(ModuleTracking.hybridID);

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

            return module.getDisplayString(rotation, player.getLoc(), pointLoc);
        }

        public static String getDirectionModule(Player player) {
            ModuleDirection module = player.getPCache().getHud().getModule(Module.DIRECTION);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";
            // add 180 to the player's yaw (-180 - 180) to get the rotation
            double rotation = player.getYaw()+180;

            return module.getDisplayString(rotation);
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
            String extraIcons = "";
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

            return module.getDisplayString(weatherIcon,extraIcons);
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

            return module.getDisplayString(hour, minute);
        }

        public static String getAngleModule(Player player) {
            ModuleAngle module = player.getPCache().getHud().getModule(Module.ANGLE);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            // assets
            return module.getDisplayString(player.getYaw(), player.getPitch());
        }

        public static String getSpeedModule(Player player) {
            ModuleSpeed module = player.getPCache().getHud().getModule(Module.SPEED);
            // if the module isn't enabled, return empty
            if (!module.isEnabled()) return "";

            // assets
            return module.getDisplayString(player.getPCache().getSpeedData().getSpeed());
        }
    }

    public static class modules {

        public static final String SETTING_ON = "on";
        public static final String SETTING_OFF = "off";
        public static final List<String> SUGGESTER_ON_OFF = List.of(SETTING_ON, SETTING_OFF);

        public static final Lang LANG = new Lang("directionhud.hud.module.");

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
            System.out.println(Arrays.toString(args));
            // UI
            if (args.length == 0) {
                UI(player, null);
                return;
            }

            // if there is -r, remove it and enable returning
            boolean Return = args[0].contains("-r");
            args[0] = args[0].replace("-r","");
            // RESET
            if (args[0].equals("reset")) {
                Module module = (args.length > 1) ? Module.fromString(args[1]) : null;
                ActionResult result = (module == null)
                        ? ModuleManager.Reset.resetEverything(player) // Reset all modules
                        : ModuleManager.Reset.resetModule(player, module); // Reset specific module

                if (Return) {
                    if (module == null) UI(player, result.getChatMessage());
                    else Edit.UI(player, result.getChatMessage(), module);
                } else {
                    player.sendMessage(result.getChatMessage());
                }
            }
            // ENABLE
            if (args[0].equals("enable")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else {
                    // get the enable order if provided
                    Integer order = args.length == 2 ? null : Num.toInt(args[2]);
                    ActionResult result = ModuleManager.State.enable(player, Module.fromString(args[1]), order);
                    if (Return) {
                        if (order == null) Disabled.UI(player,result.getChatMessage(),Num.toInt(result.extraSettings().get("page")));
                        else Edit.UI(player, result.getChatMessage(), Module.fromString(args[1]));
                    } else {
                        player.sendMessage(result.getChatMessage());
                    }
                }
            }
            // DISABLE
            if (args[0].equals("disable")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                ActionResult result = ModuleManager.State.disable(player, Module.fromString(args[1]));

                if (Return) Edit.UI(player, result.getChatMessage(), Module.fromString(result.extraSettings().get("module")));
                else player.sendMessage(result.getChatMessage());
            }
            // EDIT
            if (args[0].equals("edit")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else {
                    // if the module is selected via number, get the module at the order
                    if (Num.isInt(args[1])) {
                        Edit.UI(player, null,
                                ModuleManager.State.getEnabled(player).get(Math.max(0,Math.min(ModuleManager.State.getEnabled(player).size()-1, Num.toInt(args[1])))).getModuleType());
                    } else {
                        // else the module is selected from string
                        Edit.UI(player, null, Module.fromString(args[1]));
                    }
                }
            }
            if (args[0].equals("disabled")) {
                // send error if cmd length isn't long enough
                if (args.length < 2) Disabled.UI(player, null, 1);
                else Disabled.UI(player, null, Num.toInt(args[1]));
            }
            // SETTING
            if (args[0].equals("setting")) {
                // send error if cmd length isn't long enough
                if (args.length < 4) player.sendMessage(CUtl.error("args"));
                else {
                    Module module = Module.fromString(args[1]);
                    ActionResult result = ModuleManager.Setting.setSetting(player,module, args[2], args[3]);

                    if (Return) Edit.UI(player, result.getChatMessage(), module);
                    else player.sendMessage(result.getChatMessage());
                }
            }
            // ORDER
            if (args[0].equals("order")) {
                // send error if cmd length isn't long enough or an order number isn't entered
                if (args.length < 2) player.sendMessage(CUtl.error("args"));
                else if (args.length == 3 && Num.isInt(args[2])) {
                    Module module = Module.fromString(args[1]);
                    ActionResult result = ModuleManager.Order.move(player, module, Integer.parseInt(args[2]));

                    if (Return) Edit.UI(player, result.getChatMessage(), module);
                    else player.sendMessage(result.getChatMessage());

                } else player.sendMessage(CUtl.error("number"));
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
            if (pos == 1) {
                if (args[0].equals("setting")) {
                    suggester.addAll(PlayerData.getDefaults().getHud().getModules().stream()
                            .filter(BaseModule::hasSettings) // only get the modules with editable settings
                            .map(mod -> mod.getModuleType().getName())
                            .toList());
                } else {
                    suggester.addAll(PlayerData.getDefaults().getHud().getModules().stream().map(mod -> mod.getModuleType().getName()).toList());
                }
            }

            // modules order (module) [order]
            if (pos == 2 && args[0].equalsIgnoreCase("order")) {
                suggester.add(String.valueOf(player.getPCache().getHud().getModule(Module.fromString(args[1])).getOrder()));
            }

            // /module order setting
            if (args[0].equalsIgnoreCase("setting")) {
                // [setting-id]
                if (pos == 2) {
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
                if (pos == 3) {
                    // get the setting ID
                    String settingID = args[2];
                    // add items to the suggester based on the ID
                    switch (settingID) {
                        // COORDINATES
                        case ModuleCoordinates.xyzID -> suggester.addAll(SUGGESTER_ON_OFF);
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
                        case ModuleTracking.elevationID -> suggester.addAll(SUGGESTER_ON_OFF);
                    }
                }
            }
            return suggester;
        }

        /**
         * Validates a module and sends a message to the player if invalid override of {@link #validate(Module)}
         * @param module the module to validate
         * @param player the player to notify if the module is invalid
         * @return true if the module is valid, otherwise false
         */
        public static boolean validate(Module module, Player player) {
            if (!validate(module)) {
                player.sendMessage(LANG.err("entered"));
                return false;
            }
            return true;
        }

        /**
         * Validates the given module to check if it is a known module.
         * @param module the module to validate
         * @return true if the module is valid and not {@link Module#UNKNOWN}
         */
        public static boolean validate(Module module) {
            return !module.equals(Module.UNKNOWN);
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
            double randomRotation = random.nextDouble(0,360);
            Weather.Icons defaultIcons = Weather.Icons.defaultIcons();

            return switch (module) {
                case DISTANCE -> mod.getDisplayTxT(player, random.nextInt(50, 250));
                case DESTINATION -> mod.getDisplayTxT(player, new Dest(randomLoc, "a", "#ffffff"));
                case DIRECTION -> mod.getDisplayTxT(player, randomRotation);
                case TRACKING -> mod.getDisplayTxT(player, randomRotation, player.getLoc(), randomLoc);
                case TIME -> mod.getDisplayTxT(player, random.nextInt(1, 25), random.nextInt(0, 60));
                case WEATHER ->
                        mod.getDisplayTxT(player, random.nextBoolean() ? defaultIcons.day() : defaultIcons.night(),
                                random.nextBoolean() ? "" : random.nextBoolean() ? defaultIcons.storm() : defaultIcons.thunderstorm());
                case SPEED -> mod.getDisplayTxT(player, random.nextDouble(1, 12));
                case ANGLE -> mod.getDisplayTxT(player, random.nextFloat(-180, 180.1f), random.nextFloat(-90, 90.1f));
                // default is coordinates module
                default -> mod.getDisplayTxT(player, randomLoc);
            };
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

            String color = STATE_GREEN;

            // if no destination
            if (!Destination.dest.get(player).hasXYZ()) {
                // if destination or distance
                if (module.equals(Module.DESTINATION) || module.equals(Module.DISTANCE)) color = STATE_YELLOW;
            }
            // if tracking
            if (module.equals(Module.TRACKING)) {
                boolean hasPlayer = Destination.social.track.getTarget(player).isValid();
                boolean hasDest = Destination.dest.get(player).hasXYZ();
                ModuleTracking.Target target = mod.getSetting(ModuleTracking.targetID);

                if (mod.getSetting(ModuleTracking.hybridID)) {
                    if (!(hasPlayer || hasDest)) {
                        color = STATE_YELLOW;
                    }
                } else if (target.equals(ModuleTracking.Target.player)) {
                    if (!hasPlayer) {
                        color = STATE_YELLOW;
                    }
                } else if (target.equals(ModuleTracking.Target.dest)) {
                    if (!hasDest) {
                        color = STATE_YELLOW;
                    }
                }
            }

            // return based on yellow, if not green
            return color;
        }

        public static class Disabled {
            private static final int PER_PAGE = 6;
            public static final Lang LANG = new Lang("directionhud.hud.module.disabled.");

            public static ListPage<BaseModule> getList(Player player) {
                return new ListPage<>(ModuleManager.State.getDisabled(player), PER_PAGE);
            }

            public static void UI(Player player, CTxT aboveTxT, int pg) {
                ListPage<BaseModule> listPage = getList(player);
                CTxT msg = new CTxT(), line = CUtl.makeLine(30);

                // add the text above if available
                if (aboveTxT != null) msg.append(aboveTxT).append("\n");

                // make the top bar
                msg.append(" ").append(LANG.ui().color(Assets.mainColors.edit)).append(line).append("\n");

                // for each module in the page
                for (BaseModule mod : listPage.getPage(pg)) {
                    Module module = mod.getModuleType();
                    CTxT enable = CTxT.of(Assets.symbols.toggle).btn(true).color(CUtl.toggleColor(false))
                            .click(1, "/hud modules enable-r "+module)
                            .hover(LANG.hover("enable").color(CUtl.toggleColor(false)).append("\n")
                                    .append(LANG.hover("enable.click", CTxT.of(module.getName()).color(CUtl.s()))));

                    CTxT moduleText = new CTxT(module.getName())
                            .hover(new CTxT()
                                    .append(new CTxT(moduleExample(player, module)).append("\n")
                                            .append(new CTxT(module.getName()).color(Assets.mainColors.edit)).append("\n")
                                            .append(moduleInfo(module).color('7'))));

                    msg.append(" ").append(enable).append(" ").append(moduleText).append("\n");
                }

                // if empty
                if (listPage.getPage(0).isEmpty()) {
                    msg.append("\n ")
                            .append(LANG.ui("none")).append("\n ")
                            .append(LANG.ui("none_2",getEditButton()))
                            .append("\n");
                }

                // back button and bar
                msg.append("\n ").append(listPage.getNavButtons(pg,"/hud modules disabled "))
                        .append(" ").append(CUtl.CButton.back("/hud modules")).append(line);

                player.sendMessage(msg);
            }
        }

        public static class Edit {
            public static final Lang LANG = new Lang("directionhud.hud.module.edit.");

            /**
             * UI for editing a specific module
             * @param aboveTxT the text to show above the UI
             * @param module the module to edit
             */
            public static void UI(Player player, CTxT aboveTxT, Module module) {
                // data
                CTxT msg = new CTxT(), line = CUtl.makeLine(40), back = CUtl.CButton.back("/hud modules");

                // add the text above if available
                if (aboveTxT != null) msg.append(aboveTxT).append("\n");

                // make the top bar
                msg.append(" ").append(LANG.ui().color(Assets.mainColors.edit)).append(line);

                // if empty
                if (ModuleManager.State.getEnabled(player).isEmpty()) {
                    msg.append("\n\n ")
                            .append(LANG.ui("none")).append("\n ").append(LANG.ui("none_2", getDisabledButton()))
                            .append("\n\n ").append(back).append(line);
                    player.sendMessage(msg);
                    return;
                }

                // validate the module
                if (!validate(module, player)) return;

                BaseModule mod = player.getPCache().getHud().getModule(module);
                //state
                boolean state = mod.isEnabled();
                CTxT toggle = CTxT.of(Assets.symbols.toggle).btn(true).color(CUtl.toggleColor(state))
                        .click(1,"/hud modules disable-r "+module)
                        .hover(LANG.hover("toggle").color(Assets.mainColors.edit).append("\n").append(LANG.hover("toggle.click",
                                CTxT.of(module.getName()).color(CUtl.s()),
                                CUtl.LANG.btn(!state?"on":"off").color(!state?'a':'c'))));


                msg.append("\n ").append(createPreviewBar(player, module));
                msg.append(line);

                // ORDER
                msg.append("\n ").append(toggle).append(" ").append(createModuleOrderUI(player, module)).append(" ");

                // extra settings section
                CTxT extraSettings = ModuleManager.Setting.getSettingButtons(player, module);
                if (!extraSettings.isEmpty()) msg.append("\n\n ").append(extraSettings);

                // toggle and module switcher row
                msg.append("\n\n ").append(createModuleSwitcher(player, module));

                // reset button
                CTxT reset = CUtl.LANG.btn("reset").btn(true).color('7'); // todo reset confirmation
                // enable if the module can be reset
                if (ModuleManager.Reset.canResetSettings(mod)) {
                    reset.color('c')
                            .click(1,"/hud modules reset-r "+module.getName())
                            .hover(new CTxT(CUtl.LANG.hover("reset").color('c'))
                                    .append("\n").append(LANG.hover("reset.click",
                                            CUtl.LANG.hover("reset.fill").color('c'),
                                            new CTxT(module.getName()).color(CUtl.s()))));
                }

                // bottom buttons
                msg.append("\n\n ").append(reset);
                msg.append("  ").append(back);

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
                ArrayList<BaseModule> modules = ModuleManager.State.getEnabled(player);
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
                boolean leftEnabled = moduleIndex > 0, rightEnabled = moduleIndex < ModuleManager.State.getEnabled(player).size()-1;

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

        public static CTxT getDisabledButton() {
            return LANG.btn("disabled").color('c').btn(true)
                    .click(1,"/hud modules disabled")
                    .hover(LANG.hover("disabled").color('c').append("\n").append(LANG.hover("disabled.click")));
        }

        public static CTxT getEditButton() {
            return LANG.btn("edit").color(Assets.mainColors.edit).btn(true)
                    .click(1,"/hud modules edit 0")
                    .hover(Edit.LANG.hover("edit").color(Assets.mainColors.edit).append("\n").append(LANG.hover("edit.click")));
        }

        /**
         * the HUD Modules chat UI
         * @param aboveTxT a messages that displays above the UI
         */
        public static void UI(Player player, CTxT aboveTxT) {

            CTxT msg = CTxT.of(""), line = CUtl.makeLine(25);

            // add the text above if available
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            // make the top bar
            msg.append(" ").append(LANG.ui().color(Assets.mainColors.edit)).append(line);

            // the two buttons
            msg.append("\n ").append(getEditButton()).append(" ").append(getDisabledButton()).append("\n");

            // the reset button
            CTxT reset = CUtl.LANG.btn("reset").btn(true).color('7');
            // only make it clickable if any of the modules can reset
            if (player.getPData().getHud().getModules().stream().anyMatch(ModuleManager.Reset::canReset)) {
                reset.color('c')
                        .click(1,"/hud modules reset-r")
                        .hover(CUtl.LANG.hover("reset").color('c').append("\n")
                                // click to [reset] [all] modules.
                                .append(LANG.hover("reset.all",
                                        CUtl.LANG.hover("reset.fill").color('c'),
                                        LANG.hover("reset.all.fill").color(CUtl.s()))));
            }

            //BOTTOM ROW
            msg.append("\n ")
                    .append(reset).append("  ").append(CUtl.CButton.back("/hud"))
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
                    .hover(new CTxT().append(CTxT.of(Assets.cmdUsage.hudColor).rainbow(new Rainbow(15f,45f)).b()).append("\n").append(LANG.hover()));
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
            if (Enums.contains(args[0],HudColor.class)) {
                // color (type)
                if (args.length == 1) {
                    changeUI(player, DHud.preset.DEFAULT_UI_SETTINGS, HudColor.fromName(args[0]), null);
                    return;
                }

                // color (type) edit (settings)
                if (args[1].equals("edit")) {
                    changeUI(player, args.length==3?args[2]: DHud.preset.DEFAULT_UI_SETTINGS, HudColor.fromName(args[0]), null);
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
                else if (Enums.contains(args[0],HudColor.class)) {
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
            else if (Return) changeUI(player,UISettings,HudColor.fromName(type),msg);
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

            if (Return) changeUI(player,UISettings,HudColor.fromName(type),null);
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
            if (Return) changeUI(player,UISettings,HudColor.fromName(colorType),msg);
            else player.sendMessage(msg);
        }

        /**
         * formats the given text with the hud colors selected
         * @param txt text to be formatted
         * @param color primary/secondary
         * @param rainbow the rainbow object to use for when the hud color needs a rainbow object
         * @return returns a {@link CTxT} colored using the player's hud color
         */
        public static CTxT addColor(Player player, String txt, HudColor color, Rainbow rainbow) {
            // get the color settings for the hud color
            PDHud.Color colorSettings = color.getSettings(player);

            CTxT output = CTxT.of(txt).italic(colorSettings.getItalics()).bold(colorSettings.getBold());
            if (colorSettings.getRainbow()) return output.rainbow(rainbow);
            return output.color(colorSettings.getColor());
        }

        /**
         * overflow of {@link #addColor(Player, String, HudColor, Rainbow)} but with a CTxT input instead of a string input
         */
        public static CTxT addColor(Player player, CTxT txt, HudColor color, Rainbow rainbow) {
            return addColor(player,txt.toString(),color,rainbow);
        }

        /**
         * the chat UI for editing a HUD color
         * @param setting the color UI settings
         * @param color HUD color
         * @param aboveTxT text that gets placed above the UI
         */
        public static void changeUI(Player player, String setting, HudColor color, CTxT aboveTxT) {
            // if invalid
            if (color == null) {
                player.sendMessage(CUtl.error("args"));
                return;
            }

            CTxT msg = CTxT.of(""), line = CUtl.makeLine(31);
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");

            PDHud.Color colorData = color.getSettings(player);

            // message header
            msg.append(" ").append(addColor(player,LANG.btn(color.toString()),color,new Rainbow(15,20)))
                    .append(line).append("\n");

            // make the buttons
            CTxT reset = CUtl.LANG.btn("reset").btn(true).color('c').click(1, String.format("/hud color reset-r %s %s",color,setting))
                    .hover(LANG.hover("reset",addColor(player,LANG.get(setting),color,new Rainbow(15,20))));
            // bold
            CTxT boldButton = LANG.btn("bold").btn(true).color(CUtl.toggleColor(colorData.getBold()))
                    .click(1,String.format("/hud color %s-r bold %s %s",color,(colorData.getBold()?"off":"on"),setting))
                    .hover(LANG.hover("toggle",CUtl.toggleTxT(!colorData.getBold()),LANG.get("bold").bold(true)));
            // italics
            CTxT italicsButton = LANG.btn("italics").btn(true).color(CUtl.toggleColor(colorData.getItalics()))
                    .click(1,String.format("/hud color %s-r italics %s %s",color,(colorData.getItalics()?"off":"on"),setting))
                    .hover(LANG.hover("toggle",CUtl.toggleTxT(!colorData.getItalics()),LANG.get("italics").italic(true)));
            // rainbow
            CTxT rgbButton = LANG.btn("rgb").btn(true).color(CUtl.toggleColor(colorData.getRainbow()))
                    .click(1,String.format("/hud color %s-r rainbow %s %s",color,(colorData.getRainbow()?"off":"on"),setting))
                    .hover(LANG.hover("toggle",CUtl.toggleTxT(!colorData.getRainbow()),LANG.get("rainbow").rainbow(new Rainbow(15f,20f))));

            // build the message
            msg.append(DHud.preset.colorEditor(colorData.getColor(), setting, DHud.preset.Type.hud, color.toString(),"/hud color "+ color +" edit %s"))
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
                    .append(addColor(player,LANG.btn("primary"),HudColor.PRIMARY,new Rainbow(15,20)).btn(true).click(1,"/hud color primary edit")
                            .hover(LANG.hover("edit",addColor(player,LANG.get("primary"),HudColor.PRIMARY,new Rainbow(15,20))))).append(" ")
                    //SECONDARY
                    .append(addColor(player,LANG.btn("secondary"),HudColor.SECONDARY,new Rainbow(15,20)).btn(true).click(1,"/hud color secondary edit")
                            .hover(LANG.hover("edit",addColor(player,LANG.get("secondary"),HudColor.SECONDARY,new Rainbow(15,20))))).append("\n\n      ")
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
                        .hover(LANG.hover("reset",
                                CUtl.LANG.hover("reset.fill").color('c'),
                                LANG.get("category." + (setting.toString().startsWith("bossbar") ? "bossbar" : "hud")),
                                LANG.get(setting.toString()).color(CUtl.s())));
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
                    .hover(CUtl.LANG.hover("reset.settings",CUtl.LANG.hover("reset.fill"),CUtl.LANG.btn("all").color(CUtl.s())));
            msg.append("\n    ").append(reset).append("  ").append(CUtl.CButton.back("/hud")).append("\n")
                    .append(CTxT.of("                              ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    /**
     * creates the button for the main HUD UI
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
