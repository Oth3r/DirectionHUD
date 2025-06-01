package one.oth3r.directionhud.common.files.dimension;

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.template.CustomFile;
import one.oth3r.directionhud.utils.Utl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class DimensionSettings implements CustomFile<DimensionSettings> {

    @SerializedName("version")
    private Double version = 1.0;

    @SerializedName("dimensions")
    private ArrayList<DimensionEntry> dimensions = Utl.dim.DEFAULT_DIMENSIONS;

    @SerializedName("ratios")
    private ArrayList<RatioEntry> ratios = Utl.dim.DEFAULT_RATIOS;

    public DimensionSettings() {}

    public DimensionSettings(DimensionSettings dimensionSettings) {
        copyFileData(dimensionSettings);
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

    /**
     * @return the class of the File
     */
    @Override
    public @NotNull Class<DimensionSettings> getFileClass() {
        return DimensionSettings.class;
    }

    /**
     * loads the data from the file object into the current object - DEEP COPY
     *
     * @param newFile the file to take the properties from
     */
    @Override
    public void copyFileData(DimensionSettings newFile) {
        this.version = newFile.version;
        this.dimensions = newFile.dimensions.stream().map(DimensionEntry::new).collect(Collectors.toCollection(ArrayList::new));
        this.ratios = newFile.ratios.stream().map(RatioEntry::new).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * updates the file based on the version number of the current instance
     *
     * @param json
     */
    @Override
    public JsonElement updateJSON(JsonElement json) {
        Gson gson = Helper.getGson();
        if (json.isJsonNull()) {
             return gson.toJsonTree(new DimensionSettings());
        }
        return json;
    }

    /**
     * POST LOAD: after the JSON is loaded to this current instance, this method is called.
     */
    @Override
    public void updateFileInstance() {
        Utl.dim.addMissing(this.dimensions);
    }

    /**
     * gets the file name - including the extension
     *
     * @return ex. custom-file.json
     */
    @Override
    public String getFileName() {
        return "dimension-settings.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.getData().getConfigDirectory();
    }

    @Override
    public void reset() {
        copyFileData(new DimensionSettings());
    }
}
