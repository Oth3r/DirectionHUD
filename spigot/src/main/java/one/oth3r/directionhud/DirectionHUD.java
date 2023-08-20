package one.oth3r.directionhud;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.DirHUDCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.utils.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class DirectionHUD extends JavaPlugin {
    public static final String PRIMARY = "#ff8e16";
    public static final String SECONDARY = "#42a0ff";
    public static String PLAYERDATA_DIR;
    public static String CONFIG_DIR;
    public static Logger LOGGER;
    public static String VERSION;
    public static HashMap<Player,Boolean> players = new HashMap<>();
    public static String playerData;
    public static String configDir;
    public static final boolean isMod = false;
    public static boolean isClient = false;

    @Override
    public void onEnable() {
        PLAYERDATA_DIR = this.getDataFolder().getPath()+"/playerdata/";
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
        Objects.requireNonNull(getCommand("directionhud")).setExecutor(new DirHUDCommand());
        Objects.requireNonNull(getCommand("directionhud")).setTabCompleter(new DirHUDCommand());
        //LOOP & EVENTS
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                LoopManager.tick();
            }
        }.runTaskTimerAsynchronously(this, 0L, 1L);
    }
    @Override
    public void onDisable() {
        Events.serverEnd();
    }
}
