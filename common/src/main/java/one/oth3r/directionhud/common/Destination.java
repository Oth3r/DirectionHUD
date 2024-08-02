package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.Data;
import one.oth3r.directionhud.common.files.GlobalDest;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;
import one.oth3r.directionhud.common.utils.Helper.Num;
import one.oth3r.directionhud.common.utils.Helper.ListPage;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import one.oth3r.directionhud.common.Destination.Setting.*;

import java.util.*;
import java.util.stream.Collectors;

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
        public static ArrayList<Setting> bool() {
            return base();
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
            list.add(particles__tracking);
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
            public String getSymbol() {
                if (this.equals(request)) return Assets.symbols.envelope;
                else return Assets.symbols.lighting_bolt;
            }
        }
    }
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (!Helper.checkEnabled(player).destination()) return;
            if (args.length == 0) {
                UI(player);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Helper.trimStart(args, 1);
            switch (type) {
                case "set" -> dest.setCMDExecutor(player, trimmedArgs);
                case "clear" -> dest.clear(player, 1);
                case "saved" -> saved.CMDExecutor(player, trimmedArgs);
                case "add" -> saved.addCMDExecutor(player, trimmedArgs, false);
                case "global" -> saved.globalCMDExecutor(player,trimmedArgs);
                case "lastdeath" -> lastdeath.CMDExecutor(player, trimmedArgs);
                case "settings" -> settings.CMDExecutor(player, trimmedArgs);
                case "send" -> social.send.CMDExecutor(player, trimmedArgs);
                case "track" -> social.track.CMDExecutor(player, trimmedArgs);
                default -> player.sendMessage(CUtl.error("command"));
            }
        }

    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Helper.checkEnabled(player).destination()) return suggester;
            if (pos == 1) {
                if (Helper.checkEnabled(player).lastdeath()) suggester.add("lastdeath");
                if (Helper.checkEnabled(player).saving()) {
                    suggester.add("add");
                    suggester.add("saved");
                }
                if (Helper.checkEnabled(player).global()){
                    suggester.add("global");
                }
                suggester.add("set");
                if (dest.get(player).hasXYZ()) suggester.add("clear");
                suggester.add("settings");
                if (Helper.checkEnabled(player).send()) suggester.add("send");
                if (Helper.checkEnabled(player).track()) suggester.add("track");
            }
            if (pos > 1) {
                String command = args[0].toLowerCase();
                String[] trimmedArgs = Helper.trimStart(args, 1);
                int fixedPos = pos - 2;
                switch (command) {
                    case "saved" -> suggester.addAll(saved.CMDSuggester(player,fixedPos,trimmedArgs));
                    case "add" -> suggester.addAll(saved.addCMDSuggester(player,fixedPos,trimmedArgs));
                    case "global" -> suggester.addAll(saved.globalCMDSuggester(player,fixedPos,trimmedArgs));
                    case "settings" -> suggester.addAll(settings.CMDSuggester(player, fixedPos,trimmedArgs));
                    case "color" -> {
                        if (fixedPos == 3 && trimmedArgs[0].equals("set")) suggester.addAll(Suggester.colors(player,Suggester.getCurrent(trimmedArgs,fixedPos),true));
                    }
                    case "set" -> suggester.addAll(dest.setCMDSuggester(player,fixedPos,trimmedArgs));
                    case "send" -> suggester.addAll(social.send.CMDSuggester(player,fixedPos,trimmedArgs));
                    case "track" -> suggester.addAll(social.track.CMDSuggester(player,fixedPos,trimmedArgs));
                }
            }
            return suggester;
        }
    }

    public static final Lang LANG = new Lang("destination.");

    public static class dest {
        public static CTxT SET_BUTTON() {
            return LANG.btn("set").btn(true).color(Assets.mainColors.set).cEvent(2,"/dest set ").hEvent(
                    CTxT.of(Assets.cmdUsage.destSet).color(Assets.mainColors.set).append("\n").append(LANG.hover("set_self")));
        }
        public static CTxT CLEAR_BUTTON(Player player) {
            boolean o = Destination.dest.get(player).hasXYZ();
            return CTxT.of(Assets.symbols.x).btn(true).color(o?'c':'7').cEvent(o?1:0,"/dest clear").hEvent(
                    CTxT.of(Assets.cmdUsage.destClear).color(o?'c':'7').append("\n").append(LANG.hover("clear")));
        }
        public static Dest get(Player player) {
            // get from cache because called in a loop
            Dest dest = player.getPCache().getDEST().getDestination();
            if (!dest.hasXYZ()) return new Dest();
            if (player.getPCache().getDEST().getDestSettings().getYlevel()) dest.setY(player.getBlockY());
            return new Dest(dest);
        }
        public static boolean inAutoClearRadius(Player player, Loc loc) {
            if ((boolean) player.getPData().getDEST().getSetting(Setting.autoclear))
                return Utl.vec.distance(new Loc(player).getVec(player),loc.getVec(player)) <= (int) player.getPData().getDEST().getSetting(Setting.autoclear_rad);
            else return false;
        }
        public static int getDist(Player player) {
            return (int) Utl.vec.distance(new Loc(player).getVec(player),get(player).getVec(player));
        }

        /**
         * clears the destination without notifying the player
         */
        public static void clear(Player player) {
            player.getPData().getDEST().setDest(new Dest());
        }
        /**
         * clears the destination with a message for the player
         * @param reason the reason for clearing
         *               1 = command based, player did it
         *               2 = reaching the destination
         *               3 = switching dimensions
         */
        public static void clear(Player player, int reason) {
            // if the destination was already cleared
            if (!get(player).hasXYZ()) {
                player.sendMessage(LANG.error("cleared"));
                return;
            }
            Dest current = get(player);
            // get the reason for clearing
            CTxT reasonTxT = LANG.msg("cleared." + switch (reason) {
                default -> "command"; case 2 -> "reached"; case 3 -> "dimension";
            }).append(" ");
            // add the set buttons
            reasonTxT.append(setButtons(current,
                    // only convert if reason is switching & convertible
                    reason == 3 && Dimension.canConvert(player.getDimension(),current.getDimension())
            ));
            // clear the destination
            clear(player);
            // send the message
            player.sendMessage(CUtl.tag().append(LANG.msg("cleared", reasonTxT.color('7'))));
        }
        /**
         * sets the destination without notifying the player, still checks for autoclear instantly clearing or not
         */
        public static void set(Player player, Dest loc) {
            if (!inAutoClearRadius(player, loc)) {
                player.getPData().getDEST().setDest(loc);
            }
        }
        /**
         * generates the set message for the player
         * @param setLoc the text for the location set
         */
        public static void setMSG(Player player, CTxT setLoc) {
            player.sendMessage(CUtl.tag().append(LANG.msg("set",setLoc)).append("\n ")
                    .append(LANG.msg("set.info",
                            CUtl.toggleTxT((boolean) player.getPData().getDEST().getSetting(Setting.autoclear)),
                            CUtl.toggleTxT((boolean) player.getPData().getDEST().getSetting(Setting.autoconvert))).color('7').italic(true)));
        }
        /**
         * sets the destination with bad data checks and convert toggle
         * @param dest the location to set
         * @param convert to convert to the players dimension or not
         */
        public static void playerSet(Player player, Dest dest, boolean convert) {
            // handle bad data
            if (!dest.hasXYZ()) {
                player.sendMessage(CUtl.error("coordinates"));
                return;
            }
            if (dest.getDimension() == null) {
                player.sendMessage(CUtl.error("dimension"));
                return;
            }

            CTxT convertTag = CTxT.of("");
            if (convert && Dimension.canConvert(player.getDimension(),dest.getDimension())) {
                // fill the convert tag
                convertTag.append(" ").append(LANG.msg("set.converted").color('7').italic(true).hEvent(dest.getBadge()));
                // convert the loc
                dest.convertTo(player.getDimension());
            }
            // check if already in autoclear radius
            if (inAutoClearRadius(player,dest)) {
                player.sendMessage(LANG.error("already_at"));
                return;
            }
            // set the destination and send the message
            set(player, dest);
            setMSG(player,dest.getBadge().append(convertTag));
        }

        /**
         * sets the destination to a saved destination
         * @param global if it's a global destination
         * @param name the name of the destination to get
         * @param convert to convert to the players destination or not
         */
        public static void setSaved(Player player, String name, boolean global, boolean convert) {
            saved.DestEntry destination = new saved.DestEntry(player,name,global);
            // handle bad data
            if (!destination.hasDestRequirements()) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            CTxT convertTag = CTxT.of("");
            if (convert && Dimension.canConvert(player.getDimension(),destination.getDimension())) {
                // fill the convert tag
                convertTag.append(" ").append(LANG.msg("set.converted").color('7').italic(true).hEvent(destination.getBadge()));
                // convert the loc
                destination.convertTo(player.getDimension());
            }
            // check if already in autoclear radius (after converting)
            if (inAutoClearRadius(player,destination)) {
                player.sendMessage(LANG.error("already_at"));
                return;
            }
            set(player,destination.getThis());
            setMSG(player,destination.getBadge().append(convertTag));
        }

        /**
         * creates the set buttons for the destination
         * @param destination the command to set the destination
         * @param convert if the convert button should be there too
         * @return the set buttons
         */
        public static CTxT setButtons(Dest destination, boolean convert) {
            CTxT out = CTxT.of("");
            if (!destination.isValid()) return out;

            // get the set command
            String setCMD = "/dest set " + destination.toCMD();

            out.append(LANG.btn("set").btn(true).color(Assets.mainColors.set)
                    .cEvent(1,setCMD)
                    .hEvent(LANG.btn("set").color(Assets.mainColors.set).append("\n").append(LANG.hover("set"))));

            // make the convert button if needed
            if (convert) {
                out.append(" ").append(CTxT.of(Assets.symbols.convert).btn(true).color(Assets.mainColors.convert)
                        .cEvent(1, setCMD + " convert")
                        .hEvent(LANG.btn("convert").color(Assets.mainColors.convert).append("\n").append(LANG.hover("convert"))));
            }

            return out;
        }

        /**
         * gets a destination from command args
         * @param args the destination args ONLY (trim out excess beforehand)
         * @return the destination from the args
         */
        public static Dest getDestArgs(Player player, String[] args, boolean reqName) {
            // <xyz or xy> (dimension) (color)
            // (name) <xyz or xy> (dimension) (color)

            // (name) <x> (y) <z> (dimension) (color)

            String pDIM = player.getDimension();

            // make sure NAMES only if reqName is true
            if (args.length > 1 && Num.isNum(args[0]) && reqName) return new Dest();

            // NAME
            if (args.length == 1) {
                // if NAME (not num)
                if (!Num.isNum(args[0])) {
                    return new Dest(player,args[0],null);
                }
            }

            // x z
            if (args.length == 2) {
                return new Dest(Num.toInt(args[0]),null,Num.toInt(args[1]),pDIM,null,null);
            }

            // NAME x z OR x y (z, DIM)
            if (args.length == 3) {
                // if NAME (not num)
                if (!Num.isNum(args[0])) {
                    return new Dest(Num.toInt(args[1]), null, Num.toInt(args[2]), pDIM, args[0], null);
                }

                // NO NAME
                // if DIM
                if (Dimension.getAllIDs().contains(args[2])) {
                    return new Dest(Num.toInt(args[0]),null,Num.toInt(args[1]),args[2],null,null);
                }
                // Z
                return new Dest(Num.toInt(args[0]),Num.toInt(args[1]),Num.toInt(args[2]),pDIM,null,null);
            }

            // NAME x y (z, color, DIM) OR x y z (DIM)
            if (args.length == 4) {
                // if NAME (not num)
                if (!Num.isNum(args[0])) {
                    // if DIM
                    if (Dimension.getAllIDs().contains(args[3])) {
                        return new Dest(Num.toInt(args[1]),null,Num.toInt(args[2]),args[3],args[0],null);
                    }
                    // if color (not num)
                    if (!Num.isNum(args[3])) {
                        return new Dest(Num.toInt(args[1]),null,Num.toInt(args[2]),pDIM,args[0],CUtl.color.colorHandler(player,args[3]));
                    }
                    // z
                    return new Dest(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),pDIM,args[0],null);
                }

                // NO NAME
                // if DIM
                if (Dimension.getAllIDs().contains(args[3])) {
                    return new Dest(Num.toInt(args[0]),Num.toInt(args[1]),Num.toInt(args[2]),args[3],null,null);
                }
                // else IGNORE (don't do color on no name destinations)
                return new Dest(Num.toInt(args[0]),Num.toInt(args[1]),Num.toInt(args[2]),pDIM,null,null);
            }

            // NAME x y z (DIM, color) OR NAME x z (DIM) (color)
            if (args.length == 5) {
                // if NAME (not num)
                if (!Num.isNum(args[0])) {
                    // if there's (z)
                    if (Num.isNum(args[3])) {
                        // if DIM
                        if (Dimension.getAllIDs().contains(args[4])) {
                            return new Dest(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),args[4],args[0],null);
                        }
                        // else color
                        return new Dest(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),pDIM,args[0],CUtl.color.colorHandler(player,args[4]));
                    }

                    // if no Z
                    return new Dest(Num.toInt(args[1]),null,Num.toInt(args[2]),args[3],args[0],CUtl.color.colorHandler(player,args[4]));
                }
            }

            // NAME x y z (DIM) (color)
            if (args.length == 6) {
                // if NAME (not num)
                if (!Num.isNum(args[0])) {
                    return new Dest(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),args[4],args[0],args[5]);
                }
            }

            // if none of the above, empty destination
            return new Dest();
        }

        /**
         * returns a suggester for a destination at the pos (for turning into a destination with {@link dest#getDestArgs(Player, String[], boolean)})
         * @param pos trimmed pos starting at the destination
         * @param args trimmed args starting at the destination
         */
        public static ArrayList<String> destSuggester(Player player, int pos, String[] args, boolean reqName) {
            String current = Suggester.getCurrent(args,pos);
            ArrayList<String> suggester = new ArrayList<>();

            // (name) <x> (y) <z> (dimension) (color)

            // <NAME> OR <x>
            if (pos == 0) {
                suggester.add(Suggester.wrapQuotes("name"));
                if (!reqName) {
                    // only if name isn't required
                    suggester.addAll(Suggester.xyz(player, current, 3));
                }
            }

            // make sure there is a name if reqName is true
            if (pos > 0 && Num.isNum(args[0]) && reqName) return suggester;

            // NAME <x> OR x <y>
            if (pos == 1) {
                // NAME (if not num)
                if (!Num.isNum(args[0])) {
                    suggester.addAll(Suggester.xyz(player,current,3));
                }
                // else <y>
                else {
                    suggester.addAll(Suggester.xyz(player,current,2));
                }
            }

            // NAME x <y> OR x y <z, DIM>
            if (pos == 2) {
                // NAME (if not num)
                if (!Num.isNum(args[0])) {
                    // Y
                    suggester.addAll(Suggester.xyz(player,current,3));
                }
                // (z, DIM)
                else {
                    suggester.addAll(Suggester.xyz(player, current, 1));
                    suggester.addAll(Suggester.dims(current, false));
                }
            }

            // NAME x y <z, DIM, color> OR x y z <DIM>
            if (pos == 3) {
                // NAME (if not num)
                if (!Num.isNum(args[0])) {
                    // <z, DIM, color>
                    suggester.addAll(Suggester.xyz(player,current,1));
                    suggester.addAll(Suggester.dims(current,false));
                    suggester.addAll(Suggester.colors(player,current,false));
                }
                // DIM
                else if (Num.isNum(args[2])) {
                    suggester.addAll(Suggester.dims(current, true));
                }
            }

            // NAME x y z <DIM, color> OR NAME x z DIM <color>
            if (pos == 4) {
                // NAME (if not num)
                if (!Num.isNum(args[0])) {
                    // (<DIM, color>
                    if (Num.isInt(args[3])) {
                        suggester.addAll(Suggester.dims(current,true));
                        suggester.addAll(Suggester.colors(player,current,true));
                    }
                    // <color>
                    else if (Dimension.getAllIDs().contains(args[3])) {
                        suggester.addAll(Suggester.colors(player,current,true));
                    }
                }
            }

            // NAME x y z DIM <color>
            if (pos == 5) {
                // NAME (if not num)
                if (!Num.isNum(args[0])) {
                    // if contains Z and valid DIM
                    if (Num.isNum(args[3]) && Dimension.getAllIDs().contains(args[4])) {
                        // <color>
                        suggester.addAll(Suggester.colors(player,current,true));
                    }
                }
            }

            return suggester;
        }

        // SUGGESTERS AND EXECUTORS
        public static void setCMDExecutor(Player player, String[] args) {
            if (args.length < 1) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSet));
                return;
            }
            boolean convert = false;
            String[] destArgs = args;
            // if last is convert remove the last entry in the destArgs
            if (args[args.length-1].equals("convert")) {
                convert = true;
                destArgs = Arrays.copyOf(args, args.length-1);
            }

            // set the destination
            playerSet(player, getDestArgs(player, destArgs, false), convert);
        }
        public static ArrayList<String> setCMDSuggester(Player player, int pos, String[] args) {
            // use the dest Suggester
            ArrayList<String> suggester = new ArrayList<>(destSuggester(player, pos, args, false));

            // if the args has a dimension, add convert on the end if not there already
            if (pos > 2 && !args[args.length-1].equalsIgnoreCase("convert") &&
                    Arrays.stream(args).anyMatch(s -> Dimension.getAllIDs().contains(s))) {
                suggester.add("convert");
            }

            return suggester;
        }
    }

    public static class saved {
        private static final int PER_PAGE = 7;

        public static final Lang LANG = new Lang("destination.saved.");

        public static CTxT BUTTON = LANG.btn().btn(true).color(Assets.mainColors.saved).cEvent(1,"/dest saved")
                .hEvent(CTxT.of(Assets.cmdUsage.destSaved).color(Assets.mainColors.saved).append("\n").append(LANG.hover()));

        public static CTxT GLOBAL_BUTTON = LANG.btn("global").btn(true).color(Assets.mainColors.global).cEvent(1,"/dest global")
                .hEvent(CTxT.of(Assets.cmdUsage.destGlobal).color(Assets.mainColors.global).append("\n").append(LANG.hover()));

        public static CTxT ADD_BUTTON = CTxT.of("+").btn(true).color(Assets.mainColors.add).cEvent(2,"/dest add ")
                .hEvent(CTxT.of(Assets.cmdUsage.destAdd).color(Assets.mainColors.add).append("\n")
                        .append(LANG.hover("add",LANG.hover("add.2").color(Assets.mainColors.add))));

        public static CTxT SAVE_BUTTON(String cmd) {
            return CTxT.of("+").btn(true).color(Assets.mainColors.add).cEvent(2,cmd)
                    .hEvent(LANG.hover("save",LANG.hover("save.2").color(Assets.mainColors.add)));
        }
        public static CTxT EDIT_BUTTON(int click, String cmd) {
            return CTxT.of(Assets.symbols.pencil).btn(true).color(Assets.mainColors.edit).cEvent(click,cmd)
                    .hEvent(LANG.hover("edit",CUtl.LANG.get("destination")).color(Assets.mainColors.edit)).color(Assets.mainColors.edit);
        }

        /*
        all command executors and suggesters for SAVED
         */
        public static void CMDExecutor(Player player, String[] args) {
            // make sure saving is enabled
            if (!Helper.checkEnabled(player).saving()) return;
            // UI
            if (args.length == 0) {
                UI(player, 1);
                return;
            }
            // UI (page)
            if (Num.isInt(args[0])) {
                UI(player,Num.toInt(args[0]));
                return;
            }

            // if there is -r, remove it and enable returning
            boolean Return = args[0].contains("-r");
            args[0] = args[0].replace("-r","");

            switch (args[0]) {
                case "edit" -> editCMDExecutor(player, Helper.trimStart(args, 1), false, Return);
                case "send" -> {
                    if (args.length == 2) player.sendMessage(CUtl.LANG.error("args"));
                    if (args.length == 3) {
                        // dest saved send (name)
                        Dest dest = new DestEntry(player, args[1], false);
                        if (dest.hasDestRequirements()) social.send.logic(player,args[2],dest);
                        else player.sendMessage(Destination.LANG.error("invalid"));
                    }
                }
                case "delete" -> {
                    if (args.length == 1) player.sendMessage(CUtl.LANG.error("args"));
                    if (args.length == 2) delete(Return,player,new DestEntry(player, args[1], false));
                }
                case "set" -> {
                    // if convert is there, convert
                    boolean convert = args.length == 3 && args[2].equals("convert");

                    if (args.length >= 2) dest.setSaved(player,args[1],false,convert);
                }
                case "add" -> addCMDExecutor(player, Helper.trimStart(args,1), false);
                default -> player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
            }
        }
        public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Helper.checkEnabled(player).saving()) return suggester;
            // saved add
            // saved edit type name <arg>
            // saved send name <IGN>
            if (pos == 0) {
                suggester.add("add");
                suggester.add("edit");
                suggester.add("delete");
                suggester.add("send");
                suggester.add("set");
                return suggester;
            }
            // if -r is attached, remove it and continue with the suggester
            if (args[0].contains("-r")) args[0] = args[0].replace("-r","");
            // switch for logic
            switch (args[0]) {
                case "delete", "set" -> {
                    if (pos == 1) suggester.addAll(getCMDNames(getList(player)));
                    // add convert if setting
                    if (pos == 2 && args[0].equals("set")) suggester.add("convert");
                }
                case "send" -> {
                    // saved send (name)
                    if (pos == 1) suggester.addAll(getCMDNames(getList(player)));
                    // saved send name (player)
                    if (pos == 2) {
                        for (Player s : Utl.getPlayers()) {
                            if (s.equals(player)) continue;
                            suggester.add(s.getName());
                        }
                    }
                }
                case "add" -> suggester.addAll(addCMDSuggester(player,pos-1, Helper.trimStart(args,1)));
                case "edit" -> suggester.addAll(editCMDSuggester(player, false,pos-1, Helper.trimStart(args,1)));
            }
            return suggester;
        }

        public static void globalCMDExecutor(Player player, String[] args) {
            if (!Helper.checkEnabled(player).global()) return;

            // ui
            if (args.length == 0) {
                globalUI(player, 1);
                return;
            }

            // page nav
            if (Num.isNum(args[0])) {
                globalUI(player, Num.toInt(args[0]));
                return;
            }

            // global set
            if (args[0].equals("set")) {
                // if convert is there, convert
                boolean convert = args.length == 3 && args[2].equals("convert");
                if (args.length >= 2) dest.setSaved(player,args[1],true,convert);
            }

            // PERMS FOR EDITING
            if (!Helper.checkEnabled(player).globalEditing()) return;
            switch (args[0]) {
                case "edit" -> editCMDExecutor(player, Helper.trimStart(args,1),true,false);
                case "delete" -> {
                    if (args.length == 1) player.sendMessage(CUtl.LANG.error("args"));
                    if (args.length == 2) delete(false,player,new DestEntry(player, args[1], true));
                }
                case "add" -> addCMDExecutor(player, Helper.trimStart(args,1), true);
                default -> player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
            }
        }
        public static ArrayList<String> globalCMDSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();

            if (pos == 0) {
                suggester.add("set");
                // enabled check
                if (!Helper.checkEnabled(player).globalEditing()) return suggester;
                suggester.add("add");
                suggester.add("edit");
                suggester.add("delete");
                return suggester;
            }

            if (args[0].equals("set")) {
                if (pos == 1) suggester.addAll(getCMDNames(Data.getGlobal().getDestinations()));
                // add convert
                if (pos == 2) suggester.add("convert");
            }

            // enabled check
            if (!Helper.checkEnabled(player).globalEditing()) return suggester;
            switch (args[0]) {
                case "delete" -> {
                    if (pos == 1) suggester.addAll(getCMDNames(Data.getGlobal().getDestinations()));
                }
                case "add" -> {
                    return addCMDSuggester(player,pos-1, Helper.trimStart(args,1));
                }
                case "edit" -> {
                    return editCMDSuggester(player,true,pos-1, Helper.trimStart(args,1));
                }
            }
            return suggester;
        }

        public static void editCMDExecutor(Player player, String[] args, boolean global, boolean Return) {
            if (args.length == 0) return;
            // edit (name)
            if (args.length == 1) {
                editUI(player,new DestEntry(player, args[0], global));
                return;
            }
            // edit (type) (args)
            switch (args[0]) {
                case "name" -> {
                    if (args.length == 3) editName(Return, player, new DestEntry(player,args[1],global), args[2]);
                    else player.sendMessage(Destination.LANG.error("invalid"));
                }
                case "color" -> {
                    if (args.length == 3) setColor(player, new DestEntry(player,args[1],global), DHud.preset.DEFAULT_UI_SETTINGS, args[2], Return);
                    else player.sendMessage(Destination.LANG.error("invalid"));
                }
                case "colorui" -> {
                    if (args.length == 2) colorUI(player, DHud.preset.DEFAULT_UI_SETTINGS,args[1]);
                    if (args.length == 3) colorUI(player,args[2],args[1]);
                }
                case "order" -> {
                    if (args.length == 3) editOrder(Return, player, new DestEntry(player,args[1],global), args[2]);
                    else player.sendMessage(Destination.LANG.error("invalid"));
                }
                case "location" -> {
                    if (args.length == 2) player.sendMessage(Destination.LANG.error("invalid"));
                    // location (dimension)
                    if (args.length == 3 && !Num.isInt(args[2])) {
                        Loc loc = new Loc();
                        loc.setDimension(args[2]);
                        editLocation(Return,player,new DestEntry(player,args[1],global),loc);
                    }
                    // location x z
                    if (args.length == 4) editLocation(Return,player,new DestEntry(player,args[1],global),
                            new Loc(Num.toInt(args[2]), Num.toInt(args[3])));
                    if (args.length == 5) {
                        // location x, y, z
                        if (Num.isInt(args[4])) editLocation(Return,player,new DestEntry(player,args[1],global),
                                new Loc(Num.toInt(args[2]), Num.toInt(args[3]), Num.toInt(args[4])));
                        // location x, z, dim)
                        else editLocation(Return,player,new DestEntry(player,args[1],global),
                                new Loc(Num.toInt(args[2]), Num.toInt(args[3]), args[4]));
                    }
                    // location x, y, z, dim
                    if (args.length == 6) editLocation(Return,player,new DestEntry(player,args[1],global),
                            new Loc(Num.toInt(args[2]), Num.toInt(args[3]), Num.toInt(args[4]),args[5]));
                }
            }
        }
        public static ArrayList<String> editCMDSuggester(Player player, boolean global, int pos, String[] args) {
            String current = Suggester.getCurrent(args,pos);
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
            if (pos == 1) suggester.addAll(getCMDNames(global ? Data.getGlobal().getDestinations() : getList(player)));
            // saved edit type name (<arg>)
            if (args[0].equalsIgnoreCase("location")) {
                if (pos == 2) {
                    suggester.addAll(Suggester.xyz(player,current,3));
                    suggester.addAll(Suggester.dims(current,true));
                }
                if (pos == 3) suggester.addAll(Suggester.xyz(player,current,2));
                if (pos == 4) {
                    suggester.addAll(Suggester.xyz(player,current,1));
                    suggester.addAll(Suggester.dims(current,false));
                }
                if (pos == 5 && Num.isInt(args[4])) {
                    suggester.addAll(Suggester.dims(current,true));
                }
                return suggester;
            }
            if (pos == 2) {
                if (args[0].equalsIgnoreCase("name")) {
                    suggester.add("\"name\"");
                    suggester.add(new DestEntry(player,args[1],global).getCMDName()); // current name to edit
                }
                if (args[0].equalsIgnoreCase("color"))
                    suggester.addAll(Suggester.colors(player,current,true));
                if (args[0].equalsIgnoreCase("order"))
                    suggester.add(String.valueOf(new DestEntry(player,args[1],global).getOrder())); //current order to edit
            }
            return suggester;
        }

        public static void addCMDExecutor(Player player, String[] args, boolean global) {
            if (!Helper.checkEnabled(player).saving() && !Helper.checkEnabled(player).globalEditing()) {
                return;
            }
            add(player,new DestEntry(player,dest.getDestArgs(player,args,true),global));
        }
        public static ArrayList<String> addCMDSuggester(Player player, int pos, String[] args) {
            if (!Helper.checkEnabled(player).saving() && !Helper.checkEnabled(player).globalEditing()) {
                return new ArrayList<>();
            }
            return dest.destSuggester(player,pos,args,true);
        }

        /**
         * destination helper
         */
        public static class DestEntry extends Dest {
            // dest helper
            private transient final ArrayList<Dest> list;
            private transient final Player player;
            private transient Integer index;
            // figure out what type of destination it is on creation
            public transient final boolean global;

            /**
             * Dest from dest and list for dest
             * @param dest the destination
             * @param global weather it's a global destination or not
             */
            public DestEntry(Player player, Dest dest, boolean global) {
                super(dest);
                // global dest list handling
                this.global = global;
                this.list = getListType(player, global);
                this.index = list.indexOf(getThis());
                this.player = player;
            }

            /**
             * Dest from name in the list
             * @param name name of the dest
             * @param global weather it's a global destination or not
             */
            public DestEntry(Player player, String name, boolean global) {
                super();
                // global dest list handling
                this.global = global;
                this.list = getListType(player, global);
                this.player = player;
                // search for the destination using the name provided
                for (Dest entry: this.list) {
                    if (entry.getName().equals(name)) {
                        setDest(entry);
                        this.index = list.indexOf(entry);
                        break;
                    }
                }
            }

            /**
             * get the dest list depending on the global state
             */
            private static ArrayList<Dest> getListType(Player player, boolean global) {
                return global ? Data.getGlobal().getDestinations() : saved.getList(player);
            }

            /**
             * sets the destination
             */
            public void setDest(Dest dest) {
                setX(dest.getX());
                setY(dest.getY());
                setZ(dest.getZ());
                setDimension(dest.getDimension());
                setName(dest.getName());
                setColor(dest.getColor());
            }

            /**
             * saves changes from the List to file
             */
            private void save() {
                if (global) {
                    // set the list to the edited list
                    Data.getGlobal().setDestinations(list);
                    // save changes to file
                    GlobalDest.save();
                } else player.getPData().getDEST().setSaved(list);
            }

            /**
             * updates the destination list and saves to file
             */
            public void update() {
                if (index >= 0) {
                    list.set(index, getThis());
                    save();
                }
            }

            /**
             * adds the destination to the list and saves to file if not already
             */
            public void add() {
                if (!list.contains(getThis())) {
                    list.add(getThis());
                    // update the index & save
                    index = list.indexOf(getThis());
                    save();
                }
            }

            /**
             * removes the destination from the list and saves to file
             */
            public void remove() {
                list.remove(getThis());
                save();
            }

            public ArrayList<Dest> getList() {
                return list;
            }

            /**
             * get name formatted for commands (wrapped in quotes)
             */
            public String getCMDName() {
                return Suggester.wrapQuotes(getName());
            }

            public int getOrder() {
                return index+1;
            }

            /**
             * sets the order of the destination, has to already be added to the list
             * @param order the new order, player format +1
             */
            public void setOrder(int order) {
                list.remove(getThis());
                // sub one because player entered order is one off
                order--;
                // make sure the order is not out of bounds
                if (order < 0) order = 0;
                if (order > list.size()) order = list.size();
                // set the index
                index = order;
                // add the dest back if empty, setting throws an error
                if (list.isEmpty()) list.add(getThis());
                // set the new order in the list
                else list.set(index,getThis());
                // save the list
                update();
            }

            public Dest getThis() {
                return new Dest(this);
            }

            public boolean isGlobal() {
                return global;
            }

            /**
             * validates a destination, sends errors to the player if necessary
             * @return if errors were sent or not
             */
            public boolean sendErrors() {
                if (this.hasDestRequirements()) {
                    // if valid but name is too long
                    if (this.getName().length() > Helper.MAX_NAME) {
                        player.sendMessage(CUtl.LANG.error("length",Helper.MAX_NAME));
                        return true;
                    }
                    // valid, no errors
                    return false;
                }
                // send invalid error and return true
                player.sendMessage(Destination.LANG.error("invalid"));
                return true;
            }
        }

        /**
         * gets the player destination list
         */
        public static ArrayList<Dest> getList(Player player) {
            return player.getPData().getDEST().getSaved();
        }

        /**
         * gets a list of command friendly names from a destination list
         * @return names surrounded by quotes
         */
        public static ArrayList<String> getCMDNames(ArrayList<Dest> list) {
            return list.stream().map(dest -> Suggester.wrapQuotes(dest.getName()))
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * gets all names from a destination list
         * @return list of names
         */
        public static ArrayList<String> getNames(ArrayList<Dest> list) {
            return list.stream().map(Dest::getName).collect(Collectors.toCollection(ArrayList::new));
        }

        /**
         * saves a new destination
         * @param destination the destination to save
         */
        public static void add(Player player, DestEntry destination) {
            // format the color
            destination.setColor(CUtl.color.colorHandler(player,destination.getColor()));
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // if there are the max amount of saved destinations
            if (destination.getList().size() >= Data.getConfig().getDestination().getMaxSaved()) {
                player.sendMessage(LANG.error("max"));
                return;
            }
            // if there is a destination with the same name
            if (getNames(destination.getList()).contains(destination.getName())) {
                player.sendMessage(LANG.error("duplicate",CTxT.of(destination.getName()).color(CUtl.s())));
                return;
            }

            // save the destination
            destination.add();
            String cmdName = destination.getCMDName();
            // buttons after the destination
            CTxT buttons = CTxT.of(" ");
            // only show edit button if destination isn't global
            if (!destination.isGlobal()) buttons.append(EDIT_BUTTON(1,"/dest saved edit "+cmdName)).append(" ");
            buttons.append(dest.setButtons(destination,
                    Dimension.canConvert(player.getDimension(),destination.getDimension())));

            player.sendMessage(CUtl.tag().append(LANG.msg("add",destination.getBadge().append(buttons))));
        }

        /**
         * deletes an existing destination
         * @param Return to return back to the saved UI or not
         * @param destination the destination to remove
         */
        public static void delete(boolean Return, Player player, DestEntry destination) {
            ListPage<Dest> listPage = new ListPage<>(destination.getList(),PER_PAGE);
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;

            destination.remove();

            player.sendMessage(CUtl.tag().append(LANG.msg("delete",destination.getBadge())));
            // return back to the page that the destination was on
            if (Return) player.performCommand("dest saved " + listPage.getPageOf(destination));
        }

        /**
         * edits a destination's name
         * @param Return to return to the destination edit UI or not
         * @param destination destination to edit
         * @param newName the new name for the destination
         */
        public static void editName(boolean Return, Player player, DestEntry destination, String newName) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // if there's already a destination with the new name
            if (getNames(destination.list).contains(newName)) {
                player.sendMessage(LANG.error("duplicate",CTxT.of(newName).color(CUtl.s())));
                return;
            }

            // save the old name
            CTxT oldName = CTxT.of(destination.getName()).color(CUtl.s());
            // change the name
            destination.setName(newName);
            // update the destination
            destination.update();
            // send a message
            player.sendMessage(CUtl.tag().append(LANG.msg("set",oldName,LANG.get("name"),CTxT.of(newName).color(CUtl.s()))));
            // return to the edit screen
            if (Return) player.performCommand("dest saved edit "+destination.getCMDName());
        }

        /**
         * edits a destination's order
         * @param Return to return to the destination edit UI or not
         * @param destination destination to edit
         * @param newOrderString the new order for the destination (as a string)
         */
        public static void editOrder(boolean Return, Player player, DestEntry destination, String newOrderString) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // make sure the new order is a number
            if (!Num.isInt(newOrderString)) {
                player.sendMessage(CUtl.LANG.error("number"));
                return;
            }
            // get the formatted name
            CTxT name = CTxT.of(destination.getName()).color(CUtl.s());
            // set the order
            destination.setOrder(Num.toInt(newOrderString));
            // send the message
            player.sendMessage(CUtl.tag().append(LANG.msg("set",name,LANG.get("order"),CTxT.of(String.valueOf(destination.getOrder())).color(CUtl.s()))));
            // return if needed
            if (Return) player.performCommand("dest saved edit "+destination.getCMDName());
        }
        /**
         * edits a destination's location (xyz & dim)
         * @param Return to return to the destination edit UI or not
         * @param destination destination to edit
         * @param newLoc the new Loc for the destination
         */
        public static void editLocation(boolean Return, Player player, DestEntry destination, Loc newLoc) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;

            // set only the location data to the Loc if there's coordinates
            if (newLoc.hasXYZ()) {
                destination.setX(newLoc.getX());
                destination.setY(newLoc.getY());
                destination.setZ(newLoc.getZ());
            }
            // set only the dimension data to the Loc if there is a dimension
            if (newLoc.getDimension() != null)
                destination.setDimension(newLoc.getDimension());
            // update the destination
            destination.update();
            // get the formatted name
            CTxT name = CTxT.of(destination.getName()).color(CUtl.s());
            // send the message
            player.sendMessage(CUtl.tag().append(LANG.msg("set",name,LANG.get("location"),CTxT.of(destination.getNamelessBadge()))));
            // return if needed
            if (Return) player.performCommand("dest saved edit "+name);
        }

        /**
         * edits a destination's color, returns to colorUI
         * @param destination the destination to edit
         * @param UISettings the settings for the colorUI
         * @param newColor the color to set to
         * @param Return whether to return to the UI or not
         */
        public static void setColor(Player player, DestEntry destination, String UISettings, String newColor, boolean Return) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // handle color
            newColor = CUtl.color.colorHandler(player,newColor);
            // save the new color
            destination.setColor(newColor);
            // update the destination
            destination.update();
            // get the formatted name
            CTxT name = CTxT.of(destination.getName()).color(CUtl.s());
            // show the color UI if returning, else send set message
            if (Return) colorUI(player,UISettings,destination.getName());
            else player.sendMessage(CUtl.tag().append(LANG.msg("set",name,LANG.get("color"),CUtl.color.getBadge(newColor))));
        }

        /**
         * destination colorUI
         * @param UISettings the color UI settings
         * @param name the name of the destination to edit
         */
        public static void colorUI(Player player, String UISettings, String name) {
            DestEntry destination = new DestEntry(player,name,false);
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // get the current color
            String currentColor = destination.getColor();

            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                               ").strikethrough(true);
            msg.append(LANG.ui("color",name).color(currentColor)).append(line).append("\n");
            // get the command name of the destination
            String cmdName = destination.getCMDName();
            msg.append(DHud.preset.colorEditor(currentColor,UISettings, DHud.preset.Type.saved,name,"/dest saved edit colorui "+cmdName+" %s"))
                    .append("\n\n           ").append(CUtl.CButton.back("/dest saved edit "+cmdName)).append(line);
            player.sendMessage(msg);
        }

        /**
         * UI for editing / viewing a destination
         * @param destination the destination to edit
         */
        public static void editUI(Player player, DestEntry destination) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            ListPage<Dest> listPage = new ListPage<>(new ArrayList<>(destination.getList()),PER_PAGE);
            // get the command name of the destination
            String cmdName = destination.getCMDName();

            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                             ").strikethrough(true);
            msg.append(LANG.ui("edit").color(Assets.mainColors.saved)).append(line).append("\n");
            msg
                    // order
                    .append(" ").append(CTxT.of("#"+destination.getOrder()).btn(true).color(CUtl.p())
                            .hEvent(LANG.hover("edit",LANG.get("order").color(CUtl.p())))
                            .cEvent(2,"/dest saved edit-r order "+cmdName+" "))
                    // name
                    .append(" ").append(CTxT.of(destination.getName()).btn(true).color(CUtl.s())
                            .hEvent(LANG.hover("edit",LANG.get("name").color(CUtl.s())))
                            .cEvent(2,"/dest saved edit-r name "+cmdName+" "))
                    // color
                    .append(" ").append(CTxT.of(Assets.symbols.square).btn(true).color(destination.getColor())
                            .hEvent(LANG.hover("edit",LANG.get("color").color(destination.getColor())))
                            .cEvent(1, "/dest saved edit colorui "+cmdName))
                    // location
                    .append("\n\n ").append(CTxT.of(Assets.symbols.pencil).btn(true).color(Assets.mainColors.edit)
                            .hEvent(LANG.hover("edit",LANG.get("location").color(Assets.mainColors.edit)))
                            .cEvent(2, "/dest saved edit-r location "+cmdName+" "))
                    .append(" ").append(CTxT.of(destination.getNamelessBadge()))
                    .append("\n   ");
            // SEND BUTTON
            if (Helper.checkEnabled(player).send()) {
                msg.append(social.send.LANG.btn().btn(true).color(Assets.mainColors.send).cEvent(2,"/dest saved send "+cmdName+" ")
                        .hEvent(CTxT.of("/dest saved send "+cmdName+" <player>").color(Assets.mainColors.send)
                                .append("\n").append(social.send.LANG.hover("saved")))).append(" ");
            }
            // SET & CONVERT BUTTON
            msg
                    .append(dest.setButtons(destination,
                            Dimension.canConvert(player.getDimension(),destination.getDimension()))).append(" ")
                    .append("\n\n ")
                    .append(LANG.btn("delete").btn(true).color('c').cEvent(2,"/dest saved delete-r "+cmdName)
                            .hEvent(LANG.hover("delete",LANG.btn("delete").color('c')))).append(" ")
                    //BACK
                    .append(CUtl.CButton.back("/dest saved "+ listPage.getPageOf(destination)))
                    .append(line);
            player.sendMessage(msg);
        }

        /**
         * main saved destination UI
         * @param pg page to show
         */
        public static void UI(Player player, int pg) {
            ListPage<Dest> listPage = new ListPage<>(new ArrayList<>(getList(player)),PER_PAGE);
            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                             ").strikethrough(true);
            msg.append(LANG.ui().color(Assets.mainColors.saved)).append(line).append("\n");

            // for every destination in the current page
            for (Dest entry: listPage.getPage(pg)) {
                String cmdName = "\""+entry.getName()+"\"";
                msg.append(" ")
                        // BADGE
                        .append(entry.getBadge()).append(" ")
                        // EDIT
                        .append(EDIT_BUTTON(1,"/dest saved edit " + cmdName)).append(" ")
                        // SET & convert
                        .append(dest.setButtons(entry,
                                Dimension.canConvert(player.getDimension(), entry.getDimension())))
                        .append("\n");
            }
            // no saved
            if (listPage.getPage(pg).isEmpty()) {
                msg.append(" ").append(LANG.ui("none")).append("\n ").append(LANG.ui("none.2",ADD_BUTTON)).append("\n");
            }
            msg.append("\n ");
            // add global button if enabled
            if (Helper.checkEnabled(player).global()) msg.append(CTxT.of(Assets.symbols.global).btn(true).color(Assets.mainColors.global)
                    .hEvent(LANG.hover("global").color(Assets.mainColors.global))
                    .cEvent(1,"/dest global"));
            // else add button
            else msg.append(ADD_BUTTON);
            msg
                    // nav buttons
                    .append(" ").append(listPage.getNavButtons(pg,"/dest saved "))
                    // back button
                    .append(" ").append(CUtl.CButton.back("/dest"))
                    .append(line);
            player.sendMessage(msg);
        }

        /**
         * global destinations UI
         * @param pg page to show
         */
        public static void globalUI(Player player, int pg) {
            ListPage<Dest> listPage = new ListPage<>(new ArrayList<>(Data.getGlobal().getDestinations()), PER_PAGE);
            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                             ").strikethrough(true);
            msg.append(LANG.ui("global").color(Assets.mainColors.global)).append(line).append("\n");
            // for every destination in the current page
            for (Dest entry : listPage.getPage(pg)) {
                msg.append(" ")
                        //BADGE
                        .append(entry.getBadge()).append(" ")
                        // SET & convert
                        .append(dest.setButtons(entry,
                                Dimension.canConvert(player.getDimension(), entry.getDimension())))
                        .append("\n");
            }
            // no saved
            if (listPage.getPage(pg).isEmpty()) {
                msg.append(" ").append(LANG.ui("global.none")).append("\n");
            }
            // bottom row
            msg.append("\n ")
                    // LOCAL BUTTON
                    .append(CTxT.of(Assets.symbols.local).btn(true).color(Assets.mainColors.saved)
                            .hEvent(LANG.hover("local").color(Assets.mainColors.saved))
                            .cEvent(1, "/dest saved"))
                    // nav buttons
                    .append(" ").append(listPage.getNavButtons(pg, "/dest global "))
                    // back button
                    .append(" ").append(CUtl.CButton.back("/dest"))
                    .append(line);
            player.sendMessage(msg);
        }
    }

    public static class lastdeath {
        private static final int PER_PAGE = 4;

        public static final Lang LANG = new Lang("destination.lastdeath.");

        public static CTxT BUTTON = LANG.btn().btn(true).color(Assets.mainColors.lastdeath).cEvent(1,"/dest lastdeath")
                .hEvent(CTxT.of(Assets.cmdUsage.destLastdeath).color(Assets.mainColors.lastdeath).append("\n").append(LANG.hover()));

        public static void CMDExecutor(Player player, String[] args) {
            if (!Helper.checkEnabled(player).lastdeath()) return;
            if (args.length == 0) {
                UI(player,1);
                return;
            }
            if (args.length == 1 && Num.isInt(args[0])) {
                UI(player,Integer.parseInt(args[0]));
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destLastdeath));
        }

        /**
         * adds another location to the death list
         * @param loc Loc to add
         */
        public static void add(Player player, Loc loc) {
            ArrayList<Loc> deaths = (ArrayList<Loc>) player.getPData().getDEST().getLastdeath();
            if (Dimension.checkValid(loc.getDimension())) {
                //add to the top of the list
                deaths.add(0,loc);
                // WHILE more than max, remove the last entry (to deal with the size changing to be smaller in the future)
                while (deaths.size() > Data.getConfig().getDestination().getLastDeath().getMaxDeaths()) deaths.remove(deaths.size()-1);
            }
            player.getPData().getDEST().setLastdeath(deaths);
        }

        /**
         * the LastDeath UI
         * @param pg pg to display
         */
        public static void UI(Player player,int pg) {
            Helper.ListPage<Loc> listPage = new Helper.ListPage<>((ArrayList<Loc>)player.getPData().getDEST().getLastdeath(),PER_PAGE);
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                                   ").strikethrough(true);
            msg.append(" ").append(LANG.ui().color(Assets.mainColors.lastdeath)).append(line).append("\n ");
            // display the list
            for (Loc loc: listPage.getPage(pg)) {
                String dim = loc.getDimension();
                msg.append(loc.getBadge()).append("\n  ")
                        .append(saved.SAVE_BUTTON("/dest add \""+Dimension.getName(dim).toLowerCase()+"_death\" "+loc.toCMD()+" \""+Dimension.getColor(dim)+"\""))
                        .append(" ").append(dest.setButtons(new Dest(loc,null,null),
                                Dimension.canConvert(player.getDimension(),dim)));
                msg.append("\n ");
            }
            // if empty
            if (listPage.getPage(pg).isEmpty()) msg.append(LANG.ui("no_deaths").color('c')).append("\n");
            msg.append("\n ");
            //button nav if there are more last deaths than what can fit on one page
            if (listPage.getList().size() > PER_PAGE)
                    msg.append(listPage.getNavButtons(pg,"/dest lastdeath ")).append(" ");
            msg.append(CUtl.CButton.back("/dest")).append(line);
            player.sendMessage(msg);
        }
    }

    public static class social {

        /**
         * returns if there is a social cooldown for the player or not, and if there is send an error message
         * @return if there is a social cooldown or not
         */
        public static boolean cooldown(Player player) {
            if (player.getPCache().getSocialCooldown() != null) {
                player.sendMessage(CUtl.LANG.error("social.cooldown"));
                return true;
            }
            return false;
        }

        public static class send {

            public static final Lang LANG = new Lang("destination.send.");

            public static CTxT BUTTON = LANG.btn().btn(true).color(Assets.mainColors.send).cEvent(2,"/dest send ")
                    .hEvent(CTxT.of(Assets.cmdUsage.destSend).color(Assets.mainColors.send).append("\n").append(LANG.hover()));

            public static void CMDExecutor(Player player, String[] args) {
                // enabled check
                if (!Helper.checkEnabled(player).send()) return;
                // send <IGN>
                if (args.length == 1) {
                    logic(player,args[0],new Dest(player,null,null));
                    return;
                }
                // send <IGN> <name>
                if (args.length == 2) {
                    logic(player,args[0],new Dest(player,args[1],null));
                    return;
                }

                if (args[1].equalsIgnoreCase("saved")) {
                    // check if enabled
                    if (!Helper.checkEnabled(player).saving()) return;
                    // send <IGN> saved <name>
                    if (args.length == 3) {
                        // get the dest and the loc from the dest
                        saved.DestEntry dest = new saved.DestEntry(player,args[2],false);
                        logic(player,args[0],dest);
                        return;
                    }
                }

                // send <IGN> <xyz or xy> (dimension)
                // send <IGN> (name) <xyz or xy> (dimension) (color)

                // trim out the IGN
                String[] destArgs = Helper.trimStart(args,1);
                // use dest args
                logic(player, args[0], dest.getDestArgs(player,destArgs,false));
            }

            public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
                ArrayList<String> suggester = new ArrayList<>();
                // enabled check
                if (!Helper.checkEnabled(player).send()) return suggester;

                // send <player> (name) <x> (y) <z> (dimension) (color)

                // send (player)
                if (pos == 0) {
                    suggester.addAll(Suggester.players(player));
                }
                // send (dest)
                else {
                    suggester.addAll(dest.destSuggester(player,pos-1,Helper.trimStart(args,1),false));
                }

                return suggester;
            }

            /**
             * the main logic for sending Locs between players
             * @param player the player sending the Loc
             * @param targetPlayer the player string receiving the Loc
             * @param dest Loc to send
             */
            public static void logic(Player player, String targetPlayer, Dest dest) {
                // get the target player from string
                Player target = new Player(targetPlayer);
                // remove bad data
                // cooldown check
                if (cooldown(player)) return;
                if (!target.isValid()) {
                    player.sendMessage(CUtl.LANG.error("player", CTxT.of(targetPlayer).color(CUtl.s())));
                    return;
                }
                if (target == player) {
                    player.sendMessage(LANG.error("self"));
                    return;
                }
                // target doesn't have sending enabled
                if (!Helper.checkEnabled(player).send()) {
                    player.sendMessage(LANG.error("target_disabled",CTxT.of(target.getName()).color(CUtl.s())));
                    return;
                }
                // LOC VALIDATION
                // custom name too long
                if (dest.getName() != null &&
                        dest.getName().length() > Helper.MAX_NAME) {
                    player.sendMessage(CUtl.LANG.error("length",Helper.MAX_NAME));
                    return;
                }
                // invalid coordinates
                if (!dest.hasXYZ()) {
                    player.sendMessage(CUtl.LANG.error("coordinates"));
                    return;
                }
                // invalid dimension
                if (!Dimension.checkValid(dest.getDimension())) {
                    player.sendMessage(CUtl.LANG.error("dimension"));
                    return;
                }

                // LOGIC
                // add the cooldown
                player.getPCache().setSocialCooldown(Data.getConfig().getSocial().getCooldown());

                player.sendMessage(CUtl.tag().append(LANG.msg("sent",CTxT.of(target.getName()).color(CUtl.s()),
                        CTxT.of("\n ").append(dest.getBadge()))));

                target.sendMessage(CUtl.tag().append(LANG.msg("sent_target",CTxT.of(player.getName()).color(CUtl.s()),getSendTxt(target,dest))));

                DHud.inbox.addDest(target,player,999,dest);
            }

            /**
             * gets the sent Loc as a CTxt for the receiver
             * @param dest the Loc to display
             * @return the CTxT built
             */
            public static CTxT getSendTxt(Player player, Dest dest) {
                CTxT txt = CTxT.of("").append(dest.getBadge()).append(" ");
                // if color is null, empty string
                String colorCMD = dest.getColor() == null ? "" : " \""+dest.getColor()+"\"";
                // if no name, have the placeholder name for the player to change it later
                String nameCMD = dest.getName() == null ? Suggester.wrapQuotes(LANG.get("default_save_name")) : Suggester.wrapQuotes(dest.getName());
                // wrap the dimension in quotes
                String dimCMD = Suggester.wrapQuotes(dest.getDimension());
                // ADD
                if (Helper.checkEnabled(player).saving())
                    txt.append(saved.SAVE_BUTTON("/dest saved add "+ nameCMD +" "+dest.getXYZ()+" "+dimCMD+" "+colorCMD)).append(" ");
                // SET & CONVERT
                txt.append(Destination.dest.setButtons(dest,
                        Dimension.canConvert(player.getDimension(),dest.getDimension())));
                return txt;
            }
        }

        public static class track {

            public static final Lang LANG = new Lang("destination.track.");

            public static CTxT BUTTON(boolean x) {
                return CTxT.of("")
                        .append(LANG.btn().btn(true).color(Assets.mainColors.track).cEvent(2,"/dest track set")
                                .hEvent(CTxT.of(Assets.cmdUsage.destTrack).color(Assets.mainColors.track).append("\n").append(LANG.hover())))
                        .append(CTxT.of(Assets.symbols.x).btn(true).color(x? 'c':'7').cEvent(x? 1:0,"/dest track clear")
                                .hEvent(CTxT.of(Assets.cmdUsage.destTrackClear).color(x? 'c':'7').append("\n").append(LANG.hover("clear"))));
            }

            public static void CMDExecutor(Player player, String[] args) {
                if (!Helper.checkEnabled(player).track()) return;
                //dest track
                if (args.length == 1 && args[0].equalsIgnoreCase("clear")) {
                    clear(player, 1);
                    return;
                }
                if (args.length == 2) {
                    // if there is -r, remove it and enable returning
                    boolean Return = args[0].contains("-r");
                    args[0] = args[0].replace("-r","");

                    switch (args[0]) {
                        case "accept" -> process(player, args[1], ProcessType.accept, Return);
                        case "deny" -> process(player, args[1], ProcessType.deny, Return);
                        case "cancel" -> process(player, args[1], ProcessType.cancel, Return);
                        case "set" -> initialize(player, args[1]);
                        default -> player.sendMessage(CUtl.usage(Assets.cmdUsage.destTrack));
                    }
                } else player.sendMessage(CUtl.usage(Assets.cmdUsage.destTrack));
            }
            public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
                ArrayList<String> suggester = new ArrayList<>();
                // track (clear*|set|cancel*|accept*|deny*)
                if (pos == 0) {
                    if (hasTargetEntry(player)) suggester.add("clear");
                    suggester.add("set");
                    if (DHud.inbox.getAllType(player, DHud.inbox.Type.track_pending)!=null) suggester.add("cancel");
                    if (DHud.inbox.getAllType(player, DHud.inbox.Type.track_request)!=null) {
                        suggester.add("accept");
                        suggester.add("deny");
                    }
                }
                if (pos == 1) {
                    switch (args[0]) {
                        // track set <plauer>
                        case "set" -> suggester.addAll(Suggester.players(player));
                        // track accept/deny <target>
                        case "accept", "deny" -> {
                            // get all track requests
                            ArrayList<HashMap<String,String>> matches = DHud.inbox.getAllType(player, DHud.inbox.Type.track_request);
                            // if there are any display the names
                            if (matches != null) {
                                for (HashMap<String, String> entry : matches)
                                    suggester.add(entry.get("player_name"));
                            }
                        }
                        case "cancel" -> {
                            // get all track pendings
                            ArrayList<HashMap<String,String>> matches = DHud.inbox.getAllType(player, DHud.inbox.Type.track_pending);
                            // if there are any display the names
                            if (matches != null) {
                                for (HashMap<String,String> entry:matches)
                                    suggester.add(entry.get("player_name"));
                            }
                        }
                    }
                }
                return suggester;
            }

            /**
             * the types of track processing
             */
            public enum ProcessType {
                accept,
                deny,
                cancel
            }

            /**
             * returns the current tracking target as a player
             * @return the target
             */
            public static Player getTarget(Player player) {
                // get from cache, called from loop
                String track = player.getPCache().getDEST().getTracking();
                if (track == null) return new Player();
                return new Player(track);
            }

            /**
             * if the player has an entry in the target section (NOT IF ITS VALID OR NOT)
             */
            public static boolean hasTargetEntry(Player player) {
                return player.getPCache().getDEST().getTracking() != null;
            }

            /**
             * clear the tracker with a reason
             * @param reason 1 = command clear, 2 = tracking off, 3 = target tracking off
             */
            public static void clear(Player player, int reason) {
                // nothing to clear
                if (!hasTargetEntry(player)) {
                    player.sendMessage(LANG.error("cleared"));
                    return;
                }
                // get the reason for clearing
                CTxT reasonTxT = LANG.msg("cleared." + switch (reason) {
                    default -> "command"; case 2 -> "tracking_off"; case 3 -> "tracking_off_target";
                }).color('7');
                // clear the tracker
                clear(player);
                // send the message
                player.sendMessage(CUtl.tag().append(LANG.msg("cleared",reasonTxT)));
            }

            /**
             * clears the tracker
             */
            public static void clear(Player player) {
                // clear everything to do with tracking in the msg data system
                for (String key: player.getPCache().getMsgKeys())
                    if (key.contains("tracking")) player.getPCache().setMsg(key,0);
                // remove the target
                player.getPData().getDEST().setTracking(null);
            }

            /**
             * sets the tracker to the target player with a message
             * @param player the person tracking
             * @param target the target
             */
            public static void set(Player player, Player target) {
                // if online, use UUID, if not use the target NAME
                if (Data.getConfig().getOnline()) player.getPData().getDEST().setTracking(target.getUUID());
                else player.getPData().getDEST().setTracking(target.getName());
                // get both players as CTxT
                CTxT playerTxT = CTxT.of(player.getName()).color(CUtl.s()), targetTxT = CTxT.of(target.getName()).color(CUtl.s());
                // send messages to both target and tracker
                player.sendMessage(CUtl.tag().append(LANG.msg("set",targetTxT)));
                target.sendMessage(CUtl.tag().append(LANG.msg("accept",playerTxT)));
            }

            /**
             * logic for initializing a tracking request between 2 players
             * @param player the tracker
             * @param target_string the target (as a string)
             */
            public static void initialize(Player player, String target_string) {
                Player target = new Player(target_string);
                // cooldown check
                if (cooldown(player)) return;
                // bad data check
                if (!target.isValid()) {
                    player.sendMessage(CUtl.LANG.error("player", CTxT.of(target_string).color(CUtl.s())));
                    return;
                }
                if (target.equals(player)) {
                    player.sendMessage(LANG.error("self"));
                    return;
                }
                if (!(boolean) player.getPData().getDEST().getSetting(Setting.features__track)) {
                    player.sendMessage(LANG.error("target_disabled",target.getHighlightedName()));
                    return;
                }
                // tracking request already pending
                if (DHud.inbox.search(player, DHud.inbox.Type.track_pending,"player_name",target_string) != null) {
                    player.sendMessage(LANG.error("pending",target.getHighlightedName()));
                    return;
                }
                // already tracking the target
                if (getTarget(player).isValid() && Objects.equals(getTarget(player), target)) {
                    player.sendMessage(LANG.error("already_tracking",target.getHighlightedName()));
                    return;
                }

                // logic

                // add the cooldown
                player.getPCache().setSocialCooldown(Data.getConfig().getSocial().getCooldown());

                // target has instant tracking
                if (Enums.get(player.getPData().getDEST().getSetting(Setting.features__track_request_mode),Setting.TrackingRequestMode.class)
                        .equals(Setting.TrackingRequestMode.instant)) {
                    set(player,target);
                    return;
                }
                // add the tracking to the inbox
                DHud.inbox.addTracking(target,player,300);
                // send the messages
                player.sendMessage(CUtl.tag().append(LANG.msg("request",target.getHighlightedName())).append("\n ")
                        .append(LANG.msg("expire",300).color('7').italic(true)));

                target.sendMessage(CUtl.tag().append(LANG.msg("request_target",player.getHighlightedName())).append("\n ")
                        .append(CUtl.LANG.btn("accept").btn(true).color('a').cEvent(1,"/dest track accept "+player.getName())
                                .hEvent(CUtl.LANG.hover("accept"))).append(" ")
                        .append(CUtl.LANG.btn("deny").btn(true).color('c').cEvent(1,"/dest track deny "+player.getName())
                                .hEvent(CUtl.LANG.hover("deny"))));
            }

            /**
             * processes a tracking request
             * @param tracker the target
             * @param type the type of processing
             * @param Return if it should return to the inbox or not
             */
            public static void process(Player player, String tracker, ProcessType type, boolean Return) {
                // processing both accepting and denying @ same time because the code is so similar
                // removing bad data woo
                Player target = new Player(tracker);
                // if player in questions is null
                if (!target.isValid()) {
                    player.sendMessage(CUtl.LANG.error("player",CTxT.of(tracker).color(CUtl.s())));
                    return;
                }
                if (player == target) {
                    player.sendMessage(LANG.error("self"));
                    return;
                }

                // get the entry from the player inbox
                // tracK_request if accept or deny, track_pending if canceling
                HashMap<String, String> entry = DHud.inbox.search(player, DHud.inbox.Type.track_request,"player_name", tracker);
                if (type.equals(ProcessType.cancel)) entry = DHud.inbox.search(player, DHud.inbox.Type.track_pending,"player_name", tracker);

                // entry doesn't exist
                if (entry == null) {
                    player.sendMessage(LANG.error("none",target.getHighlightedName()));
                    return;
                }
                // get the ID
                String ID = entry.get("id");
                // the IDs don't match - SYNC ERROR
                if (DHud.inbox.search(target, null,"id", ID) ==null) {
                    DHud.inbox.removeEntry(player,entry);
                    player.sendMessage(CUtl.tag().append("SYNC ERROR - REPORT IT! (ID-MISMATCH)"));
                    return;
                }
                // if the target has tracking turned off - SYNC ERROR
                if (!(boolean) player.getPData().getDEST().getSetting(Setting.features__track)) {
                    DHud.inbox.removeEntry(player,entry);
                    player.sendMessage(CUtl.tag().append("SYNC ERROR - REPORT IT! (TARGET-TRACK-OFF)"));
                    return;
                }

                // remove from both inboxes
                DHud.inbox.delete(player,ID,false);
                DHud.inbox.delete(target,ID,false);

                //different message based on the type
                if (type.equals(ProcessType.accept)) {
                    set(target,player);
                } else if (type.equals(ProcessType.deny)) {
                    target.sendMessage(CUtl.tag().append(LANG.msg("denied",player.getHighlightedName())));
                    player.sendMessage(CUtl.tag().append(LANG.msg("deny",target.getHighlightedName())));
                } else if (type.equals(ProcessType.cancel)) {
                    player.sendMessage(CUtl.tag().append(LANG.msg("cancel",target.getHighlightedName())));
                    target.sendMessage(CUtl.tag().append(LANG.msg("canceled",player.getHighlightedName())));
                }
                if (Return) player.performCommand("dhud inbox");
            }
            /**
             * logic for sending temporary tracking messages to player
             */
            public static void logic(Player player) {
                // if there isn't an entry in the tracking
                if (!getTarget(player).isValid()) return;
                // INFO (null if not sent, not if otherwise)
                // tracking.offline = target offline
                // tracking.dimension = not in same dimension & cant convert (trail cold)
                // tracking.converted = tracker converted message

                // if the server turned social off / player has tracking disabled
                if (!Helper.checkEnabled(player).track()) {
                    Destination.social.track.clear(player, 2);
                    return;
                }
                Player target = Destination.social.track.getTarget(player);
                // clear if tracking oneself, dunno how its possible, but it happened before
                if (target.equals(player)) {
                    Destination.social.track.clear(player);
                    return;
                }
                // if the target is null, means the player cant be found, probably offline
                if (!target.isValid()) {
                    if (player.getPCache().getMsg("tracking.offline") == 0) {
                        // the offline message hasn't been sent
                        player.sendMessage(CUtl.tag().append(LANG.msg("target_offline")));
                        player.getPCache().setMsg("tracking.offline", 1);
                        // reset all other messages
                        player.getPCache().setMsg("tracking.converted",0);
                        player.getPCache().setMsg("tracking.dimension",0);
                    }
                    return;
                }
                // target turned off tracking
                if (!Helper.checkEnabled(player).track()) {
                    Destination.social.track.clear(player,3);
                    return;
                }
                // ------- TRACKING IS ON -------
                // if the offline message was sent, reset it and send the back message
                if (player.getPCache().getMsg("tracking.offline") != 0) {
                    player.sendMessage(CUtl.tag().append(LANG.msg("resumed"))); // tracking resumed msg
                    player.getPCache().setMsg("tracking.offline",0);
                }
                // target is in the same dimension as the player
                if (target.getDimension().equals(player.getDimension())) {
                    // if convert message has been sent before
                    if (player.getPCache().getMsg("tracking.converted") != 0) {
                        // send convert message to let player know the tracker was converted back to local dimension
                        player.sendMessage(CUtl.tag().append(Destination.LANG.msg("autoconvert.tracking",
                                Destination.LANG.msg("autoconvert.tracking.2",
                                                CTxT.of(Dimension.getName(target.getDimension())).italic(true).color(Dimension.getColor(target.getDimension()))))));
                        player.getPCache().setMsg("tracking.converted",0);
                    }
                    // if tracking was stopped before
                    if (player.getPCache().getMsg("tracking.dimension") != 0) {
                        /// send resume message to let player know the tracker was enabled again
                        player.sendMessage(CUtl.tag().append(LANG.msg("resumed")));
                        player.getPCache().setMsg("tracking.dimension",0);
                    }
                    return;
                }
                // ------- target isn't in the same dimension as the player -------
                // if AUTOCONVERT IS ON AND CONVERTIBLE
                if (player.getPCache().getDEST().getDestSettings().getAutoconvert() &&
                        Dimension.canConvert(player.getDimension(),target.getDimension())) {
                    // send the tracking resumed message if tracking was disabled from dimension differences (autoconvert was enabled midway, ect.)
                    if (player.getPCache().getMsg("tracking.dimension") != 0) {
                        player.sendMessage(CUtl.tag().append(LANG.msg("resumed")));
                        player.getPCache().setMsg("tracking.dimension",0);
                    }
                    // send the convert message if it hasn't been sent
                    if (player.getPCache().getMsg("tracking.converted") == 0) {
                        player.sendMessage(CUtl.tag().append(Destination.LANG.msg("autoconvert.tracking",
                                Destination.LANG.msg("autoconvert.tracking.2",
                                        CTxT.of(Dimension.getName(target.getDimension())).italic(true).color(Dimension.getColor(target.getDimension()))))));
                        // change the status on the convert message
                        player.getPCache().setMsg("tracking.converted",1);
                    }
                } else if (player.getPCache().getMsg("tracking.dimension") == 0) {
                    // if not convertible or AutoConvert is off, & the dimension difference message hasn't been sent,
                    // send the dimension message
                    player.sendMessage(CUtl.tag().append(LANG.msg("target_dimension",
                            Destination.LANG.msg("autoconvert.tracking.2",
                                            CTxT.of(Dimension.getName(target.getDimension())).italic(true).color(Dimension.getColor(target.getDimension()))))));
                    player.getPCache().setMsg("tracking.dimension", 1);
                    // make sure the converted msg is reset
                    player.getPCache().setMsg("tracking.converted", 0);
                }
            }
        }
    }

    public static class settings {
        public static final Lang LANG = new Lang("destination.setting.");
        /**
         * the main button for destination SETTINGS
         */
        public static CTxT BUTTON = CUtl.LANG.btn("settings").btn(true).color(Assets.mainColors.setting)
                .cEvent(1,"/dest settings")
                .hEvent(CTxT.of(Assets.cmdUsage.destSettings).color(Assets.mainColors.setting).append("\n")
                        .append(CUtl.LANG.hover("settings",CUtl.LANG.get("destination"))));
        public static void CMDExecutor(Player player, String[] args) {
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
                if (args.length == 1) reset(player,Setting.none,Return);
                    // reset (module) - per module
                else reset(player, Setting.get(args[1]),Return);
            }
            // SET
            if (args[0].equals("set")) {
                if (args.length != 3) player.sendMessage(CUtl.error("args"));
                // else set
                else change(player, Setting.get(args[1]),args[2],Return);
            }
            if (args[0].equals("colorui")) {
                if (args.length == 3) colorUI(player,args[2],Setting.get(args[1]));
            }
        }
        public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // base
            if (pos == 0) {
                suggester.add("set");
                suggester.add("reset");
                return suggester;
            }
            // if -r is attached, remove it and continue with the suggester
            args[0] = args[0].replaceAll("-r", "");
            // settings (set, reset)
            if (pos == 1) {
                // add all settings
                for (Setting s: Setting.values())
                    suggester.add(s.toString());
                // remove none as it was added when adding all settings
                suggester.remove(Setting.none.toString());
            }
            // settings set (type)
            if (pos == 2 && args[0].equalsIgnoreCase("set")) {
                Setting setting = Setting.get(args[1]);
                // boolean settings
                if (Setting.bool().contains(setting)) {
                    suggester.add("on");
                    suggester.add("off");
                }
                // color settings
                if (Setting.colors().contains(setting))
                    suggester.addAll(Suggester.colors(player,Suggester.getCurrent(args,pos),true));
                // tracking request
                if (setting.equals(Setting.features__track_request_mode))
                    suggester.addAll(Enums.toStringList(Enums.toArrayList(TrackingRequestMode.values())));
                // autoclear.rad
                if (setting.equals(Setting.autoclear_rad))
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
            return PlayerData.getDefaults().getDEST().getSetting(setting);
        }
        /**
         * resets the setting to the config state
         * @param setting setting to reset
         * @param Return to return back to the settings UI
         */
        public static void reset(Player player, Setting setting, boolean Return) {
            // reset all
            if (setting.equals(Setting.none)) {
                // reset every setting
                for (Setting s : Setting.values()) player.getPData().getDEST().setSetting(s,getConfig(s));
            } else {
                // else reset the selected setting
                player.getPData().getDEST().setSetting(setting,getConfig(setting));
            }
            // reset every setting that has children
            // also reset autoclear RAD if autoclear
            if (setting.equals(Setting.autoclear))
                player.getPData().getDEST().setSetting( Setting.autoclear_rad,getConfig(Setting.autoclear_rad));
            // resetting particle settings, reset the color
            if (Setting.particles().contains(setting))
                player.getPData().getDEST().setSetting(Setting.get(setting+"_color"),getConfig(Setting.get(setting+"_color")));
            // reset track mode for track reset
            if (setting.equals(Setting.features__track))
                player.getPData().getDEST().setSetting( Setting.features__track_request_mode,getConfig(Setting.features__track_request_mode));

            CTxT msg = CUtl.tag().append(CUtl.LANG.msg("reset",LANG.get(setting.toString()).color(CUtl.s())));
            if (setting.equals(Setting.none)) msg = CUtl.tag().append(LANG.msg("reset_all",CUtl.LANG.btn("all").color('c')));

            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        /**
         * code for changing Destination settings
         * @param setting the setting to change
         * @param state the new state for the setting
         * @param Return to return back to the settings UI
         */
        public static void change(Player player, Setting setting, String state, boolean Return) {
            boolean bool = state.equals("on");
            CTxT setTxT = CTxT.of("");
            // custom setter for custom settings
            if (setting.equals(Setting.autoclear_rad)) {
                if (!Helper.Num.isInt(state)) {
                    player.sendMessage(CUtl.LANG.error("number"));
                    return;
                }
                int i = Math.max(Math.min(Num.toInt(state),15),1);
                player.getPData().getDEST().setSetting(Setting.autoclear_rad,i);
                setTxT.append(CTxT.of(String.valueOf(i)).color((boolean) player.getPData().getDEST().getSetting(Setting.autoclear)?'a':'c'));
            }
            // req mode set
            if (setting.equals(Setting.features__track_request_mode)) {
                player.getPData().getDEST().setSetting( setting, Enums.get(state,Setting.TrackingRequestMode.class));
                setTxT.append(LANG.get(setting +"."+ Enums.get(state,Setting.TrackingRequestMode.class)).color(CUtl.s()));
            }
            // color set
            if (Setting.colors().contains(setting)) {
                setParticleColor(player,null,setting,state,false);
                setTxT.append(CUtl.color.getBadge((String) player.getPData().getDEST().getSetting(setting)));
            }
            // if bool, boolean set
            if (Setting.bool().contains(setting)) {
                player.getPData().getDEST().setSetting(setting,bool);
                setTxT.append(CUtl.toggleTxT(bool));
            }
            // message generator
            CTxT msg = CUtl.tag();
            // particle boolean message
            if (Setting.particles().contains(setting)) {
                msg.append(LANG.msg("set.toggle",LANG.get("particle",LANG.get(setting.toString()).color(CUtl.s())),setTxT));
            }
            // boolean message
            else if (Setting.bool().contains(setting)) {
                msg.append(LANG.msg("set.toggle",LANG.get(setting.toString()).color(CUtl.s()),setTxT));
            }
            else {
                msg.append(LANG.msg("set",LANG.get(setting.toString()).color(CUtl.s()),setTxT));
            }

            if (Return) UI(player, msg);
            else player.sendMessage(msg);
        }
        /**
         * checks if a setting can be reset by comparing the current state to the config state
         */
        public static boolean canBeReset(Player player, Setting type) {
            boolean output = false;
            if (type.equals(Setting.none)) return false;
            if (player.getPData().getDEST().getSetting(type) != getConfig(type)) output = true;
            if (type.equals(Setting.autoclear))
                if ((int)player.getPData().getDEST().getSetting(Setting.autoclear_rad) != (int)getConfig(Setting.autoclear_rad)) output = true;
            if (type.equals(Setting.features__track))
                if (!player.getPData().getDEST().getSetting(Setting.features__track_request_mode).equals(getConfig(Setting.features__track_request_mode))) output = true;
            if (Setting.colors().contains(Setting.get(type+"_color")))
                if (!player.getPData().getDEST().getSetting(Setting.get(type+"_color")).equals(getConfig(Setting.get(type+"_color")))) output = true;
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
                msg.color('c').cEvent(1, "/dest settings reset-r " + setting)
                        .hEvent(CUtl.LANG.hover("reset",
                                LANG.get(setting.toString()).color('c'),
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
            boolean state = (boolean) player.getPData().getDEST().getSetting(setting);
            CTxT button = CTxT.of("");
            if (setting.equals(Setting.none)) return button;
            // if boolean setting add the toggle button
            if (Setting.bool().contains(setting)) button.append(CUtl.toggleBtn(state,"/dest settings set-r "+setting+" ")).append(" ");
            // autoclear
            if (setting.equals(Setting.autoclear)) {
                // get int values of the doubles in the PlayerData files to look better
                button.append(CTxT.of(String.valueOf((int) player.getPData().getDEST().getSetting(Setting.get(setting+"_rad")))).btn(true)
                        .color(state?'a':'c').cEvent(2,"/dest settings set-r "+setting+"_rad ")
                        .hEvent(LANG.get(Setting.autoclear_rad+".ui").color(state?'a':'c').append("\n")
                                .append(LANG.hover("set.custom",LANG.get(Setting.autoclear_rad.toString()))).append("\n")
                                .append(LANG.get(setting+"_rad.hover").color('7'))));
            }
            // track mode
            if (setting.equals(Setting.features__track)) {
                Setting type = Setting.features__track_request_mode;
                TrackingRequestMode mode = Enums.get(player.getPData().getDEST().getSetting(type),TrackingRequestMode.class);
                TrackingRequestMode nextMode = Enums.next(mode,TrackingRequestMode.class);
                button.append(CTxT.of(mode.getSymbol()).btn(true).color(CUtl.s())
                        .cEvent(1,"/dest settings set-r "+type+" "+nextMode)
                        .hEvent(LANG.get(type+".ui").color(CUtl.s()).append(" - ").append(LANG.get(type+"."+mode)).append("\n")
                                .append(LANG.get(type+"."+mode+".info").color('7')).append("\n\n")
                                .append(LANG.hover("set",LANG.get(type.toString()),LANG.get(type+"."+nextMode).color(CUtl.s())))));
            }
            // particles
            if (Setting.particles().contains(setting)) {
                String color = (String) player.getPData().getDEST().getSetting(Setting.get(setting+"_color"));
                button.append(CTxT.of(Assets.symbols.pencil).btn(true).color(color)
                        .cEvent(1,"/dest settings colorui "+setting+"_color "+ DHud.preset.DEFAULT_UI_SETTINGS)
                        .hEvent(LANG.hover("set.color",LANG.get(setting.toString()).color(color))));
            }
            return button;
        }
        /**
         * the color UI for particle color
         * @param UISettings the UI settings
         * @param setting the particle color to edit
         */
        public static void colorUI(Player player, String UISettings, Setting setting) {
            // return if not a color setting
            if (!Setting.colors().contains(setting)) return;
            String currentColor = (String) player.getPData().getDEST().getSetting(setting);
            // get the current color
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                               ").strikethrough(true);
            msg.append(" ").append(LANG.ui("particle_color",
                            // get the base particle color name by removing _color
                            LANG.get(setting.toString().replace("_color","")+".ui")).color(currentColor))
                    .append(line).append("\n")
                    .append(DHud.preset.colorEditor(currentColor,UISettings, DHud.preset.Type.dest,setting.toString(),"/dest settings colorui "+setting+" %s"))
                    .append("\n\n           ").append(CUtl.CButton.back("/dest settings")).append(line);
            player.sendMessage(msg);
        }
        /**
         * sets the particle color
         * @param UISettings the UI settings if returning to color UI
         * @param type the particle color to change
         * @param color the color to set to
         * @param Return return to color UI
         */
        public static void setParticleColor(Player player, String UISettings, Setting type, String color, boolean Return) {
            // format color
            color = CUtl.color.colorHandler(player,color,(String)player.getPData().getDEST().getSetting(type));
            player.getPData().getDEST().setSetting(type,color);
            if (Return) colorUI(player,UISettings,type);
        }

        /**
         * the main UI for destination settings
         * @param aboveTxT the TxT to show above the UI
         */
        public static void UI(Player player, CTxT aboveTxT) {
            CTxT msg = CTxT.of(""), line = CTxT.of("\n                                ").strikethrough(true);
            if (aboveTxT != null) msg.append(aboveTxT).append("\n");
            msg.append(" ").append(LANG.ui().color(Assets.mainColors.setting)).append(line).append("\n");
            //DEST
            msg.append(" ").append(LANG.ui("category.destination").color(CUtl.p())).append(":\n  ");
            msg     //AUTOCLEAR
                    .append(resetBtn(player, Setting.autoclear)).append(" ")
                    .append(LANG.get(Setting.autoclear.toString()).hEvent(LANG.get(Setting.autoclear.toString()).append("\n")
                                    .append(LANG.get(Setting.autoclear+".info").color('7')).append("\n")
                                    .append(LANG.get(Setting.autoclear+".info.2").color('7').italic(true)).append("\n")
                                    .append(LANG.get(Setting.autoclear_rad+".ui")).append("\n")
                                    .append(LANG.get(Setting.autoclear_rad+".info").color('7'))))
                    .append(": ").append(getButtons(player, Setting.autoclear)).append("\n  ");
            msg     //AUTOCONVERT
                    .append(resetBtn(player, Setting.autoconvert)).append(" ")
                    .append(LANG.get(Setting.autoconvert.toString()).hEvent(LANG.get(Setting.autoconvert.toString()).append("\n")
                            .append(LANG.get(Setting.autoconvert+".info").color('7')).append("\n ")
                            .append(LANG.get(Setting.autoconvert+".info.2").color('7'))))
                    .append(": ").append(getButtons(player, Setting.autoconvert)).append("\n  ");
            msg     //YLEVEL
                    .append(resetBtn(player, Setting.ylevel)).append(" ")
                    .append(LANG.get(Setting.ylevel+".ui").hEvent(LANG.get(Setting.ylevel+".ui").append("\n")
                            .append(LANG.get(Setting.autoconvert+".info",
                                    LANG.get(Setting.autoconvert+".info_2").color(CUtl.s()),
                                    LANG.get(Setting.autoconvert+".info_2").color(CUtl.s())).color('7'))))
                    .append(": ").append(getButtons(player, Setting.ylevel)).append("\n ");
            //PARTICLES
            msg.append(LANG.ui("category.particles").color(CUtl.p())).append(":\n  ");
            msg     //DESTINATION
                    .append(resetBtn(player, Setting.particles__dest)).append(" ")
                    .append(LANG.get(Setting.particles__dest+".ui").hEvent(LANG.get(Setting.particles__dest+".ui").append("\n")
                            .append(LANG.get(Setting.particles__dest+".info").color('7'))))
                    .append(": ").append(getButtons(player, Setting.particles__dest)).append("\n  ");
            msg     //LINE
                    .append(resetBtn(player, Setting.particles__line)).append(" ")
                    .append(LANG.get(Setting.particles__line+".ui").hEvent(LANG.get(Setting.particles__line+".ui").append("\n")
                            .append(LANG.get(Setting.particles__line+".info").color('7'))))
                    .append(": ").append(getButtons(player, Setting.particles__line)).append("\n  ");
            msg     //TRACK
                    .append(resetBtn(player, Setting.particles__tracking)).append(" ")
                    .append(LANG.get(Setting.particles__tracking+".ui").hEvent(LANG.get(Setting.particles__tracking+".ui").append("\n")
                            .append(LANG.get(Setting.particles__tracking+".info").color('7'))))
                    .append(": ").append(getButtons(player, Setting.particles__tracking)).append("\n ");
            // only show if needed
            if (Data.getConfig().getSocial().getEnabled() || Data.getConfig().getDestination().getLastDeath().getSaving()) {
                //FEATURES
                msg.append(LANG.ui("category.features").color(CUtl.p())).append(":\n  ");
                if (Data.getConfig().getSocial().getEnabled()) {
                    msg     //SEND
                            .append(resetBtn(player, Setting.features__send)).append(" ")
                            .append(LANG.get(Setting.features__send+".ui").hEvent(LANG.get(Setting.features__send+".ui").append("\n")
                                    .append(LANG.get(Setting.features__send+".info").color('7'))))
                            .append(": ").append(getButtons(player, Setting.features__send)).append("\n  ");
                    msg     //TRACK
                            .append(resetBtn(player, Setting.features__track)).append(" ")
                            .append(LANG.get(Setting.features__track+".ui").hEvent(LANG.get(Setting.features__track+".ui").append("\n")
                                    .append(LANG.get(Setting.features__track+".info").color('7')).append("\n")
                                    .append(LANG.get(Setting.features__track_request_mode+".ui")).append("\n")
                                    .append(LANG.get(Setting.features__track_request_mode+".info").color('7'))))
                            .append(": ").append(getButtons(player, Setting.features__track)).append("\n  ");
                }
                if (Data.getConfig().getDestination().getLastDeath().getSaving()) {
                    msg     //LASTDEATH
                            .append(resetBtn(player, Setting.features__lastdeath)).append(" ")
                            .append(LANG.get(Setting.features__lastdeath.toString()).hEvent(LANG.get(Setting.features__lastdeath.toString()).append("\n")
                                    .append(LANG.get(Setting.features__lastdeath+".info").color('7'))))
                            .append(": ").append(getButtons(player, Setting.features__lastdeath)).append("\n ");
                }
            }
            CTxT reset = CUtl.LANG.btn("reset").btn(true).color('7');
            boolean resetOn = false;
            // see if a setting can be reset, then flip the switch
            for (Setting t: Setting.base()) {
                // if reset is on quit the loop
                if (resetOn) break;
                // if lastdeath is off in the config, skip
                if (!Data.getConfig().getDestination().getLastDeath().getSaving() && t.equals(Setting.features__lastdeath)) continue;
                // if social is off in the config, skip
                if (!Data.getConfig().getSocial().getEnabled() && (t.equals(Setting.features__send) || t.equals(Setting.features__track))) continue;
                resetOn = canBeReset(player,t);
            }
            if (resetOn) reset.color('c').cEvent(1,"/dest settings reset-r all")
                    .hEvent(CUtl.LANG.hover("reset",CUtl.LANG.btn("all").color('c'),CUtl.LANG.hover("reset.settings")));
            // bottom row
            msg.append("\n     ").append(reset).append("  ").append(CUtl.CButton.back("/dest")).append(line);
            player.sendMessage(msg);
        }
    }

    public static CTxT BUTTON = LANG.btn().btn(true).color(Assets.mainColors.dest).cEvent(1,"/dest").hEvent(
                CTxT.of(Assets.cmdUsage.dest).color(Assets.mainColors.dest).append("\n").append(LANG.hover()));

    /**
     * main chatUI for the destination
     */
    public static void UI(Player player) {
        CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                  ").strikethrough(true);
        msg.append(LANG.ui("commands").color(Assets.mainColors.dest)).append(line).append("\n ");
        // lmao this is a mess but is it the best way to do it? dunno
        boolean line1Free = false;
        boolean line2Free = !Helper.checkEnabled(player).lastdeath();
        boolean sendThird = Helper.checkEnabled(player).send();
        //SAVED + ADD
        if (Helper.checkEnabled(player).saving() || Helper.checkEnabled(player).global()) {
            // if saving is on saving button
            if (Helper.checkEnabled(player).saving()) {
                msg.append(saved.BUTTON).append(saved.ADD_BUTTON);
            }
            // if not and global is on global button
            else if (Helper.checkEnabled(player).global()) {
                msg.append(saved.GLOBAL_BUTTON).append("  ");
            }

            if (!line2Free) msg.append("        ");
            else msg.append("  ");
        } else line1Free = true;
        //SET + CLEAR
        msg.append(dest.SET_BUTTON()).append(dest.CLEAR_BUTTON(player));
        if (line1Free) msg.append(" ");
        else msg.append("\n\n ");
        //LASTDEATH
        if (Helper.checkEnabled(player).lastdeath()) {
            msg.append(lastdeath.BUTTON);
            if (line1Free) {
                line1Free = false;
                line2Free = true;
                msg.append("\n\n ");
            } else msg.append("  ");
        }
        //SETTINGS
        msg.append(settings.BUTTON);
        if (line1Free) {
            msg.append("\n\n ");
        } else if (line2Free) msg.append("  ");
        else msg.append("\n\n ");
        //SEND
        if (Helper.checkEnabled(player).send()) {
            msg.append(social.send.BUTTON);
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
                line2Free = false;
                sendThird = false;
            } else msg.append(" ");
        }
        //TRACK
        if (Helper.checkEnabled(player).track()) {
            msg.append(social.track.BUTTON(social.track.hasTargetEntry(player)));
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
            } else if (line2Free) {
                if (Helper.checkEnabled(player).send()) msg.append(" ");
                else msg.append("   ");
            } else if (sendThird) {
                msg.append(" ");
            } else msg.append("   ");
        }
        //back
        msg.append(CUtl.CButton.back("/dhud")).append(line);
        player.sendMessage(msg);
    }
}