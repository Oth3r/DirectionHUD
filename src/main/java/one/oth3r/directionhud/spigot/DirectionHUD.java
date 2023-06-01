package one.oth3r.directionhud.spigot;

import one.oth3r.directionhud.spigot.commands.DestinationCommand;
import one.oth3r.directionhud.spigot.commands.DirHUDCommand;
import one.oth3r.directionhud.spigot.commands.HUDCommand;
import one.oth3r.directionhud.spigot.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.spigot.files.config;
import one.oth3r.directionhud.spigot.utils.Player;
import one.oth3r.directionhud.spigot.utils.Utl;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class DirectionHUD extends JavaPlugin {
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
        configDir = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getDataFolder().getPath()+"/";
        playerData = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("DirectionHUD")).getDataFolder().getPath()+"/playerdata/";
        config.load();
        LangReader.loadLanguageFile();
        Path dirPath = Paths.get(DirectionHUD.playerData);
        try {
            Files.createDirectories(dirPath);
        } catch (Exception e) {
            LOGGER.info("Failed to create playerdata directory:\n" + e.getMessage());
        }
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
        LOGGER.info("DirectionHUD: Shutting down...");
        for (Player player:Utl.getPlayers())
            PlayerData.removePlayer(player);
    }
}
