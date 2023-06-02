package one.oth3r.directionhud.spigot;

import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.spigot.commands.DestinationCommand;
import one.oth3r.directionhud.spigot.commands.DirHUDCommand;
import one.oth3r.directionhud.spigot.commands.HUDCommand;
import one.oth3r.directionhud.spigot.utils.Player;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class DirectionHUD extends JavaPlugin {
    public static final String PLAYERDATA_DIR = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getDataFolder().getPath()+"/";
    public static final String CONFIG_DIR = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getDataFolder().getPath()+"/";
    public static Logger LOGGER;
    public static String VERSION;
    public static HashMap<Player,Boolean> players = new HashMap<>();
    public static String playerData;
    public static String configDir;

    @Override
    public void onEnable() {
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
