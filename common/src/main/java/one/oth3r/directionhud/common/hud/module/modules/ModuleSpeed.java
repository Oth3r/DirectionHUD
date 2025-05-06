package one.oth3r.directionhud.common.hud.module.modules;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

import java.util.Objects;

public class ModuleSpeed extends BaseModule {
    public static final String calculation2DID = "2d-calculation";
    @SerializedName(calculation2DID)
    protected boolean calculation2D;

    public static final String displayPatternID = "display-pattern";
    @SerializedName(displayPatternID)
    protected String displayPattern;

    @Override
    public String[] getSettingIDs() {
        return new String[] { calculation2DID, displayPatternID };
    }

    public ModuleSpeed() {
        super(one.oth3r.directionhud.common.hud.module.Module.SPEED);
    }

    public ModuleSpeed(Integer order, boolean state, boolean calculation2D, String displayPattern) {
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        return settingEquals((BaseModule) o);
    }

    @Override
    public boolean settingEquals(BaseModule module) {
        if (module instanceof ModuleSpeed mod) {
            return calculation2D == mod.calculation2D && Objects.equals(displayPattern, mod.displayPattern);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), calculation2D, displayPattern);
    }
}
