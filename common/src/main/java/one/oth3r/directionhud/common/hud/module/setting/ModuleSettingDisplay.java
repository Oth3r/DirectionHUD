package one.oth3r.directionhud.common.hud.module.setting;

import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.ModuleManager;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Lang;
import one.oth3r.directionhud.utils.CTxT;

public class ModuleSettingDisplay {

    protected final Module module;
    protected final String settingID;
    protected final ModuleSettingType settingType;
    protected final boolean showExample;

    public ModuleSettingDisplay(Module module, String settingID, ModuleSettingType settingType, boolean showExample) {
        this.module = module;
        this.settingID = settingID;
        this.settingType = settingType;
        this.showExample = showExample;
    }

    public CTxT getSetMsg(String value) {
        return switch (settingType) {
            case ENUM_SWITCH -> SetMSG.enumString(module, settingID, value);
            case BOOLEAN_SWITCH -> SetMSG.customToggle(module, settingID, Boolean.parseBoolean(value));
            case BOOLEAN_TOGGLE -> SetMSG.toggle(module, settingID, Boolean.parseBoolean(value));
            case CUSTOM -> SetMSG.custom(module, settingID, new CTxT(value));
        };
    }

    /**
     * contains helper methods to generate a set message for module setting editing
     */
    protected static class SetMSG {
        public static final Lang LANG = ModuleManager.Setting.LANG;

        public static CTxT customToggle(Module module, String settingID, boolean state) {
            Lang settingsLang = new Lang(LANG,module.getName()+"."+settingID+".");
            return setMSGBuilder("set", module, settingID, settingsLang.get(state?"on":"off").color(CUtl.s()));
        }

        public static CTxT toggle(Module module, String settingID, boolean state) {
            return setMSGBuilder("set.toggle", module, settingID, CUtl.toggleTxT(state));
        }

        public static CTxT enumString(Module module, String settingID, String enumString) {
            Lang settingsLang = new Lang(LANG,module.getName()+"."+settingID+".");
            return setMSGBuilder("set", module, settingID, settingsLang.get(enumString).color(CUtl.s()));
        }

        public static CTxT custom(Module module, String settingID, CTxT customSetMSG) {
            return setMSGBuilder("set", module, settingID, customSetMSG.color(CUtl.s()));
        }

        private static CTxT setMSGBuilder(String setLang, Module module, String settingID, CTxT setMSG) {
            return LANG.msg(setLang,
                    LANG.get(module.getName()+"."+settingID).color(CUtl.s()),
                    setMSG);
        }
    }
}
