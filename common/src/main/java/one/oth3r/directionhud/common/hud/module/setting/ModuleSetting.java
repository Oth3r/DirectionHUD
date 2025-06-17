package one.oth3r.directionhud.common.hud.module.setting;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Method;
import java.util.Objects;

public class ModuleSetting<V> implements Cloneable {

    @SerializedName("id")
    protected final String id;
    @SerializedName("value")
    protected V value;

    protected transient ModuleSettingHandler<V> validator;

    /**
     * standard constructor for static usage
     * @param id the setting ID
     * @param defaultValue the default value
     * @param validator the validator
     */
    public ModuleSetting(String id, V defaultValue, ModuleSettingHandler<V> validator) {
        this.id = id;
        this.value = defaultValue;
        this.validator = validator;
    }

    /**
     * constructor for dynamic deserialization
     * @param id the setting ID
     * @param value the value as Object (will be cast to V).
     * @param validator the validator as ModuleSettingValidator<?> (will be cast to ModuleSettingValidator<V>).
     */
    public ModuleSetting(String id, Object value, ModuleSettingHandler<?> validator, boolean isDynamic) {
        this.id = id;
        @SuppressWarnings("unchecked")
        V v = (V) value;
        this.value = v;
        @SuppressWarnings("unchecked")
        ModuleSettingHandler<V> val = (ModuleSettingHandler<V>) validator;
        this.validator = val;
    }

    public String getId() {
        return id;
    }

    public V getValue() {
        return value;
    }

    public boolean setValue(V value) {
        if (validator.isValid(value)) {
            this.value = value;
            return true;
        }
        return false;
    }

    public ModuleSettingHandler<V> getValidator() {
        return validator;
    }

    public ModuleSettingDisplay getDisplay() {
        return validator.getSettingDisplay();
    }

    @SuppressWarnings("unchecked")
    public void setValidator(ModuleSettingHandler<?> validator) {
        this.validator = (ModuleSettingHandler<V>) validator;
    }

    @Override
    public ModuleSetting<V> clone() {
        V clonedValue = cloneValue(value);
        return new ModuleSetting<>(id, clonedValue, validator);
    }

    /**
     * attempts to clone the value using its clone method or returns the same instance if not cloneable.
     */
    @SuppressWarnings("unchecked")
    protected V cloneValue(V original) {
        if (original == null) return null;
        if (original instanceof Cloneable) {
            try {
                Method cloneMethod = original.getClass().getMethod("clone");
                return (V) cloneMethod.invoke(original);
            } catch (ReflectiveOperationException ignored) {
                // fallback to shallow copy if clone fails
            }
        }
        return original;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ModuleSetting<?> that = (ModuleSetting<?>) o;
        return Objects.equals(id, that.id) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
