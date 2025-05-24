package one.oth3r.directionhud.common.hud.module.setting;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Method;

public class ModuleSetting<V> implements Cloneable {

    @SerializedName("id")
    protected final String id;
    @SerializedName("value")
    protected V value;

    protected transient ModuleSettingValidator<V> validator;

    /**
     * standard constructor for static usage
     * @param id the setting ID
     * @param defaultValue the default value
     * @param validator the validator
     */
    public ModuleSetting(String id, V defaultValue, ModuleSettingValidator<V> validator) {
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
    public ModuleSetting(String id, Object value, ModuleSettingValidator<?> validator, boolean isDynamic) {
        this.id = id;
        @SuppressWarnings("unchecked")
        V v = (V) value;
        this.value = v;
        @SuppressWarnings("unchecked")
        ModuleSettingValidator<V> val = (ModuleSettingValidator<V>) validator;
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

    public ModuleSettingValidator<V> getValidator() {
        return validator;
    }

    public ModuleSettingDisplay getDisplay() {
        return validator.getSettingDisplay();
    }

    @SuppressWarnings("unchecked")
    public void setValidator(ModuleSettingValidator<?> validator) {
        this.validator = (ModuleSettingValidator<V>) validator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModuleSetting<V> clone() {
        ModuleSetting<V> copy;
        try {
            copy = (ModuleSetting<V>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("ModuleSetting should be cloneable", e);
        }
        try {
            if (value != null) {
                Method m = value.getClass().getMethod("clone");
                copy.value = (V) m.invoke(value);
            }
            return copy;
        } catch (ReflectiveOperationException ignored) {
            return copy;
        }
    }
}
