package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingType;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandler;
import one.oth3r.directionhud.common.utils.Helper;

import java.text.DecimalFormat;
import java.util.Objects;

public class ModuleAngle extends BaseModule {
    public static final String displayID = "display";

    public ModuleAngle(Integer order, boolean state, Display display) {
        super(Module.ANGLE, order, state);
        registerSetting(displayID, display, new ModuleSettingHandler<>() {
            @Override
            public boolean isValid(Display value) {
                return Objects.nonNull(value);
            }

            @Override
            public Display convert(String value) throws IllegalArgumentException {
                return Display.valueOf(value);
            }

            @Override
            public ModuleSettingDisplay getSettingDisplay() {
                return new ModuleSettingDisplay(Module.ANGLE, displayID, ModuleSettingType.ENUM_SWITCH, true);
            }
        });
    }

    /**
     * the logic for displaying the module
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        // yaw, pitch
        DecimalFormat df = new DecimalFormat("0.0");
        String y = df.format(args[0]), p = df.format(args[1]);
        return switch (Helper.Enums.get(getSetting(displayID), Display.class)) {
            case yaw -> String.format(FileData.getModuleText().getAngle().getYaw(), y);
            case pitch -> String.format(FileData.getModuleText().getAngle().getPitch(), p);
            case both -> String.format(FileData.getModuleText().getAngle().getBoth(), y, p);
        };
    }

    public enum Display {
        yaw,
        pitch,
        both
    }
}
