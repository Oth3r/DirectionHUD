package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

public class ModuleSpeed extends BaseModule {
    @SerializedName("2d-calculation")
    protected boolean calculation2D;
    @SerializedName("display-pattern")
    protected String displayPattern;

    public ModuleSpeed() {
        super(Module.SPEED);
    }

    public ModuleSpeed(int order, boolean state, boolean calculation2D, String displayPattern) {
        super(Module.SPEED, order, state);
        this.calculation2D = calculation2D;
        this.displayPattern = displayPattern;
    }

    public boolean isCalculation2D() {
        return calculation2D;
    }

    public void setCalculation2D(boolean calculation2D) {
        this.calculation2D = calculation2D;
    }

    public String getDisplayPattern() {
        return displayPattern;
    }

    public void setDisplayPattern(String displayPattern) {
        this.displayPattern = displayPattern;
    }

    @Override
    public ModuleSpeed clone() {
        return new ModuleSpeed(this.order, this.state, this.calculation2D, this.displayPattern);
    }
}
