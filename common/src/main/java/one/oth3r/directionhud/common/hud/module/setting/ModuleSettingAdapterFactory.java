package one.oth3r.directionhud.common.hud.module.setting;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class ModuleSettingAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        // if can't make a module setting, return null
        if (!ModuleSetting.class.isAssignableFrom(typeToken.getRawType())) {
            return null;
        }
        return (TypeAdapter<T>) new DynamicModuleSettingTypeAdapter(gson);
    }
}
