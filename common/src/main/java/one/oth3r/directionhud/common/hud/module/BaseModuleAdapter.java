package one.oth3r.directionhud.common.hud.module;

import com.google.gson.*;

import java.lang.reflect.Type;

public class BaseModuleAdapter implements JsonDeserializer<BaseModule> {
    /**
     * Gson invokes this call-back method during deserialization when it encounters a field of the
     * specified type.
     * <p>In the implementation of this call-back method, you should consider invoking
     * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
     * for any non-trivial field of the returned object. However, you should never invoke it on the
     * same type passing {@code json} since that will cause an infinite loop (Gson will call your
     * call-back method again).
     *
     * @param json    The Json data being deserialized
     * @param typeOfT The type of the Object to deserialize to
     * @param context
     * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
     * @throws JsonParseException if json is not in the expected format of {@code typeofT}
     */
    @Override
    public BaseModule deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // get the module enum from the "module" entry, then the module class from the enum
        String moduleType = jsonObject.get("module").getAsString();
        BaseModule baseModule = context.deserialize(json,Module.fromString(moduleType).getModuleClass());

        // call reassignValidators() after deserialization
        baseModule.reassignValidators();

        return baseModule;
    }
}
