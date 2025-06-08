package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingButtonDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingType;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandler;
import one.oth3r.directionhud.common.utils.Helper;

import java.text.DecimalFormat;
import java.util.Objects;

public class ModuleAngle extends BaseModule {
    public static final String displayID = "angle_display";

    public ModuleAngle() {
        super(Module.ANGLE);
    }

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
                return new ModuleSettingDisplay(Module.ANGLE, displayID, ModuleSettingType.ENUM_SWITCH, true,
                        new ModuleSettingButtonDisplay(true)
                                .addMapping("yaw",Assets.symbols.arrows.leftRight)
                                .addMapping("pitch",Assets.symbols.arrows.upDown)
                                .addMapping("both",Assets.symbols.arrows.leftRight+Assets.symbols.arrows.upDown));
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
        return switch (Helper.Enums.get(getSettingValue(displayID), Display.class)) {
            case yaw -> DisplayRegistry.getFormatted(this.moduleType,DISPLAY_YAW, y);
            case pitch -> DisplayRegistry.getFormatted(this.moduleType,DISPLAY_PITCH, p);
            case both -> DisplayRegistry.getFormatted(this.moduleType,DISPLAY_BOTH, y, p);
        };
    }

    public static final String DISPLAY_YAW = "yaw";
    public static final String DISPLAY_PITCH = "pitch";
    public static final String DISPLAY_BOTH = "both";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_YAW,"&2%s");
        display.addDisplay(DISPLAY_PITCH,"&2%s");
        display.addDisplay(DISPLAY_BOTH,"&2%s&1/&2%s");

        return display;
    }

    public enum Display {
        yaw,
        pitch,
        both
    }
}
