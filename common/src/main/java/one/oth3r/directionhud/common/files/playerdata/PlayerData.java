package one.oth3r.directionhud.common.files.playerdata;

import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.utils.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerData {

    public static DefaultPData DEFAULTS = new DefaultPData();

    public static void setDEFAULTS(DefaultPData DEFAULTS) {
        if (DEFAULTS == null) return;
        PlayerData.DEFAULTS = DEFAULTS;
    }

    private static final Map<Player, PData> playerData = new HashMap<>();

    /**
     * clears everything inside the playerData map
     */
    public static void clearPlayerData() {
        playerData.clear();
    }

    public static void setPlayerData(Player player, PData pData) {
        playerData.put(player,pData);
    }

    public static void removePlayerData(Player player) {
        playerData.remove(player);
    }

    /**
     * gets the pData for the player, creating a new one if they don't have
     * @return the pData
     */
    public static PData getPData(Player player) {
        if (playerData.get(player) == null) {
            playerData.put(player,new PData(player));
            // run the updater
            Updater.run(player);
        }
        return playerData.get(player);
    }

    /**
     * adds the player into the system (when they first join)
     */
    public static void addPlayer(Player player) {
        PData.loadPlayer(player,false);
        HashMap<String, Object> dataMapDefault = new HashMap<>();
        dataMapDefault.put("speed_data", player.getVec());
        dataMapDefault.put("speed", 0.0);
        getPData(player).setDataMap(dataMapDefault);
    }

    /**
     * removes a player from the system
     */
    public static void removePlayer(Player player) {
        LoopManager.removeSavePlayer(player);
        PData.savePlayer(player);
        removePlayerData(player);
    }
}