package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.ModuleText;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.setting.BooleanModuleSettingHandler;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingType;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandler;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;

import java.util.Objects;

public class ModuleTracking extends BaseModule {
    public static final String hybridID = "hybrid";
    public static final String targetID = "target";
    public static final String typeID = "display-type";
    public static final String elevationID = "show-elevation";

    public ModuleTracking(Integer order, boolean state, boolean hybrid, Target target, Type type, boolean elevation) {
        super(Module.TRACKING, order, state);
        registerSetting(hybridID, hybrid, new BooleanModuleSettingHandler(
                Module.TRACKING,hybridID,false,false
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
                        Module.TRACKING,typeID,ModuleSettingType.ENUM_SWITCH,false
                );
            }
        });
        registerSetting(elevationID, elevation, new BooleanModuleSettingHandler(
                Module.TRACKING,elevationID,false,false
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
        double originRotation = (double) args[0];
        Loc originLoc = (Loc) args[1];
        Loc targetLoc = (Loc) args[2];

        boolean simple = getSetting(typeID).equals(Type.simple), hasElevation = getSetting(elevationID);
        double target;
        String data;
        ModuleText.ModuleTracking.Assets assets = FileData.getModuleText().getTracking().getAssets();
        ModuleText.ModuleTracking.Assets.Simple simpleArrows = assets.getSimple();
        ModuleText.ModuleTracking.Assets.Compact compactArrows = assets.getCompact();
        ModuleText.ModuleTracking.Assets.Elevation elevationArrows = assets.getElevation();


        // find the rotation needed for the originloc to 'face' the targetloc
        int x = targetLoc.getX()-originLoc.getX();
        int z = (targetLoc.getZ()-originLoc.getZ())*-1;
        target = Math.toDegrees(Math.atan2(x, z));
        if (target < 0) target += 360;

        // NORTH
        if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target,15,360), Helper.Num.wAdd(target,15,360)))
            data = simple?simpleArrows.getNorth():compactArrows.getNorth();
            // NORTH WEST
        else if (Helper.Num.inBetween(originRotation, target, Helper.Num.wAdd(target,65,360)))
            data = simple?simpleArrows.getNorthWest():compactArrows.getNorthWest();
            // WEST
        else if (Helper.Num.inBetween(originRotation, target, Helper.Num.wAdd(target,115,360)))
            data = simple?simpleArrows.getWest():compactArrows.getWest();
            // SOUTH WEST
        else if (Helper.Num.inBetween(originRotation, target, Helper.Num.wAdd(target,165,360)))
            data = simple?simpleArrows.getSouthWest():compactArrows.getSouthWest();
            // NORTH EAST
        else if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target, 65, 360), target))
            data = simple?simpleArrows.getNorthEast():compactArrows.getNorthEast();
            // EAST
        else if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target, 115, 360), target))
            data = simple?simpleArrows.getEast():compactArrows.getEast();
            // SOUTH EAST
        else if (Helper.Num.inBetween(originRotation, Helper.Num.wSubtract(target, 165, 360), target))
            data = simple?simpleArrows.getSouthEast():compactArrows.getSouthEast();
            // SOUTH
        else data = simple?simpleArrows.getSouth():compactArrows.getSouth();

        // if the elevation is enabled and the target has a Y level
        if (hasElevation && targetLoc.hasY()) {
            int originY = originLoc.getY(), targetY = targetLoc.getY();
            String elevation;
            // a dash if in Y range or the target has no Y
            if (!targetLoc.hasY() || (originY-2 < targetY && targetY < originY+2)) {
                elevation = elevationArrows.getSame();
            }
            else if (originY > targetY) {
                elevation = elevationArrows.getBelow();
            }
            else {
                elevation = elevationArrows.getAbove();
            }
            // return the formatted elevation version of the module
            return String.format(FileData.getModuleText().getTracking().getElevationTracking(), data, elevation);
        }
        // return the non elevation version of the module
        return String.format(FileData.getModuleText().getTracking().getTracking(), data);
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
