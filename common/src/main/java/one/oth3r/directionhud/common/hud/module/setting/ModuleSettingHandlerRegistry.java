package one.oth3r.directionhud.common.hud.module.setting;

import java.util.HashMap;
import java.util.Map;

public class ModuleSettingHandlerRegistry {
    private static final Map<String, ModuleSettingHandler<?>> registry = new HashMap<>();

    public static <V> void registerValidator(String settingID, ModuleSettingHandler<V> validator) {
        registry.put(settingID,validator);
    }

    public static <V> ModuleSettingHandler<V> getHandler(String settingID) {
        @SuppressWarnings("unchecked")
        ModuleSettingHandler<V> validator = (ModuleSettingHandler<V>) registry.get(settingID);
        return validator;
    }
}
