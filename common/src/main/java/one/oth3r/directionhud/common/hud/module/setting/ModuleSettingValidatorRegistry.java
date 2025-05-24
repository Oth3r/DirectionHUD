package one.oth3r.directionhud.common.hud.module.setting;

import java.util.HashMap;
import java.util.Map;

public class ModuleSettingValidatorRegistry {
    private static final Map<String, ModuleSettingValidator<?>> registry = new HashMap<>();

    public static <V> void registerValidator(String settingID, ModuleSettingValidator<V> validator) {
        registry.put(settingID,validator);
    }

    public static <V> ModuleSettingValidator<V> getValidator(String settingID) {
        @SuppressWarnings("unchecked")
        ModuleSettingValidator<V> validator = (ModuleSettingValidator<V>) registry.get(settingID);
        return validator;
    }
}
