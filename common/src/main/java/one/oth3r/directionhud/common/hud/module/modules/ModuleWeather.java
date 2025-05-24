package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

public class ModuleWeather extends BaseModule {
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
        if (extraIcons.isEmpty()) return String.format(FileData.getModuleText().getWeather().getWeatherSingle(), weatherIcon);
        // if not, dual weather module
        return String.format(FileData.getModuleText().getWeather().getWeather(), weatherIcon, extraIcons);
    }
}
