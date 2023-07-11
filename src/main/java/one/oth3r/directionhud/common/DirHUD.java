package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;

public class DirHUD {
    public static class commandExecutor {
        public static void logic(Player player, String[] args) {
            if (!Utl.checkEnabled.hud(player)) return;
            if (args.length == 0) {
                UI(player);
                return;
            }
            String type = args[0].toLowerCase();
            String[] trimmedArgs = Utl.trimStart(args, 1);
            switch (type) {
                case "reload" -> {
                    if (Utl.checkEnabled.reload(player)) reload(player);
                }
                case "defaults" -> defaultsCMD(player, trimmedArgs);
                default -> player.sendMessage(CUtl.error(CUtl.lang("error.command")));
            }
        }
        public static void defaultsCMD(Player player, String[] args) {
            if (!Utl.checkEnabled.defaults(player)) return;
            //UI
            if (args.length == 0)
                defaults.UI(player);
            if (args.length != 1) return;
            String type = args[0].toLowerCase();
            switch (type) {
                case "set" -> defaults.set(player);
                case "reset" -> defaults.reset(player);
            }
        }
    }
    public static class commandSuggester {
        public static ArrayList<String> logic(Player player, int pos, String[] args) {
            ArrayList<String> suggester = new ArrayList<>();
            if (!Utl.checkEnabled.hud(player)) return suggester;
            if (pos == 1) {
                if (Utl.checkEnabled.reload(player)) suggester.add("reload");
                if (Utl.checkEnabled.defaults(player)) suggester.add("defaults");
            }
            if (pos > 1) {
                if (args[0].equals("default") && Utl.checkEnabled.defaults(player)) {
                    suggester.add("set");
                    suggester.add("reset");
                }
            }
            if (pos == args.length) return Utl.formatSuggestions(suggester,args);
            return suggester;
        }
    }
    public static class defaults {
        public static void set(Player player) {
            config.setToPlayer(player);
            player.sendMessage(CUtl.tag().append(CUtl.lang("dirhud.defaults.set")));
        }
        public static void reset(Player player) {
            config.resetDefaults();
            player.sendMessage(CUtl.tag().append(CUtl.lang("dirhud.defaults.reset")));
        }
        public static void UI(Player player) {
            CTxT msg = CTxT.of("");
            msg.append(CUtl.lang("dirhud.ui.defaults").color(CUtl.p()))
                    .append(CTxT.of("\n                                 \n").strikethrough(true))
                    .append(" ")
                    .append(CUtl.TBtn("dirhud.defaults.set").btn(true).color(CUtl.c.set).cEvent(1,"/dirhud defaults set")
                            .hEvent(CUtl.TBtn("dirhud.defaults.set.hover")))
                    .append("  ")
                    .append(CUtl.TBtn("dirhud.defaults.reset").btn(true).color('c').cEvent(1,"/dirhud defaults reset")
                            .hEvent(CUtl.TBtn("dirhud.defaults.reset.hover")))
                    .append("  ")
                    .append(CUtl.CButton.back("/dirhud"))
                    .append(CTxT.of("\n                                 ").strikethrough(true));
            player.sendMessage(msg);
        }
    }
    public static void reload(Player player) {
        DirectionHUD.configDir = DirectionHUD.CONFIG_DIR;
        DirectionHUD.playerData = DirectionHUD.PLAYERDATA_DIR;
        LangReader.loadLanguageFile();
        config.load();
        for (Player pl: Utl.getPlayers()) {
            PlayerData.removePlayer(pl);
            PlayerData.addPlayer(pl);
        }
        if (player == null) DirectionHUD.LOGGER.info(CUtl.lang("dirhud.reload", CUtl.lang("dirhud.reload_2")).getString());
        else player.sendMessage(CUtl.tag().append(CUtl.lang("dirhud.reload",CUtl.lang("dirhud.reload_2").color('a'))));
    }
    public static void UI(Player player) {
        CTxT msg = CTxT.of("")
                .append(CTxT.of(" DirectionHUD ").color(CUtl.p()))
                .append(CTxT.of(DirectionHUD.VERSION+CUtl.symbols.link()).color(CUtl.s()).cEvent(3,"https://modrinth.com/mod/directionhud/changelog")
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
