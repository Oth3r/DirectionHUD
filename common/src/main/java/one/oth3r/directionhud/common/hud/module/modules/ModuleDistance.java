package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;

public class ModuleDistance extends BaseModule {
    public ModuleDistance() {
        super(Module.DISTANCE);
    }

    public ModuleDistance(Integer order, boolean state) {
        super(Module.DISTANCE, order, state);
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_NUMBER,args[0]);
    }

    public static final String DISPLAY_NUMBER = "number";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_NUMBER,"&1[&2%s&1]");

        return display;
    }
}
