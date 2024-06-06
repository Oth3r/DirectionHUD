package one.oth3r.directionhud.common.files.playerdata;

import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    public static class Queue {
        /**
         * anyone in the savePlayers list will have the playerData saved to file and removed from the list every second
         */
        private static final ArrayList<Player> SAVE = new ArrayList<>();

        private static final ConcurrentHashMap<Player, Integer> EXPIRE = new ConcurrentHashMap<>();

        public static void addSavePlayer(Player player) {
            if (!SAVE.contains(player)) SAVE.add(player);
        }

        public static void updateExpireTime(Player player) {
            EXPIRE.put(player, 30);
        }

        /**
         * ticks both the SAVE and EXPIRE lists
         */
        public static void tick() {
            // save everyone in the list and remove
            // use an iterator to not cause any issues
            Iterator<Player> iterator = SAVE.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                PData.savePlayer(player);
                iterator.remove();
            }
            // tick everyone in the expire map, and remove the pData for the expired people
            // use an iterator to not cause any issues
            iterator = EXPIRE.keySet().iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                EXPIRE.put(player, EXPIRE.get(player)-1);
                if (EXPIRE.get(player) < 1) {
                    removePlayerData(player);
                    iterator.remove();
                }
            }
        }

        /**
         * removes the player from the system
         */
        private static void clearPlayer(Player player) {
            SAVE.remove(player);
            EXPIRE.remove(player);
        }
    }

    private static DefaultPData defaults = new DefaultPData();

    public static void setDefaults(DefaultPData defaults) {
        if (defaults == null) return;
        PlayerData.defaults = new DefaultPData(defaults);
    }
    
    public static DefaultPData getDefaults() {
        return new DefaultPData(defaults);
    }

    private static final Map<Player, CachedPData> playerCache = new HashMap<>();

    /**
     * clears everything inside the playerCache map
     */
    public static void clearPlayerCache() {
        playerCache.clear();
    }

    public static void setPlayerCache(Player player, CachedPData pCache) {
        playerCache.put(player,pCache);
    }

    public static void removePlayerCache(Player player) {
        playerCache.remove(player);
    }

    /**
     * gets the pData for the player, creating a new one if they don't have
     * @return the pData
     */
    public static CachedPData getPCache(Player player) {
        if (!playerCache.containsKey(player)) {
            // make a new cache if there is none
            setPlayerCache(player, new CachedPData(player.getPData()));
        }
        return playerCache.get(player);
    }

    private static final Map<Player, PData> playerData = new HashMap<>();

    /**
     * clears everything inside the playerData map
     */
    public static void clearPlayerData() {
        playerData.clear();
    }

    /**
     * set a player's pData, making sure that the pData isnt null
     */
    public static void setPlayerData(Player player, PData pData) {
        // make sure the pData isnt null, if it is and the player doesn't have a pData, make a new one for them
        if (pData != null) {
            playerData.put(player,pData);
        } else if (!playerData.containsKey(player)) {
            playerData.put(player,new PData(player));
        }
    }

    public static void removePlayerData(Player player) {
        playerData.remove(player);
    }

    /**
     * gets the pData for the player, loading from the file if not gotten recently
     * @return the pData
     */
    public static PData getPData(Player player) {
        if (!playerData.containsKey(player)) {
            PData.loadPlayer(player);
        }
        // accessing the pData, bump the timer
        Queue.updateExpireTime(player);
        return playerData.get(player);
    }

    /**
     * adds the player into the system (when they first join)
     */
    public static void addPlayer(Player player) {
        PData.loadPlayer(player);
    }

    /**
     * removes a player from the system
     */
    public static void removePlayer(Player player) {
        // only clear the playerData if the player has it
        if (playerData.containsKey(player)) {
            PData.savePlayer(player);
            removePlayerData(player);
        }
        Queue.clearPlayer(player);
        removePlayerCache(player);
    }
}