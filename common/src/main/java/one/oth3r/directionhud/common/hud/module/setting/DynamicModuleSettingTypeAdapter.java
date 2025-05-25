package one.oth3r.directionhud.common.hud.module.setting;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A custom TypeAdapter for ModuleSetting that handles dynamic types.
 * This adapter is used to serialize and deserialize ModuleSetting objects
 */
final class DynamicModuleSettingTypeAdapter extends TypeAdapter<ModuleSetting<?>> {
    private final Gson gson;

    DynamicModuleSettingTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void write(JsonWriter out, ModuleSetting<?> setting) throws IOException {
        out.beginObject();
        out.name("id").value(setting.getId());
        out.name("value");
        if (setting.getValue() == null) {
            out.nullValue();
        } else {
            gson.toJson(setting.getValue(), setting.getValue().getClass(), out);
        }
        out.endObject();
    }

    @Override
    public ModuleSetting<?> read(JsonReader in) throws IOException {
        String id = null;
        JsonElement valueElement = null;

        // read the settings json object
        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("id")) {
                id = in.nextString();
            } else if (name.equals("value")) {
                valueElement = JsonParser.parseReader(in);
            } else {
                in.skipValue();
            }
        }
        in.endObject();

        if (id == null) {
            throw new JsonParseException("ModuleSetting missing 'id'");
        }

        ModuleSettingHandler<?> validator = ModuleSettingHandlerRegistry.getHandler(id);
        if (validator == null) {
            throw new JsonParseException("No validator found for setting '" + id + "'. Make sure to load the modules first to register the correct validators.");
        }

        // Try to infer the value type from the validator
        Class<?> valueClass = getValidatorValueClass(validator);
        Object value = valueElement != null && valueClass != null
                ? gson.fromJson(valueElement, valueClass)
                : null;

        // dynamically load the validator and the value
        return new ModuleSetting<>(id, value, validator, true);
    }

    private static Class<?> getValidatorValueClass(ModuleSettingHandler<?> validator) {
        // try to extract the generic type from the validator's class
        Type[] genericInterfaces = validator.getClass().getGenericInterfaces();
        for (Type t : genericInterfaces) {
            if (t instanceof ParameterizedType pt) {
                if (pt.getRawType() instanceof Class<?> raw && ModuleSettingHandler.class.isAssignableFrom(raw)) {
                    Type arg = pt.getActualTypeArguments()[0];
                    if (arg instanceof Class<?> c) return c;
                }
            }
        }
        // fallback try the superclass if failed
        Type superType = validator.getClass().getGenericSuperclass();
        if (superType instanceof ParameterizedType pt) {
            Type arg = pt.getActualTypeArguments()[0];
            if (arg instanceof Class<?> c) return c;
        }
        return null;
    }
}
