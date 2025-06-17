package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingButtonDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandler;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingType;
import one.oth3r.directionhud.common.utils.Helper;

import java.util.Objects;

public class ModuleLight extends BaseModule {
    public static final String targetID = "light_target";
    public static final String displayID = "light_display";

    public ModuleLight() {
        super(Module.LIGHT);
    }

    public ModuleLight(Integer order, boolean state, Target target, Display display) {
        super(Module.LIGHT, order, state);
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
                return new ModuleSettingDisplay(Module.LIGHT, targetID, ModuleSettingType.ENUM_SWITCH, true,
                        new ModuleSettingButtonDisplay(true)
                                .addMapping("eye", Assets.symbols.eye)
                                .addMapping("below",Assets.symbols.arrows.south));
            }
        });

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
                return new ModuleSettingDisplay(Module.LIGHT, displayID, ModuleSettingType.ENUM_SWITCH, false,
                        new ModuleSettingButtonDisplay(true));
            }
        });
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        // set to dashes if no light data (-1)
        if ((int) args[0] == -1) args[0] = "-";
        if ((int) args[1] == -1) args[1] = "-";

        return switch (Helper.Enums.get(getSettingValue(displayID), Display.class)) {
            case sky -> DisplayRegistry.getFormatted(this.moduleType, DISPLAY_SKY, args[0]);
            case block -> DisplayRegistry.getFormatted(this.moduleType,DISPLAY_BLOCK, args[1]);
            case both -> DisplayRegistry.getFormatted(this.moduleType,DISPLAY_BOTH, args[0], args[1]);
        };
    }

    public static final String DISPLAY_SKY = "sky";
    public static final String DISPLAY_BLOCK = "block";
    public static final String DISPLAY_BOTH = "both";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_SKY,"&2%s&1&b\uD83D\uDCA1");
        display.addDisplay(DISPLAY_BLOCK,"&2%s&1&b\uD83D\uDCA1");
        display.addDisplay(DISPLAY_BOTH,"&2%s&1,&2%s&1&b\uD83D\uDCA1");

        return display;
    }

    public enum Target {
        eye, below
    }
    public enum Display {
        sky, block, both
    }
}
