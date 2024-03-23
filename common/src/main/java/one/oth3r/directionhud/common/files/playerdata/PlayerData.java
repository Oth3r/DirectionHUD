package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerData {
    private static final Map<Player, PData> playerData = new HashMap<>();

    /**
     * clears everything inside the playerData map
     */
    public static void clearPlayerData() {
        playerData.clear();
    }
    public static Map<Player, PData> getPlayerData() {
        return new HashMap<>(playerData);
    }

    /**
     * gets the pData for the player, creating a new one if they don't have
     * @return the pData
     */
    public static PData getPData(Player player) {
        if (getPlayerData().get(player) == null) {
            playerData.put(player,new PData(player));
        }
        return playerData.get(player);
    }

    /**
     * adds the player into the system (when they first join)
     */
    public static void addPlayer(Player player) {
        loadFromFile(player);
        HashMap<String, Object> dataMapDefault = new HashMap<>();
        dataMapDefault.put("speed_data", player.getVec());
        dataMapDefault.put("speed", 0.0);
        getPData(player).setDataMap(dataMapDefault);
    }

    /**
     * removes a player from the system
     */
    public static void removePlayer(Player player) {
        toFile(player);
        playerData.remove(player);
    }


    public static File getFile(Player player) {
        if (config.online) return new File(DirectionHUD.DATA_DIR+"playerdata/" +player.getUUID()+".json");
        else return new File(DirectionHUD.DATA_DIR+"playerdata/"+player.getName()+".json");
    }
    public static void loadFromFile(Player player) {
        File file = getFile(player);
        if (!file.exists()) toFile(player);
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            playerData.put(player,gson.fromJson(reader, PData.class));
            Updater.run(player);
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR READING PLAYER DATA - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
        // if it couldn't get from file just get from map (generates a new one if it doesn't exist)
        getPData(player);
    }

    public static void toFile(Player player) {
        try (FileWriter writer = new FileWriter(getFile(player))){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(getPData(player)));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR WRITING PLAYER DATA - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }
}