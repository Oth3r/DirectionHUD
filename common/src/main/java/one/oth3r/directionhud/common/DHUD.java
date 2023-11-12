package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
            String[] trimmedArgs = Utl.trimStart(args, 1);
            switch (type) {
                case "inbox" -> inboxCMD(player,trimmedArgs);
                case "presets" -> presetCMD(player,trimmedArgs);
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
            if (args.length == 1 && Utl.isInt(args[0])) {
                inbox.UI(player,Helper.forceInt(args[0]));
                return;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                inbox.delete(player,args[1],true);
                return;
            }
            player.sendMessage(CUtl.usage(Assets.cmdUsage.inbox));
        }
        public static void presetCMD(Player player, String[] args) {
            if (args.length < 3) return;
            if (args[0].equals("custom")) {
                if (args[1].equals("add") && args.length == 5) {
                    CUtl.color.customSet(player,Integer.parseInt(args[2]),args[3],CUtl.unFormatCMD(args[4]));
                }
                if (args[1].equals("reset") && args.length == 5)
                    CUtl.color.customReset(player,Integer.parseInt(args[2]),CUtl.unFormatCMD(args[3]),CUtl.unFormatCMD(args[4]));
                if (args.length == 3)
                    CUtl.color.customUI(player,CUtl.unFormatCMD(args[1]),CUtl.unFormatCMD(args[2]));
            } else {
                CUtl.color.presetUI(player,args[0],CUtl.unFormatCMD(args[1]),CUtl.unFormatCMD(args[2]));
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
                suggester.add("dest");
                suggester.add("hud");
            }
            if (pos > 1) {
                String command = args[0].toLowerCase();
                // trim the start
                String[] trimmedArgs = Utl.trimStart(args, 1);
                // fix the pos
                int fixedPos = pos - 1;
                switch (command) {
                    case "dest","destination" -> suggester.addAll(Destination.commandSuggester.logic(player,fixedPos,trimmedArgs));
                    case "hud" -> suggester.addAll(HUD.commandSuggester.logic(player,fixedPos,trimmedArgs));
                }
            }
            if (pos == args.length) return Utl.formatSuggestions(suggester,args);
            return suggester;
        }
    }
    public static void reload(Player player) {
        config.load();
        LangReader.loadLanguageFile();
        //config load twice for lang change support
        config.load();
        // fully reload the players
        for (Player pl: Utl.getPlayers()) {
            Events.playerLeave(pl);
            Events.playerJoin(pl);
        }
        if (player == null) DirectionHUD.LOGGER.info(lang("reload",lang("reload_2")).toString());
        else player.sendMessage(CUtl.tag().append(lang("reload",lang("reload_2").color('a'))));
    }
    public static class inbox {
        public static final int PER_PAGE = 3;
        public enum Type {
            track_pending,
            track_request,
            destination;
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
            if (matches.size()!=0) return matches;
            return null;
        }
        public static void addTracking(Player to, Player from, int time) {
            String ID = Utl.createID();
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
            entry.put("id",Utl.createID());
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
            CUtl.PageHelper<HashMap<String, Object>> pageHelper = new CUtl.PageHelper<>(PlayerData.get.inbox(player),PER_PAGE);
            //delete via ID (command)
            ArrayList<HashMap<String, Object>> inbox = PlayerData.get.inbox(player);
            HashMap<String, Object> entry = search(player,null,"id",ID);
            // stop if there's nothing too clear
            if (entry==null) return;
            inbox.remove(entry);
            PlayerData.set.inbox(player,inbox);
            if (Return) {
                player.sendMessage(CUtl.tag().append(lang("inbox.cleared",CTxT.of((String)entry.get("player_name")).color(CUtl.s()))));
                player.performCommand("dhud inbox "+pageHelper.getPageOf(entry));
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
                msg.append(lang("inbox.track_request").color(CUtl.p())).append(" ").append(lang("inbox.time",((Double)entry.get("expire")).intValue()).color('7'))
                        .append("\n  ").append(lang("inbox.from",CTxT.of(name).color(CUtl.s()))).append("\n   ")
                        .append(CUtl.TBtn("accept").btn(true).color('a')
                                .hEvent(CUtl.TBtn("accept.hover").color('a'))
                                .cEvent(1,"/dest track accept-r "+name)).append(" ")
                        .append(CUtl.TBtn("deny").btn(true).color('c')
                                .hEvent(CUtl.TBtn("deny.hover").color('c'))
                                .cEvent(1,"/dest track deny-r "+name));
            }
            if (entry.get("type").equals(Type.track_pending.name())) {
                msg.append(lang("inbox.track_pending").color(CUtl.p())).append(" ").append(lang("inbox.time",((Double)entry.get("expire")).intValue()).color('7'))
                        .append("\n  ").append(lang("inbox.to",CTxT.of(name).color(CUtl.s()))).append("\n   ")
                        .append(CUtl.TBtn("cancel").btn(true).color('c')
                                .hEvent(CUtl.TBtn("cancel.hover").color('c'))
                                .cEvent(1, "/dest track cancel-r "+name));
            }
            if (entry.get("type").equals(Type.destination.name())) {
                msg.append(lang("inbox.destination").color(CUtl.p())).append(" ").append(lang("inbox.time",((Double)entry.get("expire")).intValue()).color('7'))
                        .append(" ").append(CTxT.of(Assets.symbols.x).btn(true).color('c')
                                .hEvent(lang("inbox.clear.hover").color('c'))
                                .cEvent(1,"/dhud inbox clear "+entry.get("id")))
                        .append("\n  ").append(lang("inbox.from",CTxT.of(name).color(CUtl.s()))).append("\n   ")
                        .append(Destination.social.getSendTxt(player,(String)entry.get("name"),new Loc(entry.get("loc").toString()),(String)entry.get("color")));
            }
            return msg;
        }
        public static void UI(Player player, int pg) {
            CUtl.PageHelper<HashMap<String, Object>> pageHelper = new CUtl.PageHelper<>(PlayerData.get.inbox(player),PER_PAGE);
            CTxT msg = CTxT.of("");
            msg.append(" ").append(lang("ui.inbox").color(Assets.mainColors.inbox)).append(CUtl.LINE_35).append("\n ");
            for (HashMap<String, Object> index : pageHelper.getPage(pg)) {
                msg.append(display(player,index)).append("\n ");
            }
            // no entries
            if (pageHelper.getList().size()==0) msg.append("\n ").append(lang("inbox.empty").color('7').italic(true)).append("\n");
            // bottom row
            msg.append("\n ");
            if (pageHelper.getList().size() > PER_PAGE) msg.append(pageHelper.getNavButtons(pg,"/dhud inbox ")).append(" ");
            msg.append(CUtl.CButton.back("/dhud")).append(CUtl.LINE_35);
            player.sendMessage(msg);
        }
    }
    public static void UI(Player player) {
        CTxT msg = CTxT.of("")
                .append(CTxT.of(" DirectionHUD ").color(CUtl.p()))
                .append(CTxT.of(DirectionHUD.VERSION+Assets.symbols.link).color(CUtl.s()).cEvent(3,"https://modrinth.com/mod/directionhud/changelog")
                        .hEvent(CUtl.TBtn("version.hover").color(CUtl.s())))
                .append(CUtl.LINE_35).append("\n ");
        //hud
        if (Utl.checkEnabled.hud(player)) msg.append(CUtl.CButton.DHUD.hud()).append("  ");
        //dest
        if (Utl.checkEnabled.destination(player)) msg.append(CUtl.CButton.DHUD.dest());
        //inbox
        if (config.social) {
            msg.append("\n\n ").append(CUtl.CButton.DHUD.inbox());
            // reload button
            if (Utl.checkEnabled.reload(player)) msg.append(" ").append(CUtl.CButton.DHUD.reload());
        }
        // reload button without inbox button
        else if (Utl.checkEnabled.reload(player)) msg.append("\n\n ").append(CUtl.CButton.DHUD.reload());
        msg.append(CUtl.LINE_35);
        player.sendMessage(msg);
    }
}
