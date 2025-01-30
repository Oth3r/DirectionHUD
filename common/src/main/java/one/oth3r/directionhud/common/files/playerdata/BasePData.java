
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.*;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Helper.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class BasePData {

    @SerializedName("version")
    protected Double version = 2.1;
    @SerializedName("hud")
    protected PDHud hud = new PDHud();
    @SerializedName("destination")
    protected PDDestination destination = new PDDestination();
    @SerializedName("color_presets")
    protected ArrayList<ColorPreset> colorPresets = new ArrayList<>();

    public BasePData() {}

    public BasePData(BasePData newFile) {
        copyBaseFileData(newFile);
    }

    protected void copyBaseFileData(BasePData newFile) {
        this.version = newFile.version;
        this.hud = new PDHud(newFile.hud);
        this.destination = new PDDestination();
        this.colorPresets = newFile.colorPresets.stream()
                .map(record -> new Helper.ColorPreset(record.name(), record.color()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public PDHud getHud() {
        return hud;
    }

    public PDDestination getDEST() {
        return destination;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public ArrayList<ColorPreset> getColorPresets() {
        return colorPresets;
    }

    public void setColorPresets(ArrayList<ColorPreset> colorPresets) {
        this.colorPresets = colorPresets;
    }

    /**
     * updates all base pData elements
     * @param json the json with the file data
     */
    public void baseUpdater(JsonElement json) {
        if (version.equals(2.0)) {

            ///  module updater
            /*
            1. get the hud order from hud.order, a list of strings that determine the order of modules in game (bad system)
            2. get the module settings from hud.setting.module, to migrate the settings
            3. get the hud module states from hud.module
            4. loop through the module order, adding the correct module to the newModule list
            5. copy needed settings
            6. save the new module settings
             */

            // the new module list
            ArrayList<BaseModule> newModules = new ArrayList<>();
            // get the hud
            JsonObject hud = json.getAsJsonObject().getAsJsonObject("hud");
            // hud.order is a string of modules in the order that they show up in game
            JsonArray order = hud.getAsJsonArray("order");
            // get the module settings - (hud.setting.module) as jsonObject
            JsonObject moduleSettings = hud.getAsJsonObject("setting").getAsJsonObject("module");
            // get the module states
            JsonObject modules = hud.getAsJsonObject("module");
            // running total
            int i = 0;
            for (JsonElement element : order) {
                // get the module
                Module module = Module.fromString(element.getAsString());

                // if invalid, skip
                if (module.equals(Module.UNKNOWN)) continue;

                // get the state of the module (hud.module.<module-name> as a boolean)
                boolean state = modules.getAsJsonPrimitive(module.getName()).getAsBoolean();

                switch (module) {
                    case COORDINATES -> newModules.add(new ModuleCoordinates(i,state));
                    case DESTINATION -> newModules.add(new ModuleDestination(i, state));
                    case DISTANCE -> newModules.add(new ModuleDistance(i, state));
                    case TRACKING -> newModules.add(new ModuleTracking(i, state,
                            moduleSettings.getAsJsonPrimitive("tracking_hybrid").getAsBoolean(),
                            moduleSettings.getAsJsonPrimitive("tracking_target").getAsString()
                                    .equals("player")? ModuleTracking.Target.player : ModuleTracking.Target.dest,
                            moduleSettings.getAsJsonPrimitive("tracking_type").getAsString()
                                    .equals("simple")? ModuleTracking.Type.simple : ModuleTracking.Type.compact, false));
                    case DIRECTION -> newModules.add(new ModuleDirection(i, state));
                    case WEATHER -> newModules.add(new ModuleWeather(i, state));
                    case TIME -> newModules.add(new ModuleTime(i, state,
                            moduleSettings.getAsJsonPrimitive("time_24hr").getAsBoolean()));
                    case ANGLE -> newModules.add(new ModuleAngle(i, state,
                            Helper.Enums.get(moduleSettings.getAsJsonPrimitive("angle_display").getAsString(),
                                    ModuleAngle.Display.class)));
                    case SPEED -> newModules.add(new ModuleSpeed(i, state,
                            !moduleSettings.getAsJsonPrimitive("speed_3d").getAsBoolean(),
                            moduleSettings.getAsJsonPrimitive("speed_pattern").getAsString()));
                }
                i++;
            }

            // copy the fixed modules
            this.hud.setModules(newModules);

            // bump the version
            this.version = 2.1;
        }

    }
}
