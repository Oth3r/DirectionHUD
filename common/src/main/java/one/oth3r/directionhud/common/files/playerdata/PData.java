
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;

public class PData extends DefaultPData {

    public PData(Player player) {
        super(player);
    }

    // LOADING AND SAVING
    public static File getPlayerFile(Player player) {
        if (config.online) return new File(DirectionHUD.DATA_DIR+"playerdata/" +player.getUUID()+".json");
        else return new File(DirectionHUD.DATA_DIR+"playerdata/"+player.getName()+".json");
    }

    public static void loadPlayer(Player player, boolean checkLegacy) {
        File file = getPlayerFile(player);
        if (!file.exists()) {
            DirectionHUD.LOGGER.info("Creating new playerdata file for "+player.getName());
            PlayerData.setPlayerData(player, new PData(player));
            savePlayer(player);
        }
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            Gson gson = new GsonBuilder().create();
            PlayerData.setPlayerData(player,gson.fromJson(reader, PData.class));
        } catch (Exception e) {
            // if not checking legacy, throw an error
            if (!checkLegacy) {
                // if it couldn't get from file just get from map (generates a new one if it doesn't exist)
                PlayerData.setPlayerData(player, new PData(player));
                DirectionHUD.LOGGER.info("ERROR READING PLAYER DATA, RESETTING PLAYER! ERROR:");
                e.printStackTrace();
            } else {
                Updater.legacy.update(player);
            }
        }
    }

    public static void savePlayer(Player player) {
        try (BufferedWriter writer = Files.newBufferedWriter(getPlayerFile(player).toPath())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(PlayerData.getPData(player)));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR WRITING PLAYER DATA - PLEASE REPORT WITH THE ERROR LOG");
            e.printStackTrace();
        }
    }
}
