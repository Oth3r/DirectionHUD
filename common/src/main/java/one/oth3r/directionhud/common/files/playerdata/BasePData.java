
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.*;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Helper.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class BasePData {

    @SerializedName("version")
    protected Double version = 2.2;
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
        this.destination = new PDDestination(newFile.destination);
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
     *
     * @param json the json with the file data
     * @return
     */
    public JsonElement baseJSONUpdater(JsonElement json, boolean factoryDefault) {
        if (json == null || json.isJsonNull()) return new JsonObject().getAsJsonNull();
        Gson gson = Helper.getGson();
        JsonObject jsonObj = json.getAsJsonObject();
        double version = jsonObj.getAsJsonPrimitive("version").getAsDouble();

        if (version == 2.0) {

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
            JsonObject hud = jsonObj.getAsJsonObject("hud");
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
                    case COORDINATES -> newModules.add(new ModuleCoordinates(i,state, true));
                    case DESTINATION -> newModules.add(new ModuleDestination(i, state,true));
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

            // fix the order
            ModuleManager.Order.fixOrder(newModules, factoryDefault);
            hud.add("modules", gson.toJsonTree(newModules));
            // bump the version; skip 2.1 & 2.2 as this would technically fix for updating 2.3 as well
            jsonObj.addProperty("version", 2.3);
        }

        if (version == 2.1) {
            /// updated module system updater
            /*
            The new system completely overhauls the hud module setting system.
            1. get the list of modules
            2. loop through each module, hard coding the extraction of the module settings based on the module type
            hud -> modules
             */

            // get the hud
            JsonObject hud = jsonObj.getAsJsonObject("hud");
            // get the module states
            JsonArray modules = hud.getAsJsonArray("modules");


            ArrayList<BaseModule> newModules = new ArrayList<>();
            for (JsonElement element : modules) {
                JsonObject module = element.getAsJsonObject();

                // get the module type
                String mod = module.getAsJsonPrimitive("module").getAsString();
                int order = module.getAsJsonPrimitive("order").getAsInt();
                boolean state = module.getAsJsonPrimitive("state").getAsBoolean();

                Module moduleType = Module.fromString(mod);
                if (moduleType.equals(Module.UNKNOWN)) continue;

                newModules.add(switch (moduleType) {
                    case COORDINATES -> {
                        boolean xyzDisplay = module.getAsJsonPrimitive("xyz-display").getAsBoolean();
                        yield new ModuleCoordinates(order, state, xyzDisplay);
                    }
                    case DESTINATION -> new ModuleDestination(order,state,true);
                    case DISTANCE -> new ModuleDistance(order,state);
                    case TRACKING -> {
                        boolean hybrid = module.getAsJsonPrimitive("hybrid").getAsBoolean();
                        ModuleTracking.Target target = Enums.get(
                                module.getAsJsonPrimitive("target").getAsString(),ModuleTracking.Target.class);
                        ModuleTracking.Type type = Enums.get(
                                module.getAsJsonPrimitive("display-type").getAsString(),ModuleTracking.Type.class);
                        boolean elevation = module.getAsJsonPrimitive("show-elevation").getAsBoolean();

                        yield new ModuleTracking(order,state,
                                hybrid,target,type,elevation);
                    }
                    case DIRECTION -> new ModuleDirection(order,state);
                    case WEATHER -> new ModuleWeather(order,state);
                    case TIME -> {
                        boolean time24hr = module.getAsJsonPrimitive("24hr-clock").getAsBoolean();
                        yield new ModuleTime(order,state,time24hr);
                    }
                    case ANGLE -> {
                        ModuleAngle.Display display = Enums.get(
                                module.getAsJsonPrimitive("display").getAsString(),ModuleAngle.Display.class);
                        yield new ModuleAngle(order,state,display);
                    }
                    case SPEED -> {
                        boolean speed2D = module.getAsJsonPrimitive("2d-calculation").getAsBoolean();
                        String speedPattern = module.getAsJsonPrimitive("display-pattern").getAsString();
                        yield new ModuleSpeed(order, state, speed2D, speedPattern);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + moduleType);
                });

            }

            /*
            This new system completely removes the order of modules when disabled, only giving module order to enabled modules - thus easier to manage enabled modules
                - calling ModuleManager.Order.fixOrder() will do this automatically!
             */
            ModuleManager.Order.fixOrder(newModules, factoryDefault);

            hud.add("modules", gson.toJsonTree(newModules));

            // skip 2.2 as this would also add the destination setting automatically
            jsonObj.addProperty("version", 2.3);
        }

        if (version == 2.2) {
            /// add the destination show-name setting
            // get the hud
            JsonObject hud = jsonObj.getAsJsonObject("hud");
            // get the module states
            JsonArray modules = hud.getAsJsonArray("modules");

            for (JsonElement element : modules) {
                JsonObject module = element.getAsJsonObject();

                // get the module type
                String mod = module.getAsJsonPrimitive("module").getAsString();
                Module moduleType = Module.fromString(mod);
                if (moduleType.equals(Module.DESTINATION)) {
                    JsonArray settings = module.getAsJsonArray("settings");

                    JsonObject showName = new JsonObject();
                    showName.addProperty("id","destination_show-name");
                    showName.addProperty("value",true);

                    settings.add(showName);
                }
            }
            // bump to 2.3
            jsonObj.addProperty("version", 2.3);
        }

        return json;
    }
}
