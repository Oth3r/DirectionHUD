package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;

public class ModuleWeather extends BaseModule {
    public ModuleWeather() {
        super(Module.WEATHER);
    }

    public ModuleWeather(Integer order, boolean state) {
        super(Module.WEATHER, order, state);
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        String weatherIcon = (String) args[0], extraIcons = (String) args[1];

        // if no extra icons, single weather
        if (extraIcons.isEmpty()) return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_WEATHER_SINGLE, weatherIcon);
        // if not, dual weather module
        return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_WEATHER, weatherIcon, extraIcons);
    }

    public static final String DISPLAY_WEATHER = "weather";
    public static final String DISPLAY_WEATHER_SINGLE = "weather_single";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_WEATHER_SINGLE,"&1%s");
        display.addDisplay(DISPLAY_WEATHER,"&1%s%s");

        return display;
    }
}
