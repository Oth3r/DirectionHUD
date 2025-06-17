package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.template.CustomFile;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GlobalDest implements CustomFile<GlobalDest> {
    private Double version = 1.0;

    private ArrayList<Dest> destinations = new ArrayList<>();

    public GlobalDest() {}

    public GlobalDest(GlobalDest globalDest) {
        copyFileData(globalDest);
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

    /**
     * @return the class of the File
     */
    @Override
    public @NotNull Class<GlobalDest> getFileClass() {
        return GlobalDest.class;
    }

    /**
     * loads the data from the file object into the current object - DEEP COPY
     *
     * @param newFile the file to take the properties from
     */
    @Override
    public void copyFileData(GlobalDest newFile) {
        this.version = newFile.version;

        ArrayList<Dest> dests = new ArrayList<>();
        for (Dest dest : newFile.destinations) dests.add(new Dest(dest));
        this.destinations = dests;
    }

    /**
     * updates the file based on the version number of the current instance
     *
     * @param json
     */
    @Override
    public JsonElement updateJSON(JsonElement json) {
        Gson gson = Helper.getGson();
        // if the json is just an array (legacy file)
        if (json.isJsonArray()) {
            JsonArray DestArray = json.getAsJsonArray();

            ArrayList<Dest> updated = new ArrayList<>();
            // convert each entry to the new dest system

            for (JsonElement element : DestArray) {
                // a dest is a list of strings
                if (element.isJsonArray()) {
                    List<JsonElement> destJson = element.getAsJsonArray().asList();
                    // make the dest using the data
                    Dest dest = new Dest(new Loc(true, destJson.get(1).getAsString()), // LOC
                            destJson.get(0).getAsString(), // NAME
                            destJson.get(2).getAsString()); // COLOR

                    // add the dest to the new list
                    updated.add(dest);
                }
            }

            json = new JsonObject();
            json.getAsJsonObject().addProperty("version", 1.0);
            json.getAsJsonObject().add("destinations",gson.toJsonTree(updated));
        }

        return json;
    }

    /**
     * POST LOAD: after the JSON is loaded to this current instance, this method is called.
     */
    @Override
    public void updateFileInstance() {

    }

    /**
     * gets the file name - including the extension
     *
     * @return ex. custom-file.json
     */
    @Override
    public String getFileName() {
        return "global-dest.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.getData().getDataDirectory();
    }

    @Override
    public void reset() {
        copyFileData(new GlobalDest());
    }
}
