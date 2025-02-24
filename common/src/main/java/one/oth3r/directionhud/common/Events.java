package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Events {
    public static void serverStart() {
        ModData.setServerStarted(true);
        try {
            Files.createDirectories(Paths.get(DirectionHUD.DATA_DIR+"playerdata/"));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Failed to create playerdata directory.");
        }
        FileData.loadFiles();
        DirectionHUD.LOGGER.info("Started server!");
    }

    public static void serverEnd() {
        for (Player player: Utl.getPlayers()) playerLeave(player);
        // clear everything as serverEnd on client can just be exiting single-player
        FileData.clearServerData();
        PlayerData.clearPlayerData();
        PlayerData.clearPlayerCache();
        DirectionHUD.clear();
        DirectionHUD.LOGGER.info("Safely shutdown DirectionHUD server!");
        ModData.setServerStarted(false);
    }

    public static void playerJoin(Player player) {
        PlayerData.addPlayer(player);

        // add the bossbar on player join to fix duplicate boss bar issue on spigot
        if (player.getPCache().getHud().getSetting().getType().equals(Hud.Setting.DisplayType.bossbar.toString())) {
            DirectionHUD.bossBarManager.addPlayer(player);
        }
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
                Dest dest = Destination.dest.get(player);
                dest.convertTo(toDIM);
                Destination.dest.set(player,dest);
                player.sendMessage(CUtl.tag().append(Destination.LANG.msg("autoconvert.destination",
                        CTxT.of("\n ").append(Destination.LANG.msg("autoconvert.destination.2",loc.getBadge(),dest.getBadge())))));
            } else if ((boolean) player.getPData().getDEST().getSetting(Destination.Setting.autoclear)) {
                // clear if autoclear is on
                Destination.dest.clear(player, 3);
            }
        }
    }

    public static void playerDeath(Player player, Loc deathLoc) {
        if (!Helper.checkEnabled(player).lastdeath()) return;
        Destination.lastdeath.add(player, deathLoc);
        CTxT msg = CUtl.tag().append(Destination.lastdeath.LANG.msg("save",
                deathLoc.getBadge()
                .append(" ").append(Destination.dest.setButtons(new Dest(deathLoc,null,null),
                        Dimension.canConvert(player.getSpawnDimension(), deathLoc.getDimension())))));
        player.sendMessage(msg);
    }
}