
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.Player;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DefaultPData {

    @SerializedName("version")
    private Double version = 2.0;
    @SerializedName("name")
    private String name;
    @SerializedName("hud")
    private PDHud hud = new PDHud();
    @SerializedName("destination")
    private PDDestination destination = new PDDestination();
    @SerializedName("inbox")
    private List<HashMap<String,Object>> inbox = new ArrayList<>();
    @SerializedName("color_presets")
    private List<String> colorPresets = new ArrayList<>();
    @SerializedName("social_cooldown")
    private Double socialCooldown;

    private transient Player player;

    /**
     * sets pData to be saved and sends the updated pData to the player client if needed
     */
    public void save() {
        LoopManager.addSavePlayer(player);
        player.sendPDataPackets();
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.hud.setPlayer(player);
        this.destination.setPlayer(player);
        save();
    }

    public DefaultPData() {}

    public DefaultPData(Player player) {
        this.name = player.getName();
        this.colorPresets = PlayerData.DEFAULTS.getColorPresets();
        this.inbox = PlayerData.DEFAULTS.getInbox();
        this.destination = PlayerData.DEFAULTS.getDEST();
        this.hud = PlayerData.DEFAULTS.getHud();
        setPlayer(player);
    }

    public PDHud getHud() {
        return hud;
    }

    public String getName() {
        return name;
    }

    public PDDestination getDEST() {
        return destination;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
        // dont need to save, something else will save
    }

    public ArrayList<HashMap<String,Object>> getInbox() {
        return (ArrayList<HashMap<String, Object>>) inbox;
    }

    public void setInbox(List<HashMap<String,Object>> inbox) {
        this.inbox = inbox;
        save();
    }

    public ArrayList<String> getColorPresets() {
        return (ArrayList<String>) colorPresets;
    }

    public void setColorPresets(List<String> colorPresets) {
        this.colorPresets = colorPresets;
        save();
    }

    public Double getSocialCooldown() {
        return socialCooldown;
    }

    public void setSocialCooldown(Double socialCooldown) {
        this.socialCooldown = socialCooldown;
        save();
    }

    // LOADING AND SAVING
    public static File getPlayerFile(Player player) {
        if (config.online) return new File(DirectionHUD.DATA_DIR+"playerdata/" +player.getUUID()+".json");
        else return new File(DirectionHUD.DATA_DIR+"playerdata/"+player.getName()+".json");
    }

    public static void loadPlayer(Player player, boolean legacy) {
        File file = getPlayerFile(player);
        if (!file.exists()) savePlayer(player);
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            Gson gson = new GsonBuilder().create();
            PlayerData.setPlayerData(player,gson.fromJson(reader, PData.class));
            Updater.run(player);
        } catch (Exception e) {
            // if not loading from legacy, try before throwing an error
            if (legacy) {
                // if it couldn't get from file just get from map (generates a new one if it doesn't exist)
                PlayerData.getPData(player);
                DirectionHUD.LOGGER.info("ERROR READING PLAYER DATA - PLEASE REPORT WITH THE ERROR LOG");
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

    public static final String DEFAULT_FILE_NAME = "default-playerdata.json";

    public static File getDefaultFile() {
        return new File(DirectionHUD.CONFIG_DIR + DEFAULT_FILE_NAME);
    }

    public static void loadDefaults() {
        File file = getDefaultFile();
        if (!file.exists()) {
            DirectionHUD.LOGGER.info(String.format("Creating new `%s`",DEFAULT_FILE_NAME));
            saveDefaults();
        }
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            Gson gson = new GsonBuilder().create();
            PlayerData.setDEFAULTS(gson.fromJson(reader, DefaultPData.class));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info(String.format("ERROR LOADING '%s`",DEFAULT_FILE_NAME));
            e.printStackTrace();
        }
        saveDefaults();

    }

    public static void saveDefaults() {
        try (BufferedWriter writer = Files.newBufferedWriter(getDefaultFile().toPath())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(PlayerData.DEFAULTS));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info(String.format("ERROR SAVING '%s`",DEFAULT_FILE_NAME));
            e.printStackTrace();
        }
    }

}
