package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Helper.Num;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.common.Assets.symbols.arrows;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import one.oth3r.directionhud.common.HUD.Setting.ModuleAngleDisplay;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;

import java.text.DecimalFormat;
import java.util.*;

public class HUD {
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
                    return DisplayType.valueOf(config.hud.defaults.DisplayType);
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
            white;
            public static BarColor get(String s) {
                try {
                    return BarColor.valueOf(s);
                } catch (IllegalArgumentException e) {
                    return BarColor.valueOf(config.hud.defaults.BarColor);
                }
            }
        }
        public enum ModuleTrackingTarget {
            player,
            dest;
            public static ModuleTrackingTarget get(String s) {
                try {
                    return ModuleTrackingTarget.valueOf(s);

                } catch (IllegalArgumentException e) {
                    return ModuleTrackingTarget.valueOf(config.hud.defaults.TrackingTarget);
                }
            }
        }
        public enum ModuleTrackingType {
            simple,
            compact;
            public static ModuleTrackingType get(String s) {
                try {
                    return ModuleTrackingType.valueOf(s);

                } catch (IllegalArgumentException e) {
                    return ModuleTrackingType.valueOf(config.hud.defaults.TrackingType);
                }
            }
        }
        public enum ModuleAngleDisplay {
            yaw,
            pitch,
            both;
            public static ModuleAngleDisplay get(String s) {
                try {
                    return ModuleAngleDisplay.valueOf(s);

                } catch (IllegalArgumentException e) {
                    return ModuleAngleDisplay.valueOf(config.hud.defaults.AngleDisplay);
                }
            }
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
    public static int minute;
    public static int hour;
    public static String weatherIcon = "?";
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (!Utl.checkEnabled.hud(player)) return;
            if (args.length == 0) {
                UI(player,null);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Helper.trimStart(args, 1);
            switch (type) {
                case "modules" -> modules.CMDExecutor(player, trimmedArgs);
                case "settings" -> settingsCMD(player,trimmedArgs);
                case "color" -> color.cmdExecutor(player, trimmedArgs);
                case "toggle" -> player.sendMessage(settings.change(player,Setting.state,(boolean) PlayerData.get.hud.setting(player,Setting.state)?"off":"on",false));
                default -> player.sendMessage(CUtl.error("command"));
            }
        }
        public static void settingsCMD(Player player, String[] args) {
            //UI
            if (args.length == 0) {
                settings.UI(player, null);
                return;
            }
            boolean Return = false;
            boolean module = false;
            // if the module has -r, remove it and enable returning
            if (args[0].contains("-r")) {
                args[0] = args[0].replace("-r","");
                Return = true;
            }
            if (args[0].contains("-m")) {
                args[0] = args[0].replace("-m","");
                module = true;
            }
            // /hud settings reset
            if (args.length == 1 && args[0].equalsIgnoreCase("reset")) settings.reset(player,Setting.none,Return);
            if (args.length == 2) {
                // /hud settings reset (module)
                if (args[0].equalsIgnoreCase("reset")) settings.reset(player, Setting.get(args[1]),Return);
                if (args[0].equalsIgnoreCase("set")) player.sendMessage(CUtl.error("setting"));
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
                // return to module UI if -m
                if (module) modules.UI(player, settings.change(player, Setting.get(args[1]),args[2],false),modules.getPageFromSetting(player,Setting.get(args[1])));
                // if returning don't send the change message
                else if (Return) settings.change(player, Setting.get(args[1]),args[2],true);
                // not returning just send the player the change message
                else player.sendMessage(settings.change(player, Setting.get(args[1]),args[2],false));
            }
        }
    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.hud(player)) return suggester;
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
                    case "settings" -> suggester.addAll(settingsCMD(fixedPos,trimmedArgs));
                    case "color" -> suggester.addAll(color.cmdSuggester(player,fixedPos,trimmedArgs));
                }
            }
            return suggester;
        }
        public static ArrayList<String> settingsCMD(int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // settings
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
                for (Setting s:Setting.baseSettings())
                    suggester.add(s.toString());
                for (Setting s:Setting.moduleSettings())
                    suggester.add(s.toString());
                // cant reset distance max, remove
                if (args[0].equalsIgnoreCase(Setting.bossbar__distance_max.toString()))
                    suggester.remove(Setting.bossbar__distance_max.toString());
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
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(Setting.DisplayType.values())));
                // bossbar.color
                if (args[1].equalsIgnoreCase(Setting.bossbar__color.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(Setting.BarColor.values())));
                // bossbar.distance_max
                if (args[1].equalsIgnoreCase(Setting.bossbar__distance_max.toString()))
                    suggester.add("0");
                // module.tracking_target
                if (args[1].equalsIgnoreCase(Setting.module__tracking_target.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(Setting.ModuleTrackingTarget.values())));
                // module.tracking_type
                if (args[1].equalsIgnoreCase(Setting.module__tracking_type.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(Setting.ModuleTrackingType.values())));
                // module.speed_pattern
                if (args[1].equalsIgnoreCase(Setting.module__speed_pattern.toString()))
                    suggester.add("\"0.0#\"");
                // module.angle_display
                if (args[1].equalsIgnoreCase(Setting.module__angle_display.toString()))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(Setting.ModuleAngleDisplay.values())));
            }
            return suggester;
        }
    }
    public static CTxT lang(String key, Object... args) {
        return CUtl.lang("hud."+key, args);
    }
    public static HashMap<Module, ArrayList<String>> getRawHUDText(Player player) {
        //return a HashMap that for each HUD module, has a string with style instructions and the actual HUD
        ArrayList<String> coordinates = new ArrayList<>();
        coordinates.add("pXYZ: ");
        coordinates.add("s"+player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
        ArrayList<String> destination = new ArrayList<>();
        ArrayList<String> distance = new ArrayList<>();
        ArrayList<String> tracking = getTrackingModule(player);
        if (Destination.get(player).hasXYZ()) {
            destination.add("p[");
            destination.add("s"+ Destination.get(player).getXYZ());
            destination.add("p]");
            distance.add("p[");
            distance.add("s"+ Destination.getDist(player));
            distance.add("p]");
        }
        ArrayList<String> direction = new ArrayList<>();
        direction.add("p"+getPlayerDirection(player));
        ArrayList<String> time = new ArrayList<>();
        if ((boolean) PlayerData.get.hud.setting(player, Setting.module__time_24hr)) {
            time.add("s"+getGameTime(false));
        } else {
            // 12 hr clock
            time.add("s"+getGameTime(true)+" ");
            time.add("p"+(hour>=12?"PM":"AM"));
        }
        ArrayList<String> weather = new ArrayList<>();
        weather.add("p"+weatherIcon);
        HashMap<Module, ArrayList<String>> filledModules = new HashMap<>();
        filledModules.put(Module.coordinates, coordinates);
        filledModules.put(Module.distance, distance);
        filledModules.put(Module.destination, destination);
        filledModules.put(Module.direction, direction);
        filledModules.put(Module.time, time);
        filledModules.put(Module.weather, weather);
        filledModules.put(Module.tracking, tracking);
        filledModules.put(Module.speed, getSpeedModule(player));
        filledModules.put(Module.angle, getAngleModule(player));
        return filledModules;
    }
    public static CTxT build(Player player, HashMap<Module, ArrayList<String>> filledModules) {
        // returns a CTxT with the fully built HUD
        int start = 1;
        CTxT msg = CTxT.of("");
        // loop for all enabled modules
        int count = 0;
        for (Module module: modules.getEnabled(player)) {
            count++;
            // if dest isn't set
            if (!Destination.get(player).hasXYZ()) {
                // if dest or distance, remove
                if (module.equals(Module.destination) || module.equals(Module.distance)) continue;
            }
            // if tracking module
            if (module.equals(Module.tracking)) {
                // if tracking type is dest and dest is off, remove.
                // else player tracking type and no player tracked, remove
                if (PlayerData.get.hud.setting(player, Setting.module__tracking_target).equals(Setting.ModuleTrackingTarget.dest.toString())) {
                    if (!Destination.get(player).hasXYZ()) continue;
                } else if (Destination.social.track.getTarget(player) == null) continue;
            }
            for (String str : filledModules.get(module)) {
                String string = str.substring(1);
                boolean strike = false;
                // if '/', remove the char and enable strikethrough for the text
                if (str.charAt(0) == '/') {
                    str = str.substring(1);
                    string = string.substring(1);
                    strike = true;
                }
                // if 'p' use primary color, 's' for secondary
                int typ = str.charAt(0) == 'p'?1:2;
                // add the color and style
                msg.append(color.addColor(player,string,typ,LoopManager.rainbowF+start,5)
                        .strikethrough(strike));
                // if rainbow, move the starting position by how many characters were turned into a rainbow, for a seamless rainbow
                if (color.getHUDRainbow(player,typ)) {
                    String rgbStr = string.replaceAll("\\s", "");
                    start = start + rgbStr.codePointCount(0, rgbStr.length())*5;
                }
            }
            if (count < modules.getEnabled(player).size()) msg.append(" ");
        }
        if (msg.equals(CTxT.of(""))) return CTxT.of("");
        //make the click event unique for detecting if an actionbar is from DirectionHUD or not
        msg.cEvent(3,"https://modrinth.com/mod/directionhud");
        return msg;
    }
    public static String getPlayerDirection(Player player) {
        double rotation = (player.getYaw() - 180) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (Num.inBetween(rotation,0,22.5)) return "N";
        else if (Num.inBetween(rotation,22.5,67.5)) return "NE";
        else if (Num.inBetween(rotation,67.5,112.5)) return "E";
        else if (Num.inBetween(rotation,112.5,157.5)) return "SE";
        else if (Num.inBetween(rotation,157.5,202.5)) return "S";
        else if (Num.inBetween(rotation,202.5,247.5)) return "SW";
        else if (Num.inBetween(rotation,247.5,292.5)) return "W";
        else if (Num.inBetween(rotation,292.5,337.5)) return "NW";
        else if (Num.inBetween(rotation,337.5,360.0)) return "N";
        else return "?";
    }
    public static String getGameTime(boolean t12hr) {
        int hr = hour;
        // add 0 to the start, then set the string to the last to numbers to always have a 2-digit number
        String min = "0" + minute;
        min = min.substring(min.length()-2);
        if (t12hr) hr = (hr % 12 == 0)? 12:hr % 12;
        return hr+":"+min;
    }
    public static ArrayList<String> getTrackingModule(Player player) {
        ArrayList<String> tracking = new ArrayList<>();
        if (!PlayerData.get.hud.module(player, Module.tracking)) return tracking;
        // pointer target
        Loc pointLoc = null;
        // player tracking mode
        Setting.ModuleTrackingTarget trackingTarget = Setting.ModuleTrackingTarget.get(
                String.valueOf(PlayerData.get.hud.setting(player, Setting.module__tracking_target)));
        boolean hybrid = (boolean) PlayerData.get.hud.setting(player,Setting.module__tracking_hybrid);
        // PLAYER or HYBRID
        if (trackingTarget.equals(Setting.ModuleTrackingTarget.player) || hybrid) {
            // make sure there's a target
            if (!(PlayerData.get.dest.tracking(player) == null)) {
                Player target = Destination.social.track.getTarget(player);
                // make sure the player is real
                if (!(target == null)) {
                    Loc plLoc = new Loc(target);
                    // not in the same dimension
                    if (!player.getDimension().equals(target.getDimension())) {
                        // can convert and autoconvert is on
                        if (Utl.dim.canConvert(player.getDimension(),target.getDimension()) && (boolean) PlayerData.get.dest.setting(player, Destination.Setting.autoconvert)) {
                            plLoc.convertTo(player.getDimension());
                        } else plLoc = null;
                    }
                    // set the loc
                    pointLoc = plLoc;
                }
            }
        }
        // DEST or (HYBRID & NULL TRACKER)
        if (trackingTarget.equals(Setting.ModuleTrackingTarget.dest) || (hybrid && pointLoc == null)) {
            // make sure theres a dest
            if (Destination.get(player).hasXYZ())
                pointLoc = Destination.get(player);
        }
        // check if there's a point set, otherwise return nothing
        if (pointLoc == null) return tracking;
        Setting.ModuleTrackingType trackingType = Setting.ModuleTrackingType.get(String.valueOf(PlayerData.get.hud.setting(player,Setting.module__tracking_type)));
        boolean simple = trackingType.equals(Setting.ModuleTrackingType.simple);
        tracking.add("/p["); // add the first bracket
        // pointer logic
        int x = pointLoc.getX()-player.getBlockX();
        int z = (pointLoc.getZ()-player.getBlockZ())*-1;
        double target = Math.toDegrees(Math.atan2(x, z));
        double rotation = (player.getYaw() - 180) % 360;
        // make sure 0 - 360
        if (rotation < 0) rotation += 360;
        if (target < 0) target += 360;
        if (Num.inBetween(rotation, Num.wSubtract(target,15,360), Num.wAdd(target,15,360)))
            tracking.add("s"+(simple?"-"+ arrows.up+"-" : arrows.north));
        // NORTH
        else if (Num.inBetween(rotation, target, Num.wAdd(target,65,360)))
            tracking.add("s"+(simple? arrows.left+ arrows.up+"-" : arrows.north_west));
        // NORTH WEST
        else if (Num.inBetween(rotation, target, Num.wAdd(target,115,360)))
            tracking.add("s"+(simple? arrows.left+"--" : arrows.west));
        // WEST
        else if (Num.inBetween(rotation, target, Num.wAdd(target,165,360)))
            tracking.add("s"+(simple? arrows.left+ arrows.down+"-" : arrows.south_west));
        // SOUTH WEST
        else if (Num.inBetween(rotation, Num.wSubtract(target, 65, 360), target))
            tracking.add("s"+(simple?"-"+ arrows.up+ arrows.right : arrows.north_east));
        // NORTH EAST
        else if (Num.inBetween(rotation, Num.wSubtract(target, 115, 360), target))
            tracking.add("s"+(simple?"--"+ arrows.right : arrows.east));
        // EAST
        else if (Num.inBetween(rotation, Num.wSubtract(target, 165, 360), target))
            tracking.add("s"+(simple?"-"+ arrows.down+ arrows.right : arrows.south_east));
        // SOUTH EAST
        else tracking.add("s"+(simple?"-"+ arrows.down+"-" : arrows.south));
        // SOUTH
        // if compact and the ylevel is different & there's a y level on the loc
        if (!simple && !(boolean) PlayerData.get.dest.setting(player, Destination.Setting.ylevel) && pointLoc.yExists()) {
            tracking.add("p|");
            if (player.getLoc().getY() > pointLoc.getY())
                tracking.add("s"+arrows.south);
            else tracking.add("s"+arrows.north);
        }
        tracking.add("/p]");
        return tracking;
    }
    public static ArrayList<String> getSpeedModule(Player player) {
        ArrayList<String> speed = new ArrayList<>();
        if (!PlayerData.get.hud.module(player,Module.speed)) return speed;
        DecimalFormat f = new DecimalFormat((String) PlayerData.get.hud.setting(player,Setting.module__speed_pattern));
        speed.add("s"+f.format(PlayerData.dataMap.get(player).get("speed")));
        speed.add("p"+" B/S");
        return speed;
    }
    public static ArrayList<String> getAngleModule(Player player) {
        ArrayList<String> angle = new ArrayList<>();
        DecimalFormat f = new DecimalFormat("0.0");
        Setting.ModuleAngleDisplay playerType = ModuleAngleDisplay.get((String)PlayerData.get.hud.setting(player,Setting.module__angle_display));
        if (playerType.equals(ModuleAngleDisplay.yaw) || playerType.equals(ModuleAngleDisplay.both)) {
            angle.add("s"+f.format(player.getYaw()));
        }
        if (playerType.equals(ModuleAngleDisplay.pitch) || playerType.equals(ModuleAngleDisplay.both)) {
            if (playerType.equals(ModuleAngleDisplay.both)) angle.add("p"+"/");
            angle.add("s"+f.format(player.getPitch()));
        }
        return angle;
    }
    public static class modules {
        private static final int PER_PAGE = 5;
        public static CTxT lang(String key, Object... args) {
            return HUD.lang("module."+key, args);
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
                suggester.add(String.valueOf(PlayerData.get.hud.order(player).indexOf(Module.get(args[1]))+1));
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
            boolean output = false;
            switch (module) {
                case coordinates -> output = config.hud.Coordinates;
                case distance -> output = config.hud.Distance;
                case tracking -> output = config.hud.Tracking;
                case destination -> output = config.hud.Destination;
                case direction -> output = config.hud.Direction;
                case time -> output = config.hud.Time;
                case weather -> output = config.hud.Weather;
                case speed -> output = config.hud.Speed;
                case angle -> output = config.hud.Angle;
            }
            return output;
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
                PlayerData.set.hud.order(player, config.hud.Order);
                // reset module settings
                for (Setting s : Setting.moduleSettings())
                    PlayerData.set.hud.setting(player, s, settings.getConfig(s));
                // reset module states
                PlayerData.set.hud.moduleMap(player, PlayerData.defaults.hudModule());
                msg.append(lang("msg.reset_all", CUtl.TBtn("reset").color('c')));
            } else {
                // reset order
                ArrayList<HUD.Module> order = PlayerData.get.hud.order(player); 
                order.remove(module);
                order.add(getDefaultOrder().indexOf(module),module);
                PlayerData.set.hud.order(player,order);
                // reset settings dealing with the module being reset
                for (Setting s : Setting.moduleSettings()) {
                    if (s.toString().startsWith("module."+module))
                        PlayerData.set.hud.setting(player, s, settings.getConfig(s));
                }
                // reset state
                PlayerData.set.hud.module(player,module,getDefaultState(module));
                // reset message
                msg.append(lang("msg.reset",CUtl.TBtn("reset").color('c'),CTxT.of(module.toString()).color(CUtl.s())));
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
            ArrayList<Module> order = PlayerData.get.hud.order(player);
            order.remove(module);
            // sub because indexes start at 0, make sure it's not out of bounds;
            pos--;
            pos = Math.max(0,Math.min(pos,order.size()));
            order.add(pos,module);
            PlayerData.set.hud.order(player,order);
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(PlayerData.get.hud.order(player),PER_PAGE);
            CTxT msg = CUtl.tag().append(lang("msg.order",CTxT.of(module.toString()).color(CUtl.s()),CTxT.of(String.valueOf(pos+1)).color(CUtl.s())));
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
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(PlayerData.get.hud.order(player),PER_PAGE);
            if (toggle == null) toggle = !PlayerData.get.hud.module(player,module);
            CTxT msg = CUtl.tag().append(lang("msg.toggle",CUtl.TBtn(toggle?"on":"off").color(toggle?'a':'c'),CTxT.of(module.toString()).color(CUtl.s())));
            PlayerData.set.hud.module(player,module,toggle);
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
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(PlayerData.get.hud.order(player),PER_PAGE);
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
         * @return a list of enabled modules
         */
        public static ArrayList<Module> getEnabled(Player player) {
            ArrayList<Module> enabled = new ArrayList<>();
            for (Module module: PlayerData.get.hud.order(player)) if (PlayerData.get.hud.module(player,module)) enabled.add(module);
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
                boolean state = (boolean) PlayerData.get.hud.setting(player,type);
                button.append(settings.lang(type+"."+(state?"on":"off")).btn(true).color(CUtl.s())
                        .hEvent(CTxT.of("")
                                .append(settings.lang(type+".ui").color('e')).append("\n")
                                .append(settings.lang("hover.info",settings.lang(type.toString())).color('7')).append("\n\n")
                                .append(settings.lang("hover.set",settings.lang(type.toString()),settings.lang(type+"."+(state?"off":"on")).color(CUtl.s()))))
                        .cEvent(1,"/hud settings set-m "+type+" "+(state?"off":"on")));
            }
            if (module.equals(Module.tracking)) {
                Setting type = Setting.module__tracking_hybrid;
                boolean state = (boolean) PlayerData.get.hud.setting(player,type);
                button.append(settings.lang(type+".icon").btn(true).color(state?'a':'c')
                        .hEvent(CTxT.of("")
                                .append(settings.lang(type+".ui").color('e')).append("\n")
                                .append(settings.lang(type+".info").color('7')).append("\n\n")
                                .append(settings.lang("hover.set",settings.lang(type.toString()),CUtl.toggleTxT(!state))))
                        .cEvent(1,"/hud settings set-m "+type+" "+(state?"off":"on")));
                type = Setting.module__tracking_target;
                Setting.ModuleTrackingTarget currentTarget = Setting.ModuleTrackingTarget.get((String) PlayerData.get.hud.setting(player,type));
                Setting.ModuleTrackingTarget nextTarget = Enums.next(currentTarget,Setting.ModuleTrackingTarget.class);
                button.append(settings.lang(type+"."+currentTarget).btn(true).color(CUtl.s()).hEvent(CTxT.of("")
                                .append(settings.lang(type+".ui").color('e')).append("\n")
                                .append(settings.lang("hover.info",settings.lang(type.toString())).color('7')).append("\n\n")
                                .append(settings.lang("hover.set",settings.lang(type.toString()),settings.lang(type+"."+nextTarget).color(CUtl.s()))))
                        .cEvent(1,"/hud settings set-m "+type+" "+nextTarget));
                type = Setting.module__tracking_type;
                Setting.ModuleTrackingType currentType = Setting.ModuleTrackingType.get((String) PlayerData.get.hud.setting(player, type));
                Setting.ModuleTrackingType nextType = Enums.next(currentType,Setting.ModuleTrackingType.class);
                button.append(CTxT.of(currentType.equals(Setting.ModuleTrackingType.simple)?arrows.up:arrows.north).btn(true).color(CUtl.s()).hEvent(CTxT.of("")
                                .append(settings.lang(type+".ui").color('e')).append(" - ")
                                .append(moduleInfo(player,Module.tracking,true).color(CUtl.s())).append("\n")
                                .append(settings.lang(type+"."+currentType+".info").color('7')).append("\n\n")
                                .append(settings.lang("hover.set",settings.lang(type.toString()),settings.lang(type+"."+nextType).color(CUtl.s()))))
                        .cEvent(1,"/hud settings set-m "+type+" "+nextType));
            }
            if (module.equals(Module.speed)) {
                Setting type = Setting.module__speed_3d;
                boolean state = (boolean) PlayerData.get.hud.setting(player,type);
                button.append(settings.lang(type+"."+(state?"on":"off")).btn(true).color(CUtl.s()).hEvent(CTxT.of("")
                                        .append(settings.lang(type+".ui").color(CUtl.s())).append("\n")
                                        .append(settings.lang(type+"."+(state?"on":"off")+".info").color('7')).append("\n\n")
                                        .append(settings.lang("hover.set",settings.lang(type.toString()),settings.lang(type+"."+(state?"off":"on")).color(CUtl.s()))))
                        .cEvent(1,"/hud settings set-m "+type+" "+(state?"off":"on")));
                type = Setting.module__speed_pattern;
                button.append(CTxT.of((String) PlayerData.get.hud.setting(player, type)).btn(true).color(CUtl.s()).hEvent(CTxT.of("")
                                .append(settings.lang(type+".ui").color(CUtl.s())).append(" - ")
                                .append(moduleInfo(player,Module.speed,true).color(CUtl.s())).append("\n")
                                .append(settings.lang(type+".info").color('7')).append("\n")
                                .append(settings.lang(type+".info.2").color('7').italic(true)).append("\n\n")
                                .append(settings.lang("hover.set.custom",settings.lang(type.toString()))))
                        .cEvent(2,"/hud settings set-m "+type+" "));
            }
            if (module.equals(Module.angle)) {
                Setting type = Setting.module__angle_display;
                ModuleAngleDisplay currentType = ModuleAngleDisplay.get((String) PlayerData.get.hud.setting(player,type));
                ModuleAngleDisplay nextType = Enums.next(currentType, ModuleAngleDisplay.class);
                String buttonIcon = arrows.leftRight;
                if (currentType.equals(ModuleAngleDisplay.both)) buttonIcon += arrows.upDown;
                else if (currentType.equals(ModuleAngleDisplay.pitch)) buttonIcon = arrows.upDown;
                button.append(CTxT.of(buttonIcon).btn(true).color(CUtl.s()).hEvent(CTxT.of("")
                                .append(settings.lang(type+".ui").color('e')).append(" - ")
                                .append(settings.lang(type+"."+currentType)).append("\n")
                                .append(settings.lang(type+"."+currentType+".info").color('7')).append("\n\n")
                                .append(settings.lang("hover.set",settings.lang(type.toString()),settings.lang(type+"."+nextType).color(CUtl.s()))))
                        .cEvent(1,"/hud settings set-m "+type+" "+nextType));
            }
            return button;
        }
        /**
         * gets the sample of the given module as a CTxT
         * @param onlyExample to return only the example
         * @return returns the sample of the HUD module
         */
        public static CTxT moduleInfo(Player player, Module module, boolean... onlyExample) {
            // get the hover info for each module
            CTxT info = CTxT.of("");
            if (module.equals(Module.coordinates))
                info.append(color.addColor(player,"XYZ: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,95,20));
            if (module.equals(Module.distance))
                info.append(color.addColor(player,"[",1,15,20))
                        .append(color.addColor(player,"0",2,35,20))
                        .append(color.addColor(player,"]",1,55,20));
            if (module.equals(Module.destination))
                info.append(color.addColor(player,"[",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,35,20))
                        .append(color.addColor(player,"]",1,95,20));
            if (module.equals(Module.direction))
                info.append(color.addColor(player,"N",1,15,20));
            if (module.equals(Module.tracking)) {
                if (Setting.ModuleTrackingType.get((String) PlayerData.get.hud.setting(player,Setting.module__tracking_type))
                        .equals(Setting.ModuleTrackingType.simple))
                    info.append(color.addColor(player,"[",1,15,20).strikethrough(true))
                            .append(color.addColor(player,"-"+ arrows.up+ arrows.right,2,35,20))
                            .append(color.addColor(player,"]",1,55,20).strikethrough(true));
                else info.append(color.addColor(player,"[",1,15,20).strikethrough(true))
                        .append(color.addColor(player,arrows.north,2,35,20))
                        .append(color.addColor(player,"|",1,55,20).strikethrough(true))
                        .append(color.addColor(player,arrows.south,2,75,20))
                        .append(color.addColor(player,"]",1,95,20).strikethrough(true));
            }
            if (module.equals(Module.time)) {
                if ((boolean) PlayerData.get.hud.setting(player, Setting.module__time_24hr))
                    info.append(color.addColor(player,"22:22",1,15,20));
                else info.append(color.addColor(player,"11:11 ",2,15,20))
                            .append(color.addColor(player,"AM",1,115,20));
            }
            if (module.equals(Module.weather))
                info.append(color.addColor(player,Assets.symbols.sun,1,15,20));
            DecimalFormat f = new DecimalFormat((String) PlayerData.get.hud.setting(player,Setting.module__speed_pattern));
            String speed = f.format(12.3456789);
            if (module.equals(Module.speed))
                info.append(color.addColor(player,speed,2,15,20))
                        .append(color.addColor(player," B/S",1,(speed.length()*20)+15,20));
            if (module.equals(Module.angle)) {
                info.append(color.addColor(player,"-15.1",2,15,20));
                if (ModuleAngleDisplay.get((String) PlayerData.get.hud.setting(player, Setting.module__angle_display)).equals(ModuleAngleDisplay.both))
                    info.append(color.addColor(player,"/",1,135,20)).append(color.addColor(player,"55.1",2,155,20));
            }
            if (onlyExample.length == 0) info.append("\n").append(lang("info."+module).color('7'));
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
            if (!PlayerData.get.hud.module(player, module)) return Assets.mainColors.gray;
            boolean yellow = false;
            if (!Destination.get(player).hasXYZ()) {
                if (module.equals(Module.destination) || module.equals(Module.distance) || (module.equals(Module.tracking) &&
                        Setting.ModuleTrackingTarget.get((String) PlayerData.get.hud.setting(player, Setting.module__tracking_target)).equals(Setting.ModuleTrackingTarget.dest)))
                    yellow = true;
            }
            if (module.equals(Module.tracking) && Destination.social.track.getTarget(player)==null && Setting.ModuleTrackingTarget.get((String) PlayerData.get.hud.setting(player, Setting.module__tracking_target)).equals(Setting.ModuleTrackingTarget.player))
                yellow = true;
            if (yellow) return "#fff419";
            return "#19ff21";
        }
        /**
         * the HUD Modules chat UI
         * @param abovemsg a messages that displays above the UI
         * @param pg the module page to display
         */
        public static void UI(Player player, CTxT abovemsg, int pg) {
            Helper.ListPage<Module> listPage = new Helper.ListPage<>(PlayerData.get.hud.order(player),PER_PAGE);
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui").color(Assets.mainColors.edit)).append(CTxT.of("\n                                     ").strikethrough(true));
            //MAKE THE TEXT
            for (Module module: listPage.getPage(pg)) {
                boolean state = PlayerData.get.hud.module(player,module);
                msg.append("\n ")
                        //ORDER
                        .append(CTxT.of(String.valueOf(listPage.getIndexOf(module)+1)).btn(true).color(CUtl.p())
                                .cEvent(2,"/hud modules order-r "+module+" ")
                                .hEvent(CUtl.TBtn("order.hover").color(CUtl.p())))
                        //TOGGLE
                        .append(CTxT.of(Assets.symbols.toggle).btn(true).color(CUtl.p())
                                .cEvent(1,"/hud modules toggle-r "+module)
                                .hEvent(CUtl.TBtn("hud.toggle.hover_mod",
                                        CTxT.of(module.toString()).color(CUtl.s()),
                                        CUtl.TBtn(!state?"on":"off").color(!state?'a':'c')))).append(" ")
                        //NAME
                        .append(CTxT.of(module.toString()).color(stateColor(player,module))
                                .hEvent(moduleInfo(player,module))).append(" ");
                //EXTRA BUTTONS
                msg.append(getButtons(player,module));
            }
            //BOTTOM ROW
            msg.append("\n\n ").append(CUtl.TBtn("reset").btn(true).color('c').cEvent(1,"/hud modules reset-r")
                            .hEvent(CUtl.TBtn("reset.hover_edit").color('c')))
                    .append(" ").append(listPage.getNavButtons(pg,"/hud modules ")).append(" ").append(CUtl.CButton.back("/hud"))
                    .append(CTxT.of("\n                                     ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static class color {
        public static void cmdExecutor(Player player, String[] args) {
            if (args.length == 0) {
                UI(player, null);
                return;
            }
            // COLOR EDITOR
            if (args.length == 3 && args[0].equals("edit")) {
                if (args[2].equals("primary") || args[2].equals("secondary")) changeUI(player, args[1], args[2], null);
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
                if (args[1].equals("edit")) changeUI(player, args.length==3?args[2]:"normal", args[0], null);
                if (args.length < 3) return;
                // color (type) set (color)
                if (args[1].equals("set")) setColor(player,null,args[0],args[2],false);
                // color (type) (bold/italics/rainbow) (on/off) (settings)
                else setToggle(player,args.length==4?args[3]:"normal",args[0],args[1],args[2],Return);
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
                if (args[1].equals("set")) return Suggester.colors(player,Suggester.getCurrent(args,pos));
                else {
                    // (type) (subType) (on/off)
                    suggester.add("on");
                    suggester.add("off");
                }
            }
            return suggester;
        }
        public static CTxT lang(String key, Object... args) {
            return HUD.lang("color."+key, args);
        }
        public static CTxT btn(String key, Object... args) {
            return lang("button."+key, args);
        }
        public static void reset(Player player, String setting, String type, boolean Return) {
            switch (type) {
                case "all" -> {
                    PlayerData.set.hud.color(player, 1, defaultFormat(1));
                    PlayerData.set.hud.color(player, 2, defaultFormat(2));
                }
                case "primary" -> PlayerData.set.hud.color(player, 1, defaultFormat(1));
                case "secondary" -> PlayerData.set.hud.color(player, 2, defaultFormat(2));
                default -> {
                    player.sendMessage(CUtl.error("args"));
                    return;
                }
            }
            CTxT msg = CUtl.tag().append(lang("msg.reset",CUtl.TBtn("reset").color('c'),lang(type)));
            if (Return && type.equals("all")) UI(player,msg);
            else if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void setColor(Player player, String setting, String type, String color, boolean Return) {
            int typ = type.equals("primary")?1:2;
            // get the current color settings into a String[]
            String[] current = PlayerData.get.hud.color(player,typ).split("-");
            // set the color
            current[0] = CUtl.color.colorHandler(player,color,config.hud.primary.Color);
            // save the current color settings
            PlayerData.set.hud.color(player,typ,String.join("-",current));
            if (Return) changeUI(player,setting,type,null);
            else player.sendMessage(CUtl.tag().append(lang("msg.set",lang(type),CUtl.color.getBadge(current[0]))));
        }
        public static void setToggle(Player player, String setting, String colorType, String type, String toggle, boolean Return) {
            int typ = colorType.equals("primary")?1:2;
            boolean state = toggle.equals("on");
            // get the current color settings into a String[]
            String[] current = PlayerData.get.hud.color(player,typ).split("-");
            // edit the correct toggle
            switch (type) {
                case "bold" -> current[1] = String.valueOf(state);
                case "italics" -> current[2] = String.valueOf(state);
                case "rainbow" -> current[3] = String.valueOf(state);
                default -> {
                    player.sendMessage(CUtl.error("args"));
                    return;
                }
            }
            // save the new color settings
            PlayerData.set.hud.color(player,typ,String.join("-",current));
            // generate the message
            CTxT msg = CUtl.tag().append(lang("msg.toggle",CUtl.toggleTxT(state),lang(colorType),lang(type)));
            if (Return) changeUI(player,setting,colorType,msg);
            else player.sendMessage(msg);
        }
        public static String defaultFormat(int i) {
            if (i==1) return config.hud.primary.Color +"-"+ config.hud.primary.Bold +"-"+ config.hud.primary.Italics +"-"+ config.hud.primary.Rainbow;
            return config.hud.secondary.Color +"-"+ config.hud.secondary.Bold +"-"+ config.hud.secondary.Italics +"-"+ config.hud.secondary.Rainbow;
        }
        public static String getHUDColor(Player player, int i) {
            return PlayerData.get.hud.color(player,i).split("-")[0];
        }
        public static Boolean getHUDBold(Player player, int i) {
            return Boolean.parseBoolean(PlayerData.get.hud.color(player,i).split("-")[1]);
        }
        public static Boolean getHUDItalics(Player player, int i) {
            return Boolean.parseBoolean(PlayerData.get.hud.color(player,i).split("-")[2]);
        }
        public static Boolean getHUDRainbow(Player player, int i) {
            return Boolean.parseBoolean(PlayerData.get.hud.color(player,i).split("-")[3]);
        }
        /**
         * formats the given text with the hud colors selected
         * @param txt text to be formatted
         * @param typ primary/secondary
         * @param start rainbow start
         * @param step rainbow step
         */
        public static CTxT addColor(Player player, String txt, int typ, float start, float step) {
            if (getHUDRainbow(player,typ)) return CTxT.of(txt).rainbow(true,start,step).italic(getHUDItalics(player,typ)).bold(getHUDBold(player,typ));
            return CTxT.of(txt).color(getHUDColor(player,typ)).italic(getHUDItalics(player,typ)).bold(getHUDBold(player,typ));
        }
        public static CTxT addColor(Player player, CTxT txt, int typ, float start, float step) {
            return addColor(player,txt.toString(),typ,start,step);
        }
        public static void changeUI(Player player, String setting, String type, CTxT abovemsg) {
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                               ").strikethrough(true);
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            int typ = type.equals("primary")?1:2;
            // if not primary or secondary
            if (typ == 2 && !type.equals("secondary")) {
                player.sendMessage(CUtl.error("args"));
                return;
            }
            // message header
            msg.append(" ").append(addColor(player,btn(type),typ,15,20)).append(line).append("\n");
            // make the buttons
            CTxT reset = CUtl.TBtn("reset").btn(true).color('c').cEvent(1, "/hud color reset-r "+type+" "+setting)
                    .hEvent(lang("hover.reset",addColor(player,lang(type),typ,15,20)));
            // bold
            CTxT boldButton = btn("bold").btn(true).color(CUtl.toggleColor(getHUDBold(player, typ)))
                    .cEvent(1,String.format("/hud color %s-r bold %s %s",type,(getHUDBold(player,typ)?"off":"on"),setting))
                    .hEvent(lang("hover.toggle",CUtl.toggleTxT(!getHUDBold(player,typ)),lang("bold").bold(true)));
            // italics
            CTxT italicsButton = btn("italics").btn(true).color(CUtl.toggleColor(getHUDItalics(player, typ)))
                    .cEvent(1,String.format("/hud color %s-r italics %s %s",type,(getHUDItalics(player,typ)?"off":"on"),setting))
                    .hEvent(lang("hover.toggle",CUtl.toggleTxT(!getHUDItalics(player,typ)),lang("italics").italic(true)));
            // rainbow
            CTxT rgbButton = btn("rgb").btn(true).color(CUtl.toggleColor(getHUDRainbow(player, typ)))
                    .cEvent(1,String.format("/hud color %s-r rainbow %s %s",type,(getHUDRainbow(player,typ)?"off":"on"),setting))
                    .hEvent(lang("hover.toggle",CUtl.toggleTxT(!getHUDRainbow(player,typ)),lang("rainbow").rainbow(true,15f,20f)));
            // build the message
            msg.append(DHUD.preset.colorEditor(getHUDColor(player,typ),setting,DHUD.preset.Type.hud,type,"/hud color edit %s "+type))
                    .append("\n\n ").append(boldButton).append(" ").append(italicsButton).append(" ").append(rgbButton)
                    .append("\n\n     ").append(reset).append(" ").append(CUtl.CButton.back("/hud color")).append(line);
            player.sendMessage(msg);
        }
        public static void UI(Player player, CTxT aboveTxT) {
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                                ").strikethrough(true);
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            msg.append(" ").append(lang("ui").rainbow(true,15f,45f)).append(line).append("\n ")
                    //PRIMARY
                    .append(addColor(player,btn("primary"),1,15,20).btn(true).cEvent(1,"/hud color primary edit")
                            .hEvent(lang("hover.edit",addColor(player,lang("primary"),1,15,20)))).append(" ")
                    //SECONDARY
                    .append(addColor(player,btn("secondary"),2,15,20).btn(true).cEvent(1,"/hud color secondary edit")
                            .hEvent(lang("hover.edit",addColor(player,lang("secondary"),2,15,20)))).append("\n\n      ")
                    //RESET
                    .append(CUtl.TBtn("reset").btn(true).color('c').cEvent(1,"/hud color reset-r all")
                            .hEvent(lang("hover.reset",lang("all").color('c')))).append("  ")
                    .append(CUtl.CButton.back("/hud")).append(line);
            player.sendMessage(msg);
        }
    }
    public static class settings {
        public static CTxT lang(String key, Object... args) {
            return HUD.lang("setting."+key, args);
        }
        public static Object getConfig(Setting type) {
            Object output = false;
            switch (type) {
                case state -> output = config.hud.State;
                case type -> output = config.hud.DisplayType;
                case bossbar__color -> output = config.hud.BarColor;
                case bossbar__distance -> output = config.hud.BarShowDistance;
                case bossbar__distance_max -> output = config.hud.ShowDistanceMAX;
                case module__time_24hr -> output = config.hud.Time24HR;
                case module__tracking_hybrid -> output = config.hud.TrackingHybrid;
                case module__tracking_target -> output = config.hud.TrackingTarget;
                case module__tracking_type -> output = config.hud.TrackingType;
                case module__speed_3d -> output = config.hud.Speed3D;
                case module__speed_pattern -> output = config.hud.SpeedPattern;
                case module__angle_display -> output = config.hud.AngleDisplay;
            }
            return output;
        }
        public static void reset(Player player, Setting type, boolean Return) {
            // non resettable settings
            if (type.equals(Setting.bossbar__distance_max)) return;
            // reset all
            if (type.equals(Setting.none)) {
                // reset all main settings, excluding module settings
                for (Setting s : Setting.baseSettings()) PlayerData.set.hud.setting(player, s, getConfig(s));
            } else {
                // reset the selected setting
                PlayerData.set.hud.setting(player,type,getConfig(type));
            }
            // if bossbar distance, reset max alongside it
            if (type.equals(Setting.bossbar__distance))
                PlayerData.set.hud.setting(player, Setting.get(type+"_max"),getConfig(Setting.get(type+"_max")));
            // update the HUD
            player.updateHUD();
            // make the reset message
            CTxT msg = CUtl.tag().append(lang("msg.reset",lang("category",
                    lang("category."+(type.toString().startsWith("bossbar")?"bossbar":"hud")),
                    lang(type.toString()).color(CUtl.s()))));
            if (type.equals(Setting.none)) msg = CUtl.tag().append(lang("msg.reset_all",CUtl.TBtn("all").color('c')));

            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        public static CTxT change(Player player, Setting type, String setting, boolean Return) {
            boolean state = setting.equals("on");
            CTxT setTxT = CTxT.of("");
            // ON/OFF simple on off toggle
            if (type.equals(Setting.bossbar__distance) || type.equals(Setting.state) || type.equals(Setting.module__tracking_hybrid)) {
                PlayerData.set.hud.setting(player,type,state);
                // if changing the state update the hud
                if (type.equals(Setting.state)) player.updateHUD();
                setTxT.append(CUtl.toggleTxT(state));
            }
            // ON/OFF with custom name for the states
            if (type.equals(Setting.module__time_24hr) || type.equals(Setting.module__speed_3d)) {
                PlayerData.set.hud.setting(player,type,state);
                setTxT.append(lang(type+"."+(state?"on":"off")).color(CUtl.s()));
            }
            // ---- CUSTOM HANDLING ----
            if (type.equals(Setting.type)) {
                Setting.DisplayType displayType = Setting.DisplayType.get(setting);
                PlayerData.set.hud.setting(player,type,displayType);
                setTxT.append(lang(type+"."+displayType).color(CUtl.s()));
                player.updateHUD();
            }
            if (type.equals(Setting.bossbar__color)) {
                Setting.BarColor barColor = Setting.BarColor.get(setting);
                PlayerData.set.hud.setting(player,type,barColor);
                setTxT.append(lang(type+"."+barColor).color(Assets.barColor(barColor)));
            }
            if (type.equals(Setting.bossbar__distance_max)) {
                // make sure the number is greater than 0
                int i = Math.max(Num.toInt(setting),0);
                PlayerData.set.hud.setting(player,type,i);
                setTxT.append(CTxT.of(String.valueOf(i)).color((boolean)PlayerData.get.hud.setting(player, Setting.bossbar__distance)?'a':'c'));
            }
            if (type.equals(Setting.module__tracking_target)) {
                Setting.ModuleTrackingTarget moduleTrackingTarget = Setting.ModuleTrackingTarget.get(setting);
                PlayerData.set.hud.setting(player, type, moduleTrackingTarget);
                setTxT.append(lang(type+"."+moduleTrackingTarget).color(CUtl.s()));
            }
            if (type.equals(Setting.module__tracking_type)) {
                Setting.ModuleTrackingType moduleTrackingType = Setting.ModuleTrackingType.get(setting);
                PlayerData.set.hud.setting(player, type, moduleTrackingType);
                setTxT.append(lang(type+"."+moduleTrackingType).color(CUtl.s()));
            }
            if (type.equals(Setting.module__speed_pattern)) {
                // try to make the decimal format, if error don't do anything
                try {
                    new DecimalFormat(setting);
                    PlayerData.set.hud.setting(player,type,setting);
                } catch (IllegalArgumentException ignored) {}
                setTxT.append(CTxT.of(String.valueOf(PlayerData.get.hud.setting(player,type))).color(CUtl.s()));
            }
            if (type.equals(Setting.module__angle_display)) {
                ModuleAngleDisplay moduleAngleDisplay = Setting.ModuleAngleDisplay.get(setting);
                PlayerData.set.hud.setting(player, type, moduleAngleDisplay);
                setTxT.append(lang(type+"."+moduleAngleDisplay).color(CUtl.s()));
            }
            // make the message
            CTxT msg = CUtl.tag(), typeTxT = lang(type.toString()).color(CUtl.s());
            String extra = "";
            // if apart of boolSettings, make it a toggle message
            if (Setting.boolSettings().contains(type)) extra = ".toggle";
            // if custom boolean, use the normal set message
            if (Setting.customBool().contains(type)) extra = "";
            if (type.toString().startsWith("bossbar.")) { // if bossbar, bossbar category
                typeTxT = lang("category",lang("category.bossbar"),typeTxT);
            } else if (!type.toString().startsWith("module.")) { // else not module, HUD category
                typeTxT = lang("category",lang("category.hud"),typeTxT);
            }

            msg.append(lang("msg.set"+extra,typeTxT,setTxT));
            if (Return) UI(player, msg);
            return msg;
        }
        /**
         * checks if a setting can be reset by comparing the current state to the config state
         */
        public static boolean canBeReset(Player player, Setting type) {
            boolean output = false;
            // bossbar max isn't a base, so skip
            if (type.equals(Setting.none) || type.equals(Setting.bossbar__distance_max)) return false;
            // flip if the default and current settings doesn't match
            if (!PlayerData.get.hud.setting(player,type).equals(getConfig(type))) output = true;
            // if bossbar.distance, check the child setting for the same thing
            if (type.equals(Setting.bossbar__distance))
                if (((Double) PlayerData.get.hud.setting(player, Setting.bossbar__distance_max)).intValue() != (int)getConfig(Setting.bossbar__distance_max)) output = true;
            return output;
        }
        public static CTxT resetBtn(Player player, Setting type) {
            CTxT msg = CTxT.of(Assets.symbols.x).btn(true).color('7');
            if (canBeReset(player,type)) {
                msg.color('c').cEvent(1, "/hud settings reset-r " + type)
                        .hEvent(lang("hover.reset",lang("category",
                                lang("category."+(type.toString().startsWith("bossbar")?"bossbar":"hud")),
                                lang(type.toString())).color('c')));
            }
            return msg;
        }
        public static CTxT getButtons(Player player, Setting type) {
            // if there's something in module the command 'end's in module, to return to the module command instead of the settings command
            CTxT button = CTxT.of("");
            if (type.equals(Setting.state)) {
                button.append(CUtl.toggleBtn((boolean) PlayerData.get.hud.setting(player,type),"/hud settings set-r "+type+" ")).append(" ");
            }
            if (type.equals(Setting.type)) {
                Setting.DisplayType nextType = Setting.DisplayType.valueOf((String) PlayerData.get.hud.setting(player,type)).next();
                button.append(lang(type+"."+ PlayerData.get.hud.setting(player,type)).btn(true).color(CUtl.s())
                        .cEvent(1,"/hud settings set-r "+type+" "+nextType)
                        .hEvent(lang("hover.set",lang("category",
                                        lang("category.hud"),lang(type.toString())),
                                lang(type+"."+nextType).color(CUtl.s()))));
            }
            if (type.equals(Setting.bossbar__color)) {
                button.append(lang(type+"."+PlayerData.get.hud.setting(player,type)).btn(true)
                        .color(Assets.barColor((Setting.BarColor.valueOf((String) PlayerData.get.hud.setting(player,type)))))
                        .cEvent(2,"/hud settings set-r "+type+" ")
                        .hEvent(lang("hover.set.custom",lang("category",
                                        lang("category.bossbar"),lang(type.toString())))));
            }
            if (type.equals(Setting.bossbar__distance)) {
                boolean state = (boolean) PlayerData.get.hud.setting(player,type);
                button.append(CUtl.toggleBtn(state,"/hud settings set-r "+type+" ")).append(" ");
                button.append(CTxT.of(String.valueOf(((Double) PlayerData.get.hud.setting(player, Setting.bossbar__distance_max)).intValue())).btn(true).color((boolean) PlayerData.get.hud.setting(player,type)?'a':'c')
                        .cEvent(2,"/hud settings set-r "+ Setting.bossbar__distance_max+" ")
                        .hEvent(lang("hover.set.custom",lang("category",
                                lang("category.bossbar"),lang(Setting.bossbar__distance_max.toString())))
                                .append("\n").append(lang(type+"_max.hover").italic(true).color('7'))));
            }
            return button;
        }
        public static void UI(Player player, CTxT aboveMSG) {
            CTxT msg = CTxT.of("");
            if (aboveMSG != null) msg.append(aboveMSG).append("\n");
            msg.append(" ").append(lang("ui").color(Assets.mainColors.setting)).append(CTxT.of("\n                              \n").strikethrough(true));
            //HUD
            msg.append(" ").append(lang("category.hud").color(CUtl.p())).append(":\n  ");
            msg     //STATE
                    .append(resetBtn(player, Setting.state)).append(" ")
                    .append(lang(Setting.state+".ui").hEvent(CTxT.of(lang(Setting.state+".ui"))
                                    .append("\n").append(lang("hover.info.toggle",lang("category.hud"),lang(Setting.state.toString())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.state))
                    .append("\n  ");
            msg     //TYPE
                    .append(resetBtn(player, Setting.type)).append(" ")
                    .append(lang(Setting.type+".ui").hEvent(CTxT.of(lang(Setting.type+".ui"))
                                    .append("\n").append(lang("hover.info",lang("category.hud"),lang(Setting.type.toString())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.type))
                    .append("\n");
            //BOSSBAR
            msg.append(" ").append(lang("category.bossbar").color(CUtl.p())).append(":\n  ");
            msg     //COLOR
                    .append(resetBtn(player, Setting.bossbar__color)).append(" ")
                    .append(lang(Setting.bossbar__color+".ui").hEvent(CTxT.of(lang(Setting.bossbar__color+".ui"))
                            .append("\n").append(lang("hover.info",lang("category.bossbar"),lang(Setting.bossbar__color.toString())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.bossbar__color))
                    .append("\n  ");
            msg     //DISTANCE
                    .append(resetBtn(player, Setting.bossbar__distance)).append(" ")
                    .append(lang(Setting.bossbar__distance+".ui").hEvent(CTxT.of(lang(Setting.bossbar__distance+".ui"))
                            .append("\n").append(lang(Setting.bossbar__distance+".info").color('7'))
                            .append("\n").append(lang(Setting.bossbar__distance_max+".ui"))
                            .append("\n").append(lang(Setting.bossbar__distance+".info.2").color('7')))).append(": ")
                    .append(getButtons(player, Setting.bossbar__distance))
                    .append("\n");
            CTxT reset = CUtl.TBtn("reset").btn(true).color('7');
            boolean resetOn = false;
            // see if a setting can be reset, then flip the switch
            for (Setting t: Setting.baseSettings()) {
                if (resetOn) break;
                resetOn = canBeReset(player,t);
            }
            if (resetOn) reset.color('c').cEvent(1,"/hud settings reset-r all")
                    .hEvent(lang("hover.reset",CUtl.TBtn("all").color('c')));
            msg.append("\n    ").append(reset).append("  ").append(CUtl.CButton.back("/hud")).append("\n")
                    .append(CTxT.of("                              ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static void UI(Player player, CTxT abovemsg) {
        CTxT msg = CTxT.of("");
        if (abovemsg != null) msg.append(abovemsg).append("\n");
        msg.append(" ").append(lang("ui").color(CUtl.p())).append(CTxT.of("\n                            \n").strikethrough(true)).append(" ");
        //COLOR
        msg.append(CUtl.CButton.hud.color()).append(" ");
        //SETTINGS
        msg.append(CUtl.CButton.hud.settings()).append("\n\n ");
        //MODULES
        msg.append(CUtl.CButton.hud.modules()).append(" ");
        //BACK
        msg.append(CUtl.CButton.back("/dhud"));
        msg.append(CTxT.of("\n                            ").strikethrough(true));
        player.sendMessage(msg);
    }
}
