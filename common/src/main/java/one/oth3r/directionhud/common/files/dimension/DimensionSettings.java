package one.oth3r.directionhud.common.files.dimension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.Updater;
import one.oth3r.directionhud.utils.Utl;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

public class DimensionSettings {

    @SerializedName("version")
    private Double version = 1.0;

    @SerializedName("dimensions")
    private ArrayList<DimensionEntry> dimensions = Utl.dim.DEFAULT_DIMENSIONS;

    @SerializedName("ratios")
    private ArrayList<RatioEntry> ratios = Utl.dim.DEFAULT_RATIOS;

    public DimensionSettings() {}

    public DimensionSettings(DimensionSettings dimensionSettings) {
        this.version = dimensionSettings.version;
        this.dimensions = new ArrayList<>(dimensionSettings.dimensions);
        this.ratios = new ArrayList<>(dimensionSettings.ratios);
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public ArrayList<DimensionEntry> getDimensions() {
        return dimensions;
    }

    public void setDimensions(ArrayList<DimensionEntry> dimensions) {
        this.dimensions = dimensions;
    }

    public ArrayList<RatioEntry> getRatios() {
        return ratios;
    }

    public void setRatios(ArrayList<RatioEntry> ratios) {
        this.ratios = ratios;
    }

    public static File getFile() {
        return new File(DirectionHUD.CONFIG_DIR+"dimension-settings.json");
    }

    public static void load() {
        File file = getFile();
        // create a new file if non-existent
        if (!file.exists()) save();
        // try reading
        try (BufferedReader reader = Files.newBufferedReader(getFile().toPath(), StandardCharsets.UTF_8)) {
            Updater.DimSettings.run(reader);
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Error loading dimension settings, reverting to default settings.");
        }
        // save the file
        save();
    }

    public static void save() {
        if (!getFile().exists()) {
            DirectionHUD.LOGGER.info(String.format("Creating new '%s'",getFile().getName()));
        }
        try (BufferedWriter writer = Files.newBufferedWriter(getFile().toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(Dimension.getDimensionSettings()));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR WRITING DIMENSION SETTINGS. "+e.getMessage());
        }
    }
}
