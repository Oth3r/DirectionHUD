package one.oth3r.directionhud.common.hud.module.modules;

import static one.oth3r.directionhud.common.Assets.symbols.arrows;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DirectionAssetGroup;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.setting.*;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;

import java.util.Objects;

public class ModuleTracking extends BaseModule {
    public static final String hybridID = "tracking_hybrid";
    public static final String targetID = "tracking_target";
    public static final String typeID = "tracking_display";
    public static final String elevationID = "tracking_show-elevation";

    public ModuleTracking() {
        super(Module.TRACKING);
    }

    public ModuleTracking(Integer order, boolean state, boolean hybrid, Target target, Type type, boolean elevation) {
        super(Module.TRACKING, order, state);

        registerSetting(hybridID, hybrid, new BooleanModuleSettingHandler(
                Module.TRACKING,hybridID,false,false,
                new ModuleSettingButtonDisplay()
                        .addTrueFalseMapping(Assets.symbols.arrows.shuffle)
        ));

        registerSetting(targetID, target, new ModuleSettingHandler<>() {
            @Override
            public boolean isValid(Target value) {
                return Objects.nonNull(value);
            }

            @Override
            public Target convert(String value) throws IllegalArgumentException {
                return Target.valueOf(value);
            }

            @Override
            public ModuleSettingDisplay getSettingDisplay() {
                return new ModuleSettingDisplay(
                        Module.TRACKING,targetID,ModuleSettingType.ENUM_SWITCH,false
                );
            }
        });

        registerSetting(typeID, type, new ModuleSettingHandler<>() {
            @Override
            public boolean isValid(Type value) {
                return Objects.nonNull(value);
            }

            @Override
            public Type convert(String value) throws IllegalArgumentException {
                return Type.valueOf(value);
            }

            @Override
            public ModuleSettingDisplay getSettingDisplay() {
                return new ModuleSettingDisplay(
                        Module.TRACKING,typeID,ModuleSettingType.ENUM_SWITCH,true,
                        new ModuleSettingButtonDisplay(true)
                                .addMapping("simple",Assets.symbols.arrows.up)
                                .addMapping("compact",Assets.symbols.arrows.north)
                );
            }
        });

        registerSetting(elevationID, elevation, new BooleanModuleSettingHandler(
                Module.TRACKING,elevationID,false,false,
                new ModuleSettingButtonDisplay()
                        .addTrueFalseMapping(Assets.symbols.mountain)
        ));
    }

    @Override
    protected String[] getSettingOrder() {
        return new String[]{hybridID, targetID, typeID, elevationID};
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        double originRotation = (double) args[0];
        Loc originLoc = (Loc) args[1];
        Loc targetLoc = (Loc) args[2];

        boolean simple = getSettingValue(typeID).equals(Type.simple), hasElevation = getSettingValue(elevationID);
        double target;
        String data;

        DisplaySettings display = DisplayRegistry.getModuleDisplay(this.moduleType);
        DisplaySettings.AssetGroup arrows = display.getAssetGroup(simple?ASSET_SIMPLE:ASSET_COMPACT);
        DisplaySettings.AssetGroup elevation = display.getAssetGroup(ASSET_ELEVATION);

        // find the rotation needed for the originloc to 'face' the targetloc
        int x = targetLoc.getX()-originLoc.getX();
        int z = (targetLoc.getZ()-originLoc.getZ())*-1;
        target = Math.toDegrees(Math.atan2(x, z));
        if (target < 0) target += 360;

        // NORTH
        if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target,15,360), Helper.Num.wAdd(target,15,360)))
            data = arrows.get(DirectionAssetGroup.NORTH);
            // NORTH WEST
        else if (Helper.Num.inBetween(originRotation, target, Helper.Num.wAdd(target,65,360)))
            data = arrows.get(DirectionAssetGroup.NORTH_WEST);
            // WEST
        else if (Helper.Num.inBetween(originRotation, target, Helper.Num.wAdd(target,115,360)))
            data = arrows.get(DirectionAssetGroup.WEST);
            // SOUTH WEST
        else if (Helper.Num.inBetween(originRotation, target, Helper.Num.wAdd(target,165,360)))
            data = arrows.get(DirectionAssetGroup.SOUTH_WEST);
            // NORTH EAST
        else if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target, 65, 360), target))
            data = arrows.get(DirectionAssetGroup.NORTH_EAST);
            // EAST
        else if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target, 115, 360), target))
            data = arrows.get(DirectionAssetGroup.EAST);
            // SOUTH EAST
        else if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target, 165, 360), target))
            data = arrows.get(DirectionAssetGroup.SOUTH_EAST);
            // SOUTH
        else data = arrows.get(DirectionAssetGroup.SOUTH);

        // if the elevation is enabled and the target has a Y level
        if (hasElevation && targetLoc.hasY()) {
            int originY = originLoc.getY(), targetY = targetLoc.getY();
            // find which elevation asset ID to use
            String elevationID;
            // a dash if in Y range or the target has no Y
            if (!targetLoc.hasY() || (originY-2 < targetY && targetY < originY+2)) {
                elevationID = ELEVATION_SAME;
            }
            else if (originY > targetY) {
                elevationID = ELEVATION_BELOW;
            }
            else {
                elevationID = ELEVATION_ABOVE;
            }

            // return the formatted elevation version of the module
            return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_ELEVATION_TRACKING, data,elevation.get(elevationID));
        }
        // return the non elevation version of the module
        return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_TRACKING, data);
    }

    public static final String ASSET_SIMPLE = "simple";
    public static final String ASSET_COMPACT = "compact";
    public static final String ASSET_ELEVATION = "elevation";
    public static final String ELEVATION_ABOVE = "above";
    public static final String ELEVATION_SAME = "same";
    public static final String ELEVATION_BELOW = "below";


    public static final String DISPLAY_TRACKING = "tracking";
    public static final String DISPLAY_ELEVATION_TRACKING = "elevation_tracking";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addAssetGroup(ASSET_SIMPLE, DirectionAssetGroup.create(
                "-" + arrows.up + arrows.right,
                "-" + arrows.up + "-",
                arrows.left + arrows.up + "-",
                arrows.left + "--",
                arrows.left + arrows.down + "-",
                "-" + arrows.down + "-",
                "-" + arrows.down + arrows.right,
                "--" + arrows.right));
        display.addAssetGroup(ASSET_COMPACT, DirectionAssetGroup.create(
                arrows.north_east,arrows.north,arrows.north_west,
                arrows.west,
                arrows.south_west,arrows.south,arrows.south_east,
                arrows.east
                ));
        display.addAsset(ASSET_ELEVATION,"above",arrows.north);
        display.addAsset(ASSET_ELEVATION,"same","-");
        display.addAsset(ASSET_ELEVATION,"below",arrows.south);

        display.addDisplay(DISPLAY_TRACKING,"&1&s[&r&2%s&1&s]");
        display.addDisplay(DISPLAY_ELEVATION_TRACKING,"&1&s[&r&2%s&1|&2%s&1&s]");

        return display;
    }

    public enum Target {
        player,
        dest
    }
    public enum Type {
        simple,
        compact
    }
}
