package one.oth3r.directionhud;

import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    @EventHandler
    public static void playerJoin(PlayerJoinEvent event) {
        Events.playerJoin(new Player(event.getPlayer()));
    }

    @EventHandler
    public static void playerQuit(PlayerQuitEvent event) {
        Events.playerLeave(new Player(event.getPlayer()));
        DirectionHUD.clientPlayers.remove(new Player(event.getPlayer()));
    }

    @EventHandler
    public static void switchWorld(PlayerChangedWorldEvent event) {
        Player player = new Player(event.getPlayer());
        Events.playerChangeWorld(player,Utl.dim.format(event.getFrom()),player.getDimension());
    }

    @EventHandler
    public static void playerDeath(PlayerDeathEvent event) {
        Player player = new Player(event.getEntity());
        Events.playerDeath(player,player.getLoc());
    }
}
