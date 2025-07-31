package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.otterlib.file.LanguageReader;
import one.oth3r.otterlib.file.ResourceReader;
import one.oth3r.otterlib.registry.LanguageReg;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Events {
    public static void init() {
        // register the module text
        List<BaseModule> modules = List.of(
                new ModuleAngle(),new ModuleCoordinates(),new ModuleDestination(),new ModuleDirection(),
                new ModuleDistance(),new ModuleSpeed(),new ModuleTime(),new ModuleTracking(),new ModuleWeather(),
                new ModuleLight()
        );
        modules.forEach(bm -> DisplayRegistry.registerModuleDisplay(bm.getModuleType(),bm.getDisplaySettings()));

        // register the main language file
        LanguageReg.registerLang(Assets.MOD_ID,new LanguageReader(
                DirectionHUD.getData().getDefaultLanguageLocation(),
                new ResourceReader(DirectionHUD.getData().getConfigDirectory()),
                "en_us",FileData.getConfig().getLang()));
    }

    public static void serverStart() {
        DirectionHUD.getData().setServerStarted(true);
        try {
            Files.createDirectories(Paths.get(DirectionHUD.getData().getDataDirectory()+"playerdata/"));
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
        DirectionHUD.getData().clear();
        DirectionHUD.LOGGER.info("Safely shutdown DirectionHUD server!");
    }

    public static void playerJoin(Player player) {
        PlayerData.addPlayer(player);

        // add the bossbar on player join to fix duplicate boss bar issue on spigot
        if (player.getPCache().getHud().getSetting().getType().equals(Hud.Setting.DisplayType.bossbar.toString())) {
            DirectionHUD.getData().getBossBarManager().addPlayer(player);
        }
    }

    public static void playerLeave(Player player) {
        playerSoftLeave(player);
        DirectionHUD.getData().getClientPlayers().remove(player);
    }

    /**
     * effectively reloads the player without deleting certain required maps (like clientPlayers)
     */
    public static void playerSoftLeave(Player player) {
        DHud.inbox.removeAllTracking(player);
        PlayerData.removePlayer(player);
        DirectionHUD.getData().getBossBarManager().removePlayer(player);
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
                        new CTxT("\n ").append(Destination.LANG.msg("autoconvert.destination.2",loc.getBadge(),dest.getBadge())))));
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