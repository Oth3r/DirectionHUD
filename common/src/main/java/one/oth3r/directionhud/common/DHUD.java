package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.GlobalDest;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Helper.ListPage;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;

public class DHUD {
    private static CTxT lang(String key, Object... args) {
        return CUtl.lang("dhud."+key, args);
    }
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (args.length == 0) {
                UI(player);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Helper.trimStart(args, 1);
            switch (type) {
                case "inbox" -> inboxCMD(player,trimmedArgs);
                case "color" -> colorCMD(player,trimmedArgs);
                case "presets", "preset" -> presetCMD(player,trimmedArgs);
                case "reload" -> {
                    if (Utl.checkEnabled.reload(player)) reload(player);
                }
                case "dest", "destination" -> Destination.commandExecutor.logic(player,trimmedArgs);
                case "hud" -> HUD.commandExecutor.logic(player,trimmedArgs);
                default -> player.sendMessage(CUtl.error("command"));
            }
        }
        public static void inboxCMD(Player player, String[] args) {
            if (!config.social) return;
            if (args.length == 0) {
                inbox.UI(player,1);
                return;
            }
            if (args.length == 1 && Helper.Num.isInt(args[0])) {
                inbox.UI(player, Helper.Num.toInt(args[0]));
                return;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                inbox.delete(player,args[1],true);
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.inbox));
        }
        public static void colorCMD(Player player, String[] args) {
            if (args.length != 5) return;
            // /dhud color (settings) (type) (subtype) (set/preset) (color/page)
            if (Enums.toStringList(Enums.toArrayList(preset.Type.values())).contains(args[1])) {
                preset.Type type = preset.Type.get(args[1]);
                if (args[3].equals("set")) preset.setColor(player,args[0],type,args[2],args[4]);
                if (args[3].equals("preset")) preset.ui(player,args[0],type,args[2],args[4]);
            }
        }
        public static void presetCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.customPresets(player)) return;
            if (args.length <= 1) {
                // preset ui
                if (args.length == 0) preset.custom.ui(player,1,null);
                // via page num
                else if (Helper.Num.isNum(args[0])) preset.custom.ui(player,Helper.Num.toInt(args[0]),null);
                // via preset name
                else {
                    ArrayList<String> presets = PlayerData.get.colorPresets(player);
                    ListPage<String> listPage = new ListPage<>(presets, preset.PER_PAGE);
                    // check if the preset is valid, then get the page for that preset
                    if (preset.custom.getNames(presets).contains(args[0])) {
                        String preset = args[0] +"|"+ DHUD.preset.custom.getColors(presets).get(DHUD.preset.custom.getNames(presets).indexOf(args[0]));
                        DHUD.preset.custom.ui(player, listPage.getPageOf(preset), null);
                    }
                }
                return;
            }
            boolean Return = false;
            // if the type has -r, remove it and enable returning
            if (args[0].contains("-r")) {
                args[0] = args[0].replace("-r","");
                Return = true;
            }
            if (args.length == 2 && args[0].equals("delete")) preset.custom.delete(player,args[1],Return);
            if (args.length == 3) {
                if (args[0].equals("save")) preset.custom.save(player,args[2],args[1],Return);
                if (args[0].equals("rename")) preset.custom.rename(player,args[1],args[2],Return);
                if (args[0].equals("colorui")) preset.custom.colorUI(player,args[2],args[1],null);
                if (args[0].equals("color")) preset.custom.setColor(player,"",args[1],args[2],Return);
            }
        }
    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.hud(player)) return suggester;
            if (pos == 1) {
                if (config.social) suggester.add("inbox");
                if (Utl.checkEnabled.reload(player)) suggester.add("reload");
                if (Utl.checkEnabled.destination(player)) suggester.add("dest");
                if (Utl.checkEnabled.hud(player)) suggester.add("hud");
                if (Utl.checkEnabled.customPresets(player)) suggester.add("preset");
            }
            if (pos > 1) {
                String command = args[0].toLowerCase();
                // trim the start
                String[] trimmedArgs = Helper.trimStart(args, 1);
                // fix the pos
                int fixedPos = pos - 2;
                switch (command) {
                    case "dest","destination" -> suggester.addAll(Destination.commandSuggester.logic(player,fixedPos+1,trimmedArgs));
                    case "hud" -> suggester.addAll(HUD.commandSuggester.logic(player,fixedPos+1,trimmedArgs));
                    case "preset" -> suggester.addAll(presetCMD(player,fixedPos,trimmedArgs));
                    case "color" -> {
                        if (fixedPos == 4) suggester.addAll(Suggester.colors(player,Suggester.getCurrent(trimmedArgs,fixedPos)));
                    }
                }
            }
            return suggester;
        }
        public static ArrayList<String> presetCMD(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.customPresets(player)) return suggester;
            /*
               preset rename (name) (newName)
               preset save (color) (name)
               preset color (name) (color)
               preset delete (name)
             */
            if (pos == 0) {
                suggester.add("rename");
                suggester.add("save");
                suggester.add("color");
                suggester.add("delete");
                return suggester;
            }
            // if -r is attached, remove it and continue with the suggester
            if (args[0].contains("-r")) args[0] = args[0].replace("-r", "");
            if (args[0].equals("save")) {
                if (pos == 1) return Suggester.colors(player,Suggester.getCurrent(args,pos));
                if (pos == 2) suggester.add("name");
            } else {
                if (pos == 1) suggester.addAll(Suggester.wrapQuotes(preset.custom.getNames(PlayerData.get.colorPresets(player))));
                else if (args[0].equals("rename")) suggester.add("name");
                else if (args[0].equals("color")) return Suggester.colors(player,Suggester.getCurrent(args,pos));
            }
            return suggester;
        }
    }
    public static void reload(Player player) {
        config.load();
        GlobalDest.fileToMap();
        // fully reload the players
        for (Player pl: Utl.getPlayers()) {
            Events.playerSoftLeave(pl);
            Events.playerJoin(pl);
        }
        if (player == null) DirectionHUD.LOGGER.info(lang("reload",lang("reload_2")).toString());
        else player.sendMessage(CUtl.tag().append(lang("reload",lang("reload_2").color('a'))));
    }
    public static class inbox {
        private static CTxT lang(String key, Object... args) {
            return DHUD.lang("inbox."+key, args);
        }
        public static final int PER_PAGE = 3;
        public enum Type {
            track_pending,
            track_request,
            destination
        }
        public static void tick(Player player) {
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            // iterate over the arraylist, as we are editing it, cant use for loop
            Iterator<HashMap<String, Object>> iterator = inbox.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> entry = iterator.next();
                double expire = (double) entry.get("expire");
                // tick the "expire" value
                entry.put("expire", String.valueOf(expire-1));
                // remove from inbox when expire is 0
                if (expire <= 0) {
                    iterator.remove();
                    // send expire messages if pending expired
                    if (entry.get("type").equals(Type.track_pending.name())) {
                        Player target = Player.of((String) entry.get("player_name"));
                        if (target==null) continue;
                        target.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired.target",CTxT.of(player.getName()).color(CUtl.s()))));
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired",CTxT.of(target.getName()).color(CUtl.s()))));
                    }
                }
            }
            PlayerData.set.inbox(player,inbox);
        }
        public static void removeAllTracking(Player player) {
            // removes all pending and requests from the player and their targets
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            // iterate over the arraylist, as we are editing it, cant use for loop
            Iterator<HashMap<String, Object>> iterator = inbox.iterator();
            while (iterator.hasNext()) {
                HashMap<String, Object> entry = iterator.next();
                // if pending or request, clear both from player and the target player (sync)
                if (entry.get("type").equals(Type.track_pending.name()) || entry.get("type").equals(Type.track_request.name())) {
                    // get the second type to search for in the target player (the opposite type of the player)
                    Type type = entry.get("type").equals(Type.track_pending.name())?
                            Type.track_request : Type.track_pending;
                    // use name if online mode is off
                    Player target = Player.of((String)entry.get("player_uuid"));
                    if (!config.online) target = Player.of((String)entry.get("player_name"));
                    if (target != null) {
                        // search for the opposite type of the player and the id to match it in target inbox and remove
                        removeEntry(target, one.oth3r.directionhud.common.DHUD.inbox.search(target, type,"id",entry.get("id")));
                    }
                    //remove from player
                    iterator.remove();
                }
            }
            PlayerData.set.inbox(player,inbox);
        }
        public static HashMap<String, Object> search(Player player, Type type, String key, Object value) {
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            for (HashMap<String, Object> entry: inbox) {
                // if the type isn't null, and it doesn't match, continue to the next entry
                if (type!=null && !entry.get("type").equals(type.name())) continue;
                if (entry.get(key).equals(value)) return entry;
            }
            return null;
        }
        public static ArrayList<HashMap<String, Object>> getAllMatches(Player player, Type type) {
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            ArrayList<HashMap<String, Object>> matches = new ArrayList<>();
            for (HashMap<String, Object> entry: inbox)
                if (entry.get("type").equals(type.name())) matches.add(0,entry);
            if (!matches.isEmpty()) return matches;
            return null;
        }
        public static void addTracking(Player to, Player from, int time) {
            String ID = Helper.createID();
            //create the track request
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(to);
            HashMap<String, Object> entry = new HashMap<>();
            entry.put("type", Type.track_request);
            entry.put("player_name",from.getName());
            entry.put("player_uuid",from.getUUID());
            entry.put("id",ID);
            entry.put("expire",time);
            inbox.add(0,entry);
            PlayerData.set.inbox(to,inbox);
            //create the track pending
            inbox = PlayerData.get.inbox(from);
            entry = new HashMap<>();
            entry.put("type", Type.track_pending);
            entry.put("player_name",to.getName());
            entry.put("player_uuid",to.getUUID());
            entry.put("id",ID);
            entry.put("expire",time);
            inbox.add(0,entry);
            PlayerData.set.inbox(from,inbox);
        }
        public static void addDest(Player to, Player from, int time, String name, Loc loc, String color) {
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(to);
            HashMap<String, Object> entry = new HashMap<>();
            entry.put("type", Type.destination.name());
            entry.put("player_name",from.getName());
            entry.put("player_uuid",from.getUUID());
            entry.put("id", Helper.createID());
            entry.put("expire",String.valueOf(time));
            entry.put("name",name);
            entry.put("loc",String.valueOf(loc.toArray()));
            // the # in the color breaks everything???
            if (color.contains("#")) color = color.substring(1);
            entry.put("color",color);
            inbox.add(0,entry);
            PlayerData.set.inbox(to,inbox);
        }
        public static void removeEntry(Player player, HashMap<String, Object> entry) {
            if (entry == null) return;
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            inbox.remove(entry);
            PlayerData.set.inbox(player,inbox);
        }
        public static void delete(Player player, String ID, boolean Return) {
            Helper.ListPage<HashMap<String, Object>> listPage = new Helper.ListPage<>(PlayerData.get.inbox(player),PER_PAGE);
            //delete via ID (command)
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            HashMap<String, Object> entry = search(player,null,"id",ID);
            // stop if there's nothing too clear
            if (entry==null) return;
            inbox.remove(entry);
            PlayerData.set.inbox(player,inbox);
            if (Return) {
                player.sendMessage(CUtl.tag().append(lang("cleared",CTxT.of((String)entry.get("player_name")).color(CUtl.s()))));
                player.performCommand("dhud inbox "+ listPage.getPageOf(entry));
            }
        }
        public static CTxT display(Player player, HashMap<String, Object> entry) {
            CTxT msg = CTxT.of("");
            String name = (String)entry.get("player_name");
            // get name from UUID if online mode is on
            if (config.online) {
                Player from = Player.of((String)entry.get("player_uuid"));
                if (from != null) name = from.getName();
            }
            if (entry.get("type").equals(Type.track_request.name())) {
                msg.append(lang("track_request").color(CUtl.p())).append(" ").append(lang("time",((Double)entry.get("expire")).intValue()).color('7'))
                        .append("\n  ").append(lang("from",CTxT.of(name).color(CUtl.s()))).append("\n   ")
                        .append(CUtl.TBtn("accept").btn(true).color('a')
                                .hEvent(CUtl.TBtn("accept.hover").color('a'))
                                .cEvent(1,"/dest track accept-r "+name)).append(" ")
                        .append(CUtl.TBtn("deny").btn(true).color('c')
                                .hEvent(CUtl.TBtn("deny.hover").color('c'))
                                .cEvent(1,"/dest track deny-r "+name));
            }
            if (entry.get("type").equals(Type.track_pending.name())) {
                msg.append(lang("track_pending").color(CUtl.p())).append(" ").append(lang("time",((Double)entry.get("expire")).intValue()).color('7'))
                        .append("\n  ").append(lang("to",CTxT.of(name).color(CUtl.s()))).append("\n   ")
                        .append(CUtl.TBtn("cancel").btn(true).color('c')
                                .hEvent(CUtl.TBtn("cancel.hover").color('c'))
                                .cEvent(1, "/dest track cancel-r "+name));
            }
            if (entry.get("type").equals(Type.destination.name())) {
                msg.append(lang("destination").color(CUtl.p())).append(" ").append(lang("inbox.time",((Double)entry.get("expire")).intValue()).color('7'))
                        .append(" ").append(CTxT.of(Assets.symbols.x).btn(true).color('c')
                                .hEvent(lang("clear.hover").color('c'))
                                .cEvent(1,"/dhud inbox clear "+entry.get("id")))
                        .append("\n  ").append(lang("from",CTxT.of(name).color(CUtl.s()))).append("\n   ")
                        .append(Destination.social.getSendTxt(player,(String)entry.get("name"),new Loc(entry.get("loc").toString()),(String)entry.get("color")));
            }
            return msg;
        }
        public static void UI(Player player, int pg) {
            Helper.ListPage<HashMap<String, Object>> listPage = new Helper.ListPage<>(PlayerData.get.inbox(player),PER_PAGE);
            CTxT msg = CTxT.of("");
            msg.append(" ").append(lang("ui").color(Assets.mainColors.inbox)).append(CUtl.LINE_35).append("\n ");
            for (HashMap<String, Object> index : listPage.getPage(pg)) {
                msg.append(display(player,index)).append("\n ");
            }
            // no entries
            if (listPage.getList().isEmpty()) msg.append("\n ").append(lang("empty").color('7').italic(true)).append("\n");
            // bottom row
            msg.append("\n ");
            if (listPage.getList().size() > PER_PAGE) msg.append(listPage.getNavButtons(pg,"/dhud inbox ")).append(" ");
            msg.append(CUtl.CButton.back("/dhud")).append(CUtl.LINE_35);
            player.sendMessage(msg);
        }
    }
    public static class preset {
        private static final int PER_PAGE = 7;
        private static CTxT lang(String key, Object... args) {
            return DHUD.lang("preset."+key, args);
        }
        public enum Type {
            hud,
            dest,
            saved,
            preset,
            unknown;
            public static Type get(String s) {
                try {
                    return Type.valueOf(s);
                } catch (IllegalArgumentException e) {
                    return unknown;
                }
            }
        }
        public static void setColor(Player player, String settings, Type type, String subtype, String color) {
            // sets color via the Type, using the setColor defined by each color type, returns to the UI
            // /dhud color (settings) (type) (subtype) set (color)
            switch (type) {
                case hud -> {
                    HUD.color.setColor(player,settings,subtype,color,true);
                }
                case dest -> {
                    Destination.settings.setColor(player,settings,
                            Destination.Setting.get(subtype),color,true);
                }
                case saved -> {
                    // if using dhud set, its always local destinations
                    Destination.saved.setColor(player,Destination.saved.getList(player),
                            settings,subtype,color,true);
                }
                case preset -> {
                    custom.setColor(player,settings,subtype,color,true);
                }
            }
        }
        public static CTxT colorEditor(String color, String settings, Type type, String subtype, String stepCMD) {
            CTxT presetsButton = CTxT.of("")
                    .append(CTxT.of("+").btn(true).color('a')
                            .cEvent(2,String.format("/dhud preset save \"%s\" ",color))
                            .hEvent(lang("hover.preset.plus",lang("hover.preset.plus_2").color(color))))
                    .append(lang("button.preset").color(Assets.mainColors.presets)
                            .cEvent(1,String.format("/dhud color %s %s \"%s\" preset default",settings,type,subtype)).btn(true)
                            .hEvent(lang("hover.preset.editor",lang("hover.preset.editor_2").color(Assets.mainColors.presets))));
            CTxT customButton = lang("button.custom").btn(true).color(Assets.mainColors.custom)
                    .cEvent(2,String.format("/dhud color %s %s \"%s\" set ",settings,type,subtype))
                    .hEvent(lang("hover.custom",lang("hover.custom.2").color(Assets.mainColors.custom)));
            CTxT defaultSquare = CTxT.of(Assets.symbols.square).color(color).hEvent(CUtl.color.getBadge(color)),
                    smallButton = lang("editor.step.button.small").color(CUtl.s()).cEvent(1,String.format(stepCMD,"small"))
                            .hEvent(lang("editor.step.hover",lang("editor.step.button.small").color(CUtl.s()))).btn(true),
                    normalButton = lang("editor.step.button.normal").color(CUtl.s()).cEvent(1,String.format(stepCMD,"normal"))
                            .hEvent(lang("editor.step.hover",lang("editor.step.button.normal").color(CUtl.s()))).btn(true),
                    bigButton = lang("editor.step.button.big").color(CUtl.s()).cEvent(1,String.format(stepCMD,"big"))
                            .hEvent(lang("editor.step.hover",lang("editor.step.button.big").color(CUtl.s()))).btn(true);
            // initialize the change amounts for each step size
            float[] changeAmounts = new float[3];
            if (settings == null || settings.equals("normal")) {
                normalButton.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                changeAmounts[0] = 0.02f;
                changeAmounts[1] = 0.05f;
                changeAmounts[2] = 0.1f;
            } else if (settings.equals("small")) {
                smallButton.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                changeAmounts[0] = 0.005f;
                changeAmounts[1] = 0.0125f;
                changeAmounts[2] = 0.025f;
            } else if (settings.equals("big")) {
                bigButton.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                changeAmounts[0] = 0.04f;
                changeAmounts[1] = 0.1f;
                changeAmounts[2] = 0.2f;
            }
            ArrayList<CTxT> hsbList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                hsbList.add(CTxT.of("-").btn(true));
                hsbList.add(CTxT.of("+").btn(true));
            }
            int i = 0;
            // todo color editor logic, dissect later i was dumb and didn't comment whyy...
            for (int changeAmt = 0; changeAmt < 3;changeAmt++) {
                for (int plus = i;plus < i+2;plus++) {
                    String editedColor = CUtl.color.editHSB(changeAmt,color,(plus%2==0)?changeAmounts[changeAmt]*-1:(changeAmounts[changeAmt]));
                    hsbList.get(plus).color(editedColor.equals(color)?Assets.mainColors.gray:editedColor);
                    if (!editedColor.equals(color)) {
                        hsbList.get(plus).hEvent(lang("color.hover.set",CUtl.color.getBadge(editedColor)));
                        hsbList.get(plus).cEvent(1,String.format("/dhud color %s %s \"%s\" set \"%s\"",settings,type,subtype,editedColor));
                    }
                }
                i = i+2;
            }
            return CTxT.of(" ")
                    .append(presetsButton).append(" ").append(customButton).append("\n\n")
                    .append("  ")
                    .append(hsbList.get(0)).append(" ").append(defaultSquare).append(" ").append(hsbList.get(1)).append(" ").append(lang("editor.hue")).append("\n  ")
                    .append(hsbList.get(2)).append(" ").append(defaultSquare).append(" ").append(hsbList.get(3)).append(" ").append(lang("editor.saturation")).append("\n  ")
                    .append(hsbList.get(4)).append(" ").append(defaultSquare).append(" ").append(hsbList.get(5)).append(" ").append(lang("editor.brightness")).append("\n\n ")
                    .append(smallButton).append(" ").append(normalButton).append(" ").append(bigButton);
        }
        public static void ui(Player player, String settings, Type type, String subtype, String page) {
            // top button initialization
            String clickCMD = String.format("/dhud color %s %s \"%s\" ",settings,type,subtype);
            CTxT defaultBtn = lang("button.default").color(CUtl.s()).cEvent(1,clickCMD+"preset default").btn(true),
                    minecraftBtn = lang("button.minecraft").color(CUtl.s()).cEvent(1,clickCMD+"preset minecraft").btn(true),
                    customBtn = CTxT.of(" ").append(lang("button.custom").color(CUtl.s()).cEvent(1,clickCMD+"preset custom").btn(true)), // space at start for alignment
                    list = CTxT.of(""); // text for inside the UI
            // code for the button selector page, default and mc colors
            if (page.equals("default") || page.equals("minecraft")) {
                List<String> colorStrings, colors;
                int rowAmt;
                if (page.equals("default")) {
                    // disable the current page button & set the data for the loop
                    defaultBtn.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                    colorStrings = List.of("red","orange","yellow","green","blue","purple","gray");
                    colors = CUtl.color.DEFAULT_COLORS;
                    rowAmt = 3;
                } else {
                    minecraftBtn.color(Assets.mainColors.gray).cEvent(1,null).hEvent(null);
                    colorStrings = List.of("red","yellow","green","aqua","blue","purple","gray");
                    colors = List.of("#FF5555","#AA0000",
                            "#FFFF55","#FFAA00",
                            "#55FF55","#00AA00",
                            "#55FFFF","#00AAAA",
                            "#5555FF","#0000AA",
                            "#FF55FF","#AA00AA",
                            "#AAAAAA","#555555");
                    rowAmt = 2;
                }
                int colorIndex = 0;
                // for all the preset color types
                for (String s : colorStrings) {
                    list.append("\n ");
                    // for x amt of colors per type
                    for (int i = 0; i < rowAmt;i++) {
                        String color = colors.get(colorIndex);
                        list.append(CTxT.of(Assets.symbols.square).btn(true).color(color)
                                .cEvent(1,String.format(clickCMD+" set \"%s\"",color))
                                .hEvent(lang("color.hover.set", CUtl.color.getBadge(color))));
                        colorIndex++;
                    }
                    list.append(" ").append(lang("color."+s));
                }
            } else {
                // custom, just numbers for the pages instead of an identifier, easier that way trust me
                int pg = Helper.Num.toInt(page);
                ListPage<String> listPage = new ListPage<>(PlayerData.get.colorPresets(player),7);
                customBtn = listPage.getNavButtons(pg,clickCMD+"preset ");
                for (String preset : listPage.getPage(pg)) {
                    String color = custom.getColor(preset), name = custom.getName(preset);
                    list.append("\n ").append(CTxT.of(Assets.symbols.square).color(color).btn(true)
                                    .cEvent(1,String.format(clickCMD+" set \"%s\"",color))
                                    .hEvent(lang("color.hover.set",CUtl.color.getBadge(color))))
                            .append(" ").append(CTxT.of(name).color(color));
                }
                // fill in the gaps if entries don't fill whole page (consistency)
                if (listPage.getPage(pg).size() != PER_PAGE) {
                    for (int i = listPage.getPage(pg).size(); i < PER_PAGE; i++)
                        list.append("\n   ");
                }
            }
            // get the correct back button
            String backCMD = switch (type) {
                case hud -> "/hud color "+subtype+" edit "+settings;
                case dest -> "/dest settings "+subtype+" "+settings;
                case saved -> "/dest saved edit colorui \""+subtype+"\" "+settings;
                case preset -> "/dhud preset colorui \""+subtype+"\" "+settings;
                default -> "/dhud";
            };
            // final building of the message
            CTxT msg = CTxT.of(" ").append(lang("ui").color(Assets.mainColors.presets))
                    .append(CTxT.of("\n                               \n").strikethrough(true))
                    .append(" ").append(defaultBtn).append(" ").append(minecraftBtn).append("\n").append(list)
                    .append("\n\n   ").append(customBtn).append("  ").append(CUtl.CButton.back(backCMD))
                    .append(CTxT.of("\n                               ").strikethrough(true));
            player.sendMessage(msg);
        }
        public static class custom {
            public static ArrayList<String> update(ArrayList<String> oldList) {
                // update from the old preset system to the new one (1.7)
                ArrayList<String> list = new ArrayList<>();
                if (oldList.size() < 14) return list; // broken list return empty
                for (byte i = 0; i < 14; i++) {
                    String old = oldList.get(i);
                    if (!old.equals("#ffffff")) list.add((i+1)+"|"+old);
                }
                return list;
            }
            public static ArrayList<String> validate(ArrayList<String> list) {
                // validate the config entry, making sure it works without throwing errors
                ArrayList<String> output = new ArrayList<>();
                for (String preset: list) {
                    String name = getName(preset), color = getColor(preset);
                    if (name.length() > Helper.MAX_NAME) break;
                    if (!preset.contains("|#")) break;
                    if (!color.equals("#ffffff") && CUtl.color.format(color).equals("#ffffff")) break;
                    output.add(preset);
                }
                return output;
            }
            public static CTxT getBadge(String preset) {
                return getBadge(preset,true);
            }
            public static CTxT getBadge(String preset, boolean square) {
                return CTxT.of((square?Assets.symbols.square+" ":"")+getName(preset)).color(getColor(preset));
            }
            public static String getName(String preset) {
                return preset.substring(0, preset.lastIndexOf("|#"));
            }
            public static ArrayList<String> getNames(ArrayList<String> presets) {
                ArrayList<String> out = new ArrayList<>();
                for (String preset : presets) out.add(getName(preset));
                return out;
            }
            public static String getColor(String preset) {
                return preset.substring(preset.lastIndexOf("|#")+1);
            }
            public static ArrayList<String> getColors(ArrayList<String> presets) {
                ArrayList<String> out = new ArrayList<>();
                for (String preset : presets) out.add(getColor(preset));
                return out;
            }
            public static void ui(Player player, int pg, CTxT aboveMSG) {
                CTxT msg = aboveMSG==null?CTxT.of(" "):aboveMSG.append("\n "),
                        line = CTxT.of("\n                               ").strikethrough(true);
                msg.append(lang("ui.custom").color(Assets.mainColors.presets)).append(line);
                CTxT addBtn = CTxT.of("+").btn(true).color('a').cEvent(2,"/dhud preset save-r ").hEvent(lang("hover.save").color('a'));
                // disable if max saved colors reached
                if (PlayerData.get.colorPresets(player).size() >= config.MAXColorPresets) addBtn.color('7').cEvent(1,null).hEvent(null);
                ListPage<String> listPage = new ListPage<>(PlayerData.get.colorPresets(player),PER_PAGE);
                for (String preset : listPage.getPage(pg)) {
                    String color = getColor(preset), name = getName(preset);
                    msg.append("\n ").append(CTxT.of(Assets.symbols.x).color('c').btn(true)
                                    .cEvent(1,String.format("/dhud preset delete-r \"%s\"",name))
                                    .hEvent(lang("hover.delete",getBadge(preset)).color('c')))
                            .append(" ")
                            .append(CTxT.of(Assets.symbols.square).color(color).btn(true)
                                    .cEvent(1,String.format("/dhud preset colorui \"%s\" normal",name))
                                    .hEvent(lang("hover.color",CUtl.color.getBadge(color))))
                            .append(CTxT.of(name).color(color).btn(true)
                                    .cEvent(2,String.format("/dhud preset rename-r \"%s\" ",name))
                                    .hEvent(lang("hover.rename",getBadge(preset,false))));
                }
                // fill in the gaps if entries don't fill whole page (consistency)
                if (listPage.getPage(pg).size() != PER_PAGE) {
                    for (int i = listPage.getPage(pg).size(); i < PER_PAGE; i++)
                        msg.append("\n");
                }
                msg.append("\n\n ")
                        .append(addBtn).append(" ")
                        .append(listPage.getNavButtons(pg,"/dhud preset "))
                        .append(" ").append(CUtl.CButton.back("/dhud"))
                        .append(line);
                player.sendMessage(msg);
            }
            public static void colorUI(Player player, String settings, String name, CTxT aboveMSG) {
                ArrayList<String> presets = PlayerData.get.colorPresets(player);
                ArrayList<String> names = getNames(presets);
                if (!names.contains(name)) return;
                String currentColor = getColors(presets).get(names.indexOf(name));
                CTxT line = CTxT.of("\n                               ").strikethrough(true);
                CTxT msg = aboveMSG == null?CTxT.of(" "):CTxT.of("").append(aboveMSG).append("\n");
                msg.append(lang("ui.color").color(currentColor))
                        .append(line).append("\n")
                        .append(preset.colorEditor(currentColor,settings,Type.preset,name,"/dhud preset colorui \""+name+"\" %s"))
                        .append("\n\n           ").append(CUtl.CButton.back(String.format("/dhud preset \"%s\"",name))).append(line);
                player.sendMessage(msg);
            }
            public static void setColor(Player player, String settings, String name, String color, boolean Return) {
                ArrayList<String> presets = PlayerData.get.colorPresets(player);
                ArrayList<String> names = getNames(presets);
                // remove the bad data
                if (!names.contains(name)) {
                    player.sendMessage(CUtl.error("dhud.preset"));
                    return;
                }
                // color fixer
                color = CUtl.color.colorHandler(player,color);
                // find the pos of the current preset and replace with the new color
                int index = names.indexOf(name);
                String oldPreset = presets.get(index), preset = name+"|"+color;
                presets.set(index,preset);
                PlayerData.set.colorPresets(player,presets);
                if (Return) colorUI(player,settings,name,null);
                else player.sendMessage(CUtl.tag().append(lang("msg.color",getBadge(oldPreset,false),CUtl.color.getBadge(color))));
            }
            public static void save(Player player, String name, String color, boolean Return) {
                ArrayList<String> presets = PlayerData.get.colorPresets(player);
                // errors
                if (getNames(presets).contains(name)) {
                    player.sendMessage(CUtl.error("dhud.preset.duplicate"));
                    return;
                }
                if (name.length() > Helper.MAX_NAME) {
                    player.sendMessage(CUtl.error("length",Helper.MAX_NAME));
                    return;
                }
                if (presets.size() >= config.MAXColorPresets) {
                    player.sendMessage(CUtl.error("dhud.preset.max"));
                    return;
                }
                // fix the color
                color = CUtl.color.colorHandler(player,color);
                // add & save the preset
                String entry = name+"|"+color;
                presets.add(entry);
                PlayerData.set.colorPresets(player,presets);
                // listPage for getting the page of the new entry when returning
                ListPage<String> listPage = new ListPage<>(presets,PER_PAGE);
                CTxT msg = CUtl.tag().append(lang("msg.save",getBadge(entry)));
                if (Return) ui(player,listPage.getPageOf(presets.get(presets.size()-1)),msg);
                else player.sendMessage(msg);
            }
            public static void rename(Player player, String name, String newName, boolean Return) {
                ArrayList<String> presets = PlayerData.get.colorPresets(player);
                ArrayList<String> names = getNames(presets);
                // remove the bad data
                if (!names.contains(name)) {
                    player.sendMessage(CUtl.error("dhud.preset"));
                    return;
                }
                if (names.contains(newName)) {
                    player.sendMessage(CUtl.error("dhud.preset.duplicate"));
                    return;
                }
                if (newName.length() > Helper.MAX_NAME) {
                    player.sendMessage(CUtl.error("length",Helper.MAX_NAME));
                    return;
                }
                int index = names.indexOf(name);
                String preset = newName+"|"+getColors(presets).get(index);
                presets.set(index,preset);
                PlayerData.set.colorPresets(player,presets);
                // player formatting
                CTxT msg = CUtl.tag().append(lang("msg.rename",getBadge(name+"|"+getColors(presets).get(index),false),getBadge(preset,false)));
                ListPage<String> listPage = new ListPage<>(names,PER_PAGE);
                if (Return) ui(player,listPage.getPageOf(name),msg);
                else player.sendMessage(msg);
            }
            public static void delete(Player player, String name, boolean Return) {
                ArrayList<String> presets = PlayerData.get.colorPresets(player);
                ArrayList<String> names = getNames(presets);
                // remove the bad data
                if (!names.contains(name)) {
                    player.sendMessage(CUtl.error("dhud.preset"));
                    return;
                }
                String preset = presets.get(names.indexOf(name));
                // remove the preset
                presets.remove(preset);
                PlayerData.set.colorPresets(player,presets);
                // player formatting
                CTxT msg = CUtl.tag().append(lang("msg.delete",getBadge(preset)));
                ListPage<String> listPage = new ListPage<>(names,PER_PAGE);
                if (Return) ui(player,listPage.getPageOf(name),msg);
                else player.sendMessage(msg);
            }
        }
    }
    public static void UI(Player player) {
        CTxT line = CTxT.of("\n                             ").strikethrough(true);
        CTxT msg = CTxT.of(" ")
                .append(CTxT.of("DirectionHUD").color(CUtl.p())
                        .hEvent(CTxT.of(DirectionHUD.VERSION+Assets.symbols.link).color(CUtl.s()))
                        .cEvent(3,"https://modrinth.com/mod/directionhud/changelog"))
                .append(line).append("\n ");
        // hud
        if (Utl.checkEnabled.hud(player)) msg.append(CUtl.CButton.DHUD.hud()).append("  ");
        // dest
        if (Utl.checkEnabled.destination(player)) msg.append(CUtl.CButton.DHUD.dest());
        msg.append("\n\n ");
        // presets
        if (Utl.checkEnabled.customPresets(player)) msg.append(CUtl.CButton.DHUD.presets()).append(" ");
        // inbox
        if (config.social) msg.append(CUtl.CButton.DHUD.inbox());
        // reload (if enabled)
        if (Utl.checkEnabled.reload(player)) msg.append("\n\n ").append(CUtl.CButton.DHUD.reload());
        msg.append(line);
        player.sendMessage(msg);
    }
}
