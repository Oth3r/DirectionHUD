package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

public class ModuleDistance extends BaseModule {
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
        return String.format(FileData.getModuleText().getDistance().getNumber(),
                args[0]);
    }
}
