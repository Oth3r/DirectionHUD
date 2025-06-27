package one.oth3r.directionhud.common.hud.module.setting;

import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.utils.CUtl;

import java.util.HashMap;
import java.util.Map;

public class ModuleSettingButtonDisplay {
    private final Map<String, TextColorPair> buttonMap;
    private final boolean customInfo;

    public ModuleSettingButtonDisplay() {
        this.buttonMap = new HashMap<>();
        this.customInfo = false;
    }

    public ModuleSettingButtonDisplay(boolean customInfo) {
        this.buttonMap = new HashMap<>();
        this.customInfo = customInfo;
    }

    public ModuleSettingButtonDisplay addMapping(String key, String icon, String color) {
        buttonMap.put(key, new TextColorPair(icon, color));
        return this;
    }

    public ModuleSettingButtonDisplay addMapping(String key, String icon) {
        buttonMap.put(key, new TextColorPair(icon, CUtl.s()));
        return this;
    }

    public ModuleSettingButtonDisplay addTrueFalseMapping(String icon) {
        buttonMap.put("true", new TextColorPair(icon, Assets.mainColors.on));
        buttonMap.put("false", new TextColorPair(icon, Assets.mainColors.off));
        return this;
    }

    public boolean hasCustomInfo() {
        return customInfo;
    }

    protected TextColorPair getPair(String key) {
        return buttonMap.getOrDefault(key, new TextColorPair(null, CUtl.s()));
    }

    public String getText(String key) {
        return getPair(key).text;
    }

    public String getColor(String key) {
        return getPair(key).color;
    }

    protected record TextColorPair(String text, String color) {}
}
