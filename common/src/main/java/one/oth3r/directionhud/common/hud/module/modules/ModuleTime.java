package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.ModuleText;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.setting.BooleanModuleSettingValidator;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.utils.Helper;

public class ModuleTime extends BaseModule {
    public static final String hour24ID = "24hr-clock";

    public ModuleTime(Integer order, boolean state, boolean hour24) {
        super(Module.TIME, order, state);
        registerSetting(hour24ID, hour24, new BooleanModuleSettingValidator(
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
        boolean time12 = !((boolean) getSetting(hour24ID));

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

        ModuleText.ModuleTime time = FileData.getModuleText().getTime();
        // get the format string based on 12 or 24 hour
        String formatString =
                time12 ? hour >=12 ? time.getHourPM() : time.getHourAM() : time.getHour24();

        return String.format(formatString, hr, min);
    }
}
