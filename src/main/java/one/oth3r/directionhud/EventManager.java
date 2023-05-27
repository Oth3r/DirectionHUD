package one.oth3r.directionhud;

import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Loc;
import one.oth3r.directionhud.utils.Utl;
import org.bukkit.entity.Player;
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
        PlayerData.addPlayer(event.getPlayer());
        DirectionHUD.players.put(event.getPlayer(),false);
    }
    @EventHandler
    public static void playerQuit(PlayerQuitEvent event) {
        PlayerData.removePlayer(event.getPlayer());
        DirectionHUD.players.remove(event.getPlayer());
    }
    @EventHandler
    public static void switchWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (Destination.get(player).hasXYZ()) {
            Loc loc = Destination.get(player);
            String oldDIM = Utl.dim.format(player.getWorld().getName());
            if (Utl.dim.canConvert(Utl.player.dim(player),Destination.get(player).getDIM()) && PlayerData.get.dest.setting.autoconvert(player)
                    && !Utl.player.dim(player).equals(Destination.get(player).getDIM())) {
                Loc cLoc = Destination.get(player);
                cLoc.convertTo(Utl.player.dim(player));
                Destination.silentSet(player,cLoc);
                player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.dest"))
                        .append("\n ").append(CUtl.lang("dest.autoconvert.info",loc.getBadge(),cLoc.getBadge()).italic(true).color('7')).b());
            } else if (PlayerData.get.dest.setting.autoclear(player)) {
                CTxT msg = CTxT.of("").append(CUtl.lang("dest.changed.cleared.dim").color('7').italic(true))
                        .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+oldDIM));
                if (Utl.dim.canConvert(Utl.player.dim(player),Destination.get(player).getDIM()))
                    msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+oldDIM+" convert"));
                Destination.clear(player, msg);
            }
        }
    }
    @EventHandler
    public static void playerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!config.deathsaving || !PlayerData.get.dest.setting.lastdeath(player)) return;
        Loc loc = new Loc(player);
        Destination.lastdeath.add(player, loc);
        CTxT msg = CUtl.tag().append(CUtl.lang("dest.lastdeath.save"))
                .append(" ").append(loc.getBadge())
                .append(" ").append(CUtl.CButton.dest.set("/dest set "+loc.getXYZ()+" "+loc.getDIM()));
        if (Utl.dim.canConvert(Utl.dim.format(Objects.requireNonNull(Objects.requireNonNull(
                        player.getBedSpawnLocation()).getWorld()).getName()),loc.getDIM()))
            msg.append(" ").append(CUtl.CButton.dest.convert("/dest set "+loc.getXYZ()+" "+loc.getDIM()+" convert"));
        player.spigot().sendMessage(msg.b());
    }
}
