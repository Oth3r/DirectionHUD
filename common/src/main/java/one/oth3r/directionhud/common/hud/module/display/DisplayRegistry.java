package one.oth3r.directionhud.common.hud.module.display;

import one.oth3r.directionhud.common.hud.module.Module;

import java.util.HashMap;
import java.util.Map;

public class DisplayRegistry {
    // todo look into json schema
    private static final Map<Module, DisplaySettings> modules = new HashMap<>();

    public static void registerModuleDisplay(Module module, DisplaySettings display) {
        modules.put(module, display);
    }

    /**
     * gets the {@link DisplaySettings} object for the module provided
     * @param module the module to grab
     */
    public static DisplaySettings getModuleDisplay(Module module) {
        return modules.get(module);
    }

    /**
     * updates the modules map with values from the provided map.
     * for each inner entry in the modules map, if an equivalent entry exists in the provided map,
     * replaces the modules map entry with the provided entry.
     */
    public static void updateModules(Map<Module, DisplaySettings> input) {
        for (Map.Entry<Module, DisplaySettings> entry : modules.entrySet()) {
            DisplaySettings moduleDisplay = entry.getValue();
            DisplaySettings inputModuleDisplay = input.get(entry.getKey());

            if (inputModuleDisplay != null) {
                moduleDisplay.updateDisplay(inputModuleDisplay);
            }
        }
    }

    /**
     * gets all registered display modules as a map
     */
    public static Map<Module, DisplaySettings> getModules() {
        return new HashMap<>(modules);
    }

    /**
     * gets the registered display string via the Module and display name
     */
    public static String getDisplayString(Module module, String displayName) {
        DisplaySettings display = modules.get(module);
        if (display == null) return "";
        return display.getDisplay(displayName);
    }

    /**
     * uses {@link String#format(String, Object...)} to format the display string, fetched from the registry
     * @param module the module to search
     * @param displayName the name of the registered display
     * @param args the objects to fill in the gaps
     * @return the formatted display string
     */
    public static String getFormatted(Module module, String displayName, Object... args) {
        return String.format(DisplayRegistry.getDisplayString(module,displayName), args);
    }

}
