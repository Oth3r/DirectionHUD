package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.ModuleText;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Dest;

public class ModuleDestination extends BaseModule {
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
        ModuleText.ModuleDestination moduleDestination = FileData.getModuleText().getDestination();
        // return based on the destination
        if (dest.getName() != null && dest.hasY()) {
            return String.format(moduleDestination.getName(), dest.getName(), dest.getX(), dest.getY(), dest.getZ());
        }
        else if (dest.getName() != null) {
            return String.format(moduleDestination.getNameXz(), dest.getName(), dest.getX(), dest.getZ());
        }
        else if (dest.hasY()) {
            return String.format(moduleDestination.getXyz(), dest.getX(), dest.getY(), dest.getZ(), dest.getZ());
        }
        else {
            return String.format(moduleDestination.getXz(), dest.getX(), dest.getZ());
        }
    }
}
