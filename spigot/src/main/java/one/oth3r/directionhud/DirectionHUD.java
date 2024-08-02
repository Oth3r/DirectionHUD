package one.oth3r.directionhud;

import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.DHUDCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.utils.BossBarManager;
import one.oth3r.directionhud.utils.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;

public class DirectionHUD extends JavaPlugin implements PluginMessageListener {
    public static ArrayList<Player> clientPlayers = new ArrayList<>();
    public static Plugin plugin;
    public static final String PRIMARY = "#ff8e16";
    public static final String SECONDARY = "#42a0ff";
    public static BossBarManager bossBarManager = new BossBarManager();
    public static String DATA_DIR;
    public static String CONFIG_DIR;
    public static Logger LOGGER;
    public static String VERSION;
    public static final boolean isMod = false;
    public static boolean isClient = false;

    @Override
    public void onEnable() {
        plugin = this;
        DATA_DIR = this.getDataFolder().getPath()+"/";
        CONFIG_DIR = this.getDataFolder().getPath()+"/";
        PluginDescriptionFile pdf = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getDescription();
        VERSION = pdf.getVersion();
        LOGGER = this.getLogger();
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
        new BukkitRunnable() {
            @Override
            public void run() {
                LoopManager.tick();
            }
        }.runTaskTimerAsynchronously(this, 0L, 1L);

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
    public void onPluginMessageReceived(String channel, org.bukkit.entity.Player player, byte[] message) {
        // make sure packets only work on the latest supported minecraft version
        if (!getMCVersion().equals("1.20.6")) return;

        if (channel.equals(PacketHelper.getChannel(Assets.packets.INITIALIZATION))) {
            // if the client has directionhud, add them to the list & send the data packets
            DirectionHUD.LOGGER.info("Received initialization packet from "+player.getName()+", connecting to client.");
            Player dPlayer = new Player(player);
            clientPlayers.add(dPlayer);
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

    public static void clear() {
        clientPlayers.clear();
    }
}
