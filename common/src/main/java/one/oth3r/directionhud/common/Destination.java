package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.GlobalDest;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;
import one.oth3r.directionhud.common.utils.Helper.Dim;
import one.oth3r.directionhud.common.utils.Helper.Num;
import one.oth3r.directionhud.common.utils.Helper.ListPage;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Lang;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import one.oth3r.directionhud.common.Destination.Setting.*;

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
            if (!Utl.checkEnabled.destination(player)) return;
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
            if (!Utl.checkEnabled.destination(player)) return suggester;
            if (pos == 1) {
                if (config.LastDeathSaving && (boolean) player.getPData().getDEST().getSetting(Setting.features__lastdeath)) suggester.add("lastdeath");
                if (Utl.checkEnabled.saving(player)) {
                    suggester.add("add");
                    suggester.add("saved");
                }
                suggester.add("set");
                if (dest.get(player).hasXYZ()) suggester.add("clear");
                suggester.add("settings");
                if (Utl.checkEnabled.send(player)) suggester.add("send");
                if (Utl.checkEnabled.track(player)) suggester.add("track");
            }
            if (pos > 1) {
                String command = args[0].toLowerCase();
                String[] trimmedArgs = Helper.trimStart(args, 1);
                int fixedPos = pos - 2;
                switch (command) {
                    case "saved" -> suggester.addAll(saved.CMDSuggester(player,fixedPos,trimmedArgs));
                    case "add" -> suggester.addAll(saved.addCMDSuggester(player,fixedPos,trimmedArgs));
                    case "settings" -> suggester.addAll(settings.CMDSuggester(player, fixedPos,trimmedArgs));
                    case "color" -> {
                        if (fixedPos == 3 && trimmedArgs[0].equals("set")) suggester.addAll(Suggester.colors(player,Suggester.getCurrent(trimmedArgs,fixedPos)));
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
    private static CTxT lang(String key, Object... args) {
        return CUtl.getLangEntry("destination."+key, args);
    }
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
        public static Loc get(Player player) {
            Loc loc = player.getPData().getDEST().getDest();
            if (!loc.hasXYZ()) return new Loc();
            if ((boolean) player.getPData().getDEST().getSetting(Setting.ylevel)) loc.setY(player.getBlockY());
            return new Loc(loc);
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
            player.setPData().getDEST().setDest(new Loc());
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
            Loc current = get(player);
            // get the reason for clearing
            CTxT reasonTxT = LANG.msg("cleared." + switch (reason) {
                default -> "command"; case 2 -> "reached"; case 3 -> "dimension";
            }).append(" ");
            // add the set buttons
            reasonTxT.append(setButtons("/dest set "+current.getX()+" "+(current.getY()==null?"":current.getY()+" ")+current.getZ()+" "+
                            (Dim.checkValid(current.getDimension())?current.getDimension():player.getDimension()),
                    // only convert if reason is switching & convertible
                    reason == 3 && Dim.canConvert(player.getDimension(),current.getDimension())
            ));
            // clear the destination
            clear(player);
            // send the message
            player.sendMessage(CUtl.tag().append(LANG.msg("cleared", reasonTxT.color('7'))));
        }
        /**
         * sets the destination without notifying the player, still checks for autoclear instantly clearing or not
         */
        public static void set(Player player, Loc loc) {
            if (!inAutoClearRadius(player, loc)) player.setPData().getDEST().setDest(loc);
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
         * @param loc the location to set
         * @param convert to convert to the players dimension or not
         */
        public static void playerSet(Player player, Loc loc, boolean convert) {
            // handle bad data
            if (!loc.hasXYZ()) {
                player.sendMessage(CUtl.error("coordinates"));
                return;
            }
            if (loc.getDimension() == null) {
                player.sendMessage(CUtl.error("dimension"));
                return;
            }

            CTxT convertTag = CTxT.of("");
            if (convert && Dim.canConvert(player.getDimension(),loc.getDimension())) {
                // fill the convert tag
                convertTag.append(" ").append(LANG.msg("set.converted").color('7').italic(true).hEvent(loc.getBadge()));
                // convert the loc
                loc.convertTo(player.getDimension());
            }
            // check if already in autoclear radius
            if (inAutoClearRadius(player,loc)) {
                player.sendMessage(LANG.error("already_at"));
                return;
            }
            // set the destination and send the message
            set(player, loc);
            setMSG(player,loc.getBadge().append(convertTag));
        }
        /**
         * sets the destination to a saved destination
         * @param global if it's a global destination
         * @param name the name of the destination to get
         * @param convert to convert to the players destination or not
         */
        public static void setSaved(Player player, String name, boolean global, boolean convert) {
            saved.Dest dest = new saved.Dest(player,name,global);
            // handle bad data
            if (!dest.isValid()) {
                player.sendMessage(CUtl.error("dest.invalid"));
                return;
            }
            CTxT convertTag = CTxT.of("");
            Loc loc = dest.getDest();
            if (convert && Dim.canConvert(player.getDimension(),loc.getDimension())) {
                // fill the convert tag
                convertTag.append(" ").append(lang("converted_badge").color('7').italic(true).hEvent(loc.getBadge()));
                // convert the loc
                loc.convertTo(player.getDimension());
            }
            // check if already in autoclear radius (after converting)
            if (inAutoClearRadius(player,loc)) {
                player.sendMessage(CUtl.error("dest.at"));
                return;
            }
            set(player,loc);
            setMSG(player,loc.getBadge().append(convertTag));
        }
        /**
         * creates the set buttons for the destination
         * @param setCMD the command to set the destination
         * @param convert if the convert button should be there too
         * @return the set buttons
         */
        public static CTxT setButtons(String setCMD, boolean convert) {
            CTxT out = CTxT.of("");
            out.append(LANG.btn("set").btn(true).color(Assets.mainColors.set).cEvent(1,setCMD)
                    .hEvent(LANG.btn("set").color(Assets.mainColors.set).append("\n").append(LANG.hover("set"))));
            // make the convert button
            if (convert) out.append(" ")
                    .append(CTxT.of(Assets.symbols.convert).btn(true).color(Assets.mainColors.convert).cEvent(1,setCMD+" convert")
                            .hEvent(LANG.btn("convert").color(Assets.mainColors.convert).append("\n").append(LANG.hover("convert"))));
            return out;
        }
        public static void setCMDExecutor(Player player, String[] args) {
            if (args.length < 1) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSet));
                return;
            }
            // /dest set saved <name> (convert)
            if (args[0].equalsIgnoreCase("saved")) {
                // check if saving is on
                if (!Utl.checkEnabled.saving(player)) return;
                if (args.length == 2) setSaved(player, args[1], false, false);
                if (args.length == 3 && args[2].equalsIgnoreCase("convert")) setSaved(player, args[1], false, true);
                return;
            }
            // /dest set global <name> (convert)
            if (args[0].equalsIgnoreCase("global")) {
                if (!config.globalDESTs) return;
                if (args.length == 2) setSaved(player, args[1],true, false);
                if (args.length == 3 && args[2].equalsIgnoreCase("convert")) setSaved(player, args[1],true, true);
                return;
            }
            if (!Num.isInt(args[0]) || !Num.isInt(args[1])) return;
            // /dest set x z
            if (args.length == 2)
                playerSet(player,new Loc(Num.toInt(args[0]), Num.toInt(args[1]),player.getDimension()),false);
            // /dest set x z DIM
            if (args.length == 3 && !Num.isInt(args[2]))
                playerSet(player,new Loc(Num.toInt(args[0]), Num.toInt(args[1]),args[2]),false);
            // /dest set x y z
            if (args.length == 3 && Num.isInt(args[2]))
                playerSet(player,new Loc(Num.toInt(args[0]), Num.toInt(args[1]), Num.toInt(args[2]),player.getDimension()),false);
            // /dest set x z DIM (convert)
            if (args.length == 4 && !Num.isInt(args[2]))
                playerSet(player,new Loc(Num.toInt(args[0]), Num.toInt(args[1]),args[2]),true);
            // /dest set x y z DIM
            if (args.length == 4 && Num.isInt(args[2]))
                playerSet(player,new Loc(Num.toInt(args[0]), Num.toInt(args[1]), Num.toInt(args[2]),args[3]),false);
            // /dest set x y z DIM (convert)
            if (args.length == 5)
                playerSet(player,new Loc(Num.toInt(args[0]), Num.toInt(args[1]), Num.toInt(args[2]),args[3]),true);
        }
        public static ArrayList<String> setCMDSuggester(Player player, int pos, String[] args) {
            String current = Suggester.getCurrent(args, pos);
            ArrayList<String> suggester = new ArrayList<>();
            // set <saved> <name> (convert)
            // set <x> (y) <z> (dim) (convert)
            if (pos == 0) {
                if (Utl.checkEnabled.saving(player)) suggester.add("saved");
                if (config.globalDESTs) suggester.add("global");
                suggester.addAll(Suggester.xyz(player,current,3));
                return suggester;
            }
            // set <saved, global, x> ((name) (y))
            if (pos == 1) {
                if (args[0].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player))
                    suggester.addAll(saved.getCMDNames(saved.getList(player)));
                else if (args[0].equalsIgnoreCase("global") && config.globalDESTs)
                    suggester.addAll(saved.getCMDNames(GlobalDest.getDestinations()));
                else suggester.addAll(Suggester.xyz(player,current,2));
            }
            // set <saved> <name> ((convert))
            // set <x> (y) (<z> (dim))
            if (pos == 2) {
                if (!Num.isInt(args[1])) {
                    suggester.add("convert");
                } else {
                    suggester.addAll(Suggester.dims(current));
                    suggester.addAll(Suggester.xyz(player,current,1));
                }
            }
            // set <x> (y) <z> (dim)
            // set x z dim (convert)
            if (pos == 3) {
                if (Num.isInt(args[2])) suggester.addAll(Suggester.dims(current));
                else suggester.add("convert");
            }
            // set x y z dim convert
            if (pos == 4) {
                if (Num.isInt(args[2])) suggester.add("convert");
            }
            return suggester;
        }
    }
    public static class saved {
        private static final int PER_PAGE = 7;
        public static final Lang LANG = new Lang("destination.saved.");
        public static CTxT BUTTON = LANG.btn().btn(true).color(Assets.mainColors.saved).cEvent(1,"/dest saved")
                .hEvent(CTxT.of(Assets.cmdUsage.destSaved).color(Assets.mainColors.saved).append("\n").append(LANG.hover()));
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
            if (!Utl.checkEnabled.saving(player)) return;
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
                case "global" -> {
                    if (!config.globalDESTs) return;
                    globalCMDExecutor(player, Helper.trimStart(args,1));
                }
                case "edit" -> editCMDExecutor(player, Helper.trimStart(args, 1), false, Return);
                case "send" -> {
                    if (args.length == 2) player.sendMessage(CUtl.LANG.error("args"));
                    if (args.length == 3) {
                        // dest saved send (name)
                        Dest dest = new Dest(player, args[1], false);
                        if (dest.isValid()) social.send.logic(player,args[2],dest.getDest());
                        else player.sendMessage(Destination.LANG.error("invalid"));
                    }
                }
                case "delete" -> {
                    if (args.length == 1) player.sendMessage(CUtl.LANG.error("args"));
                    if (args.length == 2) delete(Return,player,new Dest(player, args[1], false));
                }
                case "add" -> addCMDExecutor(player, Helper.trimStart(args,1), false);
                default -> player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
            }
        }
        public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
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
            // if -r is attached, remove it and continue with the suggester
            if (args[0].contains("-r")) args[0] = args[0].replace("-r","");
            // switch for logic
            switch (args[0]) {
                case "global" -> suggester.addAll(globalCMDSuggester(player,pos-1, Helper.trimStart(args,1)));
                case "delete" -> {
                    if (pos == 1) suggester.addAll(getCMDNames(getList(player)));
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
            if (!Utl.checkEnabled.saving(player) || !config.globalDESTs) return;
            if (args.length == 0) {
                globalUI(player, 1);
                return;
            }
            if (Num.isNum(args[0])) {
                globalUI(player, Num.toInt(args[0]));
                return;
            }
            // PERMS FOR EDITING
            if (!Utl.checkEnabled.global(player)) return;
            switch (args[0]) {
                case "edit" -> editCMDExecutor(player, Helper.trimStart(args,1),true,false);
                case "delete" -> {
                    if (args.length == 1) player.sendMessage(CUtl.LANG.error("args"));
                    if (args.length == 2) delete(false,player,new Dest(player, args[1], true));
                }
                case "add" -> addCMDExecutor(player, Helper.trimStart(args,1), true);
                default -> player.sendMessage(CUtl.usage(Assets.cmdUsage.destSaved));
            }
        }
        public static ArrayList<String> globalCMDSuggester(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            // enabled check
            if (!Utl.checkEnabled.global(player)) return suggester;
            if (pos == 0) {
                suggester.add("add");
                suggester.add("edit");
                suggester.add("delete");
                return suggester;
            }
            // global delete
            if (args[0].equalsIgnoreCase("delete")) {
                if (pos == 1) suggester.addAll(getCMDNames(GlobalDest.getDestinations()));
            }
            // saved add
            if (args[0].equalsIgnoreCase("add")) {
                return addCMDSuggester(player,pos-1, Helper.trimStart(args,1));
            }
            // global edit
            if (args[0].equalsIgnoreCase("edit")) {
                return editCMDSuggester(player,true,pos-1, Helper.trimStart(args,1));
            }
            return suggester;
        }
        public static void editCMDExecutor(Player player, String[] args, boolean global, boolean Return) {
            if (args.length == 0) return;
            // edit (name)
            if (args.length == 1) {
                editUI(player,new Dest(player, args[0], global));
                return;
            }
            // edit (type) (args)
            switch (args[0]) {
                case "name" -> {
                    if (args.length == 3) editName(Return, player, new Dest(player,args[1],global), args[2]);
                    else player.sendMessage(Destination.LANG.error("invalid"));
                }
                case "color" -> {
                    if (args.length == 3) setColor(player, new Dest(player,args[1],global),DHUD.preset.DEFAULT_UI_SETTINGS, args[2], Return);
                    else player.sendMessage(Destination.LANG.error("invalid"));
                }
                case "colorui" -> {
                    if (args.length == 2) colorUI(player,DHUD.preset.DEFAULT_UI_SETTINGS,args[1]);
                    if (args.length == 3) colorUI(player,args[2],args[1]);
                }
                case "order" -> {
                    if (args.length == 3) editOrder(Return, player, new Dest(player,args[1],global), args[2]);
                    else player.sendMessage(Destination.LANG.error("invalid"));
                }
                case "location" -> {
                    if (args.length == 2) player.sendMessage(Destination.LANG.error("invalid"));
                    // location (dimension)
                    if (args.length == 3 && !Num.isInt(args[2])) {
                        Loc loc = new Loc();
                        loc.setDimension(args[2]);
                        editLocation(Return,player,new Dest(player,args[1],global),loc);
                    }
                    // location x z
                    if (args.length == 4) editLocation(Return,player,new Dest(player,args[1],global),
                            new Loc(Num.toInt(args[2]), Num.toInt(args[3])));
                    if (args.length == 5) {
                        // location x, y, z
                        if (Num.isInt(args[4])) editLocation(true,player,new Dest(player,args[1],global),
                                new Loc(Num.toInt(args[2]), Num.toInt(args[3]), Num.toInt(args[4])));
                        // location x, z, dim)
                        else editLocation(Return,player,new Dest(player,args[1],global),
                                new Loc(Num.toInt(args[2]), Num.toInt(args[3]), args[4]));
                    }
                    // location x, y, z, dim
                    if (args.length == 6) editLocation(Return,player,new Dest(player,args[1],global),
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
            if (pos == 1) suggester.addAll(getCMDNames(global ? GlobalDest.getDestinations() : getList(player)));
            // saved edit type name (<arg>)
            if (args[0].equalsIgnoreCase("location")) {
                if (pos == 2) {
                    suggester.addAll(Suggester.xyz(player,current,3));
                    suggester.addAll(Suggester.dims(current));
                }
                if (pos == 3) suggester.addAll(Suggester.xyz(player,current,2));
                if (pos == 4) {
                    suggester.addAll(Suggester.xyz(player,current,1));
                    suggester.addAll(Suggester.dims(current,false));
                }
                if (pos == 5 && Num.isInt(args[4])) {
                    suggester.addAll(Suggester.dims(current));
                }
                return suggester;
            }
            if (pos == 2) {
                if (args[0].equalsIgnoreCase("name")) {
                    suggester.add("\"name\"");
                    suggester.add(new Dest(player,args[1],global).getCMDName()); // current name to edit
                }
                if (args[0].equalsIgnoreCase("color"))
                    suggester.addAll(Suggester.colors(player,current));
                if (args[0].equalsIgnoreCase("order"))
                    suggester.add(String.valueOf(new Dest(player,args[1],global).getOrder())); //current order to edit
            }
            return suggester;
        }
        public static void addCMDExecutor(Player player, String[] args, boolean global) {
            if (!Utl.checkEnabled.saving(player)) return;
            //dest saved add <name>
            if (args.length == 1) {
                addButton(player,new Dest(player,new Loc(player,args[0]),global));
                return;
            }
            if (!Num.inBetween(args.length, 2, 6)) {
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destAdd));
                return;
            }
            Loc baseLoc = new Loc(player,args[0]);
            String playerDIM = player.getDimension();
            //dest saved add <name> color
            //dest saved add <name> dim
            if (args.length == 2) {
                if (Dim.checkValid(args[1])) {
                    baseLoc.setDimension(args[1]);
                } else {
                    baseLoc.setColor(args[1]);
                }
                addButton(player,new Dest(player,baseLoc,global));
                return;
            }
            //dest saved add <name> x z
            if (args.length == 3) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),null, Num.toInt(args[2]),playerDIM,args[0],null),global));
                return;
            }
            //dest saved add <name> x z color
            if (args.length == 4 && !Num.isInt(args[3]) && !Dim.checkValid(args[3])) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),null,Num.toInt(args[2]),playerDIM,args[0],args[3]),global));
                return;
            }
            //dest saved add <name> x y DIM
            if (args.length == 4 && !Num.isInt(args[3])) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),null,Num.toInt(args[2]),args[3],args[0],null),false));
                return;
            }
            //dest saved add <name> x y z
            if (args.length == 4 && Num.isInt(args[3])) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),playerDIM,args[0],null),false));
                return;
            }
            //dest saved add <name> x y DIM color
            if (args.length == 5 && !Num.isInt(args[3])) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),null,Num.toInt(args[2]),args[3],args[0],args[4]),false));
                return;
            }
            //dest saved add <name> x y z color
            if (args.length == 5 && !Dim.checkValid(args[4])) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),playerDIM,args[0],args[4]),false));
                return;
            }
            //dest saved add <name> x y z DIM
            if (args.length == 5) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),args[4],args[0],null),false));
                return;
            }
            //dest saved add <name> x y z DIM color
            if (args.length == 6) {
                addButton(player,new Dest(player,new Loc(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),args[4],args[0],args[5]),false));
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.destAdd));
        }
        public static ArrayList<String> addCMDSuggester(Player player, int pos, String[] args) {
            String current = Suggester.getCurrent(args,pos);
            ArrayList<String> suggester = new ArrayList<>();
            // add <name> <x> (y) <z> (dim) (color)
            if (pos == 0) {
                suggester.add("\"name\"");
                return suggester;
            }
            // add <name> (<x> (dim) (color))
            if (pos == 1) {
                suggester.addAll(Suggester.xyz(player,current,3));
                suggester.addAll(Suggester.colors(player,current,false));
                suggester.addAll(Suggester.dims(current,false));
            }
            // add <name> <x> ((y))
            if (pos == 2) {
                if (Num.isInt(args[1])) return Suggester.xyz(player,current,2);
            }
            // add <name> <x> (y) (<z> (dim) (color))
            if (pos == 3) {
                if (Num.isInt(args[1])) suggester.addAll(Suggester.xyz(player,current,1));
                suggester.addAll(Suggester.colors(player,current,false));
                suggester.addAll(Suggester.dims(current,false));
            }
            // add <name> <x> (y) <z> ((dim) (color))
            if (pos == 4) {
                if (Num.isInt(args[3])) {
                    suggester.addAll(Suggester.dims(current));
                    suggester.addAll(Suggester.colors(player,current));
                }
                if (Dim.checkValid(args[3]))
                    suggester.addAll(Suggester.colors(player,current));
            }
            // add <name> <x> (y) <z> (dim) ((color))
            if (pos == 5) {
                if (Num.isInt(args[3]) && Dim.checkValid(args[4]))
                    suggester.addAll(Suggester.colors(player,current));
            }
            return suggester;
        }

        /**
         * destination helper
         */
        public static class Dest {
            // dest helper
            private final List<Loc> list;
            private Loc dest;
            private final Player player;
            private Integer index;
            // figure out what type of destination it is on creation
            public final boolean global;

            /**
             * Dest from dest and list for dest
             * @param destEntry the destination
             * @param global weather it's a global destination or not
             */
            public Dest(Player player, Loc destEntry, boolean global) {
                // global dest list handling
                this.global = global;
                if (global) {
                    this.list = GlobalDest.getDestinations();
                } else {
                    this.list = saved.getList(player);
                }
                this.dest = destEntry;
                this.index = list.indexOf(destEntry);
                this.player = player;
            }

            /**
             * Dest from name in the list
             * @param name name of the dest
             * @param global weather it's a global destination or not
             */
            public Dest(Player player, String name, boolean global) {
                // global dest list handling
                this.global = global;
                if (global) {
                    this.list = GlobalDest.getDestinations();
                } else {
                    this.list = saved.getList(player);
                }
                this.player = player;
                // search for the destination using the name provided
                for (Loc entry: this.list) {
                    if (entry.getName().equals(name)) {
                        this.dest = entry;
                        this.index = list.indexOf(entry);
                        break;
                    }
                }
            }
            private void save() {
                if (global) {
                    // set the list to the edited list
                    GlobalDest.setDestinations(list);
                    // save changes to file
                    GlobalDest.mapToFile();
                } else player.setPData().getDEST().setSaved(list);
            }
            private void saveList() {
                if (index >= 0) {
                    list.set(index, dest);
                    save();
                }
            }
            public boolean isValid() {
                return this.dest != null;
            }
            public Loc getDest() {
                return dest;
            }
            public String getName() {
                return this.dest.getName();
            }
            public String getCMDName() {
                return "\""+this.getName()+"\"";
            }
            public void setName(String name) {
                // make sure the length is okay
                if (name.length() > Helper.MAX_NAME) name = name.substring(0, Helper.MAX_NAME);
                // set the name
                dest.setName(name);
                saveList();
            }
            public void setDest(Loc loc) {
                if (!loc.hasDestRequirements()) return;
                this.dest = loc;
                saveList();
            }
            public String getColor() {
                return dest.getColor();
            }
            public void setColor(String color) {
                dest.setColor(CUtl.color.colorHandler(player,color,this.getColor()));
                saveList();
            }
            public int getOrder() {
                return index+1;
            }

            /**
             * sets the order of the destination, has to already be added to the list
             * @param order the new order, player format +1
             */
            public void setOrder(int order) {
                list.remove(dest);
                // sub one because player entered order is one off
                order--;
                // make sure the order is not out of bounds
                if (order < 0) order = 0;
                if (order > list.size()) order = list.size();
                // set the index
                index = order;
                // add the dest back if empty, setting throws an error
                if (list.isEmpty()) list.add(dest);
                // set the new order in the list
                else list.set(index,dest);
                // save the list
                save();
            }
            /**
             * adds the destination to the list of not already
             */
            public void add() {
                if (!list.contains(dest)) {
                    list.add(dest);
                    // update the index & save
                    index = list.indexOf(dest);
                    save();
                }
            }
            /**
             * removes the destination from the list
             */
            public void remove() {
                list.remove(dest);
                save();
            }
            public List<Loc> getList() {
                return list;
            }
            public boolean isGlobal() {
                return global;
            }

            /**
             * validates a destination, sends errors to the player if necessary
             * @return if errors were sent or not
             */
            public boolean sendErrors() {
                if (this.isValid() && this.dest.hasDestRequirements()) {
                    // if valid but name is too long
                    if (this.dest.getName().length() > Helper.MAX_NAME) {
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
        public static List<Loc> getList(Player player) {
            // get the local destination list
            return player.getPData().getDEST().getSaved();
        }

        /**
         * gets a list of command friendly names from a destination list
         * @param list destination list
         * @return names surrounded by quotes
         */
        public static List<String> getCMDNames(List<Loc> list) {
            List<String> formatted = new ArrayList<>();
            for (String name: getNames(list)) formatted.add("\""+name+"\"");
            return formatted;
        }

        /**
         * gets all names from a destination list
         * @param list destination list
         * @return list of names
         */
        public static List<String> getNames(List<Loc> list) {
            List<String> out = new ArrayList<>();
            for (Loc entry : list) out.add(entry.getName());
            return out;
        }

        /**
         * saves a new destination
         * @param destination the destination to save
         */
        public static void addButton(Player player, Dest destination) {
            // format the color
            destination.setColor(CUtl.color.colorHandler(player,destination.getColor()));
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            if (destination.getList().size() >= config.DestMAX) {
                player.sendMessage(LANG.error("max"));
                return;
            }
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
            buttons.append(dest.setButtons("/dest set saved "+cmdName,
                    Dim.canConvert(player.getDimension(),destination.getDest().getDimension())));

            player.sendMessage(CUtl.tag().append(LANG.msg("add",destination.getDest().getBadge().append(buttons))));
        }

        /**
         * deletes an existing destination
         * @param Return to return back to the saved UI or not
         * @param destination the destination to remove
         */
        public static void delete(boolean Return, Player player, Dest destination) {
            ListPage<Loc> listPage = new ListPage<>(new ArrayList<>(destination.getList()),PER_PAGE);
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            destination.remove();
            player.sendMessage(CUtl.tag().append(LANG.msg("delete",destination.getDest().getBadge())));
            // return back to the page that the destination was on
            if (Return) player.performCommand("dest saved " + listPage.getPageOf(destination.getDest()));
        }

        /**
         * edits a destination's name
         * @param Return to return to the destination edit UI or not
         * @param destination destination to edit
         * @param newName the new name for the destination
         */
        public static void editName(boolean Return, Player player, Dest destination, String newName) {
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
        public static void editOrder(boolean Return, Player player, Dest destination, String newOrderString) {
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
        public static void editLocation(boolean Return, Player player, Dest destination, Loc newLoc) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // get the destination Loc
            Loc loc = destination.getDest();
            // set only the location data to the Loc if there's coordinates
            if (newLoc.hasXYZ()) {
                loc.setX(newLoc.getX());
                loc.setY(newLoc.getY());
                loc.setZ(newLoc.getZ());
            }
            // set only the dimension data to the Loc if there is a dimension
            if (newLoc.getDimension() != null)
                loc.setDimension(newLoc.getDimension());
            // save the Loc
            destination.setDest(loc);
            // get the formatted name
            CTxT name = CTxT.of(destination.getName()).color(CUtl.s());
            // send the message
            player.sendMessage(CUtl.tag().append(LANG.msg("set",name,LANG.get("location"),CTxT.of(loc.getNamelessBadge()))));
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
        public static void setColor(Player player, Dest destination, String UISettings, String newColor, boolean Return) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // handle color
            newColor = CUtl.color.colorHandler(player,newColor);
            // save the new color
            destination.setColor(newColor);
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
            Dest destination = new Dest(player,name,false);
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            // get the current color
            String currentColor = destination.getColor();

            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                               ").strikethrough(true);
            msg.append(LANG.ui("color",name).color(currentColor)).append(line).append("\n");
            // get the command name of the destination
            String cmdName = destination.getCMDName();
            msg.append(DHUD.preset.colorEditor(currentColor,UISettings,DHUD.preset.Type.saved,name,"/dest saved edit colorui "+cmdName+" %s"))
                    .append("\n\n           ").append(CUtl.CButton.back("/dest saved edit "+cmdName)).append(line);
            player.sendMessage(msg);
        }

        /**
         * UI for editing / viewing a destination
         * @param destination the destination to edit
         */
        public static void editUI(Player player, Dest destination) {
            // if errors were sent (invalid), return
            if (destination.sendErrors()) return;
            ListPage<Loc> listPage = new ListPage<>(new ArrayList<>(destination.getList()),PER_PAGE);
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
                    .append(" ").append(CTxT.of(destination.getDest().getNamelessBadge()))
                    .append("\n   ");
            // SEND BUTTON
            if (Utl.checkEnabled.send(player)) {
                msg.append(social.send.LANG.btn().btn(true).color(Assets.mainColors.send).cEvent(2,"/dest saved send "+cmdName+" ")
                        .hEvent(CTxT.of("/dest saved send "+cmdName+" <player>").color(Assets.mainColors.send)
                                .append("\n").append(social.send.LANG.hover("saved")))).append(" ");
            }
            // SET & CONVERT BUTTON
            msg
                    .append(dest.setButtons("/dest set saved "+cmdName,
                            Dim.canConvert(player.getDimension(),destination.getDest().getDimension()))).append(" ")
                    .append("\n\n ")
                    .append(LANG.btn("delete").btn(true).color('c').cEvent(2,"/dest saved delete-r "+cmdName)
                            .hEvent(LANG.hover("delete",LANG.btn("delete").color('c')))).append(" ")
                    //BACK
                    .append(CUtl.CButton.back("/dest saved "+ listPage.getPageOf(destination.getDest())))
                    .append(line);
            player.sendMessage(msg);
        }

        /**
         * main saved destination UI
         * @param pg page to show
         */
        public static void UI(Player player, int pg) {
            ListPage<Loc> listPage = new ListPage<>(new ArrayList<>(getList(player)),PER_PAGE);
            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                             ").strikethrough(true);
            msg.append(LANG.ui().color(Assets.mainColors.saved)).append(line).append("\n");

            // for every destination in the current page
            for (Loc entry: listPage.getPage(pg)) {
                String cmdName = "\""+entry.getName()+"\"";
                msg.append(" ")
                        // BADGE
                        .append(entry.getBadge()).append(" ")
                        // EDIT
                        .append(EDIT_BUTTON(1,"/dest saved edit " + cmdName)).append(" ")
                        // SET & convert
                        .append(dest.setButtons("/dest set saved " + cmdName,
                                Dim.canConvert(player.getDimension(), entry.getDimension())))
                        .append("\n");
            }
            // no saved
            if (listPage.getPage(pg).isEmpty()) {
                msg.append(" ").append(LANG.ui("none")).append("\n ").append(LANG.ui("none.2",ADD_BUTTON)).append("\n");
            }
            msg.append("\n ");
            // add global button if enabled
            if (config.globalDESTs) msg.append(CTxT.of(Assets.symbols.global).btn(true).color(Assets.mainColors.global)
                    .hEvent(LANG.hover("global").color(Assets.mainColors.global))
                    .cEvent(1,"/dest saved global"));
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
            ListPage<Loc> listPage = new ListPage<>(new ArrayList<>(GlobalDest.getDestinations()), PER_PAGE);
            // build the message
            CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                             ").strikethrough(true);
            msg.append(LANG.ui("global").color(Assets.mainColors.global)).append(line).append("\n");
            // for every destination in the current page
            for (Loc entry : listPage.getPage(pg)) {
                String cmdName = "\""+entry.getName()+"\"";
                msg.append(" ")
                        //BADGE
                        .append(entry.getBadge()).append(" ")
                        // SET & convert
                        .append(dest.setButtons("/dest set saved " + cmdName,
                                Dim.canConvert(player.getDimension(), entry.getDimension())))
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
                    .append(" ").append(listPage.getNavButtons(pg, "/dest saved global "))
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
            if (!Utl.checkEnabled.lastdeath(player)) return;
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
            if (Dim.checkValid(loc.getDimension())) {
                //add to the top of the list
                deaths.add(0,loc);
                // WHILE more than max, remove the last entry (to deal with the size changing to be smaller in the future)
                while (deaths.size() > config.LastDeathMAX) deaths.remove(deaths.size()-1);
            }
            player.setPData().getDEST().setLastdeath(deaths);
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
                        .append(saved.SAVE_BUTTON("/dest add \""+Dim.getName(dim).toLowerCase()+"_death\" "+loc.getXYZ()+" "+dim+" \""+Dim.getColor(dim)+"\""))
                        .append(" ").append(dest.setButtons("/dest set "+loc.getXYZ()+" "+loc.getDimension(),
                                Dim.canConvert(player.getDimension(),dim)));
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
            if (player.getPData().getSocialCooldown() != null) {
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
                if (!Utl.checkEnabled.send(player)) return;
                // send <IGN>
                if (args.length == 1) {
                    logic(player,args[0],player.getLoc());
                    return;
                }
                // send <IGN> <name>
                if (args.length == 2) {
                    logic(player,args[0],new Loc(player,args[1]));
                    return;
                }

                if (args[1].equalsIgnoreCase("saved")) {
                    // check if enabled
                    if (!Utl.checkEnabled.saving(player)) return;
                    // send <IGN> saved <name>
                    if (args.length == 3) {
                        // get the dest and the loc from the dest
                        saved.Dest dest = new saved.Dest(player,args[2],false);
                        logic(player,args[0],dest.getDest());
                        return;
                    }
                }
                String pDIM = player.getDimension();
                // send <IGN> <xyz or xy> (dimension)
                // send <IGN> (name) <xyz or xy> (dimension)

                // send IGN x z
                if (args.length == 3) {
                    logic(player,args[0],new Loc(Num.toInt(args[1]), Num.toInt(args[2]),pDIM));
                }
                // send IGN NAME x z
                if (args.length == 4 && !Num.isInt(args[1])) {
                    logic(player,args[0],new Loc(Num.toInt(args[1]),null,Num.toInt(args[2]),pDIM,args[1],null));
                    return;
                }
                // send IGN x y (z, DIM, color)
                if (args.length == 4) {
                    //DIM
                    if (Dim.getAll().contains(args[3])) {
                        logic(player, args[0], new Loc(Num.toInt(args[1]), Num.toInt(args[2]), args[3]));
                    }
                    // COLOR
                    else if (!Num.isInt(args[3])) {
                        logic(player,args[0],new Loc(Num.toInt(args[1]),null,Num.toInt(args[2]),pDIM,null,args[3]));
                    }
                    // Z
                    else {
                        logic(player,args[0],new Loc(Num.toInt(args[1]),Num.toInt(args[2]),Num.toInt(args[3]),pDIM));
                    }
                    return;
                }
                // send IGN NAME x y (z, color, DIM)
                if (args.length == 5 && !Num.isInt(args[1])) {
                    // DIM
                    if (Dim.getAll().contains(args[4])) {
                        logic(player,args[0],new Loc(Num.toInt(args[2]),null,Num.toInt(args[3]),args[4],args[1],null));
                    }
                    // COLOR
                    else if (!Num.isInt(args[4])) {
                        logic(player,args[0],new Loc(Num.toInt(args[2]),null,Num.toInt(args[3]),pDIM,args[1],args[4]));
                    }
                    // Z
                    else {
                        logic(player,args[0],new Loc(Num.toInt(args[2]),Num.toInt(args[3]),Num.toInt(args[4]),pDIM,args[1],null));
                    }
                    return;
                }
                // send IGN x y z (DIM, color)
                if (args.length == 5 && Num.isInt(args[3])) {
                    // DIM
                    if (Dim.getAll().contains(args[4])) {
                        logic(player,args[0],new Loc(Num.toInt(args[1]), Num.toInt(args[2]), Num.toInt(args[3]),args[4]));
                    }
                    // COLOR
                    else {
                        logic(player,args[0],new Loc(Num.toInt(args[1]), Num.toInt(args[2]), Num.toInt(args[3]),pDIM,null,args[4]));
                    }
                    return;
                }
                // send IGN x y DIM color
                if (args.length == 5 && Dim.getAll().contains(args[3])) {
                    logic(player,args[0],new Loc(Num.toInt(args[1]),null,Num.toInt(args[2]),args[3],null,args[4]));
                    return;
                }
                // send IGN NAME x y z (DIM, color)
                // send IGN NAME x y DIM (color)
                if (args.length == 6 && !Num.isInt(args[1])) {
                    if (Num.isInt(args[4])) {
                        // DIM
                        if (Dim.getAll().contains(args[5])) {
                            logic(player,args[0],new Loc(Num.toInt(args[2]), Num.toInt(args[3]), Num.toInt(args[4]),args[5],args[1],null));
                        }
                        // COLOR
                        else {
                            logic(player,args[0],new Loc(Num.toInt(args[2]), Num.toInt(args[3]), Num.toInt(args[4]),pDIM,args[1],args[5]));
                        }
                    } else {
                        // send IGN NAME x z DIM color
                        logic(player,args[0],new Loc(Num.toInt(args[2]),null,Num.toInt(args[3]),args[4],args[1],args[5]));
                    }
                    return;
                }
                // send IGN x y z DIM color
                if (args.length == 6 && Dim.getAll().contains(args[4])) {
                    logic(player,args[0],new Loc(Num.toInt(args[1]), Num.toInt(args[2]), Num.toInt(args[3]),args[4],null,args[5]));
                    return;
                }
                // send IGN NAME x y z DIM color
                if (args.length == 7 && !Num.isInt(args[1])) {
                    logic(player,args[0],new Loc(Num.toInt(args[2]), Num.toInt(args[3]), Num.toInt(args[4]),args[5],args[1],args[6]));
                    return;
                }
                player.sendMessage(CUtl.usage(Assets.cmdUsage.destSend));
            }
            public static ArrayList<String> CMDSuggester(Player player, int pos, String[] args) {
                String current = Suggester.getCurrent(args,pos);
                ArrayList<String> suggester = new ArrayList<>();
                // enabled check
                if (!Utl.checkEnabled.send(player)) return suggester;
                // send <player> <saved> <name>
                // send <player> (name) <x> (y) <z> (dimension) (color)

                // send (player)
                if (pos == 0) {
                    suggester.addAll(Suggester.players(player));
                }
                // send <player> (<saved>, (name), <x>)
                if (pos == 1) {
                    if (Utl.checkEnabled.saving(player)) suggester.add("saved");
                    suggester.addAll(Suggester.xyz(player,current,3));
                    suggester.add(Suggester.name());
                }
                // send <player> <saved> (<name>)
                // send <player> (name) (<x>)
                // send <player> <x> ((y))
                if (pos == 2) {
                    // send <player> <saved> (<name>)
                    if (args[1].equalsIgnoreCase("saved") && Utl.checkEnabled.saving(player)) {
                        suggester.addAll(saved.getCMDNames(saved.getList(player)));
                    }
                    // send <player> (name) (<x>)
                    else if (!Num.isInt(args[1])) {
                        suggester.addAll(Suggester.xyz(player,current,3));
                    }
                    // send <player> <x> ((y))
                    else {
                        suggester.addAll(Suggester.xyz(player,current,2));
                    }
                }
                // send <player> (name) <x> ((y))
                // send <player> <x> (y) (<z> (dimension) (color))
                if (pos == 3) {
                    // Y
                    if (!Num.isInt(args[1])) {
                        suggester.addAll(Suggester.xyz(player,current,1));
                    }
                    // (<z> (dimension) (color))
                    else {
                        suggester.addAll(Suggester.xyz(player, current, 1));
                        suggester.addAll(Suggester.dims(current, false));
                        suggester.addAll(Suggester.colors(player, current, false));
                    }
                }
                // send <player> (name) <x> (y) (<z>, (dimension), (color))
                // send <player> <x> (y) <z> ((dimension), (color))
                // send <player> <x> (y) (dimension) ((color))
                if (pos == 4) {
                    // (<z>, (dimension), (color))
                    if (!Num.isInt(args[1])) {
                        suggester.addAll(Suggester.xyz(player,current,1));
                        suggester.addAll(Suggester.dims(current,false));
                        suggester.addAll(Suggester.colors(player,current,false));
                    }
                    // ((dimension), (color))
                    if (Num.isInt(args[3])) {
                        suggester.addAll(Suggester.dims(current));
                        suggester.addAll(Suggester.colors(player,current));
                    }
                    // ((color))
                    else if (Dim.getAll().contains(args[3])) {
                        suggester.addAll(Suggester.colors(player,current));
                    }
                }
                // send <player> (name) <x> (y) <z> ((dimension), (color))
                // send <player> (name) <x> (y) (dimension) ((color))
                // send <player> <x> (y) <z> (dimension) ((color))
                if (pos == 5) {
                    // NAME
                    if (!Num.isInt(args[1])) {
                        // ((dimension), (color))
                        if (Num.isInt(args[4])) {
                            suggester.addAll(Suggester.dims(current));
                            suggester.addAll(Suggester.colors(player,current));
                        }
                        // (color)
                        else if (Dim.getAll().contains(args[4])) {
                            suggester.addAll(Suggester.colors(player,current));
                        }
                    }
                    // send <player> <x> (y) <z> (dimension) ((color))
                    if (Num.isInt(args[1]) && Num.isInt(args[3]) && Dim.getAll().contains(args[4])){
                        suggester.addAll(Suggester.colors(player,current));
                    }
                }
                // send <player> (name) <x> (y) <z> (dimension) ((color))
                if (pos == 6) {
                    if (!Num.isInt(args[1]) && Num.isInt(args[4]) && Dim.getAll().contains(args[5])) {
                        suggester.addAll(Suggester.colors(player,current));
                    }
                }
                return suggester;
            }
            /**
             * the main logic for sending Locs between players
             * @param player the player sending the Loc
             * @param targetPlayer the player string receiving the Loc
             * @param loc Loc to send
             */
            public static void logic(Player player, String targetPlayer, Loc loc) {
                // get the target player from string
                Player target = Player.of(targetPlayer);
                // remove bad data
                // cooldown check
                if (cooldown(player)) return;
                if (target == null) {
                    player.sendMessage(CUtl.LANG.error("player", CTxT.of(targetPlayer).color(CUtl.s())));
                    return;
                }
                if (target == player) {
                    player.sendMessage(LANG.error("self"));
                    return;
                }
                // target doesn't have sending enabled
                if (!Utl.checkEnabled.send(target)) {
                    player.sendMessage(LANG.error("target_disabled",CTxT.of(target.getName()).color(CUtl.s())));
                    return;
                }
                // LOC VALIDATION
                // if no name, have the placeholder name for the player to change it later
                if (loc.getName() == null) {
                    loc.setName(LANG.get("default_save_name").toString());
                }
                // custom name too long
                if (loc.getName().length() > Helper.MAX_NAME) {
                    player.sendMessage(CUtl.LANG.error("length",Helper.MAX_NAME));
                    return;
                }
                // invalid coordinates
                if (!loc.hasXYZ()) {
                    player.sendMessage(CUtl.LANG.error("coordinates"));
                    return;
                }
                // invalid dimension
                if (!Dim.checkValid(loc.getDimension())) {
                    player.sendMessage(CUtl.LANG.error("dimension"));
                    return;
                }

                // LOGIC
                // add the cooldown
                player.setPData().setSocialCooldown(config.socialCooldown.doubleValue());

                player.sendMessage(CUtl.tag().append(LANG.msg("sent",CTxT.of(target.getName()).color(CUtl.s()),
                        CTxT.of("\n ").append(loc.getBadge()))));

                target.sendMessage(CUtl.tag().append(LANG.msg("sent_target",CTxT.of(player.getName()).color(CUtl.s()),getSendTxt(target,loc))));

                DHUD.inbox.addDest(target,player,999,loc);
            }

            /**
             * gets the sent Loc as a CTxt for the receiver
             * @param loc the Loc to display
             * @return the CTxT built
             */
            public static CTxT getSendTxt(Player player, Loc loc) {
                CTxT txt = CTxT.of("").append(loc.getBadge()).append(" ");
                // if color is null, empty string
                String colorCMD = loc.getColor()==null ? "" : "\""+loc.getColor()+"\"";
                // ADD
                if (Utl.checkEnabled.saving(player))
                    txt.append(saved.SAVE_BUTTON("/dest saved add \""+loc.getName()+"\" "+loc.getXYZ()+" "+loc.getDimension()+colorCMD)).append(" ");
                // SET & CONVERT
                txt.append(dest.setButtons("/dest set "+loc.getXYZ()+" "+loc.getDimension(),
                        Dim.canConvert(player.getDimension(),loc.getDimension())));
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
                if (!Utl.checkEnabled.track(player)) return;
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
                    if (getTarget(player)!=null) suggester.add("clear");
                    suggester.add("set");
                    if (DHUD.inbox.getAllType(player, DHUD.inbox.Type.track_pending)!=null) suggester.add("cancel");
                    if (DHUD.inbox.getAllType(player, DHUD.inbox.Type.track_request)!=null) {
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
                            ArrayList<HashMap<String,Object>> matches = DHUD.inbox.getAllType(player, DHUD.inbox.Type.track_request);
                            // if there are any display the names
                            if (matches != null) {
                                for (HashMap<String, Object> entry : matches)
                                    suggester.add((String) entry.get("player_name"));
                            }
                        }
                        case "cancel" -> {
                            // get all track pendings
                            ArrayList<HashMap<String,Object>> matches = DHUD.inbox.getAllType(player, DHUD.inbox.Type.track_pending);
                            // if there are any display the names
                            if (matches != null) {
                                for (HashMap<String,Object> entry:matches)
                                    suggester.add((String) entry.get("player_name"));
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
             * returns the current tracking target
             * @return the target, null if no one is being tracked
             */
            public static Player getTarget(Player player) {
                String track = player.getPData().getDEST().getTracking();
                if (track == null) return null;
                return Player.of(track);
            }

            /**
             * clear the tracker with a reason
             * @param reason 1 = command clear, 2 = tracking off, 3 = target tracking off
             */
            public static void clear(Player player, int reason) {
                // nothing to clear
                if (getTarget(player) == null) {
                    player.sendMessage(LANG.error("cleared"));
                    return;
                }
                // get the reason for clearing
                CTxT reasonTxT = LANG.msg("cleared." + switch (reason) {
                    default -> "command"; case 2 -> "tracking_off"; case 3 -> "tracking_off_target";
                });
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
                for (String key: player.getPData().getMsgKeys())
                    if (key.contains("tracking")) player.getPData().clearMsg(key);
                // remove the target
                player.setPData().getDEST().setTracking(null);
            }

            /**
             * sets the tracker to the target player with a message
             * @param player the person tracking
             * @param target the target
             */
            public static void set(Player player, Player target) {
                // if online, use UUID, if not use the target NAME
                if (config.online) player.setPData().getDEST().setTracking(target.getUUID());
                else player.setPData().getDEST().setTracking(target.getName());
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
                Player target = Player.of(target_string);
                // cooldown check
                if (cooldown(player)) return;
                if (target == null) {
                    player.sendMessage(CUtl.LANG.error("player", CTxT.of(target_string).color(CUtl.s())));
                    return;
                }
                // make the two player TxTs
                CTxT targetTxT = CTxT.of(target.getName()).color(CUtl.s()), playerTxT = CTxT.of(player.getName()).color(CUtl.s());
                if (target == player) {
                    player.sendMessage(LANG.error("self"));
                    return;
                }
                if (!(boolean) player.getPData().getDEST().getSetting(Setting.features__track)) {
                    player.sendMessage(LANG.error("target_disabled",targetTxT));
                    return;
                }
                // tracking request already pending
                if (DHUD.inbox.search(player, DHUD.inbox.Type.track_pending,"player_name",target_string) != null) {
                    player.sendMessage(LANG.error("pending",targetTxT));
                    return;
                }
                // already tracking the target
                if (getTarget(player) != null && Objects.equals(getTarget(player), target)) {
                    player.sendMessage(LANG.error("already_tracking",targetTxT));
                    return;
                }
                // add the cooldown
                player.setPData().setSocialCooldown(config.socialCooldown.doubleValue());
                // target has instant tracking
                if (Enums.get(player.getPData().getDEST().getSetting(Setting.features__track_request_mode),Setting.TrackingRequestMode.class)
                        .equals(Setting.TrackingRequestMode.instant)) {
                    set(player,target);
                    return;
                }
                // add the tracking to the inbox
                DHUD.inbox.addTracking(target,player,300);
                // send the messages
                player.sendMessage(CUtl.tag().append(LANG.msg("request",targetTxT)).append("\n ")
                        .append(LANG.msg("expire",300).color('7').italic(true)));

                target.sendMessage(CUtl.tag().append(LANG.msg("request_target",playerTxT)).append("\n ")
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
                Player target = Player.of(tracker);
                // if player in questions is null
                if (target == null) {
                    player.sendMessage(CUtl.LANG.error("player",CTxT.of(tracker).color(CUtl.s())));
                    return;
                }
                if (player == target) {
                    player.sendMessage(LANG.error("self"));
                    return;
                }
                // get the entry from the player inbox
                // tracK_request if accept or deny, track_pending if canceling
                HashMap<String, Object> entry = DHUD.inbox.search(player, DHUD.inbox.Type.track_request,"player_name", tracker);
                if (type.equals(ProcessType.cancel)) entry = DHUD.inbox.search(player, DHUD.inbox.Type.track_pending,"player_name", tracker);

                // entry doesn't exist
                if (entry == null) {
                    player.sendMessage(LANG.error("none",target.getHighlightedName()));
                    return;
                }
                // get the ID
                String ID = (String) entry.get("id");
                // the IDs don't match - SYNC ERROR
                if (DHUD.inbox.search(target, null,"id", ID) ==null) {
                    DHUD.inbox.removeEntry(player,entry);
                    player.sendMessage(CUtl.tag().append("SYNC ERROR - REPORT IT! (ID-MISMATCH)"));
                    return;
                }
                // if the target has tracking turned off - SYNC ERROR
                if (!(boolean) player.getPData().getDEST().getSetting(Setting.features__track)) {
                    DHUD.inbox.removeEntry(player,entry);
                    player.sendMessage(CUtl.tag().append("SYNC ERROR - REPORT IT! (TARGET-TRACK-OFF)"));
                    return;
                }

                // remove from both inboxes
                DHUD.inbox.delete(player,ID,false);
                DHUD.inbox.delete(target,ID,false);

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
                if (getTarget(player) == null) return;
                // INFO (null if not sent, not if otherwise)
                // tracking.offline = target offline
                // tracking.dimension = not in same dimension & cant convert (trail cold)
                // tracking.converted = tracker converted message

                // if the server turned social off, clear w no msg
                if (!Utl.checkEnabled.track(player)) {
                    Destination.social.track.clear(player);
                    return;
                }
                // player has tracking disabled
                if (!(boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__track)) {
                    Destination.social.track.clear(player,2);
                    return;
                }
                Player target = Destination.social.track.getTarget(player);
                // clear if tracking oneself, dunno how its possible, but it happened before
                if (target == player) {
                    Destination.social.track.clear(player);
                    return;
                }
                // if the target is null, means the player cant be found, probably offline
                if (target == null) {
                    if (player.getPData().getMsg("tracking.offline").isBlank()) {
                        // the offline message hasn't been sent
                        player.sendMessage(CUtl.tag().append(LANG.msg("target_offline")));
                        player.getPData().setMsg("tracking.offline", "1");
                        // reset all other messages
                        player.getPData().clearMsg("tracking.converted");
                        player.getPData().clearMsg("tracking.dimension");
                    }
                    return;
                }
                // target turned off tracking
                if (!(boolean)player.getPData().getDEST().getSetting(Destination.Setting.features__track)) {
                    Destination.social.track.clear(player,3);
                    return;
                }
                // ------- TRACKING IS ON -------
                // if the offline message was sent, reset it and send the back message
                if (!player.getPData().getMsg("tracking.offline").isBlank()) {
                    player.sendMessage(CUtl.tag().append(LANG.msg("resumed"))); // tracking resumed msg
                    player.getPData().clearMsg("tracking.offline");
                }
                // target is in the same dimension as the player
                if (target.getDimension().equals(player.getDimension())) {
                    // if convert message has been sent before
                    if (!player.getPData().getMsg("tracking.converted").isBlank()) {
                        // send convert message to let player know the tracker was converted back to local dimension
                        player.sendMessage(CUtl.tag().append(Destination.LANG.msg("autoconvert.tracking",
                                Destination.LANG.msg("autoconvert.tracking.2",
                                                CTxT.of(Dim.getName(target.getDimension())).italic(true).color(Dim.getColor(target.getDimension()))))));
                        player.getPData().clearMsg("tracking.converted");
                    }
                    // if tracking was stopped before
                    if (!player.getPData().getMsg("tracking.dimension").isBlank()) {
                        /// send resume message to let player know the tracker was enabled again
                        player.sendMessage(CUtl.tag().append(LANG.msg("resumed")));
                        player.getPData().clearMsg("tracking.dimension");
                    }
                    return;
                }
                // ------- target isn't in the same dimension as the player -------
                // if AUTOCONVERT IS ON AND CONVERTIBLE
                if ((boolean) player.getPData().getDEST().getSetting(Destination.Setting.autoconvert) &&
                        Dim.canConvert(player.getDimension(),target.getDimension())) {
                    // send the tracking resumed message if tracking was disabled from dimension differences (autoconvert was enabled midway, ect.)
                    if (!player.getPData().getMsg("tracking.dimension").isBlank()) {
                        player.sendMessage(CUtl.tag().append(LANG.msg("resumed")));
                        player.getPData().clearMsg("tracking.dimension");
                    }
                    // send the convert message if it hasn't been sent
                    if (player.getPData().getMsg("tracking.converted").isBlank()) {
                        player.sendMessage(CUtl.tag().append(Destination.LANG.msg("autoconvert.tracking",
                                Destination.LANG.msg("autoconvert.tracking.2",
                                        CTxT.of(Dim.getName(target.getDimension())).italic(true).color(Dim.getColor(target.getDimension()))))));
                        // change the status on the convert message
                        player.getPData().setMsg("tracking.converted",target.getDimension());
                    }
                } else if (player.getPData().getMsg("tracking.dimension").isBlank()) {
                    // if not convertible or AutoConvert is off, & the dimension difference message hasn't been sent,
                    // send the dimension message
                    player.sendMessage(CUtl.tag().append(LANG.msg("target_dimension",
                            Destination.LANG.msg("autoconvert.tracking.2",
                                            CTxT.of(Dim.getName(target.getDimension())).italic(true).color(Dim.getColor(target.getDimension()))))));
                    player.getPData().setMsg("tracking.dimension", "1");
                    // make sure the converted msg is reset
                    player.getPData().clearMsg("tracking.converted");
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
                    suggester.addAll(Suggester.colors(player,Suggester.getCurrent(args,pos)));
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
            Object output = false;
            switch (setting) {
                case autoclear -> output = config.dest.AutoClear;
                case autoclear_rad -> output = config.dest.AutoClearRad;
                case autoconvert -> output = config.dest.AutoConvert;
                case ylevel -> output = config.dest.YLevel;
                case particles__dest -> output = config.dest.particles.Dest;
                case particles__dest_color -> output = config.dest.particles.DestColor;
                case particles__line -> output = config.dest.particles.Line;
                case particles__line_color -> output = config.dest.particles.LineColor;
                case particles__tracking -> output = config.dest.particles.Tracking;
                case particles__tracking_color -> output = config.dest.particles.TrackingColor;
                case features__send -> output = config.dest.Send;
                case features__track -> output = config.dest.Track;
                case features__track_request_mode -> output = config.dest.TrackingRequestMode;
                case features__lastdeath -> output = config.dest.Lastdeath;
            }
            return output;
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
                for (Setting s : Setting.values()) player.setPData().getDEST().setSetting(s,getConfig(s));
            } else {
                // else reset the selected setting
                player.setPData().getDEST().setSetting(setting,getConfig(setting));
            }
            // reset every setting that has children
            // also reset autoclear RAD if autoclear
            if (setting.equals(Setting.autoclear))
                player.setPData().getDEST().setSetting( Setting.autoclear_rad,getConfig(Setting.autoclear_rad));
            // resetting particle settings, reset the color
            if (Setting.particles().contains(setting))
                player.setPData().getDEST().setSetting(Setting.get(setting+"_color"),getConfig(Setting.get(setting+"_color")));
            // reset track mode for track reset
            if (setting.equals(Setting.features__track))
                player.setPData().getDEST().setSetting( Setting.features__track_request_mode,getConfig(Setting.features__track_request_mode));

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
                player.setPData().getDEST().setSetting(Setting.autoclear_rad,i);
                setTxT.append(CTxT.of(String.valueOf(i)).color((boolean) player.getPData().getDEST().getSetting(Setting.autoclear)?'a':'c'));
            }
            // req mode set
            if (setting.equals(Setting.features__track_request_mode)) {
                player.setPData().getDEST().setSetting( setting, Enums.get(state,Setting.TrackingRequestMode.class));
                setTxT.append(LANG.get(setting +"."+ Enums.get(state,Setting.TrackingRequestMode.class)).color(CUtl.s()));
            }
            // color set
            if (Setting.colors().contains(setting)) {
                setParticleColor(player,null,setting,state,false);
                setTxT.append(CUtl.color.getBadge((String) player.getPData().getDEST().getSetting(setting)));
            }
            // if bool, boolean set
            if (Setting.bool().contains(setting)) {
                player.setPData().getDEST().setSetting(setting,bool);
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
                        .cEvent(1,"/dest settings colorui "+setting+"_color "+DHUD.preset.DEFAULT_UI_SETTINGS)
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
                    .append(DHUD.preset.colorEditor(currentColor,UISettings, DHUD.preset.Type.dest,setting.toString(),"/dest settings colorui "+setting+" %s"))
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
            player.setPData().getDEST().setSetting(type,color);
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
            if (config.social || config.LastDeathSaving) {
                //FEATURES
                msg.append(LANG.ui("category.features").color(CUtl.p())).append(":\n  ");
                if (config.social) {
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
                if (config.LastDeathSaving) {
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
                if (!config.LastDeathSaving && t.equals(Setting.features__lastdeath)) continue;
                // if social is off in the config, skip
                if (!config.social && (t.equals(Setting.features__send) || t.equals(Setting.features__track))) continue;
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
    public static void UI(Player player) {
        CTxT msg = CTxT.of(" "), line = CTxT.of("\n                                  ").strikethrough(true);
        msg.append(LANG.ui("commands").color(Assets.mainColors.dest)).append(line).append("\n ");
        // lmao this is a mess but is it the best way to do it? dunno
        boolean line1Free = false;
        boolean line2Free = !((boolean) player.getPData().getDEST().getSetting(Setting.features__lastdeath) && config.LastDeathSaving);
        boolean sendThird = Utl.checkEnabled.send(player);
        //SAVED + ADD
        if (Utl.checkEnabled.saving(player)) {
            msg.append(saved.BUTTON).append(saved.ADD_BUTTON);
            if (!line2Free) msg.append("        ");
            else msg.append("  ");
        } else line1Free = true;
        //SET + CLEAR
        msg.append(dest.SET_BUTTON()).append(dest.CLEAR_BUTTON(player));
        if (line1Free) msg.append(" ");
        else msg.append("\n\n ");
        //LASTDEATH
        if (Utl.checkEnabled.lastdeath(player)) {
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
        if (Utl.checkEnabled.send(player)) {
            msg.append(social.send.BUTTON);
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
                line2Free = false;
                sendThird = false;
            } else msg.append(" ");
        }
        //TRACK
        if (Utl.checkEnabled.track(player)) {
            msg.append(social.track.BUTTON(social.track.getTarget(player)!=null));
            if (line2Free && !line1Free) {
                msg.append("\n\n ");
            } else if (line2Free) {
                if (Utl.checkEnabled.send(player)) msg.append(" ");
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