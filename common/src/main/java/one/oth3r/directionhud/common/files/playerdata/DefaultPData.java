
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.Updater;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.common.utils.Helper.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultPData {

    @SerializedName("version")
    private Double version = 2.0;
    @SerializedName("name")
    private String name;
    @SerializedName("hud")
    private PDHud hud = new PDHud();
    @SerializedName("destination")
    private PDDestination destination = new PDDestination();
    @SerializedName("color_presets")
    private ArrayList<ColorPreset> colorPresets = new ArrayList<>();
    @SerializedName("inbox")
    private ArrayList<HashMap<String,String>> inbox = new ArrayList<>();
    @SerializedName("social_cooldown")
    private Integer socialCooldown;

    private transient Player player;

    /**
     * sets pData to be saved and sends the updated pData to the player client if needed
     */
    public void save() {
        if (player == null) return;

        // add to saving queue
        PlayerData.Queue.addSavePlayer(player);

        // update the cached variables
        CachedPData cachedPData = PlayerData.getPCache(player);
        inbox = cachedPData.getInbox();
        socialCooldown = cachedPData.getSocialCooldown();
        cachedPData.update(this);

        player.sendPDataPackets();
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.hud.setPlayer(player);
        this.destination.setPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }

    public DefaultPData() {}

    public DefaultPData(DefaultPData defaultPData) {
        version = defaultPData.version;
        name = defaultPData.getName();
        hud = defaultPData.getHud();
        destination = defaultPData.getDEST();
        colorPresets = defaultPData.getColorPresets();
        inbox = defaultPData.getInbox();
        socialCooldown = defaultPData.getSocialCooldown();
        player = defaultPData.getPlayer();
    }

    public DefaultPData(Player player) {
        this.name = player.getName();
        this.colorPresets = PlayerData.getDefaults().getColorPresets();
        this.inbox = PlayerData.getDefaults().getInbox();
        this.destination = PlayerData.getDefaults().getDEST();
        this.hud = PlayerData.getDefaults().getHud();
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

    public ArrayList<ColorPreset> getColorPresets() {
        return colorPresets;
    }

    public void setColorPresets(ArrayList<ColorPreset> colorPresets) {
        this.colorPresets = colorPresets;
        save();
    }

    // CACHE ITEMS (write in cache)

    public ArrayList<HashMap<String,String>> getInbox() {
        return inbox;
    }

    public Integer getSocialCooldown() {
        return socialCooldown;
    }


    // LOADING AND SAVING

    public static final String FILE_NAME = "default-playerdata.json";

    public static File getDefaultFile() {
        return new File(DirectionHUD.CONFIG_DIR + FILE_NAME);
    }

    /**
     * loads the default pData file to the system
     */
    public static void loadDefaults() {
        File file = getDefaultFile();
        // create the file if non-existent
        if (!file.exists()) saveDefaults();
        // try loading
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            Updater.DefaultPlayerData.run(reader);
        } catch (NullPointerException | IOException e) {
            DirectionHUD.LOGGER.info(String.format("Error loading '%s`, reverting to defaults.", FILE_NAME));
        }
        // save after everything
        saveDefaults();
    }

    /**
     * saves the default pData to file
     */
    public static void saveDefaults() {
        if (!getDefaultFile().exists()) {
            DirectionHUD.LOGGER.info(String.format("Creating new `%s`", FILE_NAME));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getDefaultFile().toPath())) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(PlayerData.getDefaults()));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info(String.format("ERROR SAVING '%s`.", FILE_NAME));
            e.printStackTrace();
        }
    }
}
