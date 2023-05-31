package one.oth3r.directionhud.commands;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.files.LangReader;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;
import org.bukkit.Bukkit;

import java.util.Objects;

public class DirHUD {
    public static void setDefaults(Player player) {
        config.DESTAutoClear = PlayerData.get.dest.setting.autoclear(player);
        config.DESTAutoClearRad = PlayerData.get.dest.setting.autoclearrad(player);
        config.DESTAutoConvert = PlayerData.get.dest.setting.autoconvert(player);
        config.DESTDestParticles = PlayerData.get.dest.setting.particle.dest(player);
        config.DESTDestParticleColor = PlayerData.get.dest.setting.particle.destcolor(player);
        config.DESTLineParticles = PlayerData.get.dest.setting.particle.line(player);
        config.DESTLineParticleColor = PlayerData.get.dest.setting.particle.linecolor(player);
        config.DESTTrackingParticles = PlayerData.get.dest.setting.particle.tracking(player);
        config.DESTTrackingParticleColor = PlayerData.get.dest.setting.particle.trackingcolor(player);
        config.DESTYLevel = PlayerData.get.dest.setting.ylevel(player);
        config.DESTSend = PlayerData.get.dest.setting.send(player);
        config.DESTTrack = PlayerData.get.dest.setting.track(player);

        config.HUD24HR = PlayerData.get.hud.setting.time24h(player);
        config.HUDTracking = PlayerData.get.hud.module.tracking(player);
        config.HUDCoordinates = PlayerData.get.hud.module.coordinates(player);
        config.HUDDistance = PlayerData.get.hud.module.distance(player);
        config.HUDDirection = PlayerData.get.hud.module.direction(player);
        config.HUDDestination = PlayerData.get.hud.module.destination(player);
        config.HUDTime = PlayerData.get.hud.module.time(player);
        config.HUDWeather = PlayerData.get.hud.module.weather(player);

        config.HUDEnabled = PlayerData.get.hud.state(player);
        config.HUDOrder = PlayerData.get.hud.order(player);

        config.HUDPrimaryColor = HUD.color.getHUDColors(player)[0];
        config.HUDPrimaryBold = HUD.color.getHUDBold(player,1);
        config.HUDPrimaryItalics = HUD.color.getHUDItalics(player, 1);

        config.HUDSecondaryColor = HUD.color.getHUDColors(player)[1];
        config.HUDSecondaryBold = HUD.color.getHUDBold(player, 2);
        config.HUDSecondaryItalics = HUD.color.getHUDItalics(player, 2);
        config.save();
        player.sendMessage(CUtl.tag().append(CUtl.lang("dirhud.defaults.set")));
    }
    public static void resetDefaults(Player player) {
        config.resetDefaults();
        player.sendMessage(CUtl.tag().append(CUtl.lang("dirhud.defaults.reset")));
    }
    public static void defaults(Player player) {
        CTxT msg = CTxT.of("");
        msg.append(CUtl.lang("dirhud.ui.defaults").color(CUtl.pTC()))
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
    public static void reload(Player player) {
        if (DirectionHUD.configDir == null) DirectionHUD.configDir = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getDataFolder().getPath()+"/";
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
                .append(CTxT.of(" DirectionHUD ").color(CUtl.pTC()))
                .append(CTxT.of("v"+DirectionHUD.VERSION+CUtl.symbols.link()).color(CUtl.sTC()).cEvent(3,"https://modrinth.com/mod/directionhud/changelog")
                        .hEvent(CUtl.TBtn("version.hover").color(CUtl.sTC())))
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
