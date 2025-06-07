package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.utils.Dest;

public class ModuleDestination extends BaseModule {
    public ModuleDestination() {
        super(Module.DESTINATION);
    }

    public ModuleDestination(Integer order, boolean state) {
        super(Module.DESTINATION, order, state);
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        Dest dest = (Dest) args[0];
        // return based on the destination
        if (dest.getName() != null && dest.hasY()) {
            return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_NAME, dest.getName(), dest.getX(), dest.getY(), dest.getZ());
        }
        else if (dest.getName() != null) {
            return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_NAME_XZ, dest.getName(), dest.getX(), dest.getZ());
        }
        else if (dest.hasY()) {
            return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_XYZ, dest.getX(), dest.getY(), dest.getZ(), dest.getZ());
        }
        else {
            return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_XZ, dest.getX(), dest.getZ());
        }
    }

    public static final String DISPLAY_XYZ = "xyz";
    public static final String DISPLAY_XZ = "xz";
    public static final String DISPLAY_NAME = "name";
    public static final String DISPLAY_NAME_XZ = "name_xz";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_XYZ,"&1[&2%s %s %s&1]");
        display.addDisplay(DISPLAY_XZ,"&1[&2%s %s&1]");
        display.addDisplay(DISPLAY_NAME,"&1[&2%s&1]");
        display.addDisplay(DISPLAY_NAME_XZ,"&1[&2%s&1]");

        return display;
    }
}
