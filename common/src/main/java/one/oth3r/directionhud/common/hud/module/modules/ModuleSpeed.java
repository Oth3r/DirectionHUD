package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.setting.BooleanModuleSettingHandler;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingDisplay;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingType;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandler;

import java.text.DecimalFormat;

public class ModuleSpeed extends BaseModule {
    public static final String calculation2DID = "2d-calculation";
    public static final String displayPatternID = "display-pattern";

    public ModuleSpeed(Integer order, boolean state, boolean calculation2D, String displayPattern) {
        super(Module.SPEED, order, state);
        registerSetting(calculation2DID, calculation2D, new BooleanModuleSettingHandler(
                Module.SPEED,calculation2DID,true,false
        ));
        registerSetting(displayPatternID, displayPattern, new ModuleSettingHandler<>() {
            @Override
            public boolean isValid(String value) {
                try {
                    new DecimalFormat(value);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public String convert(String value) throws IllegalArgumentException {
                return value;
            }

            /**
             * gets the module display variable for the module setting
             */
            @Override
            public ModuleSettingDisplay getSettingDisplay() {
                return new ModuleSettingDisplay(Module.SPEED,displayPatternID,
                        ModuleSettingType.CUSTOM,true);
            }
        });
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        double speed = (double) args[0];
        // assets
        boolean speed2D = getSetting(calculation2DID);
        DecimalFormat df = new DecimalFormat(getSetting(displayPatternID));
        String data = df.format(speed);

        if (speed2D) return String.format(FileData.getModuleText().getSpeed().getXzSpeed(), data);
        return String.format(FileData.getModuleText().getSpeed().getXyzSpeed(), data);
    }
}
