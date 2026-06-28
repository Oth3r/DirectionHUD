package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.common.utils.DirectionHudData;
import one.oth3r.otterlib.chat.LoaderText;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PluginData extends DirectionHudData {
    private final BossBarManager bossBarManager = new BossBarManager();
    private ArrayList<DPlayer> clientPlayers = new ArrayList<>();

    private Plugin plugin;

    public PluginData(boolean isMod, String primary, String secondary) {
        super(isMod, primary, secondary);
    }

    @Override
    public CTxT getCTxTFromObj(Object obj) {
        return switch (obj) {
            case CTxT txt -> txt.clone();
            case LoaderText<?> txt -> new CTxT(txt.b());
            case TextComponent ignored -> new CTxT((TextComponent) obj);
            // else, try to convert into a string
            case null, default -> new CTxT(String.valueOf(obj));
        };
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public ArrayList<DPlayer> getClientPlayers() {
        return clientPlayers;
    }

    public void setClientPlayers(ArrayList<DPlayer> clientPlayers) {
        this.clientPlayers = clientPlayers;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
