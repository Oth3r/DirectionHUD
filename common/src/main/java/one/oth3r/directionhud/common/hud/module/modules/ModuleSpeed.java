package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.setting.*;
import one.oth3r.directionhud.common.hud.module.Module;

import java.text.DecimalFormat;

public class ModuleSpeed extends BaseModule {
    public static final String calculation2DID = "2d-calculation";
    public static final String displayPatternID = "display-pattern";

    public ModuleSpeed() {
        super(Module.SPEED);
    }

    public ModuleSpeed(Integer order, boolean state, boolean calculation2D, String displayPattern) {
        super(Module.SPEED, order, state);

        registerSetting(calculation2DID, calculation2D, new BooleanModuleSettingHandler(
                Module.SPEED,calculation2DID,true,false,
                new ModuleSettingButtonDisplay(true)
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

    @Override
    protected String[] getSettingOrder() {
        return new String[] {calculation2DID,displayPatternID};
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
        boolean speed2D = getSettingValue(calculation2DID);
        DecimalFormat df = new DecimalFormat(getSettingValue(displayPatternID));
        String data = df.format(speed);

        if (speed2D) return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_XYZ,data);
        return DisplayRegistry.getFormatted(this.moduleType,DISPLAY_XZ,data);
    }

    public static final String DISPLAY_XYZ = "xyz_speed";
    public static final String DISPLAY_XZ = "xz_speed";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_XZ,"&2%s &1B/S");
        display.addDisplay(DISPLAY_XYZ,"&2%s &1B/S");

        return display;
    }
}
