package one.oth3r.directionhud.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import one.oth3r.directionhud.common.utils.DirectionHudData;

import java.util.ArrayList;

public class ModData extends DirectionHudData {
    private boolean isSingleplayer;
    private boolean onSupportedServer;

    private PlayerManager playerManager;
    private MinecraftServer server;
    private CommandManager commandManager;

    // todo make bossbar manager a common object
    private final BossBarManager bossBarManager = new BossBarManager();
    private final HudClientActionBarOverride actionBarOverride = new HudClientActionBarOverride(40);
    // todo make clientPlayers common
    private final ArrayList<Player> clientPlayers = new ArrayList<>();

    public ModData(boolean isMod, String version, String primary, String secondary) {
        super(isMod, version, primary, secondary);
        this.isSingleplayer = false;
        this.onSupportedServer = false;
    }

    @Override
    public void clear() {
        super.clear();

        isSingleplayer = false;
        onSupportedServer = false;

        playerManager = null;
        server = null;
        commandManager = null;

        bossBarManager.clear();
        actionBarOverride.clear();
        clientPlayers.clear();
    }

    public boolean isSingleplayer() {
        return isSingleplayer;
    }

    public void setSingleplayer(boolean singleplayer) {
        isSingleplayer = singleplayer;
    }

    public boolean isOnSupportedServer() {
        return onSupportedServer;
    }

    public void setOnSupportedServer(boolean onSupportedServer) {
        this.onSupportedServer = onSupportedServer;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public HudClientActionBarOverride getActionBarOverride() {
        return actionBarOverride;
    }

    public ArrayList<Player> getClientPlayers() {
        return clientPlayers;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public void setPlayerManager(PlayerManager playerManager) {
        this.playerManager = playerManager;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public void setServer(MinecraftServer server) {
        this.server = server;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }
}
