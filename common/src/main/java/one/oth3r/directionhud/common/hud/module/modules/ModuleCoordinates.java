package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.setting.BooleanModuleSettingHandler;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Loc;

public class ModuleCoordinates extends BaseModule {
    public static final String xyzID = "coordinates_xyz-display";

    public ModuleCoordinates() {
        super(Module.COORDINATES);
    }

    public ModuleCoordinates(Integer order, boolean state, boolean xyz) {
        super(Module.COORDINATES, order, state);
        registerSetting(xyzID, xyz,new BooleanModuleSettingHandler(
                this.moduleType,xyzID,true,false
        ));
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        Loc loc = (Loc) args[0];
        return ((Boolean) getSettingValue(xyzID)) ?
                DisplayRegistry.getFormatted(this.moduleType,DISPLAY_XYZ,
                        loc.getX(), loc.getY(), loc.getZ()) :
                DisplayRegistry.getFormatted(this.moduleType,DISPLAY_XZ,
                        loc.getX(), loc.getZ());
    }

    public static final String DISPLAY_XYZ = "xyz";
    public static final String DISPLAY_XZ = "xz";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_XYZ,"&1XYZ: &2%s %s %s");
        display.addDisplay(DISPLAY_XZ,"&1XZ: &2%s %s");

        return display;
    }
}
