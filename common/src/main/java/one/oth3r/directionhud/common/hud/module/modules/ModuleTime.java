package one.oth3r.directionhud.common.hud.module.modules;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

import java.util.Objects;

public class ModuleTime extends BaseModule {
    public static final String hour24ID = "24hr-clock";
    @SerializedName(hour24ID)
    protected boolean hour24;

    @Override
    public String[] getSettingIDs() {
        return new String[] { hour24ID };
    }

    public ModuleTime() {
        super(one.oth3r.directionhud.common.hud.module.Module.TIME);
        this.order = 1;
        this.state = true;
        this.hour24 = false;
    }

    public ModuleTime(Integer order, boolean state, boolean hour24) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModuleTime that = (ModuleTime) o;
        return hour24 == that.hour24;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hour24);
    }
}
