package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.display.DisplaySettings;
import one.oth3r.directionhud.common.hud.module.display.DisplayRegistry;
import one.oth3r.directionhud.common.hud.module.setting.BooleanModuleSettingHandler;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Helper;

public class ModuleTime extends BaseModule {
    public static final String hour24ID = "24hr-clock";

    public ModuleTime() {
        super(Module.TIME);
    }

    public ModuleTime(Integer order, boolean state, boolean hour24) {
        super(Module.TIME, order, state);
        registerSetting(hour24ID, hour24, new BooleanModuleSettingHandler(
                Module.TIME,hour24ID,true,false
        ));
    }

    /**
     * the logic for getting the string for the module display
     *
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    @Override
    protected String display(Object... args) {
        int hour = (int) args[0], minute = (int) args[1];
        // assets
        boolean time12 = !((boolean) getSettingValue(hour24ID));

        String hr;
        // if 12 hr, fix the hour mark
        if (time12) {
            int hourMod = hour % 12;
            // if hr % 12 = 0, its 12 am/pm
            if (hourMod == 0) hr = String.valueOf(12);
            else hr = String.valueOf(hourMod);
        } else {
            // make sure 24 hr time HR mark is two digits
            hr = Helper.Num.formatToTwoDigits(hour);
        }

        // add 0 to the start, then set the string to the last two numbers to always have a 2-digit number
        String min = Helper.Num.formatToTwoDigits(minute);

        // get the format string based on 12 or 24 hour
        String formatString =
                time12 ? hour >=12 ? DISPLAY_PM : DISPLAY_AM : DISPLAY_24;

        return DisplayRegistry.getFormatted(this.moduleType,formatString, hr, min);
    }

    public static final String DISPLAY_AM = "hour_AM";
    public static final String DISPLAY_PM = "hour_PM";
    public static final String DISPLAY_24 = "hour_24";

    @Override
    public DisplaySettings getDisplaySettings() {
        DisplaySettings display = new DisplaySettings();
        display.addDisplay(DISPLAY_AM,"&2%s&1:&2%s &1AM");
        display.addDisplay(DISPLAY_PM,"&2%s&1:&2%s &1PM");
        display.addDisplay(DISPLAY_24,"&2%s&1:&2%s");

        return display;
    }
}
