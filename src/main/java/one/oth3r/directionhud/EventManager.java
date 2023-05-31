package one.oth3r.directionhud;

import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

public class EventManager implements Listener {
    @EventHandler
    public static void playerJoin(PlayerJoinEvent event) {
        PlayerData.addPlayer(Player.of(event.getPlayer()));
        DirectionHUD.players.put(event.getPlayer(),false);
    }
    @EventHandler
    public static void playerQuit(PlayerQuitEvent event) {
        PlayerData.removePlayer(Player.of(event.getPlayer()));
        DirectionHUD.players.remove(event.getPlayer());
    }
    @EventHandler
    public static void switchWorld(PlayerChangedWorldEvent event) {
        Player player = Player.of(event.getPlayer());
        assert player != null;
        if (Destination.get(player).hasXYZ()) {
            Loc loc = Destination.get(player);
            String oldDIM = player.getDimension();
            if (Utl.dim.canConvert(oldDIM,Destination.get(player).getDIM()) && PlayerData.get.dest.setting.autoconvert(player)) {
                Loc cLoc = Destination.get(player);
                cLoc.convertTo(oldDIM);
                Destination.silentSet(player,cLoc);
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.dest"))
                        .append("\n ").append(CUtl.lang("dest.autoconvert.info",loc.getBadge(),cLoc.getBadge()).italic(true).color('7')));
            } else if (PlayerData.get.dest.setting.autoclear(player)) {
                CTxT msg = CTxT.of("").append(CUtl.lang("dest.changed.cleared.dim").color('7').italic(true))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+oldDIM));
                if (Utl.dim.canConvert(oldDIM,Destination.get(player).getDIM()))
                    msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+oldDIM+" convert"));
                Destination.clear(player, msg);
            }
        }
    }
    @EventHandler
    public static void playerDeath(PlayerDeathEvent event) {
        Player player = Player.of(event.getEntity());
        assert player != null;
        if (!config.deathsaving || !PlayerData.get.dest.setting.lastdeath(player)) return;
        Loc loc = new Loc(player);
        Destination.lastdeath.add(player, loc);
        CTxT msg = CUtl.tag().append(CUtl.lang("dest.lastdeath.save"))
                .append(" ").append(loc.getBadge())
                .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+loc.getDIM()));
        if (Utl.dim.canConvert(Utl.dim.format(Objects.requireNonNull(Objects.requireNonNull(
                        player.getPlayer().getBedSpawnLocation()).getWorld()).getName()),loc.getDIM()))
            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+loc.getDIM()+" convert"));
        player.sendMessage(msg);
    }
}
