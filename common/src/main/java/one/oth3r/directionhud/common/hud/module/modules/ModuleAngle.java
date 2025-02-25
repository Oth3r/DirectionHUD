package one.oth3r.directionhud.common.hud.module.modules;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

import java.util.Objects;

public class ModuleAngle extends BaseModule {
    public static final String displayID = "display";
    @SerializedName(displayID)
    protected Display display;

    @Override
    public String[] getSettingIDs() {
        return new String[] { displayID };
    }

    public ModuleAngle() {
        super(one.oth3r.directionhud.common.hud.module.Module.ANGLE);
        this.order = 1;
        this.state = true;
        this.display = Display.both;
    }

    public ModuleAngle(int order, boolean state, Display display) {
        super(Module.ANGLE, order, state);
        this.display = display;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public enum Display {
        yaw,
        pitch,
        both
    }

    @Override
    public ModuleAngle clone() {
        return new ModuleAngle(this.order,this.state,this.display);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModuleAngle that = (ModuleAngle) o;
        return display == that.display;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), display);
    }
}
