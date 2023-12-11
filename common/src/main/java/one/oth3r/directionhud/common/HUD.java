package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

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
        public static ArrayList<Setting> base() {
            ArrayList<Setting> list = new ArrayList<>(Arrays.asList(values()));
            list.remove(bossbar__distance_max);
            list.remove(none);
            return list;
        }
        public enum DisplayType {
            actionbar,
            bossbar;
            public static final DisplayType[] values = values();
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
        public enum HUDTrackingTarget {
            player,
            dest;
            public static final HUDTrackingTarget[] values = values();
            public HUDTrackingTarget next() {
                return values[(ordinal() + 1) % values.length];
            }
            public static HUDTrackingTarget get(String s) {
                try {
                    return HUDTrackingTarget.valueOf(s);

                } catch (IllegalArgumentException e) {
                    return HUDTrackingTarget.valueOf(config.hud.defaults.TrackingTarget);
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
        unknown;
        public static Module get(String s) {
            try {
                return Module.valueOf(s);
            } catch (IllegalArgumentException e) {
                return unknown;
            }
        }
        public static ArrayList<String> toStringList(ArrayList<Module> moduleList) {
            ArrayList<String> stringList = new ArrayList<>();
            for (Module module:moduleList) stringList.add(module.toString());
            return stringList;
        }
        public static ArrayList<Module> toModuleList(ArrayList<String> stringList) {
            ArrayList<HUD.Module> moduleList = new ArrayList<>();
            for (String module:stringList) moduleList.add(get(module));
            return moduleList;
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
                case "modules" -> modulesCMD(player, trimmedArgs);
                case "settings" -> settingsCMD(player,trimmedArgs);
                case "color" -> colorCMD(player, trimmedArgs);
                case "toggle" -> player.sendMessage(settings.change(player,Setting.state,(boolean)PlayerData.get.hud.setting.get(player,Setting.state)?"off":"on",false));
                default -> player.sendMessage(CUtl.error("command"));
            }
        }
        public static void settingsCMD(Player player, String[] args) {
            //UI
            if (args.length == 0) settings.UI(player, null);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("reset")) settings.reset(player, Setting.get(args[1]),true);
                else settings.change(player, Setting.get(args[0]),args[1],true);
            }
            if (args.length == 3) {
                if (args[2].equalsIgnoreCase("module")) {
                    modules.UI(player, settings.change(player, Setting.get(args[0]),args[1],false),1);
                } else settings.change(player, Setting.get(args[0]),args[1],false);
            }
        }
        public static void modulesCMD(Player player, String[] args) {
            //UI
            if (args.length == 0) modules.UI(player, null, 1);
            if (args.length < 1) return;
            boolean Return = false;
            // if the type has -r, remove it and enable returning
            if (args[0].contains("-r")) {
                args[0] = args[0].replace("-r","");
                Return = true;
            }
            if (Utl.isInt(args[0])) modules.UI(player,null,Integer.parseInt(args[0]));
            //RESET
            if (args[0].equals("reset")) modules.reset(player, Return);
            if (args[0].equals("toggle")) {
                if (args.length < 2) player.sendMessage(CUtl.error("hud.module"));
                else modules.toggle(player, Module.valueOf(args[1]),null,Return);
            }
            if (args[0].equals("order")) {
                if (args.length < 2) player.sendMessage(CUtl.error("hud.module"));
                else if (args.length == 3 && Utl.isInt(args[2]))
                    modules.move(player, Module.get(args[1]), Integer.parseInt(args[2]), Return);
                else player.sendMessage(CUtl.error("number"));
            }
        }
        public static void colorCMD(Player player, String[] args) {
            if (args.length == 0) {
                color.UI(player, null);
                return;
            }
            //COLOR
            if (args.length == 3 && args[0].equals("edit")) {
                if (args[2].equals("primary")) color.changeUI(player, args[1], args[2], null);
                if (args[2].equals("secondary")) color.changeUI(player, args[1], args[2], null);
            }
            //RESET
            if (args[0].equals("reset")) {
                if (args.length == 1) color.reset(player,null,null,true);
                if (args.length == 3) color.reset(player,args[1],args[2],true);
            }
            if (args[0].equals("preset") && args.length >= 3) {
                // /hud color preset add type
                if (args[1].equals("add") && args.length == 4) {
                    CUtl.color.customAddUI(player,color.getHUDColor(player,args[3].equals("primary")?1:2),"/hud color edit "+args[2]+" "+args[3]);
                } else color.presets(player,args[1],args[2]);
            }
            //CHANGE COLOR
            if (args.length < 4) return;
            String type = args[0].toLowerCase();
            switch (type) {
                case "set" -> color.setColor(player, args[1], args[2], args[3], true);
                case "bold" -> color.setBold(player, args[1], args[2], Boolean.parseBoolean(args[3]), true);
                case "italics" -> color.setItalics(player, args[1], args[2], Boolean.parseBoolean(args[3]), true);
                case "rgb" -> color.setRGB(player, args[1], args[2], Boolean.parseBoolean(args[3]), true);
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
                    case "modules" -> suggester.addAll(moduleCMD(player,fixedPos,trimmedArgs));
                }
            }
            if (pos == 5 && args[1].equals("set")) {
                suggester.add("ffffff");
            }
            if (pos == 3 && args[0].equals("settings")) {
                if (args[1].equals(Setting.bossbar__color.toString())) {
                    for (Setting.BarColor color : Setting.BarColor.values())
                        suggester.add(color.toString());
                }
                if (args[1].equals(Setting.bossbar__distance_max.toString()))
                    suggester.add("0");
            }
            return suggester;
        }
        public static ArrayList<String> moduleCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // modules
            if (pos == 0) {
                suggester.add("order");
                suggester.add("toggle");
                suggester.add("reset");
                return suggester;
            }
            // if -r is attached, remove it and continue with the suggester
            if (args[0].contains("-r")) args[0] = args[0].replace("-r","");
            if (pos == 1) {
                if (args[0].equalsIgnoreCase("order") || args[0].equalsIgnoreCase("toggle")) {
                    suggester.addAll(Module.toStringList(modules.getDefault()));
                }
            }
            if (pos == 2 && args[0].equalsIgnoreCase("order"))
                suggester.add(String.valueOf(PlayerData.get.hud.order(player).indexOf(Module.get(args[1]))+1));
            return suggester;
        }
    }
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("hud."+key, args);
    }
    public static HashMap<Module, ArrayList<String>> getRawHUDText(Player player) {
        //return a HashMap that for each HUD module, has a string with style instructions and the actual HUD
        ArrayList<String> coordinates = new ArrayList<>();
        coordinates.add("pXYZ: ");
        coordinates.add("s"+player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
        ArrayList<String> destination = new ArrayList<>();
        ArrayList<String> distance = new ArrayList<>();
        ArrayList<String> tracking = new ArrayList<>();
        if (Destination.get(player).hasXYZ()) {
            destination.add("pDEST: ");
            destination.add("s"+ Destination.get(player).getXYZ());
            distance.add("p[");
            distance.add("s"+ Destination.getDist(player));
            distance.add("p]");
        }
        //TRACKING
        if (PlayerData.get.hud.getModule(player, Module.tracking)) {
            tracking.add("/p[");
            tracking.add("s"+getTracking(player));
            tracking.add("/p]");
        }
        ArrayList<String> direction = new ArrayList<>();
        direction.add("p"+getPlayerDirection(player));
        ArrayList<String> time = new ArrayList<>();
        if ((boolean) PlayerData.get.hud.setting.get(player, Setting.module__time_24hr)) {
            time.add("s"+getGameTime(false));
        } else {
            // 12 hr clock
            time.add("s"+getGameTime(true)+" ");
            time.add("p"+(hour>=12?lang("module.time.pm"):lang("module.time.am")));
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
                if (PlayerData.get.hud.setting.get(player, Setting.module__tracking_target).equals(Setting.HUDTrackingTarget.dest.toString())) {
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
                if (color.getHUDRGB(player,typ)) {
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
        if (0 <= rotation && rotation < 22.5) {
            return "N";
        } else if (22.5 <= rotation && rotation < 67.5) {
            return "NE";
        } else if (67.5 <= rotation && rotation < 112.5) {
            return "E";
        } else if (112.5 <= rotation && rotation < 157.5) {
            return "SE";
        } else if (157.5 <= rotation && rotation < 202.5) {
            return "S";
        } else if (202.5 <= rotation && rotation < 247.5) {
            return "SW";
        } else if (247.5 <= rotation && rotation < 292.5) {
            return "W";
        } else if (292.5 <= rotation && rotation < 337.5) {
            return "NW";
        } else if (337.5 <= rotation && rotation < 360.0) {
            return "N";
        } else {
            return "?";
        }
    }
    public static String getGameTime(boolean t12hr) {
        int hr = hour;
        // add 0 to the start, then set the string to the last to numbers to always have a 2-digit number
        String min = "0" + minute;
        min = min.substring(min.length()-2);
        if (t12hr) hr = (hr % 12 == 0)? 12:hr % 12;
        return hr+":"+min;
    }
    public static String getTracking(Player player) {
        // pointer target
        Loc pointLoc;
        // player tracking mode
        if (PlayerData.get.hud.setting.get(player, Setting.module__tracking_target).equals(Setting.HUDTrackingTarget.player.name())) {
            // no target
            if (PlayerData.get.dest.getTracking(player) == null) return "???";
            Player target = Destination.social.track.getTarget(player);
            if (target == null) return "???";
            Loc plLoc = new Loc(target);
            // not in the same dimension
            if (!player.getDimension().equals(target.getDimension())) {
                // can convert and autoconvert is on
                if (Utl.dim.canConvert(player.getDimension(),target.getDimension()) && (boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.autoconvert)) {
                    plLoc.convertTo(player.getDimension());
                } else return "-?-";
            }
            pointLoc = plLoc;
        } else {
            // dest mode
            if (!Destination.get(player).hasXYZ()) return "???";
            pointLoc = Destination.get(player);
        }
        // pointer logic
        int x = pointLoc.getX()-player.getBlockX();
        int z = (pointLoc.getZ()-player.getBlockZ())*-1;
        double target = Math.toDegrees(Math.atan2(x, z));
        double rotation = (player.getYaw() - 180) % 360;
        // make sure 0 - 360
        if (rotation < 0) rotation += 360;
        if (target < 0) target += 360;
        if (Helper.inBetween(rotation, Helper.wSubtract(target,15,360), Helper.wAdd(target,15,360))) return "-"+Assets.symbols.up+"-";
        if (Helper.inBetween(rotation, target, Helper.wAdd(target,65,360))) return Assets.symbols.left+Assets.symbols.up+"-";
        if (Helper.inBetween(rotation, target, Helper.wAdd(target,115,360))) return Assets.symbols.left+"--";
        if (Helper.inBetween(rotation, target, Helper.wAdd(target,165,360))) return Assets.symbols.left+Assets.symbols.down+"-";
        if (Helper.inBetween(rotation, Helper.wSubtract(target, 65, 360), target)) return "-"+Assets.symbols.up+Assets.symbols.right;
        if (Helper.inBetween(rotation, Helper.wSubtract(target, 115, 360), target)) return "--"+Assets.symbols.right;
        if (Helper.inBetween(rotation, Helper.wSubtract(target, 165, 360), target)) return "-"+Assets.symbols.down+Assets.symbols.right;
        return "-"+Assets.symbols.down+"-";
    }
    public static class modules {
        private static final int PER_PAGE = 5;
        public static ArrayList<Module> getDefault() {
            ArrayList<Module> list = new ArrayList<>();
            list.add(Module.coordinates);
            list.add(Module.distance);
            list.add(Module.tracking);
            list.add(Module.destination);
            list.add(Module.direction);
            list.add(Module.time);
            list.add(Module.weather);
            return list;
        }
        public static void reset(Player player, boolean Return) {
            PlayerData.set.hud.order(player, config.hud.Order);
            PlayerData.set.hud.setting.set(player, Setting.module__time_24hr, settings.getConfig(Setting.module__time_24hr));
            PlayerData.set.hud.setting.set(player, Setting.module__tracking_target, settings.getConfig(Setting.module__tracking_target));
            PlayerData.set.hud.setModuleMap(player, PlayerData.defaults.hudModule());
            CTxT msg = CUtl.tag().append(lang("module.reset",CUtl.TBtn("reset").color('c')));
            if (Return) UI(player, msg, 1);
            else player.sendMessage(msg);
        }
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
            CUtl.PageHelper<Module> pageHelper = new CUtl.PageHelper<>(PlayerData.get.hud.order(player),PER_PAGE);
            CTxT msg = CUtl.tag().append(lang("module.order",CTxT.of(module.toString()).color(CUtl.s()),CTxT.of(String.valueOf(pos+1)).color(CUtl.s())));
            if (Return) UI(player, msg, pageHelper.getPageOf(module));
            else player.sendMessage(msg);
        }
        public static void toggle(Player player, Module module, Boolean toggle, boolean Return) {
            if (module.equals(Module.unknown)) {
                player.sendMessage(CUtl.error("hud.module"));
                return;
            }
            CUtl.PageHelper<Module> pageHelper = new CUtl.PageHelper<>(PlayerData.get.hud.order(player),PER_PAGE);
            if (toggle == null) toggle = !PlayerData.get.hud.getModule(player,module);
            CTxT msg = CUtl.tag().append(lang("module.toggle",CUtl.TBtn(toggle?"on":"off").color(toggle?'a':'c'),lang("module."+module).color(CUtl.s())));
            PlayerData.set.hud.setModule(player,module,toggle);
            if (Return) UI(player, msg, pageHelper.getPageOf(module));
            else player.sendMessage(msg);
        }
        public static ArrayList<Module> fixOrder(ArrayList<Module> list) {
            ArrayList<Module> allModules = getDefault();
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
        public static ArrayList<Module> getEnabled(Player player) {
            ArrayList<Module> enabled = new ArrayList<>();
            for (Module module: PlayerData.get.hud.order(player)) if (PlayerData.get.hud.getModule(player,module)) enabled.add(module);
            return enabled;
        }
        public static CTxT moduleInfo(Player player, Module module) {
            // get the hover info for each module
            CTxT info = CTxT.of("").append(lang("module."+module+".info")).append("\n");
            if (module.equals(Module.coordinates))
                info.append(color.addColor(player,"XYZ: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,95,20));
            if (module.equals(Module.distance))
                info.append(color.addColor(player,"[",1,15,20))
                        .append(color.addColor(player,"0",2,35,20))
                        .append(color.addColor(player,"]",1,55,20));
            if (module.equals(Module.destination))
                info.append(color.addColor(player,"DEST: ",1,15,20))
                        .append(color.addColor(player,"0 0 0",2,115,20));
            if (module.equals(Module.direction))
                info.append(color.addColor(player,"N",1,15,20));
            if (module.equals(Module.tracking))
                info.append(color.addColor(player,"[",1,15,20).strikethrough(true))
                        .append(color.addColor(player,"-"+Assets.symbols.up+Assets.symbols.right,2,35,20))
                        .append(color.addColor(player,"]",1,55,20).strikethrough(true));
            if (module.equals(Module.time)) {
                if ((boolean)PlayerData.get.hud.setting.get(player, Setting.module__time_24hr))
                    info.append(color.addColor(player,"22:22",1,15,20));
                else info.append(color.addColor(player,"11:11 ",2,15,20))
                            .append(color.addColor(player,"AM",1,115,20));
            }
            if (module.equals(Module.weather))
                info.append(color.addColor(player,Assets.symbols.sun,1,15,20));
            return info;
        }
        public static String stateColor(Player player, Module module) {
            if (!PlayerData.get.hud.getModule(player, module)) return Assets.mainColors.gray;
            boolean yellow = false;
            if (!Destination.get(player).hasXYZ()) {
                if (module.equals(Module.destination) || module.equals(Module.distance) || (module.equals(Module.tracking) &&
                        Setting.HUDTrackingTarget.get((String)PlayerData.get.hud.setting.get(player, Setting.module__tracking_target)).equals(Setting.HUDTrackingTarget.dest)))
                    yellow = true;
            }
            if (module.equals(Module.tracking) && Destination.social.track.getTarget(player)==null && Setting.HUDTrackingTarget.get((String)PlayerData.get.hud.setting.get(player, Setting.module__tracking_target)).equals(Setting.HUDTrackingTarget.player))
                yellow = true;
            if (yellow) return "#fff419";
            return "#19ff21";
        }
        public static void UI(Player player, CTxT abovemsg, int pg) {
            CUtl.PageHelper<Module> pageHelper = new CUtl.PageHelper<>(PlayerData.get.hud.order(player),PER_PAGE);
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.modules").color(Assets.mainColors.edit)).append(CTxT.of("\n                                     ").strikethrough(true));
            //MAKE THE TEXT
            for (Module module:pageHelper.getPage(pg)) {
                boolean state = PlayerData.get.hud.getModule(player,module);
                msg.append("\n ")
                        //ORDER
                        .append(CTxT.of(String.valueOf(pageHelper.getIndexOf(module)+1)).btn(true).color(CUtl.p())
                                .cEvent(2,"/hud modules order-r "+module+" ")
                                .hEvent(CUtl.TBtn("order.hover").color(CUtl.p())))
                        //TOGGLE
                        .append(CTxT.of(Assets.symbols.toggle).btn(true).color(CUtl.p())
                                .cEvent(1,"/hud modules toggle-r "+module)
                                .hEvent(CUtl.TBtn("hud.toggle.hover_mod",
                                        lang("module."+module).color(CUtl.s()),
                                        CUtl.TBtn(!state?"on":"off").color(!state?'a':'c')))).append(" ")
                        //NAME
                        .append(lang("module."+module).color(stateColor(player,module))
                                .hEvent(moduleInfo(player,module))).append(" ");
                //EXTRA BUTTONS
                if (module.equals(Module.time))
                    msg.append(settings.getButtons(player, Setting.module__time_24hr,true));
                if (module.equals(Module.tracking))
                    msg.append(settings.getButtons(player, Setting.module__tracking_target,true));
            }
            //BOTTOM ROW
            msg.append("\n\n ").append(CUtl.TBtn("reset").btn(true).color('c').cEvent(1,"/hud modules reset-r")
                            .hEvent(CUtl.TBtn("reset.hover_edit").color('c')))
                    .append(" ").append(pageHelper.getNavButtons(pg,"/hud modules ")).append(" ").append(CUtl.CButton.back("/hud"))
                    .append(CTxT.of("\n                                     ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static class color {
        public static void reset(Player player,String setting, String type, boolean Return) {
            if (type == null) {
                PlayerData.set.hud.color(player,1, defaultFormat(1));
                PlayerData.set.hud.color(player,2, defaultFormat(2));
                type = "all";
            } else if (type.equals("primary")) {
                PlayerData.set.hud.color(player,1, defaultFormat(1));
            } else if (type.equals("secondary")) {
                PlayerData.set.hud.color(player,2, defaultFormat(2));
            } else return;
            CTxT msg = CUtl.tag().append(lang("color.reset",CUtl.TBtn("reset").color('c'),lang("color."+type)));
            if (Return && type.equals("all")) UI(player,msg);
            else if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void presets(Player player, String setting, String type) {
            CUtl.color.presetUI(player,"default","/hud color set "+setting+" "+type+" ","/hud color edit "+setting+" "+type);
        }
        public static void setColor(Player player, String setting, String type, String color, boolean Return) {
            int typ = type.equals("primary")?1:2;
            if (CUtl.color.checkValid(color,getHUDColor(player,typ))) {
                PlayerData.set.hud.color(player,typ,CUtl.color.format(color)+"-"+getHUDBold(player,typ)+"-"+getHUDItalics(player,typ)+"-"+getHUDRGB(player,typ));
            } else {
                player.sendMessage(CUtl.error("error.color"));
                return;
            }
            CTxT msg = CUtl.tag().append(lang("color.set",lang("color."+type),CUtl.color.getBadge(color)));
            if (Return) changeUI(player,setting, type,msg);
            else player.sendMessage(msg);
        }
        public static void setBold(Player player, String setting, String type, boolean state, boolean Return) {
            int typ = type.equals("primary")?1:2;
            PlayerData.set.hud.color(player,typ,getHUDColor(player,typ)+"-"+state+"-"+getHUDItalics(player,typ)+"-"+getHUDRGB(player,typ));
            CTxT msg = CUtl.tag().append(lang("color.set.bold",
                    CUtl.lang("button."+(state?"on":"off")).color(state?'a':'c'),lang("color."+type)));
            if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void setItalics(Player player, String setting, String type, boolean state, boolean Return) {
            int typ = type.equals("primary")?1:2;
            PlayerData.set.hud.color(player,typ,getHUDColor(player,typ)+"-"+getHUDBold(player,typ)+"-"+state+"-"+getHUDRGB(player,typ));
            CTxT msg = CUtl.tag().append(lang("color.set.italics",
                    CUtl.lang("button."+(state?"on":"off")).color(state?'a':'c'),lang("color."+type)));
            if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void setRGB(Player player, String setting, String type, boolean state, boolean Return) {
            int typ = type.equals("primary")?1:2;
            if (getHUDRGB(player, typ)==state) return;
            PlayerData.set.hud.color(player,typ, getHUDColor(player,typ)+"-"+getHUDBold(player,typ)+"-"+getHUDItalics(player,typ)+"-"+state);
            CTxT msg = CUtl.tag().append(lang("color.set.rgb",
                    CUtl.lang("button."+(state?"on":"off")).color(state?'a':'c'),lang("color."+type)));
            if (Return) changeUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static String defaultFormat(int i) {
            if (i==1) return config.hud.primary.Color +"-"+ config.hud.primary.Bold +"-"+ config.hud.primary.Italics +"-"+ config.hud.primary.Rainbow;
            return config.hud.secondary.Color +"-"+ config.hud.secondary.Bold +"-"+ config.hud.secondary.Italics +"-"+ config.hud.secondary.Rainbow;
        }
        public static String getHUDColor(Player player, int i) {
            String[] p = PlayerData.get.hud.color(player,1).split("-");
            String[] s = PlayerData.get.hud.color(player,2).split("-");
            if (i==1) return p[0];
            return s[0];
        }
        public static Boolean getHUDBold(Player player, int i) {
            String[] p = PlayerData.get.hud.color(player,1).split("-");
            String[] s = PlayerData.get.hud.color(player,2).split("-");
            if (i==1) return Boolean.parseBoolean(p[1]);
            return Boolean.parseBoolean(s[1]);
        }
        public static Boolean getHUDItalics(Player player, int i) {
            String[] p = PlayerData.get.hud.color(player,1).split("-");
            String[] s = PlayerData.get.hud.color(player,2).split("-");
            if (i==1) return Boolean.parseBoolean(p[2]);
            return Boolean.parseBoolean(s[2]);
        }
        public static Boolean getHUDRGB(Player player, int i) {
            String[] p = PlayerData.get.hud.color(player,1).split("-");
            String[] s = PlayerData.get.hud.color(player,2).split("-");
            if (i==1) return Boolean.parseBoolean(p[3]);
            return Boolean.parseBoolean(s[3]);
        }
        public static CTxT addColor(Player player, String txt, int i, float start, float step) {
            if (getHUDRGB(player,i)) return CTxT.of(txt).rainbow(true,start,step).italic(getHUDItalics(player,i)).bold(getHUDBold(player,i));
            return CTxT.of(txt).color(getHUDColor(player,i)).italic(getHUDItalics(player,i)).bold(getHUDBold(player,i));
        }
        public static void changeUI(Player player, String setting, String type, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            CTxT back = CUtl.CButton.back("/hud color");
            int typ;
            if (type.equals("primary")) typ = 1;
            else if (type.equals("secondary")) typ = 2;
            else return;
            String currentColor = getHUDColor(player,typ);
            msg.append(" ").append(addColor(player, Helper.capitalizeFirst(lang("color."+type).toString()),typ,15,20))
                    .append(CTxT.of("\n                               \n").strikethrough(true));
            CTxT reset = CUtl.TBtn("reset").btn(true).color('c').cEvent(1, "/hud color reset "+setting+" "+type)
                    .hEvent(CUtl.lang("button.reset.hover_color_hud",addColor(player,lang("color."+type).toString().toUpperCase(),typ,15,20)));
            CTxT presetsButton = CTxT.of("")
                    .append(CTxT.of("+").btn(true).color('a').cEvent(1,"/hud color preset add "+setting+" "+type)
                            .hEvent(CUtl.TBtn("color.presets.add.hover",CUtl.TBtn("color.presets.add.hover_2").color(getHUDColor(player,typ)))))
                    .append(CUtl.TBtn("color.presets").color(Assets.mainColors.presets)
                            .cEvent(1,"/hud color preset "+setting+" "+type).btn(true)
                            .hEvent(CUtl.TBtn("color.presets.hover",CUtl.TBtn("color.presets.hover_2").color(Assets.mainColors.presets))));
            CTxT customButton = CUtl.TBtn("color.custom").btn(true).color(Assets.mainColors.custom)
                    .cEvent(2,"/hud color set "+setting+" "+type+" ")
                    .hEvent(CUtl.TBtn("color.custom.hover",CUtl.TBtn("color.custom.hover_2").color(Assets.mainColors.custom)));
            CTxT boldButton = CUtl.TBtn("color.bold").btn(true).color(getHUDBold(player, typ)?'a':'c')
                    .cEvent(1,"/hud color bold "+setting+" "+type+" "+(getHUDBold(player,typ)?"false":"true"))
                    .hEvent(CUtl.TBtn("color.bold.hover",CUtl.TBtn(getHUDBold(player,typ)?"off":"on").color(getHUDBold(player, typ)?'a':'c'),lang("color."+type)));
            CTxT italicsButton = CUtl.TBtn("color.italics").btn(true).color(getHUDItalics(player, typ)?'a':'c')
                    .cEvent(1,"/hud color italics "+setting+" "+type+" "+(getHUDItalics(player,typ)?"false":"true"))
                    .hEvent(CUtl.TBtn("color.italics.hover",CUtl.TBtn(getHUDItalics(player,typ)?"off":"on").color(getHUDItalics(player, typ)?'a':'c'),lang("color."+type)));
            CTxT rgbButton = CUtl.TBtn("color.rgb").btn(true).color(getHUDRGB(player, typ)?'a':'c')
                    .cEvent(1,"/hud color rgb "+setting+" "+type+" "+(getHUDRGB(player,typ)?"false":"true"))
                    .hEvent(CUtl.TBtn("color.rgb.hover",CUtl.TBtn(getHUDRGB(player,typ)?"off":"on").color(getHUDRGB(player, typ)?'a':'c'),lang("color."+type)));
            msg.append(" ")
                    .append(presetsButton).append(" ").append(customButton).append("\n\n")
                    .append(CUtl.color.colorEditor(currentColor,setting,"/hud color set "+setting+" "+type+" ","/hud color edit big "+type)).append("\n\n ")
                    .append(boldButton).append(" ").append(italicsButton).append(" ").append(rgbButton).append("\n\n     ")
                    .append(reset).append(" ").append(back)
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void UI(Player player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.color").rainbow(true,15f,45f))
                    .append(CTxT.of("\n                                \n").strikethrough(true)).append(" ");
            //PRIMARY
            msg.append(CTxT.of(addColor(player,CUtl.TBtn("color.primary").toString(),1,15,20)).btn(true).cEvent(1,"/hud color edit normal primary")
                    .hEvent(CUtl.TBtn("color.edit.hover",addColor(player,CUtl.TBtn("color.primary").toString(),1,15,20)))).append(" ");
            //SECONDARY
            msg.append(CTxT.of(addColor(player,CUtl.TBtn("color.secondary").toString(),2,15,20)).btn(true).cEvent(1,"/hud color edit normal secondary")
                    .hEvent(CUtl.TBtn("color.edit.hover",addColor(player,CUtl.TBtn("color.secondary").toString(),2,15,20)))).append("\n\n      ");
            //RESET
            msg.append(CUtl.TBtn("reset").btn(true).color('c').cEvent(1,"/hud color reset")
                            .hEvent(CUtl.TBtn("reset.hover_color",CUtl.TBtn("all").color('c'))))
                    .append("  ").append(CUtl.CButton.back("/hud"))
                    .append(CTxT.of("\n                                ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static class settings {
        public static Object getConfig(Setting type) {
            Object output = false;
            switch (type) {
                case state -> output = config.hud.State;
                case type -> output = config.hud.DisplayType;
                case bossbar__color -> output = config.hud.BarColor;
                case bossbar__distance -> output = config.hud.BarShowDistance;
                case bossbar__distance_max -> output = config.hud.ShowDistanceMAX;
                case module__time_24hr -> output = config.hud.Time24HR;
                case module__tracking_target -> output = config.hud.TrackingTarget;
            }
            return output;
        }
        public static void reset(Player player, Setting type, boolean Return) {
            if (type.equals(Setting.none)) {
                for (Setting s : Setting.values())
                    PlayerData.set.hud.setting.set(player, s, getConfig(s));
            } else {
                PlayerData.set.hud.setting.set(player,type,getConfig(type));
            }
            if (type.equals(Setting.bossbar__distance))
                PlayerData.set.hud.setting.set(player, Setting.get(type+"_max"),getConfig(Setting.get(type+"_max")));
            CTxT typ = CTxT.of(lang("settings."+type).toString().toUpperCase()).color('c');
            player.updateHUD();
            if (type.equals(Setting.none)) typ = CTxT.of(CUtl.TBtn("all")).color('c');
            CTxT msg = CUtl.tag().append(lang("setting.reset",typ));
            if (Return) UI(player, msg);
            else UI(player, null);
        }
        public static CTxT change(Player player, Setting type, String setting, boolean Return) {
            boolean state = setting.equals("on");
            CTxT stateTxT = CUtl.TBtn(state?"on":"off").color(state?'a':'c');
            setting = setting.toLowerCase();
            CTxT setTxT = CTxT.of("");
            // simple on off toggle
            if (type.equals(Setting.bossbar__distance) || type.equals(Setting.state)) {
                PlayerData.set.hud.setting.set(player,type,state);
                // if changing the state update the hud
                if (type.equals(Setting.state)) player.updateHUD();
                setTxT.append(stateTxT);
            }
            if (type.equals(Setting.type)) {
                PlayerData.set.hud.setting.set(player,type, Setting.DisplayType.valueOf(setting));
                setTxT.append(lang("settings."+type+"."+ Setting.DisplayType.valueOf(setting)).color(CUtl.s()));
                player.updateHUD();
            }
            if (type.equals(Setting.bossbar__color)) {
                PlayerData.set.hud.setting.set(player,type, Setting.BarColor.valueOf(setting));
                setTxT.append(lang("settings."+type+"."+ Setting.BarColor.valueOf(setting)).color(Assets.barColor(Setting.BarColor.valueOf(setting))));
            }
            if (type.equals(Setting.bossbar__distance_max)) {
                int i = Math.max(Integer.parseInt(setting),0);
                PlayerData.set.hud.setting.set(player,type,i);
                setTxT.append(CTxT.of(String.valueOf(i)).color((boolean) PlayerData.get.hud.setting.get(player, Setting.bossbar__distance)?'a':'c'));
            }
            if (type.equals(Setting.module__time_24hr)) {
                PlayerData.set.hud.setting.set(player,type,state);
                setTxT.append(lang("settings."+type+"."+(state?"on":"off")).color(CUtl.s()));
            }
            if (type.equals(Setting.module__tracking_target)) {
                PlayerData.set.hud.setting.set(player, type, Setting.HUDTrackingTarget.valueOf(setting));
                setTxT.append(lang("settings."+type+"." + Setting.HUDTrackingTarget.valueOf(setting)).color(CUtl.s()));
            }
            CTxT msg = CUtl.tag().append(lang("settings."+type+".set",setTxT));
            if (Return) UI(player, msg);
            return msg;
        }
        public static boolean canBeReset(Player player, Setting type) {
            boolean output = false;
            if (type.equals(Setting.none)) return false;
            if (!PlayerData.get.hud.setting.get(player,type).equals(getConfig(type))) output = true;
            if (type.equals(Setting.bossbar__distance))
                if (((Double) PlayerData.get.hud.setting.get(player, Setting.bossbar__distance_max)).intValue() != (int)getConfig(Setting.bossbar__distance_max)) output = true;
            return output;
        }
        public static CTxT resetB(Player player, Setting type) {
            CTxT msg = CTxT.of(Assets.symbols.x).btn(true).color('7');
            if (canBeReset(player,type)) {
                msg.color('c').cEvent(1, "/hud settings reset " + type)
                        .hEvent(CUtl.TBtn("reset.hover_settings",lang("settings."+type).color('c')));
            }
            return msg;
        }
        public static CTxT getButtons(Player player, Setting type) {
            // if there's something in module the command 'end's in module, to return to the module command instead of the settings command
            CTxT button = CTxT.of("");
            if (type.equals(Setting.state)) {
                button.append(CUtl.toggleBtn((boolean)PlayerData.get.hud.setting.get(player,type),"/hud settings "+type+" ")).append(" ");
            }
            if (type.equals(Setting.type)) {
                Setting.DisplayType nextType = Setting.DisplayType.valueOf((String) PlayerData.get.hud.setting.get(player,type)).next();
                button.append(lang("settings."+type+"."+PlayerData.get.hud.setting.get(player,type)).btn(true).color(CUtl.s())
                        .cEvent(1,"/hud settings "+type+" "+nextType)
                        .hEvent(lang("settings."+type+".hover",lang("settings."+type+"."+nextType).color(CUtl.s()))));
            }
            if (type.equals(Setting.bossbar__color)) {
                button.append(CUtl.lang("color.presets."+PlayerData.get.hud.setting.get(player,type)).btn(true)
                        .color(Assets.barColor((Setting.BarColor.valueOf((String) PlayerData.get.hud.setting.get(player,type)))))
                        .cEvent(2,"/hud settings "+type+" ").hEvent(lang("settings."+type+".hover")));
            }
            if (type.equals(Setting.bossbar__distance)) {
                boolean state = (boolean) PlayerData.get.hud.setting.get(player,type);
                button.append(CUtl.toggleBtn(state,"/hud settings "+type+" ")).append(" ");
                button.append(CTxT.of(String.valueOf(((Double) PlayerData.get.hud.setting.get(player, Setting.bossbar__distance_max)).intValue())).btn(true).color((boolean) PlayerData.get.hud.setting.get(player,type)?'a':'c')
                        .cEvent(2,"/hud settings "+ Setting.bossbar__distance_max+" ")
                        .hEvent(lang("settings."+type+"_max.hover").append("\n").append(lang("settings."+type+"_max.hover_2").italic(true).color('7'))));
            }
            return button;
        }
        public static void UI(Player player, CTxT aboveMSG) {
            CTxT msg = CTxT.of("");
            if (aboveMSG != null) msg.append(aboveMSG).append("\n");
            msg.append(" ").append(lang("ui.settings").color(Assets.mainColors.setting)).append(CTxT.of("\n                              \n").strikethrough(true));
            //HUD
            msg.append(" ").append(lang("settings.hud").color(CUtl.p())).append(":\n  ");
            msg     //STATE
                    .append(resetB(player, Setting.state)).append(" ")
                    .append(lang("settings."+ Setting.state).hEvent(lang("settings."+ Setting.state+".info"))).append(": ")
                    .append(getButtons(player, Setting.state))
                    .append("\n  ");
            msg     //TYPE
                    .append(resetB(player, Setting.type)).append(" ")
                    .append(lang("settings."+ Setting.type).hEvent(lang("settings."+ Setting.type+".info"))).append(": ")
                    .append(getButtons(player, Setting.type))
                    .append("\n");
            //BOSSBAR
            msg.append(" ").append(lang("settings.bossbar").color(CUtl.p())).append(":\n  ");
            msg     //COLOR
                    .append(resetB(player, Setting.bossbar__color)).append(" ")
                    .append(lang("settings."+ Setting.bossbar__color).hEvent(lang("settings."+ Setting.bossbar__color+".info"))).append(": ")
                    .append(getButtons(player, Setting.bossbar__color))
                    .append("\n  ");
            msg     //DISTANCE
                    .append(resetB(player, Setting.bossbar__distance)).append(" ")
                    .append(lang("settings."+ Setting.bossbar__distance).hEvent(lang("settings."+ Setting.bossbar__distance+".info")
                            .append("\n").append(lang("settings."+ Setting.bossbar__distance+".info_2").color('e')))).append(": ")
                    .append(getButtons(player, Setting.bossbar__distance))
                    .append("\n");
            //MODULE
            msg.append(" ").append(lang("settings.module").color(CUtl.p())).append(":\n  ");
            msg.append(CUtl.CButton.hud.modules()).append("\n");
            CTxT reset = CUtl.TBtn("reset").btn(true).color('7');
            boolean resetOn = false;
            // see if a setting can be reset, then flip the switch
            for (Setting t: Setting.main()) {
                if (resetOn) break;
                resetOn = canBeReset(player,t);
            }
            if (resetOn) reset.color('c').cEvent(1,"/hud settings reset all")
                    .hEvent(CUtl.TBtn("reset.hover_settings",CUtl.TBtn("all").color('c')));
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
