package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

public class ModuleAngle extends BaseModule {
    @SerializedName("display")
    protected Display display;

    public ModuleAngle() {
        super(Module.ANGLE);
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
}
