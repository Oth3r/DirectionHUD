package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.setting.BooleanModuleSettingValidator;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Loc;

public class ModuleCoordinates extends BaseModule {
    public static final String xyzID = "xyz-display";

    public ModuleCoordinates(Integer order, boolean state, boolean xyz) {
        super(Module.COORDINATES, order, state);
        registerSetting(xyzID, xyz,new BooleanModuleSettingValidator(
                Module.COORDINATES,xyzID,true,false
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
        return ((Boolean) getSetting(xyzID)) ?
                String.format(FileData.getModuleText().getCoordinates().getXyz(),
                        loc.getX(), loc.getY(), loc.getZ()) :
                String.format(FileData.getModuleText().getCoordinates().getXz(),
                        loc.getX(), loc.getZ());
    }
}
