package one.oth3r.directionhud.common.hud.module.setting;

import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.ModuleManager;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Lang;
import one.oth3r.directionhud.utils.CTxT;

public class ModuleSettingDisplay {

    protected final Module module;
    protected final String settingID;
    protected final ModuleSettingType settingType;
    protected final boolean showExample;
    protected final ModuleSettingButtonDisplay buttonDisplay;

    public ModuleSettingDisplay(Module module, String settingID, ModuleSettingType settingType, boolean showExample) {
        this.module = module;
        this.settingID = settingID;
        this.settingType = settingType;
        this.showExample = showExample;
        this.buttonDisplay = new ModuleSettingButtonDisplay();
    }

    public ModuleSettingDisplay(Module module, String settingID, ModuleSettingType settingType, boolean showExample, ModuleSettingButtonDisplay buttonDisplay) {
        this.module = module;
        this.settingID = settingID;
        this.settingType = settingType;
        this.showExample = showExample;
        this.buttonDisplay = buttonDisplay;
    }

    public CTxT getSetMsg(String value) {
        return switch (settingType) {
            case ENUM_SWITCH -> SetMSG.enumString(module, settingID, value);
            case BOOLEAN_SWITCH -> SetMSG.customToggle(module, settingID, Boolean.parseBoolean(value));
            case BOOLEAN_TOGGLE -> SetMSG.toggle(module, settingID, Boolean.parseBoolean(value));
            case CUSTOM -> SetMSG.custom(module, settingID, new CTxT(value));
        };
    }

    @SuppressWarnings("unchecked")
    public <T> CTxT getButton(String value) {
        Lang LANG = ModuleManager.Setting.LANG, moduleLang = new Lang(LANG,module.getName()+".");
        String setCMD = "/hud modules setting-r "+module.getName()+" ";

        String buttonString = buttonDisplay.getText(value);
        CTxT buttonTxT, exampleTxT = new CTxT(" - ").append(moduleLang.get(settingID+"."+value).color('7'));

        if (buttonString == null) {
            buttonTxT = moduleLang.get(settingID+"."+value);
        } else {
            buttonTxT = new CTxT(buttonString);
        }
        // the color defaults to secondary color even if null
        buttonTxT.btn(true).color(buttonDisplay.getColor(value));

        // create the start of the hover text
        CTxT hover = moduleLang.get(settingID+".ui").color(CUtl.s());
        // append the current example
        if (showExample) hover.append(exampleTxT);
        // add setting info (custom info if set)
        hover.append("\n")
                .append(moduleLang.get(settingID+(buttonDisplay.hasCustomInfo()?"."+value:"")+".info").color('7'))
                .append("\n\n");

        switch (settingType) {
            case BOOLEAN_SWITCH, BOOLEAN_TOGGLE -> {
                boolean state = Boolean.parseBoolean(value);

                // if toggle and the button string is null (ON/OFF button text)
                if (settingType.equals(ModuleSettingType.BOOLEAN_TOGGLE) && buttonString == null) {
                    buttonTxT.text(CUtl.DLANG.btn(state?"on":"off").color(state?'a':'c')).btn(true);
                }

                // if switch get the opposite lang
                if (settingType.equals(ModuleSettingType.BOOLEAN_SWITCH)) {
                    hover.append(LANG.hover("set",moduleLang.get(settingID),
                            moduleLang.get(settingID+"."+!state).color(CUtl.s())));
                }
                // if not get the off / on lang and get toggle hover text
                else {
                    hover.append(LANG.hover("set.toggle",moduleLang.get(settingID),
                            CUtl.DLANG.get("fill."+(!state?"on":"off")).color(!state?'a':'c')));
                }

                // build the button TxT
                buttonTxT
                        .hover(hover)
                        .click(1,setCMD+settingID+" "+!state);
            }
            case ENUM_SWITCH -> {
                // should convert no issue
                T current = (T) ModuleSettingHandlerRegistry.getHandler(settingID).convert(value);
                Enum<?> enumValue = (Enum<?>) current;
                @SuppressWarnings("rawtypes")
                T next = (T) Helper.Enums.next(
                        (Enum) enumValue, // raw type cast
                        (Class) enumValue.getClass() // raw type cast
                );
                // build the hover text
                hover.append(LANG.hover("set",moduleLang.get(settingID),
                        moduleLang.get(settingID+"."+next).color(CUtl.s())));
                // build the button TxT
                buttonTxT
                        .hover(hover)
                        .click(1,setCMD+settingID+" "+next);
            }
            case CUSTOM -> {
                // rebuild the hover text cuz it's custom
                hover = moduleLang.get(settingID+".ui").color(CUtl.s());
                if (showExample) hover.append(" - ").append(new CTxT(value).color('7'));
                hover.append("\n")
                        .append(moduleLang.get(settingID+".info").color('7'))
                        .append("\n\n");
                // set the button text to the value if no buttonString
                if (buttonString == null) {
                    buttonTxT.text(value);
                }

                // build the hover text
                hover.append(LANG.hover("set.custom",moduleLang.get(settingID),
                        moduleLang.get(settingID).color(CUtl.s())));
                // build the button TxT
                buttonTxT
                        .hover(hover)
                        .click(2,setCMD+settingID+" ");
            }
        }

        return buttonTxT;
    }

    /**
     * contains helper methods to generate a set message for module setting editing
     */
    protected static class SetMSG {
        public static final Lang LANG = ModuleManager.Setting.LANG;

        public static CTxT customToggle(Module module, String settingID, boolean state) {
            Lang settingsLang = new Lang(LANG,module.getName()+"."+settingID+".");
            return setMSGBuilder("set", module, settingID, settingsLang.get(state?"true":"false").color(CUtl.s()));
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
