package one.oth3r.directionhud.spigot;

import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.spigot.utils.Player;
import one.oth3r.directionhud.spigot.utils.Utl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventManager implements Listener {
    @EventHandler
    public static void playerJoin(PlayerJoinEvent event) {
        Events.playerJoin(Player.of(event.getPlayer()));
    }
    @EventHandler
    public static void playerQuit(PlayerQuitEvent event) {
        Events.playerLeave(Player.of(event.getPlayer()));
    }
    @EventHandler
    public static void switchWorld(PlayerChangedWorldEvent event) {
        Player player = Player.of(event.getPlayer());
        Events.playerChangeWorld(player,Utl.dim.format(event.getFrom().getName()),player.getDimension());
    }
    @EventHandler
    public static void playerDeath(PlayerDeathEvent event) {
        Player player = Player.of(event.getEntity());
        Events.playerDeath(player,player.getLoc());
    }
}
