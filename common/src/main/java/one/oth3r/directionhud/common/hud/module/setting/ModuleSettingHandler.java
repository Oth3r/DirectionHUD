package one.oth3r.directionhud.common.hud.module.setting;

public interface ModuleSettingHandler<T> {
    boolean isValid(T value);

    /**
     * a method to convert the string value to the correct type
     * @param value the string value to convert
     */
    T convert(String value) throws IllegalArgumentException;

    /**
     * gets the module display variable for the module setting
     */
    ModuleSettingDisplay getSettingDisplay();
}
