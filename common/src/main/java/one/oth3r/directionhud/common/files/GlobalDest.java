package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Dest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class GlobalDest {
    private Double version = 1.0;

    private ArrayList<Dest> destinations = new ArrayList<>();

    public GlobalDest() {}

    public GlobalDest(GlobalDest globalDest) {
        this.version = globalDest.version;
        this.destinations = globalDest.destinations;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public ArrayList<Dest> getDestinations() {
        return destinations;
    }

    public void setDestinations(ArrayList<Dest> destinations) {
        this.destinations = destinations;
    }

    // SAVING AND LOADING

    public static final String FILE_NAME = "global-dest.json";

    public static File getFile() {
        return new File(DirectionHUD.DATA_DIR+FILE_NAME);
    }

    public static void load() {
        File file = getFile();
        // create a new file if non-existent
        if (!file.exists()) save();
        // try reading
        try (BufferedReader reader = Files.newBufferedReader(getFile().toPath(), StandardCharsets.UTF_8)) {
            Updater.Global.run(reader);
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Error loading global destinations, clearing!.");
        }
        // save the file
        save();
    }

    public static void save() {
        if (!getFile().exists()) {
            DirectionHUD.LOGGER.info(String.format("Creating new '%s'",FILE_NAME));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getFile().toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(Data.getGlobal()));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info(String.format("ERROR WRITING `%s`: %s",FILE_NAME,e.getMessage()));
        }
    }
}
