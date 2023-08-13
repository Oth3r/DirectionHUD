package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;

public class Destination {
    public enum Settings {
        autoclear,
        autoclear_rad,
        autoconvert,
        ylevel,
        particles__dest,
        particles__dest_color,
        particles__line,
        particles__line_color,
        particles__tracking,
        particles__tracking_color,
        features__send,
        features__track,
        features__lastdeath,
        none;
        @Override
        public String toString() {
            return name().replace("__",".");
        }
        public static Settings get(String s) {
            try {
                return Settings.valueOf(s.replace(".","__"));

            } catch (IllegalArgumentException e) {
                return none;
            }
        }
        public static ArrayList<Settings> colors() {
            return new ArrayList<>(Arrays.asList(particles__dest_color, particles__line_color, particles__tracking_color));
        }
        public static ArrayList<Settings> base() {
            ArrayList<Settings> list = new ArrayList<>(Arrays.asList(values()));
            list.remove(autoclear_rad);
            list.remove(none);
            list.removeAll(colors());
            return list;
        }
        // . = _ and _ == __
    }
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (!Utl.checkEnabled.destination(player)) return;
            if (args.length == 0) {
                UI(player);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Utl.trimStart(args, 1);
            switch (type) {
                case "set" -> setCMD(player, trimmedArgs);
                case "clear" -> clear(player, null);
                case "saved" -> savedCMD(player, trimmedArgs);
                case "add" -> addCMD(player, trimmedArgs);
                case "remove" -> removeCMD(player, trimmedArgs);
                case "lastdeath" -> lastdeathCMD(player, trimmedArgs);
                case "settings" -> settingsCMD(player, trimmedArgs);
                case "color" -> colorCMD(player,trimmedArgs);
                case "send" -> sendCMD(player, trimmedArgs);
                case "track" -> trackCMD(player, trimmedArgs);
                default -> player.sendMessage(CUtl.error(CUtl.lang("error.command")));
            }
        }
        public static void colorCMD(Player player, String[] args) {
            if (args.length == 3 && args[0].equals("preset")) {
                CUtl.color.presetUI(player,"custom","/dest color set "+args[1]+" "+args[2]+" ","/dest settings "+args[2]+" "+args[1]);
            }
            if (args.length == 4 && args[0].equals("set")) {
                setting.setColor(player,args[1],Settings.get(args[2]),args[3],true);
            }
        }
        public static void setCMD(Player player, String[] args) {
            if (!Utl.inBetween(args.length, 2,5)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSet));
                return;
            }
            // /dest set saved <name> (convert)
            if (args[0].equalsIgnoreCase("saved")) {
                if (!Utl.checkEnabled.saving(player)) return;
                if (args.length == 2) setName(player, args[1], false);
                if (args.length == 3 && args[2].equalsIgnoreCase("convert")) setName(player, args[1], true);
                return;
            }
            if (!Utl.isInt(args[0]) || !Utl.isInt(args[1])) return;
            // /dest set x z
            if (args.length == 2)
                set(player,new Loc(Utl.tryInt(args[0]),Utl.tryInt(args[1]),player.getDimension()),false);
            // /dest set x z DIM
            if (args.length == 3 && !Utl.isInt(args[2]))
                set(player,new Loc(Utl.tryInt(args[0]),Utl.tryInt(args[1]),args[2]),false);
            // /dest set x y z
            if (args.length == 3 && Utl.isInt(args[2]))
                set(player,new Loc(Utl.tryInt(args[0]),Utl.tryInt(args[1]),Utl.tryInt(args[2]),player.getDimension()),false);
            // /dest set x z DIM (convert)
            if (args.length == 4 && !Utl.isInt(args[2]))
                set(player,new Loc(Utl.tryInt(args[0]),Utl.tryInt(args[1]),args[2]),true);
            // /dest set x y z DIM
            if (args.length == 4 && Utl.isInt(args[2]))
                set(player,new Loc(Utl.tryInt(args[0]),Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),false);
            // /dest set x y z DIM (convert)
            if (args.length == 5)
                set(player,new Loc(Utl.tryInt(args[0]),Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),true);
        }
        public static void addCMD(Player player, String[] args) {
            //dest saved add <name>
            if (args.length == 1) {
                saved.add(true,player,args[0],new Loc(player),null);
                return;
            }
            if (!Utl.inBetween(args.length, 2, 6)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destAdd));
                return;
            }
            //dest saved add <name> color
            //dest saved add <name> dim
            if (args.length == 2) {
                if (Utl.dim.checkValid(args[1])) saved.add(true,player,args[0],new Loc(player,args[1]),null);
                else saved.add(true,player,args[0],new Loc(player),args[1]);
                return;
            }
            //dest saved add <name> x y
            if (args.length == 3) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),player.getDimension()),null);
                return;
            }
            //dest saved add <name> x y color
            if (args.length == 4 && !Utl.isInt(args[3]) && !Utl.dim.checkValid(args[3])) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),player.getDimension()),args[3]);
                return;
            }
            //dest saved add <name> x y DIM
            if (args.length == 4 && !Utl.isInt(args[3])) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),null);
                return;
            }
            //dest saved add <name> x y z
            if (args.length == 4 && Utl.isInt(args[3])) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),player.getDimension()),null);
                return;
            }
            //dest saved add <name> x y DIM color
            if (args.length == 5 && !Utl.isInt(args[3])) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),args[4]);
                return;
            }
            //dest saved add <name> x y z color
            if (args.length == 5 && !Utl.dim.checkValid(args[4])) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),player.getDimension()),args[4]);
                return;
            }
            //dest saved add <name> x y z DIM
            if (args.length == 5) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),null);
            }
            //dest saved add <name> x y z DIM color
            if (args.length == 6) {
                saved.add(true,player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),args[5]);
            }
        }
        public static void removeCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.saving(player)) return;
            if (args.length == 1) saved.delete(true, player, args[0]);
        }
        public static void savedCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.saving(player)) return;
            if (args.length == 0) {
                saved.UI(player, 1);
                return;
            }
            if (args.length == 1 && Utl.isInt(args[0])) {
                saved.UI(player, Integer.parseInt(args[0]));
                return;
            }
            if (args[0].equalsIgnoreCase("edit")) {
                if (args.length == 1) return;
                if (args.length == 2) saved.viewDestinationUI(true, player, args[1]);
                if (args[1].equalsIgnoreCase("name")) {
                    if (args.length == 3) player.sendMessage(error("dest.saved.set",lang("saved.name")));
                    if (args.length == 4) saved.editName(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("color")) {
                    if (args.length == 3) player.sendMessage(error("dest.saved.set",lang("saved.color")));
                    if (args.length == 4) saved.editColor(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("order")) {
                    if (args.length == 3) player.sendMessage(error("dest.saved.set",lang("saved.order")));
                    if (args.length == 4) saved.editOrder(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("dim")) {
                    if (args.length == 3) player.sendMessage(error("dest.saved.set",lang("saved.dimension")));
                    if (args.length == 4) saved.editDimension(true, player, args[2], args[3]);
                }
                if (args[1].equalsIgnoreCase("loc")) {
                    if (args.length == 3) player.sendMessage(error("dest.saved.set",lang("saved.location")));
                    if (args.length == 5) saved.editLocation(true,player,args[2],new Loc(Utl.tryInt(args[3]),Utl.tryInt(args[4])));
                    if (args.length == 6) saved.editLocation(true,player,args[2],new Loc(Utl.tryInt(args[3]),Utl.tryInt(args[4]),Utl.tryInt(args[5])));
                }
                return;
            }
            //SEND
            if (args[0].equalsIgnoreCase("send")) {
                if (args.length == 2) player.sendMessage(error("dest.send.player"));
                if (args.length == 3) social.send(player,args[2],null,args[1]);
                return;
            }
            //ADD
            if (args[0].equalsIgnoreCase("add")) {
                addCMD(player,Utl.trimStart(args,1));
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
        }
        public static void lastdeathCMD(Player player, String[] args) {
            if (!config.deathsaving || !(boolean)PlayerData.get.dest.setting.get(player,Settings.features__lastdeath)) return;
            if (args.length == 0) {
                lastdeath.UI(player, null);
                return;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("clear_all")) {
                    lastdeath.clearAll(true, player);
                }
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destLastdeath));
        }
        public static void settingsCMD(Player player, String[] args) {
            if (args.length == 0) setting.UI(player, null);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("reset")) setting.reset(player,Settings.get(args[1]), true);
                else setting.change(player,Settings.get(args[0]), args[1], true);
            }
            if (args.length == 3) setting.change(player,Settings.get(args[0]), args[1], false);
        }
        public static void sendCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.send(player)) return;
            if (!Utl.inBetween(args.length, 3, 6)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSend));
                return;
            }
            // /dest send <IGN> saved <name>
            if (args[1].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                if (args.length > 3) player.sendMessage(CUtl.usage(Assets.cmdUsage.destSend));
                else social.send(player,args[0],null,args[2]);
                return;
            }
            String pDIM = player.getDimension();
            //dest send <IGN> <xyz or xy> (dimension)
            //dest send <IGN> (name) <xyz or xy> (dimension)
            //dest send IGN x z
            if (args.length == 3) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),pDIM),null);
            }
            //dest send IGN NAME x z
            if (args.length == 4 && !Utl.isInt(args[1])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),pDIM),args[1]);
                return;
            }
            //dest send IGN x z DIM
            if (args.length == 4 && !Utl.isInt(args[3])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),null);
                return;
            }
            //dest send IGN x y z
            if (args.length == 4) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),pDIM),null);
            }
            //dest send IGN NAME x z DIM
            if (args.length == 5 && !Utl.isInt(args[1]) && !Utl.isInt(args[4])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),args[1]);
                return;
            }
            //dest send IGN NAME x y z
            if (args.length == 5 && !Utl.isInt(args[1])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),pDIM),args[1]);
                return;
            }
            //dest send IGN x y z DIM
            if (args.length == 5) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),null);
            }
            //dest send IGN NAME x y z DIM
            if (args.length == 6 && !Utl.isInt(args[1])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),args[5]),args[1]);
            }
        }
        public static void trackCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.track(player)) return;
            //dest track <name>
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase(".clear")) {
                    social.track.clear(player, null);
                    return;
                }
                social.track.initialize(player, args[0]);
                return;
            }
            if (args.length == 3) {
                //dest track accept/deny <name> <id>
                if (args[0].equalsIgnoreCase("acp")) {
                    social.track.accept(player, args[1], args[2]);
                    return;
                }
                if (args[0].equalsIgnoreCase("dny")) {
                    social.track.deny(player, args[1], args[2]);
                    return;
                }
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destTrack));
        }
    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.destination(player)) return suggester;
            if (pos == 1) suggester.addAll(base(player));
            if (pos > 1) {
                String command = args[0].toLowerCase();
                String[] trimmedArgs = Utl.trimStart(args, 1);
                int fixedPos = pos - 2;
                switch (command) {
                    case "saved" -> suggester.addAll(savedCMD(player,fixedPos,trimmedArgs));
                    case "add" -> suggester.addAll(addCMD(player,fixedPos,trimmedArgs));
                    case "settings" -> {
                        if (fixedPos == 0) suggester.add("reset");
                    }
                    case "color" -> {
                        if (fixedPos == 3 && trimmedArgs[0].equals("set")) suggester.add("ffffff");
                    }
                    case "set" -> suggester.addAll(setCMD(player,fixedPos,trimmedArgs));
                    case "send" -> suggester.addAll(sendCMD(player,fixedPos,trimmedArgs));
                    case "track" -> suggester.addAll(trackCMD(player,fixedPos));
                }
            }
            if (pos == args.length) return Utl.formatSuggestions(suggester,args);
            return suggester;
        }
        public static ArrayList<String> base(Player player) {
            ArrayList<String> suggester = new ArrayList<>();
            if (config.deathsaving && (boolean)PlayerData.get.dest.setting.get(player,Settings.features__lastdeath)) suggester.add("lastdeath");
            if (Utl.checkEnabled.saving(player)) {
                suggester.add("add");
                suggester.add("saved");
            }
            suggester.add("set");
            if (get(player).hasXYZ()) suggester.add("clear");
            suggester.add("settings");
            if (Utl.checkEnabled.send(player)) suggester.add("send");
            if (Utl.checkEnabled.track(player)) suggester.add("track");
            return suggester;
        }
        public static ArrayList<String> addCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // add <name> <x> (y) <z> (dim) (color)
            if (pos == 0) {
                suggester.add("name");
                return suggester;
            }
            // add <name> (<x> (dim) (color))
            if (pos == 1) {
                suggester.addAll(Utl.xyzSuggester(player,"x"));
                if (args.length == 2 && !Utl.isInt(args[1]) && !args[1].equals("")) {
                    suggester.add("ffffff");
                    suggester.addAll(Utl.dim.getList());
                    return suggester;
                }
            }
            // add <name> <x> ((y))
            if (pos == 2) {
                if (Utl.isInt(args[1])) return Utl.xyzSuggester(player,"y");
            }
            // add <name> <x> (y) (<z> (dim) (color))
            if (pos == 3) {
                if (Utl.isInt(args[1])) suggester.addAll(Utl.xyzSuggester(player,"z"));
                if (args.length == 4 && !Utl.isInt(args[3])) {
                    suggester.add("ffffff");
                    suggester.addAll(Utl.dim.getList());
                }
                return suggester;
            }
            // add <name> <x> (y) <z> ((dim) (color))
            if (pos == 4) {
                if (Utl.isInt(args[3])) {
                    suggester.addAll(Utl.dim.getList());
                    if (args.length == 5 && !Utl.dim.checkValid(args[4]))
                        suggester.add("ffffff");
                    return suggester;
                }
                if (Utl.dim.checkValid(args[3]))
                    suggester.add("ffffff");
                return suggester;
            }
            // add <name> <x> (y) <z> (dim) ((color))
            if (pos == 5) {
                if (Utl.isInt(args[3]) && Utl.dim.checkValid(args[4])) {
                    suggester.add("ffffff");
                    return suggester;
                }
            }
            return suggester;
        }
        public static ArrayList<String> savedCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.saving(player)) return suggester;
            // saved add
            // saved edit type name <arg>
            // saved send name <IGN>
            if (pos == 0) {
                suggester.add("add");
                return suggester;
            }
            // saved add
            if (args[0].equalsIgnoreCase("add")) {
                return commandSuggester.addCMD(player,pos-1,Utl.trimStart(args,1));
            }
            // saved edit
            if (args[0].equalsIgnoreCase("edit")) {
                if (pos < 1) return suggester;
                // saved edit type name (<arg>)
                if (args[1].equalsIgnoreCase("loc")) {
                    if (pos == 3) return Utl.xyzSuggester(player,"x");
                    if (pos == 4) return Utl.xyzSuggester(player,"y");
                    if (pos == 5) return Utl.xyzSuggester(player,"z");
                }
                if (pos == 3) {
                    if (args[1].equalsIgnoreCase("name")) suggester.add("name");
                    if (args[1].equalsIgnoreCase("color")) suggester.add("ffffff");
                    if (args[1].equalsIgnoreCase("dim")) suggester.addAll(Utl.dim.getList());
                    return suggester;
                }
            }
            // saved send
            if (args[0].equalsIgnoreCase("send")) {
                // saved send name (<ign>)
                if (pos != 2) return suggester;
                for (Player s : Utl.getPlayers()) {
                    if (s.equals(player)) continue;
                    suggester.add(s.getName());
                }
                return suggester;
            }
            return suggester;
        }
        public static ArrayList<String> setCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // set <saved> <name> (convert)
            // set <x> (y) <z> (dim) (convert)
            if (pos == 0) {
                if (Utl.checkEnabled.saving(player)) suggester.add("saved");
                suggester.addAll(Utl.xyzSuggester(player,"x"));
                return suggester;
            }
            // set <saved, x> ((name) (y))
            if (pos == 1) {
                if (args[0].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                    suggester.addAll(saved.getNames(player));
                    return suggester;
                }
                return Utl.xyzSuggester(player,"y");
            }
            // set <saved> <name> ((convert))
            // set <x> (y) (<z> (dim))
            if (pos == 2) {
                if (!Utl.isInt(args[1])) {
                    suggester.add("convert");
                    return suggester;
                }
                if (args.length == 3 && !Utl.isInt(args[2]))
                    suggester.addAll(Utl.dim.getList());
                suggester.addAll(Utl.xyzSuggester(player,"z"));
                return suggester;
            }
            // set <x> (y) <z> (dim)
            // set x z dim (convert
            if (pos == 3) {
                if (Utl.isInt(args[2])) suggester.addAll(Utl.dim.getList());
                else suggester.add("convert");
                return suggester;
            }
            // set x y z dim convert
            if (pos == 4) {
                if (Utl.isInt(args[2])) suggester.add("convert");
                return suggester;
            }
            return suggester;
        }
        public static ArrayList<String> sendCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // send <player> <saved> <name>
            // send <player> (name) <x> (y) <z> (dimension)
            if (pos == 0) {
                for (Player p : Utl.getPlayers()) {
                    if (p.equals(player)) continue;
                    suggester.add(p.getName());
                }
                return suggester;
            }
            // send <player> (<saved>, (name), <x>)
            if (pos == 1) {
                if (Utl.checkEnabled.saving(player)) suggester.add("saved");
                suggester.addAll(Utl.xyzSuggester(player,"x"));
                suggester.add("name");
                return suggester;
            }
            // send <player> <saved> (<name>)
            // send <player> (name) (<x>)
            // send <player> <x> ((y))
            if (pos == 2) {
                if (args[1].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                    suggester.addAll(saved.getNames(player));
                    return suggester;
                }
                if (!Utl.isInt(args[1])) {
                    return Utl.xyzSuggester(player,"x");
                }
                return Utl.xyzSuggester(player,"y");
            }
            // send <player> (name) <x> ((y))
            // send <player> <x> (y) (<z> (dimension))
            if (pos == 3) {
                if (!Utl.isInt(args[1])) {
                    return Utl.xyzSuggester(player,"y");
                }
                if (args.length == 4)
                    suggester.addAll(Utl.dim.getList());
                suggester.addAll(Utl.xyzSuggester(player,"z"));
                return suggester;
            }
            // send <player> (name) <x> (y) (<z> (dimension))
            // send <player> <x> (y) <z> ((dimension))
            if (pos == 4) {
                if (!Utl.isInt(args[1])) {
                    if (args.length == 5)
                        suggester.addAll(Utl.dim.getList());
                    suggester.addAll(Utl.xyzSuggester(player,"z"));
                    return suggester;
                }
                if (Utl.isInt(args[3]))
                    suggester.addAll(Utl.dim.getList());
                return suggester;
            }
            // send <player> (name) <x> (y) <z> ((dimension))
            if (pos == 5) {
                if (!Utl.isInt(args[1]) && Utl.isInt(args[4])) {
                    suggester.addAll(Utl.dim.getList());
                    return suggester;
                }
            }
            return suggester;
        }
        public static ArrayList<String> trackCMD(Player player, int pos) {
            ArrayList<String> suggester = new ArrayList<>();
            // track <player>
            if (pos == 0) {
                suggester.add(".clear");
                for (Player p : Utl.getPlayers()) {
                    if (p.equals(player)) continue;
                    suggester.add(p.getName());
                }
            }
            return suggester;
        }
    }
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("dest."+key, args);
    }
    private static CTxT error(String key, Object... args) {
        return CUtl.error(CUtl.lang("error."+key, args));
    }
    public static Loc get(Player player) {
        Loc loc = PlayerData.get.dest.getDest(player);
        if (!loc.hasXYZ()) return new Loc();
        if ((boolean)PlayerData.get.dest.setting.get(player,Settings.ylevel) && loc.yExists())
            loc.setY(player.getBlockY());
        return loc;
    }
    public static boolean checkDist(Player player, Loc loc) {
        if ((boolean)PlayerData.get.dest.setting.get(player,Settings.autoclear))
            return Utl.vec.distance(new Loc(player).getVec(player),loc.getVec(player)) <= (long)PlayerData.get.dest.setting.get(player,Settings.autoclear_rad);
        else return false;
    }
    public static int getDist(Player player) {
        return (int) Utl.vec.distance(new Loc(player).getVec(player),get(player).getVec(player));
    }
    public static void clear(Player player) {
        PlayerData.set.dest.setDest(player, new Loc());
    }
    public static void clear(Player player, CTxT reason) {
        CTxT msg = CUtl.tag().append(lang("changed", lang("changed.cleared").color('a')));
        if (!get(player).hasXYZ()) {
            player.sendMessage(error("dest.already_clear"));
            return;
        }
        clear(player);
        if (reason == null) {
            player.sendMessage(msg);
            return;
        }
        player.sendMessage(msg.append("\n ").append(reason));
    }
    public static CTxT setMSG(Player player) {
        boolean ac = (boolean)PlayerData.get.dest.setting.get(player,Settings.autoclear);
        CTxT btn = CUtl.TBtn(ac?"off":"on").btn(true).color(ac?'c':'a').cEvent(1,"/dest settings autoclear "+!ac+" n").hEvent(
                CTxT.of(Assets.cmdUsage.destSettings).color(ac?'c':'a').append("\n").append(CUtl.TBtn("state.hover",
                        CUtl.TBtn(ac?"off":"on").color(ac?'c':'a'))));
        return CTxT.of(" ").append(lang("set.autoclear",CUtl.lang(ac?"on":"off").italic(true),btn).color('7').italic(true));
    }
    public static void silentSet(Player player, Loc loc) {
        if (!checkDist(player, loc)) PlayerData.set.dest.setDest(player, loc);
    }
    //convert converts loc dim to player dim
    public static void set(Player player, Loc loc, boolean convert) {
        if (!loc.hasXYZ()) {
            player.sendMessage(error("coordinates"));
            return;
        }
        if (loc.getDIM() == null) {
            player.sendMessage(error("dimension"));
            return;
        }
        CTxT convertMsg = CTxT.of("");
        if (Utl.dim.canConvert(player.getDimension(),loc.getDIM()) && convert) {
            convertMsg.append(" ").append(lang("converted_badge").color('7').italic(true).hEvent(loc.getBadge()));
            loc.convertTo(player.getDimension());
        }
        if (checkDist(player,loc)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        silentSet(player, loc);
        player.sendMessage(CUtl.tag().append(lang("set",loc.getBadge())).append(convertMsg));
        player.sendMessage(setMSG(player));
    }
    public static void setName(Player player, String name, boolean convert) {
        if (!saved.getNames(player).contains(name)) {
            player.sendMessage(error("dest.invalid"));
            return;
        }
        int key = saved.getNames(player).indexOf(name);
        CTxT convertMsg = CTxT.of("");
        Loc loc = saved.getLocs(player).get(key);
        if (convert && Utl.dim.canConvert(player.getDimension(),loc.getDIM())) {
            convertMsg.append(" ").append(lang("converted_badge").color('7').italic(true).hEvent(loc.getBadge()));
            loc.convertTo(player.getDimension());
        }
        if (checkDist(player,loc)) {
            player.sendMessage(error("dest.at"));
            return;
        }
        silentSet(player,loc);
        player.sendMessage(CUtl.tag().append(lang("set",
                CTxT.of("").append(loc.getBadge(saved.getNames(player).get(key),saved.getColors(player).get(key))).append(convertMsg))));
        player.sendMessage(setMSG(player));
    }
    public static class saved {
        public static int getIndexFromName(Player player, String name) {
            return getNames(player).indexOf(name);
        }
        public static List<List<String>> getList(Player player) {
            return PlayerData.get.dest.getSaved(player);
        }
        public static void setList(Player player, List<List<String>> list) {
            PlayerData.set.dest.setSaved(player, list);
        }
        public static List<String> getNames(Player player) {
            List<List<String>> list = getList(player);
            List<String> all = new ArrayList<>();
            for (List<String> i: list) all.add(i.get(0));
            return all;
        }
        public static List<Loc> getLocs(Player player) {
            List<List<String>> list = getList(player);
            List<Loc> all = new ArrayList<>();
            for (List<String> i: list) all.add(new Loc(i.get(1)));
            return all;
        }
        public static List<String> getColors(Player player) {
            List<List<String>> list = getList(player);
            List<String> all = new ArrayList<>();
            for (List<String> i: list) all.add(i.get(2));
            return all;
        }
        public static Integer getMaxPage(Player player) {
            double i = getList(player).size() - 1;
            i = i / 8;
            i = i - 0.5;
            return (int) Math.round(i) + 1;
        }
        public static Integer getPGOf (Player player, String name) {
            List<String> names = getNames(player);
            if (!names.contains(name)) return 1;
            double i = names.indexOf(name);
            i = i / 8;
            i = i - 0.5;
            return (int) Math.round(i) + 1;
        }
        public static void add(boolean send, Player player, String name, Loc loc, String color) {
            List<List<String>> all = getList(player);
            if (getList(player).size() >= config.MAXSaved) {
                if (send) player.sendMessage(error("dest.saved.max"));
                return;
            }
            if (getNames(player).contains(name)) {
                if (send) player.sendMessage(error("dest.saved.duplicate",lang("saved.name"),name));
                return;
            }
            if (name.equalsIgnoreCase("saved")) {
                if (send) player.sendMessage(error("dest.saved.not_allowed"));
                return;
            }
            if (name.length() > 16) {
                if (send) player.sendMessage(error("dest.saved.length",16));
                return;
            }
            if (!Utl.dim.checkValid(loc.getDIM())) {
                if (send) player.sendMessage(error("dimension"));
                return;
            }
            if (!loc.hasXYZ()) {
                player.sendMessage(error("coordinates"));
                return;
            }
            color = CUtl.color.format(color,"ffffff");
            all.add(Arrays.asList(name,loc.getLocC(),color));
            setList(player, all);
            if (send) {
                CTxT buttons = CTxT.of(" ").append(CUtl.CButton.dest.edit(1,"/dest saved edit " + name))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set saved "+name));
                if (Utl.dim.canConvert(player.getDimension(),loc.getDIM()))
                    buttons.append(" ").append(CUtl.CButton.dest.convert("/dest set saved "+name+" convert"));
                player.sendMessage(CUtl.tag().append(lang("saved.add",loc.getBadge(name,color).append(buttons))));
            }
        }
        public static void delete(boolean send, Player player, String name) {
            List<String> names = getNames(player);
            List<List<String>> all = getList(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            int index = getIndexFromName(player,name);
            Loc loc = getLocs(player).get(index);
            String color = getColors(player).get(index);
            int pg = getPGOf(player, name);
            all.remove(names.indexOf(name));
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.delete",loc.getBadge(name,color))));
                player.performCommand("dest saved "+pg);
            }
        }
        public static void editName(boolean send, Player player, String name, String newName) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            if (names.contains(newName)) {
                if (send) player.sendMessage(error("dest.saved.duplicate",lang("saved.name"),CTxT.of(newName).color(CUtl.s())));
                return;
            }
            if (newName.equalsIgnoreCase("saved")) {
                if (send) player.sendMessage(error("dest.saved.not_allowed"));
                return;
            }
            if (newName.length() > 16) {
                if (send) player.sendMessage(error("dest.saved.length", 16));
                return;
            }
            int i = names.indexOf(name);
            List<List<String>> all = getList(player);
            List<String> current = all.get(i);
            current.set(0, newName);
            all.set(i,current);
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.name"),CTxT.of(newName).color(CUtl.s()))));
                player.performCommand("dest saved edit "+newName);
            }
        }
        public static void editOrder(boolean send, Player player, String name, String orderNumber) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            if (!Utl.isInt(orderNumber)) {
                if (send) player.sendMessage(error("number"));
                return;
            }
            int newOrderNum = Integer.parseInt(orderNumber);
            if (getIndexFromName(player, name)+1==newOrderNum) {
                if (send) player.sendMessage(error("dest.saved.duplicate",lang("saved.order"),CTxT.of(orderNumber).color(CUtl.s())));
                return;
            }
            if (newOrderNum == 0) newOrderNum = 1;
            List<List<String>> all = getList(player);
            List<String> move = all.get(names.indexOf(name));
            //IF ORDER NUM TOO HIGH
            if (newOrderNum > all.size()) {
                all.remove(move);
                all.add(all.size(), move);
            } else {
                all.remove(move);
                all.add(newOrderNum - 1, move);
            }
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.order"),CTxT.of(String.valueOf(getList(player).indexOf(move) + 1)).color(CUtl.s()))));
                player.performCommand("dest saved edit "+name);
            }
        }
        public static void editLocation(boolean send, Player player, String name, Loc loc) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            if (!loc.hasXYZ()) {
                if (send) player.sendMessage(error("coordinates"));
                return;
            }
            int i = names.indexOf(name);
            if (getLocs(player).get(i).getXYZ().equals(loc.getXYZ())) {
                if (send) player.sendMessage(error("dest.saved.duplicate",lang("saved.location"),CTxT.of(loc.getXYZ()).color(CUtl.s())));
                return;
            }
            loc.setDIM(getLocs(player).get(i).getDIM());
            List<List<String>> all = getList(player);
            List<String> current = all.get(i);
            current.set(1,loc.getLocC());
            all.set(i,current);
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.location"),CTxT.of(loc.getXYZ()).color(CUtl.s()))));
                player.performCommand("dest saved edit "+name);
            }
        }
        public static void editDimension(boolean send, Player player, String name, String dimension) {
            if (!getNames(player).contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            int i = getIndexFromName(player,name);
            if (!Utl.dim.checkValid(dimension)) {
                if (send) player.sendMessage(error("dimension"));
                return;
            }
            if (getLocs(player).get(i).getDIM().equalsIgnoreCase(dimension)) {
                if (send) player.sendMessage(error("dest.saved.duplicate",lang("saved.dimension"),CTxT.of(Utl.dim.getName(dimension).toUpperCase()).color(Utl.dim.getHEX(dimension))));
                return;
            }
            Loc loc = getLocs(player).get(i);
            loc.setDIM(dimension);
            List<List<String>> all = getList(player);
            List<String> current = all.get(i);
            current.set(1,loc.getLocC());
            all.set(i,current);
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.dimension"),CTxT.of(Utl.dim.getName(dimension).toUpperCase()).color(Utl.dim.getHEX(dimension)))));
                player.performCommand("dest saved edit "+name);
            }
        }
        public static void editColor(boolean send, Player player, String name, String color) {
            List<String> names = getNames(player);
            if (!names.contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            int i = names.indexOf(name);
            color = CUtl.color.format(color,"#ffffff");
            if (getColors(player).get(i).equals(color)) {
                if (send) player.sendMessage(error("dest.saved.duplicate",lang("saved.color"),CUtl.color.getBadge(color)));
                return;
            }
            List<List<String>> all = getList(player);
            List<String> current = all.get(i);
            current.set(2, color.toLowerCase());
            all.set(i,current);
            setList(player, all);
            if (send) {
                player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.color"),CUtl.color.getBadge(color))));
                player.performCommand("dest saved edit "+name);
            }
        }
        public static void viewDestinationUI(boolean send, Player player, String name) {
            int i = getIndexFromName(player,name);
            if (!getNames(player).contains(name)) {
                if (send) player.sendMessage(error("dest.invalid"));
                return;
            }
            String dName = Utl.capitalizeFirst(lang("saved.name").getString());
            String dColor = Utl.capitalizeFirst(lang("saved.color").getString());
            String dOrder = Utl.capitalizeFirst(lang("saved.order").getString());
            String dDimension = Utl.capitalizeFirst(lang("saved.dimension").getString());
            String dLocation = Utl.capitalizeFirst(lang("saved.location").getString());
            Loc loc = getLocs(player).get(i);
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved.edit").color(Assets.mainColors.saved)).append(CTxT.of("\n                                               \n").strikethrough(true));
            msg.append(" ")
                    //NAME
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit name "+name+ " ")).append(" ")
                    .append(CTxT.of(dName).color(CUtl.p())).append(" "+name).append("\n ")
                    //COLOR
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit color " +name+ " ")).append(" ")
                    .append(CTxT.of(dColor).color(CUtl.p())).append(" ")
                    .append(CUtl.color.getBadge(getColors(player).get(i))).append("\n ")
                    //ORDER
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit order " +name+ " ")).append(" ")
                    .append(CTxT.of(dOrder).color(CUtl.p())).append(" "+(i + 1)).append("\n ")
                    //DIMENSION
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit dim " +name+ " ")).append(" ")
                    .append(CTxT.of(dDimension).color(CUtl.p())).append(" ").append(CTxT.of(Utl.dim.getName(loc.getDIM())).color(Utl.dim.getHEX(loc.getDIM()))).append("\n ")
                    //LOCATION
                    .append(CUtl.CButton.dest.edit(2,"/dest saved edit loc " +name+ " ")).append(" ")
                    .append(CTxT.of(dLocation).color(CUtl.p())).append(" "+loc.getXYZ()).append("\n       ");
            //SEND BUTTON
            if ((boolean) PlayerData.get.dest.setting.get(player,Settings.features__send)) {
                msg.append(CUtl.TBtn("dest.send").btn(true).color(Assets.mainColors.send).cEvent(2,"/dest saved send "+name+" ")
                        .hEvent(CTxT.of("/dest saved send "+name+" <player>").color(Assets.mainColors.send)
                                .append("\n").append(CUtl.TBtn("dest.send.hover_saved")))).append(" ");
            }
            //SET BUTTON
            msg.append(CUtl.CButton.dest.set("/dest set saved " +name)).append(" ");
            //CONVERT
            if (Utl.dim.canConvert(player.getDimension(),getLocs(player).get(i).getDIM()))
                msg.append(CUtl.CButton.dest.convert("/dest set saved " +name+ " convert"));
            //DELETE
            msg.append("\n\n ")
                    .append(CUtl.TBtn("delete").btn(true).color('c').cEvent(2,"/dest remove "+name)
                            .hEvent(CUtl.TBtn("delete.hover_dest").color('c'))).append(" ")
                    //BACK
                    .append(CUtl.CButton.back("/dest saved " + getPGOf(player, name)))
                    .append(CTxT.of("\n                                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void UI(Player player, int pg) {
            CTxT addB = CUtl.TBtn("dest.add").btn(true).color(Assets.mainColors.add).cEvent(2,"/dest add ").hEvent(
                    CTxT.of(Assets.cmdUsage.destAdd).color(Assets.mainColors.add).append("\n").append(CUtl.TBtn("dest.add.hover",
                            CUtl.TBtn("dest.add.hover_2").color(Assets.mainColors.add))));
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved").color(Assets.mainColors.saved)).append(CTxT.of("\n                                               \n").strikethrough(true));
            List<String> names = getNames(player);
            if (pg > getMaxPage(player)) {
                pg = 1;
            }
            if (pg == 0) pg = 1;
            String plDimension = player.getDimension();
            if (names.size() != 0) {
                for (int i = 1; i <= 8; i++) {
                    int get = i + ((pg - 1) * 8) - 1;
                    if (names.size() > get) {
                        String dimension = getLocs(player).get(get).getDIM();
                        msg.append(" ")//BADGE
                                .append(getLocs(player).get(get).getBadge(names.get(get),getColors(player).get(get))).append(" ")
                                //EDIT
                                .append(CUtl.CButton.dest.edit(1,"/dest saved edit " + names.get(get))).append(" ")
                                //SET
                                .append(CUtl.CButton.dest.set("/dest set saved " + names.get(get)));
                        //CONVERT
                        if (Utl.dim.canConvert(plDimension, dimension))
                            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set saved " + names.get(get) + " convert"));
                        msg.append("\n");
                    }
                }
            } else {
                msg.append(" ").append(lang("saved.none")).append("\n ").append(lang("saved.none_2", addB)).append("\n\n ");
                msg
                        .append(CTxT.of("<<").btn(true).color('7')).append(" ")
                        .append(CUtl.TBtn("dest.saved.page.hover", 1).color(CUtl.s())).append(" ")
                        .append(CTxT.of(">>").btn(true).color('7')).append(" ").append(addB).append(" ")
                        .append(CUtl.CButton.back("/dest"))
                        .append(CTxT.of("\n                                               ").strikethrough(true));
                player.sendMessage(msg);
                return;
            }
            int finalPg = pg;
            msg.append(" ");
            if (pg == 1) msg.append(CTxT.of("<<").btn(true).color('7'));
            else msg.append(CTxT.of("<<").btn(true).color(CUtl.p()).cEvent(1,"/dest saved " + (finalPg-1)));
            msg.append(" ").append(CUtl.TBtn("dest.saved.page.hover", pg).color(CUtl.s())).append(" ");
            if (pg == getMaxPage(player)) msg.append(CTxT.of(">>").btn(true).color('7'));
            else msg.append(CTxT.of(">>").btn(true).color(CUtl.p()).cEvent(1,"/dest saved " + (finalPg+1)));
            msg.append(" ").append(addB).append(" ")
                    .append(CUtl.CButton.back("/dest"))
                    .append(CTxT.of("\n                                               ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static class lastdeath {
        public static void add(Player player, Loc loc) {
            ArrayList<String> deaths = PlayerData.get.dest.getLastdeaths(player);
            if (Utl.dim.checkValid(loc.getDIM())) {
                int i = 0;
                for (String s: deaths) {
                    if (new Loc(s).getDIM().equals(loc.getDIM())) {
                        deaths.set(deaths.indexOf(s),loc.getLocC());
                        i++;
                        break;
                    }
                }
                if (i == 0) deaths.add(loc.getLocC());
            }
            PlayerData.set.dest.setLastdeaths(player,deaths);
        }
        public static void clearAll(boolean send, Player player) {
            PlayerData.set.dest.setLastdeaths(player,new ArrayList<>());
            if (send) UI(player, lang("lastdeath.clear",CUtl.TBtn("all").color('c')));
        }
        public static void UI(Player player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.lastdeath").color(Assets.mainColors.lastdeath)).append(CTxT.of("\n                                  \n").strikethrough(true));
            int num = 0;
            msg.append(" ");
            for (String s: PlayerData.get.dest.getLastdeaths(player)) {
                Loc loc = new Loc(s);
                if (!Utl.dim.checkValid(loc.getDIM())) continue;
                num++;
                String dim = loc.getDIM();
                msg.append(loc.getBadge()).append("\n  ")
                        .append(CUtl.CButton.dest.add("/dest add "+Utl.dim.getName(dim).toLowerCase()+"_death "+loc.getXYZ()+" "+dim+" "+Utl.dim.getHEX(dim).substring(1)))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+loc.getDIM()));
                if (Utl.dim.canConvert(player.getDimension(),dim)) msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+dim+" convert"));
                msg.append("\n ");
            }
            int reset = 1;
            char resetC = 'c';
            if (num == 0) {
                reset = 0;
                resetC = '7';
                msg.append(lang("lastdeath.no_deaths").color('c')).append("\n");
            }
            msg.append("\n      ")
                    .append(CUtl.TBtn("clear").btn(true).color(resetC).cEvent(reset,"/dest lastdeath clear_all")
                            .hEvent(CUtl.TBtn("clear.hover_ld",CUtl.TBtn("all").color('c'))))
                    .append("  ").append(CUtl.CButton.back("/dest"))
                    .append(CTxT.of("\n                                  ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static class social {
        public static void send(Player player, String sendPLayer, Loc loc, String name) {
            Player pl = Player.of(sendPLayer);
            if (pl == null) {
                player.sendMessage(error("player", CTxT.of(sendPLayer).color(CUtl.s())));
                return;
            }
            if (!(boolean)PlayerData.get.dest.setting.get(player,Settings.features__send)) {
                player.sendMessage(error("disabled"));
                return;
            }
            if (pl == player) {
                player.sendMessage(error("dest.send.alone"));
                return;
            }
            if (!(boolean)PlayerData.get.dest.setting.get(pl,Settings.features__send)) {
                player.sendMessage(error("dest.send.disabled_player",CTxT.of(pl.getName()).color(CUtl.s())));
                return;
            }
            if (name != null && name.length() > 16) {
                player.sendMessage(error("dest.saved.length",16));
                return;
            }
            String color = "";
            if (loc == null) {
                if (!saved.getNames(player).contains(name)) {
                    player.sendMessage(error("dest.invalid"));
                    return;
                }
                int i = saved.getNames(player).indexOf(name);
                loc = saved.getLocs(player).get(i);
                color = saved.getColors(player).get(i);
            }
            if (!loc.hasXYZ()) {
                player.sendMessage(error("coordinates"));
                return;
            }
            if (!Utl.dim.checkValid(loc.getDIM())) {
                player.sendMessage(error("dimension"));
                return;
            }
            CTxT xyzB = CTxT.of("");
            if (name==null) {
                name = lang("send.change_name").getString();
                xyzB.append(loc.getBadge());
            } else xyzB.append(loc.getBadge(name,color.equals("")?"white":color));
            String plDimension = pl.getDimension();

            CTxT msg = CTxT.of("\n ");
            msg.append(xyzB).append(" ");
            if (Utl.checkEnabled.saving(player))
                msg.append(CUtl.CButton.dest.add("/dest saved add "+name+" "+loc.getXYZ()+" "+loc.getDIM()+" "+color)).append(" ");
            msg.append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+loc.getDIM())).append(" ");
            if (Utl.dim.canConvert(plDimension,loc.getDIM()))
                msg.append(CUtl.CButton.dest.convert("/dest set " +loc.getXYZ()+" "+loc.getDIM()+" convert")).append(" ");
            player.sendMessage(CUtl.tag().append(lang("send",CTxT.of(pl.getName()).color(CUtl.s()),
                    CTxT.of("\n ").append(xyzB))));
            pl.sendMessage(CUtl.tag().append(lang("send_player",CTxT.of(player.getName()).color(CUtl.s()),msg)));
        }
        public static class track {
            public static Player getTarget(Player player) {
                String track = PlayerData.get.dest.getTracking(player);
                if (track == null) return null;
                return Player.of(track);
            }
            public static void clear(Player player, CTxT reason) {
                CTxT msg = CUtl.tag().append(lang("track.clear"));
                if (PlayerData.get.dest.getTracking(player) == null) {
                    player.sendMessage(error("dest.track.cleared"));
                    return;
                }
                clear(player);
                if (reason == null) {
                    player.sendMessage(msg);
                    return;
                }
                player.sendMessage(msg.append("\n ").append(reason));
            }
            public static void clear(Player player) {
                for (String s: PlayerData.oneTimeMap.get(player).keySet())
                    if (s.contains("tracking")) PlayerData.setOneTime(player,s,null);
                PlayerData.set.dest.setTracking(player,null);
            }
            public static void set(Player player, Player pl, boolean send) {
                if (config.online) PlayerData.set.dest.setTracking(player,pl.getUUID());
                else PlayerData.set.dest.setTracking(player,pl.getName());
                if (!send) return;
                player.sendMessage(CUtl.tag().append(lang("track.accepted",CTxT.of(pl.getName()).color(CUtl.s()))));
                player.sendMessage(setMSG(player));
                pl.sendMessage(CUtl.tag()
                        .append(lang("track.accept", CTxT.of(player.getName()).color(CUtl.s())))
                        .append(" ")
                        .append(CUtl.TBtn("settings").btn(true).color(Assets.mainColors.setting).cEvent(1,"/dest settings ").hEvent(
                                CTxT.of(Assets.cmdUsage.destSettings).color('c').append("\n").append(
                                        CUtl.TBtn("state.hover",CUtl.TBtn("off").color('c'))))));
            }
            public static void initialize(Player player, String player2) {
                Player pl = Player.of(player2);
                if (pl == null) {
                    player.sendMessage(error("player",CTxT.of(player2).color(CUtl.s())));
                    return;
                }
                if (pl == player) {
                    player.sendMessage(error("dest.track.alone"));
                    return;
                }
                if (!(boolean)PlayerData.get.dest.setting.get(player,Settings.features__track)) {
                    player.sendMessage(error("disabled"));
                    return;
                }
                if (!(boolean)PlayerData.get.dest.setting.get(pl,Settings.features__track)) {
                    player.sendMessage(error("dest.track.disabled",CTxT.of(pl.getName()).color(CUtl.s())));
                    return;
                }
                if (PlayerData.get.temp.track.exists(player)) {
                    player.sendMessage(error("dest.track.pending"));
                    return;
                }
                if (getTarget(player) != null && Objects.equals(getTarget(player), pl)) {
                    player.sendMessage(error("dest.track.already_tracking",CTxT.of(pl.getName()).color(CUtl.s())));
                    return;
                }
                String trackID = Utl.createID();
                PlayerData.set.temp.track.id(player, trackID);
                PlayerData.set.temp.track.expire(player, 90);
                PlayerData.set.temp.track.target(player, pl.getName());
                player.sendMessage(CUtl.tag().append(lang("track",CTxT.of(pl.getName()).color(CUtl.s())))
                        .append("\n ").append(lang("track_expire", 90).color('7').italic(true)));
                pl.sendMessage(CUtl.tag().append(lang("track_player",CTxT.of(player.getName()).color(CUtl.s()))).append("\n ")
                        .append(CUtl.TBtn("accept").btn(true).color('a').cEvent(1,"/dest track acp "+player.getName()+" "+trackID)
                                .hEvent(CUtl.TBtn("accept.hover"))).append(" ")
                        .append(CUtl.TBtn("deny").btn(true).color('c').cEvent(1,"/dest track dny "+player.getName()+" "+trackID)
                                .hEvent(CUtl.TBtn("deny.hover"))));
            }
            public static void accept(Player pl, String player2, String ID) {
                Player player = Player.of(player2);
                // player is tracker, pl is tracked
                if (player == null) {
                    pl.sendMessage(error("player",CTxT.of(player2).color(CUtl.s())));
                    return;
                }
                if (pl == player) {
                    pl.sendMessage(error("how"));
                    return;
                }
                if (!PlayerData.get.temp.track.exists(player) || !PlayerData.get.temp.track.id(player).equals(ID)) {
                    //expired
                    pl.sendMessage(error("dest.track.expired"));
                    return;
                }
                if (!(boolean)PlayerData.get.dest.setting.get(player,Settings.features__track)) {
                    pl.sendMessage(error("dest.track.disabled",CTxT.of(pl.getName()).color(CUtl.s())));
                    PlayerData.set.temp.track.remove(player);
                    return;
                }
                if (!Objects.equals(PlayerData.get.temp.track.target(player), pl.getName())) {
                    pl.sendMessage(error("how"));
                    return;
                }
                set(player, pl,true);
                PlayerData.set.temp.track.remove(player);
            }
            public static void deny(Player pl, String player2, String ID) {
                // player is tracker, pl is tracked
                Player player = Player.of(player2);
                if (player == null) {
                    pl.sendMessage(error("player",CTxT.of(player2).color(CUtl.s())));
                    return;
                }
                if (pl == player) {
                    pl.sendMessage(error("how"));
                    return;
                }
                if (PlayerData.get.temp.track.id(player) == null || !PlayerData.get.temp.track.id(player).equals(ID)) {
                    pl.sendMessage(error("dest.track.expired"));
                    return;
                }
                if (!Objects.equals(PlayerData.get.temp.track.target(player), pl.getName())) {
                    pl.sendMessage(error("how"));
                    return;
                }
                player.sendMessage(CUtl.tag().append(lang("track.denied",CTxT.of(pl.getName()).color(CUtl.s()))));
                PlayerData.set.temp.track.remove(player);
                pl.sendMessage(CUtl.tag().append(lang("track.deny",CTxT.of(player.getName()).color(CUtl.s()))));
            }
        }
    }
    public static class setting {
        public static Object getConfig(Settings type) {
            Object output = false;
            switch (type) {
                case autoclear -> output = config.DESTAutoClear;
                case autoclear_rad -> output = config.DESTAutoClearRad;
                case autoconvert -> output = config.DESTAutoConvert;
                case ylevel -> output = config.DESTYLevel;
                case particles__dest -> output = config.DESTDestParticles;
                case particles__dest_color -> output = config.DESTDestParticleColor;
                case particles__line -> output = config.DESTLineParticles;
                case particles__line_color -> output = config.DESTLineParticleColor;
                case particles__tracking -> output =config.DESTTrackingParticles;
                case particles__tracking_color -> output=config.DESTTrackingParticleColor;
                case features__send -> output=config.DESTSend;
                case features__track -> output=config.DESTTrack;
                case features__lastdeath -> output=config.DESTLastdeath;
            }
            return output;
        }
        public static void reset(Player player, Settings type, boolean Return) {
            if (type.equals(Settings.none)) {
                for (Settings s: Settings.values())
                    PlayerData.set.dest.setting.set(player,s,getConfig(s));
            } else {
                PlayerData.set.dest.setting.set(player,type,getConfig(type));
            }
            if (type.equals(Settings.autoclear))
                PlayerData.set.dest.setting.set(player,Settings.autoclear_rad,getConfig(Settings.autoclear_rad));
            if (Settings.colors().contains(Settings.get(type+"_color")))
                PlayerData.set.dest.setting.set(player,Settings.get(type+"_color"),getConfig(Settings.get(type+"_color")));
            CTxT typ = CTxT.of(lang("settings."+type).getString().toUpperCase()).color('c');
            if (type.equals(Settings.none)) typ = CTxT.of(CUtl.TBtn("all").getString().toUpperCase()).color('c');
            CTxT msg = CUtl.tag().append(lang("settings.reset",typ));
            if (Return) UI(player, msg);
            else UI(player, null);
        }
        public static void change(Player player, Settings type, String setting, boolean Return) {
            boolean state = setting.equals("on");
            CTxT stateTxT = CUtl.TBtn(state?"on":"off").color(state?'a':'c');
            setting = setting.toLowerCase();
            CTxT msg = CUtl.tag();
            if (type.equals(Settings.autoclear_rad)) {
                if (!Utl.isInt(setting)) {
                    player.sendMessage(error("number"));
                    return;
                }
                int i = Math.max(Math.min(Integer.parseInt(setting),15),1);
                PlayerData.set.dest.setting.set(player, Settings.autoclear_rad,i);
                msg.append(lang("settings."+type+".set",CTxT.of(String.valueOf(i)).color((boolean) PlayerData.get.dest.setting.get(player,Settings.autoclear)?'a':'c')));
            }
            if (Settings.colors().contains(type)) {
                colorUI(player,setting,type,null);
                return;
            }
            if (Settings.base().contains(type)) {
                PlayerData.set.dest.setting.set(player,type,state);
                msg.append(lang("settings."+type+".set",stateTxT));
            }
            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        public static boolean canBeReset(Player player, Settings type) {
            boolean output = false;
            if (type.equals(Settings.none)) return false;
            if (PlayerData.get.dest.setting.get(player,type) != getConfig(type)) output = true;
            if (type.equals(Settings.autoclear))
                if ((long)PlayerData.get.dest.setting.get(player, Settings.autoclear_rad) != (int)getConfig(Settings.autoclear_rad)) output = true;
            if (Settings.colors().contains(Settings.get(type+"_color")))
                if (!PlayerData.get.dest.setting.get(player,Settings.get(type+"_color")).equals(getConfig(Settings.get(type+"_color")))) output = true;
            return output;
        }
        public static CTxT resetB(Player player, Settings type) {
            CTxT msg = CTxT.of(Assets.symbols.x).btn(true).color('7');
            if (canBeReset(player,type)) {
                msg.color('c').cEvent(1, "/dest settings reset " + type)
                        .hEvent(CUtl.TBtn("reset.hover_settings",lang("settings."+type).color('c')));
            }
            return msg;
        }
        public static CTxT getButtons(Player player, Settings type) {
            boolean state = (boolean) PlayerData.get.dest.setting.get(player,type);
            CTxT button = CTxT.of("");
            if (type.equals(Settings.none)) return button;
            button.append(CUtl.toggleBtn(state,"/dest settings "+type+" ")).append(" ");
            if (type.equals(Settings.autoclear)) {
                button.append(CTxT.of(String.valueOf((long) PlayerData.get.dest.setting.get(player,Settings.get(type+"_rad")))).btn(true)
                        .color(state?'a':'c').cEvent(2,"/dest settings "+type+"_rad ")
                        .hEvent(lang("settings."+type+"_rad.hover")));
            }
            if (Settings.colors().contains(Settings.get(type+"_color"))) {
                String color = (String) PlayerData.get.dest.setting.get(player,Settings.get( type+"_color"));
                button.append(CTxT.of(Assets.symbols.pencil).btn(true).color(color)
                        .cEvent(1,"/dest settings "+type+"_color normal")
                        .hEvent(lang("settings.particles.color.hover",lang("settings.particles.color.hover_2").color(color))));
            }
            return button;
        }
        public static void colorUI(Player player, String setting, Settings type, CTxT aboveMSG) {
            if (!Settings.colors().contains(type)) return;
            String currentColor = (String) PlayerData.get.dest.setting.get(player,type);
            CTxT uiType = lang("settings."+type.toString().substring(0,type.toString().length()-6));
            CTxT msg = CTxT.of("");
            if (aboveMSG != null) msg.append(aboveMSG).append("\n");
            msg.append(" ").append(uiType.color(currentColor))
                    .append(CTxT.of("\n                               \n").strikethrough(true));
            CTxT back = CUtl.CButton.back("/dest settings");
            CTxT presetsButton = CUtl.TBtn("color.presets").color(Assets.mainColors.presets)
                    .cEvent(1,"/dest color preset "+setting+" "+type).btn(true)
                    .hEvent(CUtl.TBtn("color.presets.hover",CUtl.TBtn("color.presets.hover_2").color(Assets.mainColors.presets)));
            CTxT customButton = CUtl.TBtn("color.custom").btn(true).color(Assets.mainColors.custom)
                    .cEvent(2,"/dest color set "+setting+" "+type+" ")
                    .hEvent(CUtl.TBtn("color.custom.hover",CUtl.TBtn("color.custom.hover_2").color(Assets.mainColors.custom)));
            msg.append("   ")
                    .append(presetsButton).append(" ").append(customButton).append("\n\n")
                    .append(CUtl.color.colorEditor(currentColor,setting,"/dest color set "+setting+" "+type+" ","/dest settings "+type+" big"))
                    .append("\n\n           ").append(back)
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void setColor(Player player, String setting, Settings type, String color, boolean Return) {
            CTxT uiType = lang("settings."+type.toString().substring(0,type.toString().length()-6));
            PlayerData.set.dest.setting.set(player,type,color);
            CTxT msg = CUtl.tag().append(lang("settings.particles.color.set",uiType.getString().toUpperCase(),CUtl.color.getBadge(color)));
            if (Return) colorUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void UI(Player player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.settings").color(Assets.mainColors.setting)).append(CTxT.of("\n                                \n").strikethrough(true));
            //DEST
            msg.append(" ").append(lang("settings.destination").color(CUtl.p())).append(":\n  ");
            msg     //AUTOCLEAR
                    .append(resetB(player, Settings.autoclear)).append(" ")
                    .append(lang("settings."+ Settings.autoclear).hEvent(lang("settings."+ Settings.autoclear+".info")
                            .append("\n").append(lang("settings."+ Settings.autoclear+".info_2").italic(true).color('7')))).append(": ")
                    .append(getButtons(player, Settings.autoclear)).append("\n  ");
            msg     //AUTOCONVERT
                    .append(resetB(player, Settings.autoconvert)).append(" ")
                    .append(lang("settings."+ Settings.autoconvert).hEvent(lang("settings."+ Settings.autoconvert+".info")
                            .append("\n").append(lang("settings."+ Settings.autoconvert+".info_2").italic(true).color('7')))).append(": ")
                    .append(getButtons(player, Settings.autoconvert)).append("\n  ");
            msg     //YLEVEL
                    .append(resetB(player, Settings.ylevel)).append(" ")
                    .append(lang("settings."+ Settings.ylevel).hEvent(lang("settings."+ Settings.ylevel+".info",
                            lang("settings."+ Settings.ylevel+".info_2").color(CUtl.s()),
                            lang("settings."+ Settings.ylevel+".info_2").color(CUtl.s())))).append(": ")
                    .append(getButtons(player, Settings.ylevel)).append("\n ");
            //PARTICLES
            msg.append(lang("settings.particles").color(CUtl.p())).append(":\n  ");
            msg     //DESTINATION
                    .append(resetB(player, Settings.particles__dest)).append(" ")
                    .append(lang("settings."+ Settings.particles__dest).hEvent(lang("settings."+ Settings.particles__dest+".info"))).append(": ")
                    .append(getButtons(player, Settings.particles__dest)).append("\n  ");
            msg     //LINE
                    .append(resetB(player, Settings.particles__line)).append(" ")
                    .append(lang("settings."+ Settings.particles__line).hEvent(lang("settings."+ Settings.particles__line+".info"))).append(": ")
                    .append(getButtons(player, Settings.particles__line)).append("\n  ");
            msg     //TRACK
                    .append(resetB(player, Settings.particles__tracking)).append(" ")
                    .append(lang("settings."+ Settings.particles__tracking).hEvent(lang("settings."+ Settings.particles__tracking+".info"))).append(": ")
                    .append(getButtons(player, Settings.particles__tracking)).append("\n ");
            if (config.social || config.deathsaving) {
                //FEATURES
                msg.append(lang("settings.features").color(CUtl.p())).append(":\n  ");
                if (config.social) {
                    msg     //SEND
                            .append(resetB(player, Settings.features__send)).append(" ")
                            .append(lang("settings."+ Settings.features__send).hEvent(lang("settings."+ Settings.features__send +".info",
                                    lang("settings."+ Settings.features__send +".info_1").color(CUtl.s()),
                                    lang("settings."+ Settings.features__send +".info_2").color(CUtl.s()),
                                    lang("settings."+ Settings.features__send +".info_3").color(CUtl.s())))).append(": ")
                            .append(getButtons(player, Settings.features__send)).append("\n  ");
                    msg     //TRACK
                            .append(resetB(player, Settings.features__track)).append(" ")
                            .append(lang("settings."+ Settings.features__track).hEvent(lang("settings."+ Settings.features__track +".info"))).append(": ")
                            .append(getButtons(player, Settings.features__track)).append("\n  ");
                }
                if (config.deathsaving) {
                    msg     //LASTDEATH
                            .append(resetB(player, Settings.features__lastdeath)).append(" ")
                            .append(lang("settings."+ Settings.features__lastdeath).hEvent(lang("settings."+ Settings.features__lastdeath +".info"))).append(": ")
                            .append(getButtons(player, Settings.features__lastdeath)).append("\n ");
                }
            }
            CTxT reset = CUtl.TBtn("reset").btn(true).color('7');
            boolean resetOn = false;
            for (Settings t: Settings.base()) {
                if (resetOn) break;
                if (!config.deathsaving && t.equals(Settings.features__lastdeath)) continue;
                if (!config.social && (t.equals(Settings.features__send) || t.equals(Settings.features__track))) continue;
                resetOn = canBeReset(player,t);
            }
            if (resetOn) reset.color('c').cEvent(1,"/dest settings reset all")
                    .hEvent(CUtl.TBtn("reset.hover_settings",CUtl.TBtn("all").color('c')));
            msg.append("\n     ").append(reset).append("  ").append(CUtl.CButton.back("/dest")).append("\n")
                    .append(CTxT.of("                                ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static void UI(Player player) {
        CTxT msg = CTxT.of(" ");
        msg.append(lang("ui").color(Assets.mainColors.dest)).append(CTxT.of("\n                                  ").strikethrough(true)).append("\n ");
        // lmao this is a mess but is it the best way to do it? dunno
        boolean line1Free = false;
        boolean line2Free = !((boolean) PlayerData.get.dest.setting.get(player,Settings.features__lastdeath) && config.deathsaving);
        boolean trackBig = PlayerData.get.dest.getTracking(player) != null;
        boolean sendThird = Utl.checkEnabled.send(player);
        //SAVED + ADD
        if (Utl.checkEnabled.saving(player)) {
            msg.append(CUtl.CButton.dest.saved()).append(CUtl.CButton.dest.add());
            if (!line2Free) msg.append("        ");
            else msg.append("  ");
        } else line1Free = true;
        //SET + CLEAR
        msg.append(CUtl.CButton.dest.set()).append(CUtl.CButton.dest.clear(player));
        if (line1Free) msg.append(" ");
        else msg.append("\n\n ");
        //LASTDEATH
        if (Utl.checkEnabled.lastdeath(player)) {
            msg.append(CUtl.CButton.dest.lastdeath());
            if (line1Free) {
                line1Free = false;
                line2Free = true;
                msg.append("\n\n ");
            } else msg.append("  ");
        }
        //SETTINGS
        msg.append(CUtl.CButton.dest.settings());
        if (line1Free) {
            msg.append("\n\n ");
        } else if (line2Free) msg.append("  ");
        else msg.append("\n\n ");
        //SEND
        if (Utl.checkEnabled.send(player)) {
            msg.append(CUtl.CButton.dest.send());
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
                line2Free = false;
                sendThird = false;
            } else if (trackBig) msg.append(" ");
            else msg.append("   ");
        }
        //TRACK
        if (Utl.checkEnabled.track(player)) {
            msg.append(CUtl.CButton.dest.track());
            if (trackBig) msg.append(CUtl.CButton.dest.trackX());
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
            } else if (trackBig && line2Free) {
                if (Utl.checkEnabled.send(player)) msg.append(" ");
                else msg.append("   ");
            } else if (sendThird && trackBig) {
                msg.append(" ");
            } else msg.append("   ");
        }
        //back
        msg.append(CUtl.CButton.back("/directionhud")).append(CTxT.of("\n                                  ").strikethrough(true));
        player.sendMessage(msg);
    }
}