
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.Data;
import one.oth3r.directionhud.common.exception.UnsupportedVersionException;
import one.oth3r.directionhud.common.files.Updater;
import one.oth3r.directionhud.utils.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class PData extends DefaultPData {

    public PData(Player player) {
        super(player);
    }

    public PData(Player player, DefaultPData defaultPData) {
        super(defaultPData);
        setPlayer(player);
    }

    public PData(DefaultPData defaultPData) {
        super(defaultPData);
    }

    // LOADING AND SAVING
    public static File getPlayerFile(Player player) {
        if (Data.getConfig().getOnline()) return new File(DirectionHUD.DATA_DIR+"playerdata/" +player.getUUID()+".json");
        else return new File(DirectionHUD.DATA_DIR+"playerdata/"+player.getName()+".json");
    }

    /**
     * loads the player file to PlayerData.playerData
     */
    public static void loadPlayer(Player player) {
        File file = getPlayerFile(player);
        if (file.exists()) {
            // if the file is real
            try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
                Updater.PlayerFile.run(player, reader);
            } catch (UnsupportedVersionException ignored) {
                // unsupported playerdata version
                DirectionHUD.LOGGER.info(String.format("Old PlayerData version detected for %s! Trying to load from legacy...", player.getName()));
                Updater.PlayerFile.legacy.update(player);
            } catch (NullPointerException | IOException ignored) {
                // the file data is null. generates a new playerdata file for the player
                PlayerData.setPlayerData(player, new PData(player));
                DirectionHUD.LOGGER.info(String.format("There was an error reading the PlayerData file of %s. Resetting the player to defaults.", player.getName()));
            }
        } else {
            // file doesnt exist
            DirectionHUD.LOGGER.info("Creating new playerdata file for "+player.getName());
            PlayerData.setPlayerData(player, new PData(player));
        }
        // save the file
        savePlayer(player);
    }

    public static void savePlayer(Player player) {
        try (BufferedWriter writer = Files.newBufferedWriter(getPlayerFile(player).toPath())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(PlayerData.getPData(player)));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR WRITING PLAYER DATA: "+e.getMessage());
        }
    }
}
