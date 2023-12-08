package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.GlobalDest;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;

public class Destination {
    public enum Setting {
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
        features__track_request_mode,
        features__lastdeath,
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
        public static ArrayList<Setting> colors() {
            return new ArrayList<>(Arrays.asList(particles__dest_color, particles__line_color, particles__tracking_color));
        }
        public static ArrayList<Setting> base() {
            ArrayList<Setting> list = new ArrayList<>(Arrays.asList(values()));
            list.remove(features__track_request_mode);
            list.remove(autoclear_rad);
            list.remove(none);
            list.removeAll(colors());
            return list;
        }
        public static ArrayList<Setting> dest() {
            ArrayList<Setting> list = new ArrayList<>();
            list.add(autoclear);
            list.add(autoconvert);
            list.add(ylevel);
            return list;
        }
        public static ArrayList<Setting> particles() {
            ArrayList<Setting> list = new ArrayList<>();
            list.add(particles__line);
            list.add(particles__dest);
            list.add(particles__dest);
            return list;
        }
        public static ArrayList<Setting> features() {
            ArrayList<Setting> list = new ArrayList<>();
            list.add(features__track);
            list.add(features__send);
            list.add(features__lastdeath);
            return list;
        }
        public enum TrackingRequestMode {
            request,
            instant;
            public static final TrackingRequestMode[] values = values();
            public TrackingRequestMode next() {
                return values[(ordinal() + 1) % values.length];
            }
            public static TrackingRequestMode get(String s) {
                try {
                    return TrackingRequestMode.valueOf(s);

                } catch (IllegalArgumentException e) {
                    return TrackingRequestMode.valueOf(config.dest.defaults.TrackingRequestMode);
                }
            }
        }
    }
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (!Utl.checkEnabled.destination(player)) return;
            if (args.length == 0) {
                UI(player);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Helper.trimStart(args, 1);
            switch (type) {
                case "set" -> setCMD(player, trimmedArgs);
                case "clear" -> clear(player, null);
                case "saved" -> savedCMD(player, trimmedArgs);
                case "add" -> addCMD(player,saved.getList(player), trimmedArgs);
                case "lastdeath" -> lastdeathCMD(player, trimmedArgs);
                case "settings" -> settingsCMD(player, trimmedArgs);
                case "color" -> colorCMD(player,trimmedArgs);
                case "send" -> sendCMD(player, trimmedArgs);
                case "track" -> trackCMD(player, trimmedArgs);
                default -> player.sendMessage(CUtl.error("command"));
            }
        }
        public static void colorCMD(Player player, String[] args) {
            if (args.length >= 3 && args[0].equals("preset")) {
                if (args[1].equals("add") && args.length == 4) {
                    CUtl.color.customAddUI(player, (String) PlayerData.get.dest.setting.get(player, Setting.get(args[3])),"/dest settings "+args[3]+" "+args[2]);
                } else CUtl.color.presetUI(player,"default","/dest color set "+args[1]+" "+args[2]+" ","/dest settings "+args[2]+" "+args[1]);
            }
            if (args.length >= 3 && args[0].equals("preset_s")) {
                if (args[1].equals("add") && args.length == 4) {
                    CUtl.color.customAddUI(player, new saved.Dest(player,saved.getList(player),args[3]).getColor(),"/dest saved edit colorui "+args[3]+" "+args[2]);
                } else CUtl.color.presetUI(player,"default","/dest color set_s "+args[1]+" "+args[2]+" ","/dest saved edit colorui "+args[2]+" "+args[1]);
            }
            if (args.length == 4 && args[0].equals("set")) {
                settings.setColor(player,args[1], Setting.get(args[2]),args[3],true);
            }
            if (args.length == 4 && args[0].equals("set_s")) {
                saved.setColor(player,saved.getList(player),args[1],args[2],args[3],true);
            }
        }
        public static void setCMD(Player player, String[] args) {
            if (!Helper.inBetween(args.length, 2,5)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSet));
                return;
            }
            // /dest set saved <name> (convert)
            if (args[0].equalsIgnoreCase("saved")) {
                if (!Utl.checkEnabled.saving(player)) return;
                if (args.length == 2) setSaved(player,saved.getList(player), args[1], false);
                if (args.length == 3 && args[2].equalsIgnoreCase("convert")) setSaved(player,saved.getList(player), args[1], true);
                return;
            }
            // /dest set global <name> (convert)
            if (args[0].equalsIgnoreCase("global")) {
                if (!config.globalDESTs) return;
                if (args.length == 2) setSaved(player,GlobalDest.dests, args[1], false);
                if (args.length == 3 && args[2].equalsIgnoreCase("convert")) setSaved(player,GlobalDest.dests, args[1], true);
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
        public static void addCMD(Player player, List<List<String>> list, String[] args) {
            if (!Utl.checkEnabled.saving(player)) return;
            //dest saved add <name>
            if (args.length == 1) {
                saved.add(true,player,list,args[0],new Loc(player),null);
                return;
            }
            if (!Helper.inBetween(args.length, 2, 6)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destAdd));
                return;
            }
            //dest saved add <name> color
            //dest saved add <name> dim
            if (args.length == 2) {
                if (Utl.dim.checkValid(args[1])) saved.add(true,player,list,args[0],new Loc(player,args[1]),null);
                else saved.add(true,player,list,args[0],new Loc(player),args[1]);
                return;
            }
            //dest saved add <name> x y
            if (args.length == 3) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),player.getDimension()),null);
                return;
            }
            //dest saved add <name> x y color
            if (args.length == 4 && !Utl.isInt(args[3]) && !Utl.dim.checkValid(args[3])) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),player.getDimension()),args[3]);
                return;
            }
            //dest saved add <name> x y DIM
            if (args.length == 4 && !Utl.isInt(args[3])) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),null);
                return;
            }
            //dest saved add <name> x y z
            if (args.length == 4 && Utl.isInt(args[3])) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),player.getDimension()),null);
                return;
            }
            //dest saved add <name> x y DIM color
            if (args.length == 5 && !Utl.isInt(args[3])) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),args[4]);
                return;
            }
            //dest saved add <name> x y z color
            if (args.length == 5 && !Utl.dim.checkValid(args[4])) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),player.getDimension()),args[4]);
                return;
            }
            //dest saved add <name> x y z DIM
            if (args.length == 5) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),null);
            }
            //dest saved add <name> x y z DIM color
            if (args.length == 6) {
                saved.add(true,player,list,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),args[5]);
            }
        }
        public static void globalCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.saving(player) || !Utl.checkEnabled.global(player)) return;
            if (args.length == 0) {
                saved.globalUI(player, 1);
                return;
            }
            if (args[0].equalsIgnoreCase("edit")) {
                destEditCMD(player,GlobalDest.dests, Helper.trimStart(args,1),false);
                return;
            }
            //DELETE
            if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 1) player.sendMessage(CUtl.error("dest.saved.delete"));
                if (args.length == 2) saved.delete(false,player,GlobalDest.dests,args[1]);
                return;
            }
            //ADD
            if (args[0].equalsIgnoreCase("add")) {
                addCMD(player,GlobalDest.dests, Helper.trimStart(args,1));
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
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
            boolean Return = false;
            // if the type has -r, remove it and enable returning
            if (args[0].contains("-r")) {
                args[0] = args[0].replace("-r","");
                Return = true;
            }
            if (args[0].equalsIgnoreCase("global") && config.globalDESTs) {
                if (args.length == 1) {
                    saved.globalUI(player, 1);
                    return;
                }
                if (args.length == 2 && Utl.isInt(args[1])) {
                    saved.globalUI(player, Integer.parseInt(args[1]));
                    return;
                }
                globalCMD(player, Helper.trimStart(args,1));
                return;
            }
            if (args[0].equalsIgnoreCase("edit")) {
                destEditCMD(player, saved.getList(player), Helper.trimStart(args, 1), Return);
                return;
            }
            //SEND
            if (args[0].equalsIgnoreCase("send")) {
                if (args.length == 2) player.sendMessage(CUtl.error("dest.send.player"));
                if (args.length == 3) social.send(player,args[2],null,args[1],null);
                return;
            }
            //DELETE
            if (args[0].equalsIgnoreCase("delete")) {
                if (args.length == 1) player.sendMessage(CUtl.error("dest.saved.delete"));
                if (args.length == 2) saved.delete(Return,player,saved.getList(player),args[1]);
                return;
            }
            //ADD
            if (args[0].equalsIgnoreCase("add")) {
                addCMD(player,saved.getList(player), Helper.trimStart(args,1));
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
        }
        public static void destEditCMD(Player player, List<List<String>> list, String[] args, boolean Return) {
            if (args.length == 0) return;
            if (args.length == 1) saved.viewDestinationUI(true, player, args[0]);
            if (args[0].equalsIgnoreCase("name")) {
                if (args.length == 2) player.sendMessage(CUtl.error("dest.saved.set",lang("saved.name")));
                if (args.length == 3) saved.editName(Return, player,list, args[1], args[2]);
            }
            if (args[0].equalsIgnoreCase("color")) {
                if (args.length == 2) player.sendMessage(CUtl.error("dest.saved.set",lang("saved.color")));
                if (args.length == 3) saved.setColor(player,list, "normal", args[1], args[2], Return);
            }
            if (args[0].equalsIgnoreCase("colorui")) {
                if (args.length == 2) saved.colorUI(player,"normal",args[1],null);
                if (args.length == 3) saved.colorUI(player,args[2],args[1],null);
            }
            if (args[0].equalsIgnoreCase("order")) {
                if (args.length == 2) player.sendMessage(CUtl.error("dest.saved.set",lang("saved.order")));
                if (args.length == 3) saved.editOrder(Return, player,list, args[1], args[2]);
            }
            if (args[0].equalsIgnoreCase("location")) {
                if (args.length == 2) player.sendMessage(CUtl.error("dest.saved.set",lang("saved.location")));
                // dest saved edit name dim
                if (args.length == 3 && !Utl.isInt(args[2])) {
                    Loc loc = new saved.Dest(player,list,args[1]).getLoc();
                    loc.setDIM(args[2]);
                    saved.editLocation(Return,player,list,args[1],loc);
                }
                if (args.length == 4) saved.editLocation(Return,player,list,args[1],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3])));
                if (args.length == 5) {
                    // dest saved edit name x, y (z, dim)
                    if (Utl.isInt(args[4])) saved.editLocation(true,player,list,args[1],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4])));
                    else saved.editLocation(Return,player,list,args[1],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]));
                }
                if (args.length == 6) saved.editLocation(Return,player,list,args[1],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),args[5]));
            }
        }
        public static void lastdeathCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.lastdeath(player)) return;
            if (args.length == 0) {
                lastdeath.UI(player,1,null);
                return;
            }
            if (args.length == 1 && Utl.isInt(args[0])) {
                lastdeath.UI(player,Integer.parseInt(args[0]),null);
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destLastdeath));
        }
        public static void settingsCMD(Player player, String[] args) {
            if (args.length == 0) settings.UI(player, null);
            if (args.length == 2) {
                if (args[0].equalsIgnoreCase("reset")) settings.reset(player, Setting.get(args[1]), true);
                else settings.change(player, Setting.get(args[0]), args[1], true);
            }
            if (args.length == 3) settings.change(player, Setting.get(args[0]), args[1], false);
        }
        public static void sendCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.send(player)) return;
            // /dest send <IGN>
            if (args.length == 1) {
                social.send(player,args[0],player.getLoc(),null,null);
                return;
            }
            // /dest send <IGN> <name>
            if (args.length == 2) {
                social.send(player,args[0],player.getLoc(),args[1],null);
                return;
            }
            if (!Helper.inBetween(args.length, 3, 7)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSend));
                return;
            }
            // /dest send <IGN> saved <name>
            if (args[1].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                if (args.length > 3) player.sendMessage(CUtl.usage(Assets.cmdUsage.destSend));
                else social.send(player,args[0],null,args[2],null);
                return;
            }
            String pDIM = player.getDimension();
            //dest send <IGN> <xyz or xy> (dimension)
            //dest send <IGN> (name) <xyz or xy> (dimension)
            //dest send IGN x z
            if (args.length == 3) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),pDIM),null,null);
            }
            //dest send IGN NAME x z
            if (args.length == 4 && !Utl.isInt(args[1])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),pDIM),args[1],null);
                return;
            }
            //dest send IGN x y (z, DIM, color)
            if (args.length == 4) {
                //DIM
                if (Utl.dim.getList().contains(args[3]))
                    social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),null,null);
                else if (!Utl.isInt(args[3]) || !CUtl.color.format(args[3]).equals("#ffffff")) {
                    //COLOR - if not int or if it doesn't get reset it's a color (I hope)
                    social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),pDIM),null,args[3]);
                } else social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),pDIM),null,null);
            }
            //dest send IGN NAME x y (z, color, DIM)
            if (args.length == 5 && !Utl.isInt(args[1])) {
                //dest send IGN NAME x z DIM
                if (Utl.dim.getList().contains(args[4]))
                    social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),args[1],null);
                else if (!Utl.isInt(args[4]) || !CUtl.color.format(args[4]).equals("#ffffff")) {
                    //dest send IGN NAME x z color
                    //if not int or if it doesn't get reset it's a color (I hope)
                    social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),pDIM),args[1],args[4]);
                } else social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),pDIM),args[1],null);
                return;
            }
            //dest send IGN x y z (DIM, color)
            if (args.length == 5 && Utl.isInt(args[3])) {
                if (Utl.dim.getList().contains(args[4]))
                    social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),null,null);
                else social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),pDIM),null,args[4]);
            }
            //dest send IGN x y DIM color
            if (args.length == 5 && Utl.dim.getList().contains(args[3])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),args[3]),null,args[4]);
            }
            //dest send IGN NAME x y z (DIM, color)
            if (args.length == 6 && !Utl.isInt(args[1])) {
                if (Utl.isInt(args[4])) {
                    if (Utl.dim.getList().contains(args[5]))
                        social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),args[5]),args[1],null);
                    else social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),pDIM),args[1],args[5]);
                } else {
                    //dest send IGN NAME x z DIM color
                    social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),args[1],args[5]);
                }
                return;
            }
            //dest send IGN x y z DIM color
            if (args.length == 6 && Utl.dim.getList().contains(args[4])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[1]),Utl.tryInt(args[2]),Utl.tryInt(args[3]),args[4]),null,args[5]);
            }
            //dest send IGN NAME x y z DIM color
            if (args.length == 7 && !Utl.isInt(args[1])) {
                social.send(player,args[0],new Loc(Utl.tryInt(args[2]),Utl.tryInt(args[3]),Utl.tryInt(args[4]),args[5]),args[1],args[6]);
            }
        }
        public static void trackCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.track(player)) return;
            //dest track
            if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                social.track.clear(player, null);
                return;
            }
            if (args.length == 2) {
                boolean Return = false;
                // if the type has -r, remove it and enable returning
                if (args[0].contains("-r")) {
                    args[0] = args[0].replace("-r", "");
                    Return = true;
                }
                switch (args[0]) {
                    case "accept" -> social.track.process(player, args[1], social.track.ProcessType.accept, Return);
                    case "deny" -> social.track.process(player, args[1], social.track.ProcessType.deny, Return);
                    case "cancel" -> social.track.process(player, args[1], social.track.ProcessType.cancel, Return);
                    case "set" -> social.track.initialize(player, args[1]);
                    default -> player.sendMessage(CUtl.usage(Assets.cmdUsage.destTrack));
                }
            } else player.sendMessage(CUtl.usage(Assets.cmdUsage.destTrack));
        }
    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.destination(player)) return suggester;
            if (pos == 1) suggester.addAll(base(player));
            if (pos > 1) {
                String command = args[0].toLowerCase();
                String[] trimmedArgs = Helper.trimStart(args, 1);
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
                    case "track" -> suggester.addAll(trackCMD(player,fixedPos,trimmedArgs));
                }
            }
            return suggester;
        }
        public static ArrayList<String> base(Player player) {
            ArrayList<String> suggester = new ArrayList<>();
            if (config.LastDeathSaving && (boolean)PlayerData.get.dest.setting.get(player, Setting.features__lastdeath)) suggester.add("lastdeath");
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
                suggester.addAll(Helper.xyzSuggester(player,"x"));
                if (args.length == 2 && !Utl.isInt(args[1]) && !args[1].equals("")) {
                    suggester.add("ffffff");
                    suggester.addAll(Utl.dim.getList());
                    return suggester;
                }
            }
            // add <name> <x> ((y))
            if (pos == 2) {
                if (Utl.isInt(args[1])) return Helper.xyzSuggester(player,"y");
            }
            // add <name> <x> (y) (<z> (dim) (color))
            if (pos == 3) {
                if (Utl.isInt(args[1])) suggester.addAll(Helper.xyzSuggester(player,"z"));
                if (args.length == 4 && !Utl.isInt(args[3])) {
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                    suggester.addAll(Utl.dim.getList());
                }
                return suggester;
            }
            // add <name> <x> (y) <z> ((dim) (color))
            if (pos == 4) {
                if (Utl.isInt(args[3])) {
                    suggester.addAll(Utl.dim.getList());
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                    return suggester;
                }
                if (Utl.dim.checkValid(args[3])) {
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                }
                return suggester;
            }
            // add <name> <x> (y) <z> (dim) ((color))
            if (pos == 5) {
                if (Utl.isInt(args[3]) && Utl.dim.checkValid(args[4])) {
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                    return suggester;
                }
            }
            return suggester;
        }
        public static ArrayList<String> globalCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.global(player)) return suggester;
            if (pos == 0) {
                suggester.add("add");
                suggester.add("edit");
                suggester.add("delete");
                return suggester;
            }
            // global delete
            if (args[0].equalsIgnoreCase("delete")) {
                if (pos == 1) suggester.addAll(saved.getNames(GlobalDest.dests));
            }
            // saved add
            if (args[0].equalsIgnoreCase("add")) {
                return commandSuggester.addCMD(player,pos-1, Helper.trimStart(args,1));
            }
            // global edit
            if (args[0].equalsIgnoreCase("edit")) {
                return savedEdit(player,GlobalDest.dests,pos-1, Helper.trimStart(args,1));
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
                suggester.add("edit");
                suggester.add("delete");
                suggester.add("send");
                if (config.globalDESTs) suggester.add("global");
                return suggester;
            }
            if (args[0].equalsIgnoreCase("global"))
                suggester.addAll(globalCMD(player,pos-1, Helper.trimStart(args,1)));
            // if -r is attached, remove it and continue with the suggester
            if (args[0].contains("-r")) args[0] = args[0].replace("-r","");
            // saved delete
            if (args[0].equalsIgnoreCase("delete")) {
                if (pos == 1) suggester.addAll(saved.getNames(saved.getList(player)));
            }
            // saved send
            if (args[0].equalsIgnoreCase("send")) {
                // saved send (name)
                if (pos == 1) suggester.addAll(saved.getNames(saved.getList(player)));
                if (pos != 2) return suggester;
                // saved send name (player)
                for (Player s : Utl.getPlayers()) {
                    if (s.equals(player)) continue;
                    suggester.add(s.getName());
                }
                return suggester;
            }
            // saved add
            if (args[0].equalsIgnoreCase("add")) {
                return commandSuggester.addCMD(player,pos-1, Helper.trimStart(args,1));
            }
            // saved edit
            if (args[0].equalsIgnoreCase("edit")) {
                return savedEdit(player,saved.getList(player),pos-1, Helper.trimStart(args,1));
            }
            return suggester;
        }
        public static ArrayList<String> savedEdit(Player player,List<List<String>> list, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // saved edit (type)
            if (pos == 0) {
                suggester.add("location");
                suggester.add("color");
                suggester.add("name");
                suggester.add("order");
                return suggester;
            }
            // saved edit type (name)
            if (pos == 1) suggester.addAll(saved.getNames(list));
            // saved edit type name (<arg>)
            if (args[0].equalsIgnoreCase("location")) {
                if (pos == 2) {
                    suggester.addAll(Helper.xyzSuggester(player, "x"));
                    suggester.addAll(Utl.dim.getList());
                }
                if (pos == 3) suggester.addAll(Helper.xyzSuggester(player,"y"));
                if (pos == 4) {
                    suggester.addAll(Helper.xyzSuggester(player,"z"));
                    suggester.addAll(Utl.dim.getList());
                }
                if (pos == 5 && Utl.isInt(args[4])) {
                    suggester.addAll(Utl.dim.getList());
                }
                return suggester;
            }
            if (pos == 2) {
                if (args[0].equalsIgnoreCase("name")) {
                    suggester.add("name");
                    suggester.add(new saved.Dest(player,list,args[1]).getName());
                }
                if (args[0].equalsIgnoreCase("color")) {
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                    suggester.add("ffffff");
                }
                if (args[0].equalsIgnoreCase("order")) suggester.add(String.valueOf(new saved.Dest(player,list,args[1]).getOrder()));
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
                if (config.globalDESTs) suggester.add("global");
                suggester.addAll(Helper.xyzSuggester(player,"x"));
                return suggester;
            }
            // set <saved, x> ((name) (y))
            if (pos == 1) {
                if (args[0].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                    suggester.addAll(saved.getNames(saved.getList(player)));
                    return suggester;
                }
                if (args[0].equalsIgnoreCase("global") && config.globalDESTs) {
                    suggester.addAll(saved.getNames(GlobalDest.dests));
                    return suggester;
                }
                return Helper.xyzSuggester(player,"y");
            }
            // set <saved> <name> ((convert))
            // set <x> (y) (<z> (dim))
            if (pos == 2) {
                if (!Utl.isInt(args[1])) {
                    suggester.add("convert");
                    return suggester;
                }
                suggester.addAll(Utl.dim.getList());
                suggester.addAll(Helper.xyzSuggester(player,"z"));
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
            // send <player> (name) <x> (y) <z> (dimension) (color)
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
                suggester.addAll(Helper.xyzSuggester(player,"x"));
                suggester.add("name");
                return suggester;
            }
            // send <player> <saved> (<name>)
            // send <player> (name) (<x>)
            // send <player> <x> ((y))
            if (pos == 2) {
                if (args[1].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                    suggester.addAll(saved.getNames(saved.getList(player)));
                    return suggester;
                }
                if (!Utl.isInt(args[1])) {
                    return Helper.xyzSuggester(player,"x");
                }
                return Helper.xyzSuggester(player,"y");
            }
            // send <player> (name) <x> ((y))
            // send <player> <x> (y) (<z> (dimension))
            if (pos == 3) {
                if (!Utl.isInt(args[1])) {
                    return Helper.xyzSuggester(player,"y");
                }
                suggester.addAll(Utl.dim.getList());
                suggester.add("ffffff");
                suggester.addAll(CUtl.color.presetsSuggester(player));
                suggester.addAll(Helper.xyzSuggester(player,"z"));
                return suggester;
            }
            if (pos == 4) {
                // send <player> (name) <x> (y) (<z>, (dimension), (color))
                if (!Utl.isInt(args[1])) {
                    suggester.addAll(Utl.dim.getList());
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                    suggester.addAll(Helper.xyzSuggester(player,"z"));
                    return suggester;
                }
                // send <player> <x> (y) <z> ((dimension), (color))
                if (Utl.isInt(args[3])) {
                    suggester.addAll(Utl.dim.getList());
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                } else if (Utl.dim.getList().contains(args[3])) {
                    // send <player> <x> (y) (dimension) ((color))
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                }
                return suggester;
            }
            if (pos == 5) {
                // send <player> (name) <x> (y) <z> ((dimension), (color))
                if (!Utl.isInt(args[1])) {
                    if (Utl.isInt(args[4])) {
                        suggester.addAll(Utl.dim.getList());
                        suggester.add("ffffff");
                        suggester.addAll(CUtl.color.presetsSuggester(player));
                    } else if (Utl.dim.getList().contains(args[4])) {
                        suggester.add("ffffff");
                        suggester.addAll(CUtl.color.presetsSuggester(player));
                    }
                }
                // send <player> <x> (y) <z> (dimension) ((color))
                if (Utl.isInt(args[1]) && Utl.isInt(args[3]) && Utl.dim.getList().contains(args[4])){
                    suggester.add("ffffff");
                    suggester.addAll(CUtl.color.presetsSuggester(player));
                }
                return suggester;
            }
            // send <player> (name) <x> (y) <z> (dimension) ((color))
            if (pos == 6) {
                if (!Utl.isInt(args[1]) && Utl.isInt(args[4]) && Utl.dim.getList().contains(args[5])) {
                    suggester.addAll(Utl.dim.getList());
                    suggester.add("ffffff");
                    return suggester;
                }
            }
            return suggester;
        }
        public static ArrayList<String> trackCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // track (clear*|set|cancel*|accept*|deny*)
            if (pos == 0) {
                if (PlayerData.get.dest.getTracking(player)!=null) suggester.add("clear");
                suggester.add("set");
                if (DHUD.inbox.getAllMatches(player, DHUD.inbox.Type.track_pending)!=null) suggester.add("cancel");
                if (DHUD.inbox.getAllMatches(player, DHUD.inbox.Type.track_request)!=null) {
                    suggester.add("accept");
                    suggester.add("deny");
                }
            }
            if (pos == 1) {
                if (args[0].equalsIgnoreCase("set"))
                    suggester.addAll(Utl.getPlayersEx(player));
                if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("deny")) {
                    ArrayList<HashMap<String,Object>> matches = DHUD.inbox.getAllMatches(player, DHUD.inbox.Type.track_request);
                    if (matches==null) return suggester;
                    for (HashMap<String,Object> entry:matches) suggester.add((String) entry.get("player_name"));
                }
                if (args[0].equalsIgnoreCase("cancel")) {
                    ArrayList<HashMap<String,Object>> matches = DHUD.inbox.getAllMatches(player, DHUD.inbox.Type.track_pending);
                    if (matches==null) return suggester;
                    for (HashMap<String,Object> entry:matches) suggester.add((String) entry.get("player_name"));
                }
            }
            return suggester;
        }
    }
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("dest."+key, args);
    }
    public static Loc get(Player player) {
        Loc loc = PlayerData.get.dest.getDest(player);
        if (!loc.hasXYZ()) return new Loc();
        if ((boolean)PlayerData.get.dest.setting.get(player, Setting.ylevel) && loc.yExists())
            loc.setY(player.getBlockY());
        return loc;
    }
    public static boolean checkDist(Player player, Loc loc) {
        if ((boolean)PlayerData.get.dest.setting.get(player, Setting.autoclear))
            return Utl.vec.distance(new Loc(player).getVec(player),loc.getVec(player)) <= (double)PlayerData.get.dest.setting.get(player, Setting.autoclear_rad);
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
            player.sendMessage(CUtl.error("dest.already_clear"));
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
        boolean ac = (boolean)PlayerData.get.dest.setting.get(player, Setting.autoclear);
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
            player.sendMessage(CUtl.error("coordinates"));
            return;
        }
        if (loc.getDIM() == null) {
            player.sendMessage(CUtl.error("dimension"));
            return;
        }
        CTxT convertMsg = CTxT.of("");
        if (Utl.dim.canConvert(player.getDimension(),loc.getDIM()) && convert) {
            convertMsg.append(" ").append(lang("converted_badge").color('7').italic(true).hEvent(loc.getBadge()));
            loc.convertTo(player.getDimension());
        }
        if (checkDist(player,loc)) {
            player.sendMessage(CUtl.error("dest.at"));
            return;
        }
        silentSet(player, loc);
        player.sendMessage(CUtl.tag().append(lang("set",loc.getBadge())).append(convertMsg));
        player.sendMessage(setMSG(player));
    }
    public static void setSaved(Player player,List<List<String>> list, String name, boolean convert) {
        saved.Dest dest = new saved.Dest(player,list, name);
        if (dest.getDest() == null) {
            player.sendMessage(CUtl.error("dest.invalid"));
            return;
        }
        CTxT convertMsg = CTxT.of("");
        Loc loc = dest.getLoc();
        if (convert && Utl.dim.canConvert(player.getDimension(),loc.getDIM())) {
            convertMsg.append(" ").append(lang("converted_badge").color('7').italic(true).hEvent(loc.getBadge()));
            loc.convertTo(player.getDimension());
        }
        if (checkDist(player,loc)) {
            player.sendMessage(CUtl.error("dest.at"));
            return;
        }
        silentSet(player,loc);
        player.sendMessage(CUtl.tag().append(lang("set",
                CTxT.of("").append(loc.getBadge(dest.getName(),dest.getColor())).append(convertMsg))));
        player.sendMessage(setMSG(player));
    }
    public static class saved {
        private static final int PER_PAGE = 7;
        public static final int MAX_NAME = 16;
        public static class Dest {
            // dest helper
            private List<List<String>> list;
            private List<String> dest;
            private Player player;
            private int index;
            // need to figure out what type of list it is for saving the dest later?
            public final boolean global;
            public Dest(Player player, List<List<String>> list, List<String> entry) {
                this.global = list.equals(GlobalDest.dests);
                this.dest = entry;
                this.index = list.indexOf(entry);
                this.player = player;
                this.list = list;
            }
            public Dest(Player player,List<List<String>> list, String name) {
                this.global = list.equals(GlobalDest.dests);
                this.list = list;
                for (List<String> i: list)
                    if (name.equals(i.get(0))) {
                        this.dest = i;
                        this.index = list.indexOf(i);
                        this.player = player;
                        break;
                    }
            }
            private void save() {
                if (global) {
                    GlobalDest.dests = list;
                    GlobalDest.mapToFile();
                    // make the default entry move back to the bottom & set it to the internal list
                    list = GlobalDest.fileToMap();
                } else PlayerData.set.dest.setSaved(player,list);
            }
            public List<String> getDest() {
                return dest;
            }
            public String getName() {
                return dest==null?"":dest.get(0);
            }
            public void setName(String name) {
                name = name.replace(" ","");
                if (name.length() > MAX_NAME) name = name.substring(0,MAX_NAME);
                dest.set(0,name);
                list.set(index,dest);
                save();
            }
            public Loc getLoc() {
                return new Loc(dest.get(1));
            }
            public void setLoc(Loc loc) {
                if (!loc.hasXYZ() || loc.getDIM() == null) return;
                dest.set(1,loc.toArray());
                list.set(index,dest);
                save();
            }
            public String getColor() {
                return dest.get(2);
            }
            public void setColor(String color) {
                dest.set(2,CUtl.color.format(color,dest.get(2)));
                list.set(index,dest);
                save();
            }
            public int getOrder() {
                return index+1;
            }
            public void setOrder(int order) {
                list.remove(dest);
                order--;
                if (order < 0) order = 0;
                if (order > list.size()) order = list.size();
                list.add(order,dest);
                save();
                index = list.indexOf(dest);
            }
            public void add() {
                if (!list.contains(dest)) {
                    list.add(dest);
                    save();
                }
            }
            public void remove() {
                list.remove(dest);
                save();
            }
        }
        public static List<List<String>> getList(Player player) {
            // get the local destination list
            return PlayerData.get.dest.getSaved(player);
        }
        public static List<String> getNames(List<List<String>> list) {
            // get all name from a destination list
            List<String> all = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                // skip the last entry if global
                if (i==list.size()-1 && list==GlobalDest.dests) continue;
                all.add(list.get(i).get(0));
            }
            return all;
        }
        public static void add(boolean send, Player player,List<List<String>> list,String name, Loc loc, String color) {
            //get rid of spaces for now
            name = name.replace(" ","");
            if (list.size() >= config.DestMAX) {
                if (send) player.sendMessage(CUtl.error("dest.saved.max"));
                return;
            }
            if (getNames(list).contains(name)) {
                if (send) player.sendMessage(CUtl.error("dest.saved.add.duplicate",CTxT.of(name).color(CUtl.s())));
                return;
            }
            if (name.equalsIgnoreCase("saved")) {
                if (send) player.sendMessage(CUtl.error("dest.saved.not_allowed"));
                return;
            }
            if (name.length() > MAX_NAME) {
                if (send) player.sendMessage(CUtl.error("dest.saved.length",MAX_NAME));
                return;
            }
            if (!Utl.dim.checkValid(loc.getDIM())) {
                if (send) player.sendMessage(CUtl.error("dimension"));
                return;
            }
            if (!loc.hasXYZ()) {
                player.sendMessage(CUtl.error("coordinates"));
                return;
            }
            //if color is preset, get the preset color
            if (!(color == null) && color.contains("preset"))
                color = PlayerData.get.colorPresets(player).get(Integer.parseInt(color.substring(7))-1);
            color = CUtl.color.format(color,"#ffffff");
            Dest dest = new Dest(player,list,Arrays.asList(name,loc.toArray(),color));
            dest.add();
            if (send) {
                CTxT buttons = CTxT.of(" ");
                // there's no editing UI for global dests
                if (!dest.global) buttons.append(CUtl.CButton.dest.edit(1,"/dest saved edit " + name)).append(" ");
                buttons.append(CUtl.CButton.dest.set("/dest set saved "+name));
                if (Utl.dim.canConvert(player.getDimension(),loc.getDIM()))
                    buttons.append(" ").append(CUtl.CButton.dest.convert("/dest set saved "+name+" convert"));
                player.sendMessage(CUtl.tag().append(lang("saved.add",loc.getBadge(name,color).append(buttons))));
            }
        }
        public static void delete(boolean Return, Player player,List<List<String>> list, String name) {
            CUtl.PageHelper<List<String>> pageHelper = new CUtl.PageHelper<>(new ArrayList<>(list),PER_PAGE);
            Dest dest = new Dest(player,list,name);
            if (dest.getDest() == null) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            dest.remove();
            player.sendMessage(CUtl.tag().append(lang("saved.delete",dest.getLoc().getBadge(name,dest.getColor()))));
            if (Return) player.performCommand("dest saved "+pageHelper.getPageOf(dest.getDest()));
        }
        public static void editName(boolean Return, Player player,List<List<String>> list, String name, String newName) {
            Dest dest = new Dest(player,list,name);
            // remove the bad data
            if (dest.getDest()==null) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            if (getNames(list).contains(newName)) {
                player.sendMessage(CUtl.error("dest.saved.duplicate",lang("saved.name"),CTxT.of(newName).color(CUtl.s())));
                return;
            }
            if (newName.equalsIgnoreCase("saved")) {
                player.sendMessage(CUtl.error("dest.saved.not_allowed"));
                return;
            }
            if (newName.length() > 16) {
                player.sendMessage(CUtl.error("dest.saved.length", 16));
                return;
            }
            dest.setName(newName);
            player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.name"),CTxT.of(newName).color(CUtl.s()))));
            if (Return) player.performCommand("dest saved edit "+newName);
        }
        public static void editOrder(boolean Return, Player player,List<List<String>> list, String name, String orderNumber) {
            Dest dest = new Dest(player,list,name);
            // remove the bad data
            if (dest.getDest() == null) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            if (!Utl.isInt(orderNumber)) {
                player.sendMessage(CUtl.error("number"));
                return;
            }
            dest.setOrder(Integer.parseInt(orderNumber));
            player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.order"),CTxT.of(String.valueOf(dest.getOrder())).color(CUtl.s()))));
            if (Return) player.performCommand("dest saved edit "+name);
        }
        public static void editLocation(boolean Return, Player player,List<List<String>> list, String name, Loc loc) {
            Dest dest = new Dest(player,list,name);
            // remove the bad data
            if (dest.getDest() == null) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            if (!loc.hasXYZ()) {
                player.sendMessage(CUtl.error("coordinates"));
                return;
            }
            if (dest.getLoc().equals(loc)) {
                player.sendMessage(CUtl.error("dest.saved.duplicate",lang("saved.location"),CTxT.of(loc.getXYZ()).color(CUtl.s())));
                return;
            }
            if (loc.getDIM() == null) loc.setDIM(dest.getLoc().getDIM());
            dest.setLoc(loc);
            player.sendMessage(CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.location"),CTxT.of(loc.getBadge()))));
            if (Return) player.performCommand("dest saved edit "+name);
        }
        public static void setColor(Player player,List<List<String>> list, String stepSize, String name, String color, boolean Return) {
            Dest dest = new Dest(player,list,name);
            // remove the bad data
            if (dest.getDest() == null) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            //if color is preset, get the preset color
            if (color.contains("preset")) color = PlayerData.get.colorPresets(player).get(Integer.parseInt(color.substring(7))-1);
            if (!CUtl.color.checkValid(color,dest.getColor())) {
                player.sendMessage(CUtl.error("color"));
                return;
            }
            dest.setColor(CUtl.color.format(color));
            CTxT msg = CUtl.tag().append(lang("saved.set",CTxT.of(name).color(CUtl.s()),lang("saved.color"),CUtl.color.getBadge(color)));
            if (Return) colorUI(player,stepSize,name,msg);
            else player.sendMessage(msg);
        }
        public static void viewDestinationUI(boolean send, Player player, String name) {
            Dest dest = new Dest(player,getList(player),name);
            CUtl.PageHelper<List<String>> pageHelper = new CUtl.PageHelper<>(new ArrayList<>(getList(player)),PER_PAGE);
            if (dest.getDest() == null) {
                if (send) player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved.edit").color(Assets.mainColors.saved)).append(CUtl.LARGE).append("\n");
            msg
                    .append(" ").append(CTxT.of("#"+dest.getOrder()).btn(true).color(CUtl.p())
                            .hEvent(CUtl.TBtn("order.hover").color(CUtl.p()))
                            .cEvent(2,"/dest saved edit-r order "+name+" "))
                    .append(" ").append(CTxT.of(name).btn(true).color(CUtl.s())
                            .hEvent(CUtl.TBtn("dest.saved.name.hover").color(CUtl.s()))
                            .cEvent(2,"/dest saved edit-r name "+name+" "))
                    .append(" ").append(CTxT.of(Assets.symbols.square).btn(true).color(dest.getColor())
                            .hEvent(CUtl.TBtn("dest.saved.color.hover").color(dest.getColor()))
                            .cEvent(1, "/dest saved edit colorui "+name))
                    .append("\n\n ").append(CUtl.CButton.dest.edit(2,"/dest saved edit location " +name+ " ")).append(" ").append(CTxT.of(dest.getLoc().getBadge()))
                    .append("\n   ");
            //SEND BUTTON
            if (Utl.checkEnabled.send(player)) {
                msg.append(CUtl.TBtn("dest.send").btn(true).color(Assets.mainColors.send).cEvent(2,"/dest saved send "+name+" ")
                        .hEvent(CTxT.of("/dest saved send "+name+" <player>").color(Assets.mainColors.send)
                                .append("\n").append(CUtl.TBtn("dest.send.hover_saved")))).append(" ");
            }
            //SET BUTTON
            msg.append(CUtl.CButton.dest.set("/dest set saved " +name)).append(" ");
            //CONVERT
            if (Utl.dim.canConvert(player.getDimension(),dest.getLoc().getDIM()))
                msg.append(CUtl.CButton.dest.convert("/dest set saved " +name+ " convert"));
            msg.append("\n\n ")
                    //DELETE
                    .append(CUtl.TBtn("delete").btn(true).color('c').cEvent(2,"/dest saved delete-r "+name)
                            .hEvent(CUtl.TBtn("delete.hover_dest").color('c'))).append(" ")
                    //BACK
                    .append(CUtl.CButton.back("/dest saved "+pageHelper.getPageOf(dest.getDest())))
                    .append(CUtl.LARGE);
            player.sendMessage(msg);
        }
        public static void colorUI(Player player, String stepSize, String name, CTxT aboveMSG) {
            if (!getNames(getList(player)).contains(name)) return;
            Dest dest = new Dest(player,getList(player),name);
            String currentColor = dest.getColor();
            CTxT uiType = lang("ui.saved.color").color(currentColor);
            CTxT msg = CTxT.of("");
            if (aboveMSG != null) msg.append(aboveMSG).append("\n");
            msg.append(" ").append(uiType).append(CTxT.of("\n                               \n").strikethrough(true));
            CTxT back = CUtl.CButton.back("/dest saved edit "+name);
            CTxT presetsButton = CTxT.of("")
                    .append(CTxT.of("+").btn(true).color('a').cEvent(1,"/dest color preset_s add "+stepSize+" "+name)
                            .hEvent(CUtl.TBtn("color.presets.add.hover",CUtl.TBtn("color.presets.add.hover_2").color(currentColor))))
                    .append(CUtl.TBtn("color.presets").color(Assets.mainColors.presets)
                            .cEvent(1,"/dest color preset_s "+stepSize+" "+name).btn(true)
                            .hEvent(CUtl.TBtn("color.presets.hover",CUtl.TBtn("color.presets.hover_2").color(Assets.mainColors.presets))));
            CTxT customButton = CUtl.TBtn("color.custom").btn(true).color(Assets.mainColors.custom)
                    .cEvent(2,"/dest saved edit-r color "+name+" ")
                    .hEvent(CUtl.TBtn("color.custom.hover",CUtl.TBtn("color.custom.hover_2").color(Assets.mainColors.custom)));
            msg.append(" ")
                    .append(presetsButton).append(" ").append(customButton).append("\n\n")
                    .append(CUtl.color.colorEditor(currentColor,stepSize,"/dest color set_s "+stepSize+" "+name+" ","/dest saved edit colorui "+name+" big"))
                    .append("\n\n           ").append(back)
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void UI(Player player, int pg) {
            CTxT addB = CUtl.TBtn("dest.add").btn(true).color(Assets.mainColors.add).cEvent(2,"/dest add ").hEvent(
                    CTxT.of(Assets.cmdUsage.destAdd).color(Assets.mainColors.add).append("\n").append(CUtl.TBtn("dest.add.hover",
                            CUtl.TBtn("dest.add.hover_2").color(Assets.mainColors.add))));
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved").color(Assets.mainColors.saved)).append(CUtl.LARGE).append("\n");
            CUtl.PageHelper<List<String>> pageHelper = new CUtl.PageHelper<>(new ArrayList<>(getList(player)),PER_PAGE);
            int count = 0;
            for (List<String> entry: pageHelper.getPage(pg)) {
                count++;
                Dest dest = new Dest(player,getList(player),entry);
                msg.append(" ")//BADGE
                        .append(dest.getLoc().getBadge(dest.getName(),dest.getColor())).append(" ")
                        //EDIT
                        .append(CUtl.CButton.dest.edit(1,"/dest saved edit " + dest.getName())).append(" ")
                        //SET
                        .append(CUtl.CButton.dest.set("/dest set saved " + dest.getName()));
                //CONVERT
                if (Utl.dim.canConvert(player.getDimension(), dest.getLoc().getDIM()))
                    msg.append(" ").append(CUtl.CButton.dest.convert("/dest set saved " + dest.getName() + " convert"));
                msg.append("\n");
            }
            // no saved
            if (count == 0) {
                msg.append(" ").append(lang("saved.none")).append("\n ").append(lang("saved.none_2", addB)).append("\n");
            }
            msg.append("\n ");
            // add global button
            if (config.globalDESTs) msg.append(CTxT.of(Assets.symbols.global).btn(true).color(Assets.mainColors.global)
                    .hEvent(CUtl.TBtn("dest.saved.global.hover").color(Assets.mainColors.global))
                    .cEvent(1,"/dest saved global"));
            else msg.append(addB);
            msg
                    .append(" ").append(pageHelper.getNavButtons(pg,"/dest saved "))
                    .append(" ").append(CUtl.CButton.back("/dest"))
                    .append(CUtl.LARGE);
            player.sendMessage(msg);
        }
        public static void globalUI(Player player, int pg) {
            CTxT msg = CTxT.of(" ");
            msg.append(lang("ui.saved.global").color(Assets.mainColors.global)).append(CUtl.LARGE).append("\n");
            CUtl.PageHelper<List<String>> pageHelper = new CUtl.PageHelper<>(new ArrayList<>(GlobalDest.dests), PER_PAGE);
            int count = 0;
            for (List<String> entry : pageHelper.getPage(pg)) {
                count++;
                // skip the last one because dummy entry
                if (count==pageHelper.getList().size()) continue;
                Dest dest = new Dest(player, GlobalDest.dests, entry);
                msg.append(" ")//BADGE
                        .append(dest.getLoc().getBadge(dest.getName(), dest.getColor())).append(" ")
                        //SET
                        .append(CUtl.CButton.dest.set("/dest set global " + dest.getName()));
                //CONVERT
                if (Utl.dim.canConvert(player.getDimension(), dest.getLoc().getDIM()))
                    msg.append(" ").append(CUtl.CButton.dest.convert("/dest set global " + dest.getName() + " convert"));
                msg.append("\n");
            }
            // no saved
            if (count == 1) {
                msg.append(" ").append(lang("saved.global.none")).append("\n");
            }
            msg.append("\n ").append(CTxT.of(Assets.symbols.local).btn(true).color(Assets.mainColors.saved)
                            .hEvent(CUtl.TBtn("dest.saved.local.hover").color(Assets.mainColors.saved))
                            .cEvent(1, "/dest saved"))
                    .append(" ").append(pageHelper.getNavButtons(pg, "/dest saved global "))
                    .append(" ").append(CUtl.CButton.back("/dest"))
                    .append(CUtl.LARGE);
            player.sendMessage(msg);
        }
    }
    public static class lastdeath {
        private static final int PER_PAGE = 4;
        public static void add(Player player, Loc loc) {
            ArrayList<String> deaths = PlayerData.get.dest.getLastdeaths(player);
            if (Utl.dim.checkValid(loc.getDIM())) {
                //add to the top of the list
                deaths.add(0,loc.toArray());
                // WHILE more than max, remove the last entry (to deal with the size changing to be smaller in the future)
                while (deaths.size() > config.LastDeathMAX) deaths.remove(deaths.size()-1);
            }
            PlayerData.set.dest.setLastdeaths(player,deaths);
        }
        public static void UI(Player player,int pg,CTxT abovemsg) {
            CUtl.PageHelper<String> pageHelper = new CUtl.PageHelper<>(PlayerData.get.dest.getLastdeaths(player),PER_PAGE);
            // rewrite
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.lastdeath").color(Assets.mainColors.lastdeath)).append(CUtl.LINE_35).append("\n ");
            for (String s:pageHelper.getPage(pg)) {
                Loc loc = new Loc(s);
                String dim = loc.getDIM();
                msg.append(loc.getBadge()).append("\n  ")
                        .append(CUtl.CButton.dest.add("/dest add "+Utl.dim.getName(dim).toLowerCase()+"_death "+loc.getXYZ()+" "+dim+" "+Utl.dim.getHEX(dim).substring(1)))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+loc.getDIM()));
                if (Utl.dim.canConvert(player.getDimension(),dim)) msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+dim+" convert"));
                msg.append("\n ");
            }
            if (pageHelper.getList().size()==0)
                msg.append(lang("lastdeath.no_deaths").color('c')).append("\n");
            msg.append("\n ");
            //button nav if there are more lastdeaths than what can fit on one page
            if (pageHelper.getList().size() > PER_PAGE)
                    msg.append(pageHelper.getNavButtons(pg,"/dest lastdeath ")).append(" ");
            msg.append(CUtl.CButton.back("/dest")).append(CUtl.LINE_35);
            player.sendMessage(msg);
        }
    }
    public static class social {
        public static void send(Player player, String sendPLayer, Loc loc, String name,String color) {
            // remove bad data
            Player target = Player.of(sendPLayer);
            // cooldown check
            if (PlayerData.get.socialCooldown(player) != null) {
                player.sendMessage(CUtl.error("dest.social.cooldown"));
                return;
            }
            if (target == null) {
                player.sendMessage(CUtl.error("player", CTxT.of(sendPLayer).color(CUtl.s())));
                return;
            }
            if (target == player) {
                player.sendMessage(CUtl.error("dest.send.alone"));
                return;
            }
            // they don't have sending enabled
            if (!(boolean)PlayerData.get.dest.setting.get(target, Setting.features__send)) {
                player.sendMessage(CUtl.error("dest.send.disabled_player",CTxT.of(target.getName()).color(CUtl.s())));
                return;
            }
            // custom name too long
            if (name != null && name.length() > saved.MAX_NAME) {
                player.sendMessage(CUtl.error("dest.saved.length", saved.MAX_NAME));
                return;
            }
            // if LOC is null it's a saved destination
            if (loc == null) {
                if (!saved.getNames(saved.getList(player)).contains(name)) {
                    player.sendMessage(CUtl.error("dest.invalid"));
                    return;
                }
                saved.Dest dest = new saved.Dest(player,saved.getList(player),name);
                loc = dest.getLoc();
                color = dest.getColor();
            }
            if (!loc.hasXYZ()) {
                player.sendMessage(CUtl.error("coordinates"));
                return;
            }
            if (!Utl.dim.checkValid(loc.getDIM())) {
                player.sendMessage(CUtl.error("dimension"));
                return;
            }
            // add the cooldown
            PlayerData.set.socialCooldown(player,config.socialCooldown.doubleValue());
            if (color == null) color = "#ffffff";
            //if color is preset, get the preset color
            if (color.contains("preset")) color = PlayerData.get.colorPresets(player).get(Integer.parseInt(color.substring(7))-1);
            // get rid of the hashtag for error fixing
            color = CUtl.color.format(color).substring(1);
            player.sendMessage(CUtl.tag().append(lang("send",CTxT.of(target.getName()).color(CUtl.s()),
                    CTxT.of("\n ").append(getSendBadge(name,loc,color)))));
            target.sendMessage(CUtl.tag().append(lang("send_player",CTxT.of(player.getName()).color(CUtl.s()),getSendTxt(target,name,loc,color))));
            DHUD.inbox.addDest(target,player,999,name,loc,color);
        }
        public static CTxT getSendBadge(String name, Loc loc, String color) {
            if (name==null) return loc.getBadge();
            else return loc.getBadge(name,color);
        }
        public static CTxT getSendTxt(Player player, String name, Loc loc, String color) {
            CTxT txt = CTxT.of("").append(getSendBadge(name,loc,color)).append(" ");
            name = name==null?lang("send.change_name").toString():name;
            // if color is white, empty string
            color = color.equals("ffffff")?"":" "+color;
            // ADD
            if (Utl.checkEnabled.saving(player))
                txt.append(CUtl.CButton.dest.add("/dest saved add "+name+" "+loc.getXYZ()+" "+loc.getDIM()+color)).append(" ");
            // SET
            txt.append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+loc.getDIM())).append(" ");
            // CONVERT
            if (Utl.dim.canConvert(player.getDimension(),loc.getDIM()))
                txt.append(CUtl.CButton.dest.convert("/dest set " +loc.getXYZ()+" "+loc.getDIM()+" convert")).append(" ");
            return txt;
        }
        public static class track {
            public enum ProcessType {
                accept,
                deny,
                cancel;
            }
            public static Player getTarget(Player player) {
                String track = PlayerData.get.dest.getTracking(player);
                if (track == null) return null;
                return Player.of(track);
            }
            public static void clear(Player player, CTxT reason) {
                CTxT msg = CUtl.tag().append(lang("track.clear"));
                if (PlayerData.get.dest.getTracking(player) == null) {
                    player.sendMessage(CUtl.error("dest.track.cleared"));
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
                //remove everything to do with tracking in the one time map
                for (String s: PlayerData.oneTimeMap.get(player).keySet())
                    if (s.contains("tracking")) PlayerData.setOneTime(player,s,null);
                //clear the player
                PlayerData.set.dest.setTracking(player,null);
            }
            public static void set(Player player, Player target, boolean send) {
                if (config.online) PlayerData.set.dest.setTracking(player,target.getUUID());
                else PlayerData.set.dest.setTracking(player,target.getName());
                if (!send) return;
                player.sendMessage(CUtl.tag().append(lang("track.set",CTxT.of(target.getName()).color(CUtl.s()))));
                target.sendMessage(CUtl.tag()
                        .append(lang("track.accept", CTxT.of(player.getName()).color(CUtl.s())))
                        .append(" ").append(CUtl.CButton.dest.settings()));
            }
            public static void initialize(Player player, String tracker) {
                Player target = Player.of(tracker);
                // cooldown check
                if (PlayerData.get.socialCooldown(player) != null) {
                    player.sendMessage(CUtl.error("dest.social.cooldown"));
                    return;
                }
                if (target == null) {
                    player.sendMessage(CUtl.error("player",CTxT.of(tracker).color(CUtl.s())));
                    return;
                }
                if (target == player) {
                    player.sendMessage(CUtl.error("dest.track.alone"));
                    return;
                }
                if (!(boolean)PlayerData.get.dest.setting.get(target, Setting.features__track)) {
                    player.sendMessage(CUtl.error("dest.track.disabled",CTxT.of(target.getName()).color(CUtl.s())));
                    return;
                }
                // tracking request already pending
                if (DHUD.inbox.search(player, DHUD.inbox.Type.track_pending,"player_name",tracker)!=null) {
                    player.sendMessage(CUtl.error("dest.track.duplicate",CTxT.of(target.getName()).color(CUtl.s())));
                    return;
                }
                // make sure the player isn't already tracking the player
                if (getTarget(player) != null && Objects.equals(getTarget(player), target)) {
                    player.sendMessage(CUtl.error("dest.track.already_tracking",CTxT.of(target.getName()).color(CUtl.s())));
                    return;
                }
                // add the cooldown
                PlayerData.set.socialCooldown(player,config.socialCooldown.doubleValue());
                // instant mode
                if (Setting.TrackingRequestMode.valueOf((String) PlayerData.get.dest.setting.get(target, Setting.features__track_request_mode)).equals(Setting.TrackingRequestMode.instant)) {
                    set(player,target,true);
                    return;
                }
                DHUD.inbox.addTracking(target,player,300);
                player.sendMessage(CUtl.tag().append(lang("track",CTxT.of(target.getName()).color(CUtl.s())))
                        .append("\n ").append(lang("track_expire", 300).color('7').italic(true)));
                target.sendMessage(CUtl.tag().append(lang("track_player",CTxT.of(player.getName()).color(CUtl.s()))).append("\n ")
                        .append(CUtl.TBtn("accept").btn(true).color('a').cEvent(1,"/dest track accept "+player.getName())
                                .hEvent(CUtl.TBtn("accept.hover"))).append(" ")
                        .append(CUtl.TBtn("deny").btn(true).color('c').cEvent(1,"/dest track deny "+player.getName())
                                .hEvent(CUtl.TBtn("deny.hover"))));
            }
            public static void process(Player player, String tracker, ProcessType type, boolean Return) {
                // processing both accepting and denying @ same time because the code is so similar
                // removing bad data woo
                Player target = Player.of(tracker);
                // if player in questions is null
                if (target == null) {
                    player.sendMessage(CUtl.error("player",CTxT.of(tracker).color(CUtl.s())));
                    return;
                }
                if (player == target) {
                    player.sendMessage(CUtl.error("alone"));
                    return;
                }
                // get the id from the player inbox
                HashMap<String, Object> entry = DHUD.inbox.search(player, DHUD.inbox.Type.track_request,"player_name", tracker);
                // tracK_request if accept or deny, track_pending if canceling
                if (type.equals(ProcessType.cancel)) entry = DHUD.inbox.search(player, DHUD.inbox.Type.track_pending,"player_name", tracker);
                // entry doesn't exist
                if (entry == null) {
                    player.sendMessage(CUtl.error("dest.track.none",CTxT.of(target.getName()).color(CUtl.s())));
                    return;
                }
                String ID = (String) entry.get("id");
                // the IDs don't match - SYNC ERROR
                if (DHUD.inbox.search(target, null,"id",ID)==null) {
                    DHUD.inbox.removeEntry(player,entry);
                    player.sendMessage(CUtl.tag().append("SYNC ERROR - REPORT IT! (ID-MISMATCH)"));
                    return;
                }
                // if the target has tracking turned off - SYNC ERROR
                if (!(boolean)PlayerData.get.dest.setting.get(target, Setting.features__track)) {
                    DHUD.inbox.removeEntry(player,entry);
                    player.sendMessage(CUtl.tag().append("SYNC ERROR - REPORT IT! (TARGET-TRACK-OFF)"));
                    return;
                }
                // remove from both inboxes
                DHUD.inbox.delete(player,ID,false);
                DHUD.inbox.delete(target,ID,false);
                //different message based on the type
                if (type.equals(ProcessType.accept)) {
                    set(target,player,true);
                } else if (type.equals(ProcessType.deny)) {
                    target.sendMessage(CUtl.tag().append(lang("track.denied",CTxT.of(player.getName()).color(CUtl.s()))));
                    player.sendMessage(CUtl.tag().append(lang("track.deny",CTxT.of(target.getName()).color(CUtl.s()))));
                } else if (type.equals(ProcessType.cancel)) {
                    player.sendMessage(CUtl.tag().append(lang("track.cancel",CTxT.of(target.getName()).color(CUtl.s()))));
                    target.sendMessage(CUtl.tag().append(lang("track.canceled",CTxT.of(player.getName()).color(CUtl.s()))));
                }
                if (Return) player.performCommand("dhud inbox");
            }
        }
    }
    public static class settings {
        public static Object getConfig(Setting type) {
            Object output = false;
            switch (type) {
                case autoclear -> output = config.dest.AutoClear;
                case autoclear_rad -> output = config.dest.AutoClearRad;
                case autoconvert -> output = config.dest.AutoConvert;
                case ylevel -> output = config.dest.YLevel;
                case particles__dest -> output = config.dest.particles.Dest;
                case particles__dest_color -> output = config.dest.particles.DestColor;
                case particles__line -> output = config.dest.particles.Line;
                case particles__line_color -> output = config.dest.particles.LineColor;
                case particles__tracking -> output = config.dest.particles.Tracking;
                case particles__tracking_color -> output= config.dest.particles.TrackingColor;
                case features__send -> output= config.dest.Send;
                case features__track -> output= config.dest.Track;
                case features__track_request_mode -> output= config.dest.TrackingRequestMode;
                case features__lastdeath -> output= config.dest.Lastdeath;
            }
            return output;
        }
        public static void reset(Player player, Setting type, boolean Return) {
            if (type.equals(Setting.none)) {
                for (Setting s: Setting.values())
                    PlayerData.set.dest.setting.set(player,s,getConfig(s));
            } else {
                PlayerData.set.dest.setting.set(player,type,getConfig(type));
            }
            if (type.equals(Setting.autoclear))
                PlayerData.set.dest.setting.set(player, Setting.autoclear_rad,getConfig(Setting.autoclear_rad));
            if (Setting.colors().contains(Setting.get(type+"_color")))
                PlayerData.set.dest.setting.set(player, Setting.get(type+"_color"),getConfig(Setting.get(type+"_color")));
            if (type.equals(Setting.features__track))
                PlayerData.set.dest.setting.set(player, Setting.features__track_request_mode,getConfig(Setting.features__track_request_mode));
            CTxT typ = CTxT.of(lang("settings."+type).toString().toUpperCase()).color('c');
            if (type.equals(Setting.none)) typ = CTxT.of(CUtl.TBtn("all").toString().toUpperCase()).color('c');
            CTxT msg = CUtl.tag().append(lang("settings.reset",typ));
            if (Return) UI(player, msg);
        }
        public static void change(Player player, Setting type, String setting, boolean Return) {
            boolean state = setting.equals("on");
            CTxT stateTxT = CUtl.TBtn(state?"on":"off").color(state?'a':'c');
            setting = setting.toLowerCase();
            CTxT setTxT = CTxT.of("");
            if (type.equals(Setting.autoclear_rad)) {
                if (!Utl.isInt(setting)) {
                    player.sendMessage(CUtl.error("number"));
                    return;
                }
                int i = Math.max(Math.min(Integer.parseInt(setting),15),1);
                PlayerData.set.dest.setting.set(player, Setting.autoclear_rad,i);
                setTxT.append(CTxT.of(String.valueOf(i)).color((boolean) PlayerData.get.dest.setting.get(player, Setting.autoclear)?'a':'c'));
            }
            if (type.equals(Setting.features__track_request_mode)) {
                PlayerData.set.dest.setting.set(player, type, Setting.TrackingRequestMode.valueOf(setting));
                setTxT.append(lang("settings."+type+"." + Setting.TrackingRequestMode.valueOf(setting)).color(CUtl.s()));
            }
            if (Setting.colors().contains(type)) {
                colorUI(player,setting,type,null);
                return;
            }
            if (Setting.base().contains(type)) {
                PlayerData.set.dest.setting.set(player,type,state);
                setTxT.append(stateTxT);
            }
            CTxT msg = CUtl.tag().append(lang("settings."+type+".set",setTxT));
            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        public static boolean canBeReset(Player player, Setting type) {
            boolean output = false;
            if (type.equals(Setting.none)) return false;
            if (PlayerData.get.dest.setting.get(player,type) != getConfig(type)) output = true;
            if (type.equals(Setting.autoclear))
                if (((Double)PlayerData.get.dest.setting.get(player, Setting.autoclear_rad)).intValue() != (int)getConfig(Setting.autoclear_rad)) output = true;
            if (type.equals(Setting.features__track))
                if (!PlayerData.get.dest.setting.get(player, Setting.features__track_request_mode).equals(getConfig(Setting.features__track_request_mode))) output = true;
            if (Setting.colors().contains(Setting.get(type+"_color")))
                if (!PlayerData.get.dest.setting.get(player, Setting.get(type+"_color")).equals(getConfig(Setting.get(type+"_color")))) output = true;
            return output;
        }
        public static CTxT resetB(Player player, Setting type) {
            CTxT msg = CTxT.of(Assets.symbols.x).btn(true).color('7');
            if (canBeReset(player,type)) {
                msg.color('c').cEvent(1, "/dest settings reset " + type)
                        .hEvent(CUtl.TBtn("reset.hover_settings",lang("settings."+type).color('c')));
            }
            return msg;
        }
        public static CTxT getButtons(Player player, Setting type) {
            boolean state = (boolean) PlayerData.get.dest.setting.get(player,type);
            CTxT button = CTxT.of("");
            if (type.equals(Setting.none)) return button;
            button.append(CUtl.toggleBtn(state,"/dest settings "+type+" ")).append(" ");
            if (type.equals(Setting.autoclear)) {
                //ok so the numbers are all doubles so cast to a double and get the int value to format correctly
                button.append(CTxT.of(String.valueOf(((Double) PlayerData.get.dest.setting.get(player, Setting.get(type+"_rad"))).intValue())).btn(true)
                        .color(state?'a':'c').cEvent(2,"/dest settings "+type+"_rad ")
                        .hEvent(lang("settings."+type+"_rad.hover").append("\n").append(lang("settings."+type+"_rad.hover_2").color('7'))));
            }
            if (type.equals(Setting.features__track)) {
                Setting modeType = Setting.features__track_request_mode;
                Setting.TrackingRequestMode mode = Setting.TrackingRequestMode.valueOf((String) PlayerData.get.dest.setting.get(player,modeType));
                Setting.TrackingRequestMode nextMode = mode.next();
                button.append(CTxT.of(getSymbol(mode.toString())).btn(true).color(CUtl.s())
                        .cEvent(1,"/dest settings "+modeType+" "+nextMode)
                        .hEvent(lang("settings."+modeType+".current",lang("settings."+modeType+"."+mode).color(CUtl.s())).append("\n")
                                .append(lang("settings."+modeType+"."+mode+".info").color('7')).append("\n\n")
                                .append(lang("settings."+modeType+".hover",lang("settings."+modeType+"."+nextMode).color(CUtl.s())))));
            }
            if (Setting.colors().contains(Setting.get(type+"_color"))) {
                String color = (String) PlayerData.get.dest.setting.get(player, Setting.get( type+"_color"));
                button.append(CTxT.of(Assets.symbols.pencil).btn(true).color(color)
                        .cEvent(1,"/dest settings "+type+"_color normal")
                        .hEvent(lang("settings.particles.color.hover",lang("settings.particles.color.hover_2").color(color))));
            }
            return button;
        }
        public static String getSymbol(String string) {
            if (string.equals(Setting.TrackingRequestMode.request.toString())) {
                return Assets.symbols.envelope;
            }
            if (string.equals(Setting.TrackingRequestMode.instant.toString())) {
                return Assets.symbols.lighting_bolt;
            }
            return Assets.symbols.x;
        }
        public static void colorUI(Player player, String setting, Setting type, CTxT aboveMSG) {
            if (!Setting.colors().contains(type)) return;
            String currentColor = (String) PlayerData.get.dest.setting.get(player,type);
            CTxT uiType = lang("settings."+type.toString().substring(0,type.toString().length()-6));
            CTxT msg = CTxT.of("");
            if (aboveMSG != null) msg.append(aboveMSG).append("\n");
            msg.append(" ").append(uiType.color(currentColor))
                    .append(CTxT.of("\n                               \n").strikethrough(true));
            CTxT back = CUtl.CButton.back("/dest settings");
            CTxT presetsButton = CTxT.of("")
                    .append(CTxT.of("+").btn(true).color('a').cEvent(1,"/dest color preset add "+setting+" "+type)
                            .hEvent(CUtl.TBtn("color.presets.add.hover",CUtl.TBtn("color.presets.add.hover_2").color(currentColor))))
                    .append(CUtl.TBtn("color.presets").color(Assets.mainColors.presets)
                            .cEvent(1,"/dest color preset "+setting+" "+type).btn(true)
                            .hEvent(CUtl.TBtn("color.presets.hover",CUtl.TBtn("color.presets.hover_2").color(Assets.mainColors.presets))));
            CTxT customButton = CUtl.TBtn("color.custom").btn(true).color(Assets.mainColors.custom)
                    .cEvent(2,"/dest color set "+setting+" "+type+" ")
                    .hEvent(CUtl.TBtn("color.custom.hover",CUtl.TBtn("color.custom.hover_2").color(Assets.mainColors.custom)));
            msg.append(" ")
                    .append(presetsButton).append(" ").append(customButton).append("\n\n")
                    .append(CUtl.color.colorEditor(currentColor,setting,"/dest color set "+setting+" "+type+" ","/dest settings "+type+" big"))
                    .append("\n\n           ").append(back)
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static void setColor(Player player, String setting, Setting type, String color, boolean Return) {
            CTxT uiType = lang("settings."+type.toString().substring(0,type.toString().length()-6));
            if (CUtl.color.checkValid(color,(String)PlayerData.get.dest.setting.get(player,type))) {
                PlayerData.set.dest.setting.set(player,type,CUtl.color.format(color));
            } else {
                player.sendMessage(CUtl.error("color"));
                return;
            }
            CTxT msg = CUtl.tag().append(lang("settings.particles.color.set",uiType.toString().toUpperCase(),CUtl.color.getBadge(color)));
            if (Return) colorUI(player,setting,type,msg);
            else player.sendMessage(msg);
        }
        public static void UI(Player player, CTxT abovemsg) {
            CTxT msg = CTxT.of("");
            if (abovemsg != null) msg.append(abovemsg).append("\n");
            msg.append(" ").append(lang("ui.settings").color(Assets.mainColors.setting)).append(CTxT.of("\n                                \n").strikethrough(true));
            //DEST
            msg.append(" ").append(lang("ui.dest").color(CUtl.p())).append(":\n  ");
            msg     //AUTOCLEAR
                    .append(resetB(player, Setting.autoclear)).append(" ")
                    .append(lang("settings."+ Setting.autoclear).hEvent(lang("settings."+ Setting.autoclear+".info")
                            .append("\n").append(lang("settings."+ Setting.autoclear+".info_2").italic(true).color('7')))).append(": ")
                    .append(getButtons(player, Setting.autoclear)).append("\n  ");
            msg     //AUTOCONVERT
                    .append(resetB(player, Setting.autoconvert)).append(" ")
                    .append(lang("settings."+ Setting.autoconvert).hEvent(lang("settings."+ Setting.autoconvert+".info")
                            .append("\n").append(lang("settings."+ Setting.autoconvert+".info_2").italic(true).color('7')))).append(": ")
                    .append(getButtons(player, Setting.autoconvert)).append("\n  ");
            msg     //YLEVEL
                    .append(resetB(player, Setting.ylevel)).append(" ")
                    .append(lang("settings."+ Setting.ylevel).hEvent(lang("settings."+ Setting.ylevel+".info",
                            lang("settings."+ Setting.ylevel+".info_2").color(CUtl.s()),
                            lang("settings."+ Setting.ylevel+".info_2").color(CUtl.s())))).append(": ")
                    .append(getButtons(player, Setting.ylevel)).append("\n ");
            //PARTICLES
            msg.append(lang("ui.settings.particles").color(CUtl.p())).append(":\n  ");
            msg     //DESTINATION
                    .append(resetB(player, Setting.particles__dest)).append(" ")
                    .append(lang("settings."+ Setting.particles__dest).hEvent(lang("settings."+ Setting.particles__dest+".info"))).append(": ")
                    .append(getButtons(player, Setting.particles__dest)).append("\n  ");
            msg     //LINE
                    .append(resetB(player, Setting.particles__line)).append(" ")
                    .append(lang("settings."+ Setting.particles__line).hEvent(lang("settings."+ Setting.particles__line+".info"))).append(": ")
                    .append(getButtons(player, Setting.particles__line)).append("\n  ");
            msg     //TRACK
                    .append(resetB(player, Setting.particles__tracking)).append(" ")
                    .append(lang("settings."+ Setting.particles__tracking).hEvent(lang("settings."+ Setting.particles__tracking+".info"))).append(": ")
                    .append(getButtons(player, Setting.particles__tracking)).append("\n ");
            if (config.social || config.LastDeathSaving) {
                //FEATURES
                msg.append(lang("ui.settings.features").color(CUtl.p())).append(":\n  ");
                if (config.social) {
                    msg     //SEND
                            .append(resetB(player, Setting.features__send)).append(" ")
                            .append(lang("settings."+ Setting.features__send).hEvent(lang("settings."+ Setting.features__send +".info",
                                    lang("settings."+ Setting.features__send +".info_1").color(CUtl.s()),
                                    lang("settings."+ Setting.features__send +".info_2").color(CUtl.s()),
                                    lang("settings."+ Setting.features__send +".info_3").color(CUtl.s())))).append(": ")
                            .append(getButtons(player, Setting.features__send)).append("\n  ");
                    msg     //TRACK
                            .append(resetB(player, Setting.features__track)).append(" ")
                            .append(lang("settings."+ Setting.features__track).hEvent(lang("settings."+ Setting.features__track +".info"))).append(": ")
                            .append(getButtons(player, Setting.features__track)).append("\n  ");
                }
                if (config.LastDeathSaving) {
                    msg     //LASTDEATH
                            .append(resetB(player, Setting.features__lastdeath)).append(" ")
                            .append(lang("settings."+ Setting.features__lastdeath).hEvent(lang("settings."+ Setting.features__lastdeath +".info"))).append(": ")
                            .append(getButtons(player, Setting.features__lastdeath)).append("\n ");
                }
            }
            CTxT reset = CUtl.TBtn("reset").btn(true).color('7');
            boolean resetOn = false;
            for (Setting t: Setting.base()) {
                if (resetOn) break;
                if (!config.LastDeathSaving && t.equals(Setting.features__lastdeath)) continue;
                if (!config.social && (t.equals(Setting.features__send) || t.equals(Setting.features__track))) continue;
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
        boolean line2Free = !((boolean) PlayerData.get.dest.setting.get(player, Setting.features__lastdeath) && config.LastDeathSaving);
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
        msg.append(CUtl.CButton.back("/dhud")).append(CTxT.of("\n                                  ").strikethrough(true));
        player.sendMessage(msg);
    }
}