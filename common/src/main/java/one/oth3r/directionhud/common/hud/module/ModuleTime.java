package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.playerdata.BasePData;

public class ModuleTime extends BaseModule {
    @SerializedName("24hr-clock")
    protected boolean hour24;

    public ModuleTime() {
        super(Module.TIME);
        // todo get default
        BasePData pData = new BasePData();
        this.order = 1;
        this.state = true;
        this.hour24 = false;
    }

    public ModuleTime(int order, boolean state, boolean hour24) {
        super(Module.TIME, order, state);
        this.hour24 = hour24;
    }

    public boolean isHour24() {
        return hour24;
    }

    public void setHour24(boolean hour24) {
        this.hour24 = hour24;
    }

    @Override
    public ModuleTime clone() {
        return new ModuleTime(this.order, this.state, this.hour24);
    }
}
