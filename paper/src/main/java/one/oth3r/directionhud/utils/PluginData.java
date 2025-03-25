package one.oth3r.directionhud.utils;

import one.oth3r.directionhud.common.utils.DirectionHudData;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PluginData extends DirectionHudData {
    private final BossBarManager bossBarManager = new BossBarManager();
    private ArrayList<Player> clientPlayers = new ArrayList<>();

    private Plugin plugin;

    public PluginData(boolean isMod, String primary, String secondary) {
        super(isMod, primary, secondary);
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public ArrayList<Player> getClientPlayers() {
        return clientPlayers;
    }

    public void setClientPlayers(ArrayList<Player> clientPlayers) {
        this.clientPlayers = clientPlayers;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
