package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.GlobalDest;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Events {
    public static void serverStart() {
        try {
            Files.createDirectories(Paths.get(DirectionHUD.DATA_DIR+"playerdata/"));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Failed to create playerdata directory:\n" + e.getMessage());
        }
        config.load();
        DirectionHUD.LOGGER.info("Started server!");
    }
    public static void serverEnd() {
        for (Player player: Utl.getPlayers()) playerLeave(player);
        // clear everything as serverEnd on client can just be exiting single-player
        GlobalDest.clear();
        PlayerData.clearPlayerData();
        DirectionHUD.clear();
        DirectionHUD.LOGGER.info("Safely shutdown!");
    }
    public static void playerJoin(Player player) {
        PlayerData.addPlayer(player);
    }
    public static void playerLeave(Player player) {
        playerSoftLeave(player);
        DirectionHUD.clientPlayers.remove(player);
    }
    /**
     * effectively reloads the player without deleting certain required maps (like clientPlayers)
     */
    public static void playerSoftLeave(Player player) {
        DHud.inbox.removeAllTracking(player);
        PlayerData.removePlayer(player);
        DirectionHUD.bossBarManager.removePlayer(player);
    }
    public static void playerChangeWorld(Player player, String fromDIM, String toDIM) {
        if (Destination.dest.get(player).hasXYZ()) {
            Loc loc = Destination.dest.get(player);
            // don't clear if the dest's dim is the same as the new dim
            if (toDIM.equals(loc.getDimension())) return;
            if (Dimension.canConvert(toDIM, loc.getDimension()) &&
                    (boolean) player.getPData().getDEST().getSetting(Destination.Setting.autoconvert)) {
                //DEST AutoConvert logic
                Loc cLoc = Destination.dest.get(player);
                cLoc.convertTo(toDIM);
                Destination.dest.set(player,cLoc);
                player.sendMessage(CUtl.tag().append(Destination.LANG.msg("autoconvert.destination",
                        CTxT.of("\n ").append(Destination.LANG.msg("autoconvert.destination.2",loc.getBadge(),cLoc.getBadge())))));
            } else if ((boolean) player.getPData().getDEST().getSetting(Destination.Setting.autoclear)) {
                // clear if autoclear is on
                Destination.dest.clear(player, 3);
            }
        }
    }
    public static void playerDeath(Player player, Loc death) {
        if (!config.LastDeathSaving || !(boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__lastdeath)) return;
        Destination.lastdeath.add(player, death);
        CTxT msg = CUtl.tag().append(Destination.lastdeath.LANG.msg("save",
                death.getBadge()
                .append(" ").append(Destination.dest.setButtons("/dest set "+death.getXYZ()+" "+death.getDimension(),
                        Dimension.canConvert(player.getSpawnDimension(),death.getDimension())))));
        player.sendMessage(msg);
    }
}