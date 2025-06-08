package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.modules.ModuleDestination;
import one.oth3r.directionhud.common.template.CustomFile;
import one.oth3r.directionhud.common.utils.Helper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ModuleText implements CustomFile<ModuleText> {
    @SerializedName("version")
    private Double version = 1.1;
    @SerializedName("load-missing")
    private Boolean loadMissing = false;
    @SerializedName("modules")
    private Map<Module, DisplaySettings> modules = new HashMap<>();

    @Override
    public void reset() {
        copyFileData(new ModuleText());
    }

    @Override
    public @NotNull Class<ModuleText> getFileClass() {
        return ModuleText.class;
    }

    @Override
    public void copyFileData(ModuleText newFile) {
        this.version = newFile.version;
        this.loadMissing = newFile.loadMissing;
        this.modules = newFile.modules;
    }

    @Override
    public JsonElement updateJSON(JsonElement json) {
        Gson gson = Helper.getGson();
        JsonObject jsonObject = json.getAsJsonObject();
        if (jsonObject == null) return json;
        double version = jsonObject.get("version").getAsDouble();

        if (version == 1) {
            /*
                fixes the destination module name and name_xz entries
                the entry in version 1.0 had the primary and secondary color flipped
                version 1.01 fixes this issue
             */
            final String brokenDestinationName = "&2[&1%s&2]",
                    fixedDestinationName = new ModuleDestination().getDisplaySettings().getDisplay(ModuleDestination.DISPLAY_NAME);
            JsonObject destination = jsonObject.getAsJsonObject("destination");
            if (destination.getAsJsonPrimitive("name").getAsString().equals(brokenDestinationName))
                destination.addProperty("name", fixedDestinationName);
            if (destination.getAsJsonPrimitive("name_xz").getAsString().equals(brokenDestinationName))
                destination.addProperty("name_xz", fixedDestinationName);

            // update the version
            jsonObject.addProperty("version", 1.01);
        }
        // update to the dynamic system
        if (jsonObject.getAsJsonPrimitive("version").getAsDouble() < 1.1) {
            /*
                this update restructures the module-text.json file from having all the modules on the root to having them in a hashmap under modules
                it will only update the entries that have been updated per the new boolean \/
                this update also adds a new boolean, load-missing on the root (defaults to false)

                each module now has their own "displays" and "assets" subcategories to further help with reading and loading the file.
             */
            JsonElement newJson = gson.toJsonTree(new ModuleText());
            JsonObject modules = newJson.getAsJsonObject().getAsJsonObject("modules");

            // get a map with all the currently registered displays (for comparison’s sake)
            JsonObject registeredModuleList = gson.toJsonTree(DisplayRegistry.getModules()).getAsJsonObject();

            // list of module string from <=1.8
            String[] moduleStrings = {"coordinates","destination","distance","tracking","direction","weather","time","angle","speed"};
            // loop through each module
            for (String moduleString : moduleStrings) {
                JsonObject moduleObject = new JsonObject(), oldModule = jsonObject.getAsJsonObject(moduleString),
                        registeredModule = registeredModuleList.getAsJsonObject(moduleString);
                JsonObject displays = new JsonObject();
                JsonObject assets = new JsonObject();

                switch (moduleString) {
                    case "coordinates" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"xyz", "xz"});
                    case "destination" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"xyz", "xz", "name", "name_xz"});
                    case "distance" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"number"});
                    case "tracking" -> DynamicUpdater.handleModuleWithAssets(registeredModule,oldModule,displays,assets,new String[]{"tracking","elevation_tracking"},new String[]{"simple","compact","elevation"});
                    case "direction" -> DynamicUpdater.handleModuleWithAssets(registeredModule,oldModule,displays,assets,new String[]{"facing"}, new String[]{"cardinal"});
                    case "weather" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"weather","weather_single"});
                    case "time" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"hour_AM","hour_PM","hour_24"});
                    case "angle" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"yaw","pitch","both"});
                    case "speed" -> DynamicUpdater.handleSimpleUpdate(registeredModule,oldModule, displays, new String[]{"xz_speed","xyz_speed"});
                }
                if (!displays.isEmpty() || !assets.isEmpty()) {
                    if (!displays.isEmpty()) moduleObject.add("displays", displays);
                    if (!assets.isEmpty()) moduleObject.add("assets", assets);
                    modules.add(moduleString, moduleObject);
                }
            }
            // update the json
            json = newJson;
        }

        return json;
    }

    /**
     * the updater class from the old, static way of moduletext to the new, dynamic registering way.
     */
    private static class DynamicUpdater {
        private static void handleSimpleUpdate(JsonObject registeredModule, JsonObject oldModule, JsonObject displays, String[] displayStrings) {
            // filter down to each display
            registeredModule = registeredModule.getAsJsonObject("displays");
            for (String key : displayStrings) {
                String oldString = oldModule.get(key).getAsString();
                // don't do anything if the old module was left as default (the new system default to not populating old entries)
                if (registeredModule.get(key).getAsString().equals(oldString)) return;
                // else add it (there was a change)
                displays.addProperty(key, oldString);
            }
        }
        private static void handleModuleWithAssets(JsonObject registeredModule, JsonObject oldModule, JsonObject displays, JsonObject assets, String[] displayStrings, String[] assetStrings) {
            handleSimpleUpdate(registeredModule,oldModule,displays,displayStrings);
            // filter down to each asset
            JsonObject registeredAssets = registeredModule.getAsJsonObject("assets");
            JsonObject oldAssets = oldModule.get("assets").getAsJsonObject();

            for (String key : assetStrings) {
                JsonObject assetObject = oldAssets.getAsJsonObject(key);
                // don't do anything if the old asset was left as default
                if (registeredAssets.getAsJsonObject(key).equals(assetObject)) return;
                // else add the old asset
                assets.add(key, assetObject);
            }
        }
    }

    /**
     * POST LOAD: after the JSON is loaded to this current instance, this method is called.
     */
    @Override
    public void updateFileInstance() {
        DisplayRegistry.updateModules(modules);
        // if the missing modules need to be loaded, do it here
        if (loadMissing) {
            modules = DisplayRegistry.getModules();
            save();
        }
    }

    @Override
    public String getFileName() {
        return "module-text.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.getData().getConfigDirectory();
    }
}
