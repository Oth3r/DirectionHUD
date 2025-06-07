package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.*;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.*;
import one.oth3r.directionhud.common.utils.Helper;

public class ModuleDirection extends BaseModule {
    public static final String ASSET_CARDINAl = "cardinal";
    public static final String DISPLAY_FACING = "facing";

    public ModuleDirection() {
        super(Module.DIRECTION);
    }
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
        DisplaySettings.AssetGroup cardinals = DisplayRegistry.getModuleDisplay(this.moduleType).getAssetGroup(ASSET_CARDINAl);
        String cardinal;

        if (Helper.Num.inBetween(rotation,22.5,67.5)) cardinal = cardinals.get(DirectionAssetGroup.NORTH_EAST);
        else if (Helper.Num.inBetween(rotation,67.5,112.5)) cardinal = cardinals.get(DirectionAssetGroup.EAST);
        else if (Helper.Num.inBetween(rotation,112.5,157.5)) cardinal = cardinals.get(DirectionAssetGroup.SOUTH_EAST);
        else if (Helper.Num.inBetween(rotation,157.5,202.5)) cardinal = cardinals.get(DirectionAssetGroup.SOUTH);
        else if (Helper.Num.inBetween(rotation,202.5,247.5)) cardinal = cardinals.get(DirectionAssetGroup.SOUTH_WEST);
        else if (Helper.Num.inBetween(rotation,247.5,292.5)) cardinal = cardinals.get(DirectionAssetGroup.WEST);
        else if (Helper.Num.inBetween(rotation,292.5,337.5)) cardinal = cardinals.get(DirectionAssetGroup.NORTH_WEST);
        else cardinal = cardinals.get(DirectionAssetGroup.NORTH);

        return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_FACING,cardinal);
    }

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addAssetGroup(ASSET_CARDINAl, DirectionAssetGroup.create(
                "NE","N","NW","W","SW","S","SE","E"));
        display.addDisplay(DISPLAY_FACING,"&1%s");

        return display;
    }
}
