package one.oth3r.directionhud.common.hud.module.setting;

import one.oth3r.directionhud.common.hud.module.Module;

import java.util.Objects;

public class BooleanModuleSettingHandler implements ModuleSettingHandler<Boolean> {
    private final ModuleSettingDisplay moduleSettingDisplay;

    public BooleanModuleSettingHandler(Module module, String settingID, boolean isSwitchType, boolean showExample) {
        this.moduleSettingDisplay = new ModuleSettingDisplay(module,settingID,
                isSwitchType? ModuleSettingType.BOOLEAN_SWITCH : ModuleSettingType.BOOLEAN_TOGGLE,showExample);
    }

    public BooleanModuleSettingHandler(Module module, String settingID, boolean isSwitchType, boolean showExample, ModuleSettingButtonDisplay buttonDisplay) {
        this.moduleSettingDisplay = new ModuleSettingDisplay(module,settingID,
                isSwitchType? ModuleSettingType.BOOLEAN_SWITCH : ModuleSettingType.BOOLEAN_TOGGLE,showExample,buttonDisplay);
    }

    @Override
    public boolean isValid(Boolean value) {
        return Objects.nonNull(value);
    }

    @Override
    public Boolean convert(String value) throws IllegalArgumentException {
        return value.equalsIgnoreCase("on") || value.equalsIgnoreCase("true");
    }

    /**
     * gets the module display variable for the module setting
     */
    @Override
    public ModuleSettingDisplay getSettingDisplay() {
        return moduleSettingDisplay;
    }
}
