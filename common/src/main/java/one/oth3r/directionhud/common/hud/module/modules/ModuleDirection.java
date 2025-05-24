package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.ModuleText;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Helper;

public class ModuleDirection extends BaseModule {
    public ModuleDirection(Integer order, boolean state) {
        super(Module.DIRECTION, order, state);
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        double rotation = (double) args[0];
        ModuleText.ModuleDirection.Assets.Cardinal cardinals = FileData.getModuleText().getDirection().getAssets().getCardinal();
        String cardinal;

        if (Helper.Num.inBetween(rotation,22.5,67.5)) cardinal = cardinals.getNorthEast();
        else if (Helper.Num.inBetween(rotation,67.5,112.5)) cardinal = cardinals.getEast();
        else if (Helper.Num.inBetween(rotation,112.5,157.5)) cardinal = cardinals.getSouthEast();
        else if (Helper.Num.inBetween(rotation,157.5,202.5)) cardinal = cardinals.getSouth();
        else if (Helper.Num.inBetween(rotation,202.5,247.5)) cardinal = cardinals.getSouthWest();
        else if (Helper.Num.inBetween(rotation,247.5,292.5)) cardinal = cardinals.getWest();
        else if (Helper.Num.inBetween(rotation,292.5,337.5)) cardinal = cardinals.getNorthWest();
        else cardinal = cardinals.getNorth();

        return String.format(FileData.getModuleText().getDirection().getFacing(),cardinal);
    }
}
