package one.oth3r.directionhud;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.DHUDCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.PluginData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.logging.Logger;

public class DirectionHUD extends JavaPlugin implements PluginMessageListener {
    public static Logger LOGGER;


    private static final PluginData pluginData = new PluginData(false, "#3b82f6", "#ffee35");

    public static PluginData getData() {
        return pluginData;
    }

    @Override
    public void onEnable() {
        getData().setPlugin(this);
        getData().setDataDirectory(this.getDataFolder().getPath()+"/");
        getData().setConfigDirectory(this.getDataFolder().getPath()+"/");
        getData().setVersion(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getPluginMeta().getVersion());
        LOGGER = this.getLogger();

        Events.init();
        Events.serverStart();

        //COMMANDS
        Objects.requireNonNull(getCommand("destination")).setExecutor(new DestinationCommand());
        Objects.requireNonNull(getCommand("destination")).setTabCompleter(new DestinationCommand());
        Objects.requireNonNull(getCommand("hud")).setExecutor(new HUDCommand());
        Objects.requireNonNull(getCommand("hud")).setTabCompleter(new HUDCommand());
        Objects.requireNonNull(getCommand("directionhud")).setExecutor(new DHUDCommand());
        Objects.requireNonNull(getCommand("directionhud")).setTabCompleter(new DHUDCommand());

        //LOOP & EVENTS
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        if (isFolia()) {
            GlobalRegionScheduler globalScheduler = this.getServer().getGlobalRegionScheduler();
            globalScheduler.runAtFixedRate(this, (scheduledTask) -> LoopManager.tick(), 1L, 1L);
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    LoopManager.tick();
                }
            }.runTaskTimerAsynchronously(this, 0L, 1L);
        }

        // register incoming and outgoing packets
        this.getServer().getMessenger().registerIncomingPluginChannel(this, PacketHelper.getChannel(Assets.packets.INITIALIZATION), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, PacketHelper.getChannel(Assets.packets.PLAYER_DATA));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, PacketHelper.getChannel(Assets.packets.HUD));
    }

    @Override
    public void onDisable() {
        Events.serverEnd();
        // unregister packet listeners on disable
        this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, org.bukkit.entity.@NotNull Player player, byte @NotNull [] message) {
        // make sure packets only work on the supported minecraft version(s)
        if (getMCVersion() < 20.6) return;

        if (channel.equals(PacketHelper.getChannel(Assets.packets.INITIALIZATION))) {
            // if the client has directionhud, add them to the list & send the data packets
            DirectionHUD.LOGGER.info("Received initialization packet from "+player.getName()+", connecting to client.");
            Player dPlayer = new Player(player);
            getData().getClientPlayers().add(dPlayer);
            dPlayer.sendPDataPackets();
        }
    }

    /**
     * gets the MC version as a float, removing the first decimal, eg 20.6
     */
    public static float getMCVersion() {
        // like 1.20.6
        String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
        // remove 1.
        String last2 = version.substring(version.indexOf(".")+1);
        // get as float, 20.6
        return Float.parseFloat(last2);
    }

    private static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
