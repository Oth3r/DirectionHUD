package one.oth3r.directionhud.common.files.dimension;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
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

    public static DimensionSettings load() {
        File file = getFile();
        if (!file.exists()) {
            DirectionHUD.LOGGER.info(String.format("Creating new '%s'",getFile().getName()));
            return new DimensionSettings();
        }

        try (BufferedReader reader = Files.newBufferedReader(getFile().toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(reader, DimensionSettings.class);
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("Error loading dimension settings, reverting to default settings.");
            return new DimensionSettings();
        }
    }

    public static void save(DimensionSettings dimensionSettings) {
        try (BufferedWriter writer = Files.newBufferedWriter(getFile().toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(dimensionSettings));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR WRITING DIMENSION SETTINGS - PLEASE REPORT WITH THE ERROR LOG");
            e.printStackTrace();
        }
    }
}
