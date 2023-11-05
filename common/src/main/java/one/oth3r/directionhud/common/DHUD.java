package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.FloodGateHandler;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;

public class DHUD {
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (args.length == 0) {
                UI(player);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Utl.trimStart(args, 1);
            switch (type) {
                case "presets" -> presetCMD(player,trimmedArgs);
                case "reload" -> {
                    if (Utl.checkEnabled.reload(player)) reload(player);
                }
                default -> player.sendMessage(CUtl.error("error.command"));
            }
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
                if (Utl.checkEnabled.reload(player)) suggester.add("reload");
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
        for (Player pl: Utl.getPlayers()) {
            PlayerData.removePlayer(pl);
            PlayerData.addPlayer(pl);
        }
        if (player == null) DirectionHUD.LOGGER.info(CUtl.lang("dirhud.reload", CUtl.lang("dirhud.reload_2")).toString());
        else player.sendMessage(CUtl.tag().append(CUtl.lang("dirhud.reload",CUtl.lang("dirhud.reload_2").color('a'))));
    }
    public static void UI(Player player) {
        if (FloodGateHandler.isFloodgate(player)) {
            FloodGateHandler.UI.base(player);
            return;
        }
        CTxT msg = CTxT.of("")
                .append(CTxT.of(" DirectionHUD ").color(CUtl.p()))
                .append(CTxT.of(DirectionHUD.VERSION+Assets.symbols.link).color(CUtl.s()).cEvent(3,"https://modrinth.com/mod/directionhud/changelog")
                        .hEvent(CUtl.TBtn("version.hover").color(CUtl.s())))
                .append(CTxT.of("\n                                 \n").strikethrough(true)).append(" ");
        //hud
        if (Utl.checkEnabled.hud(player)) msg.append(CUtl.CButton.dirHUD.hud()).append("  ");
        //dest
        if (Utl.checkEnabled.destination(player)) msg.append(CUtl.CButton.dirHUD.dest());
        if (Utl.checkEnabled.reload(player)) {
            msg.append("\n\n ").append(CUtl.CButton.dirHUD.reload());
            if (Utl.checkEnabled.defaults(player)) msg.append("  ").append(CUtl.CButton.dirHUD.defaults());
        } else if (Utl.checkEnabled.defaults(player)) msg.append("\n\n ").append(CUtl.CButton.dirHUD.defaults());
        msg.append(CTxT.of("\n                                 ").strikethrough(true));
        player.sendMessage(msg);
    }
}
