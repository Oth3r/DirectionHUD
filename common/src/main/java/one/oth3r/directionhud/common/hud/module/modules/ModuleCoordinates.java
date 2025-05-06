package one.oth3r.directionhud.common.hud.module.modules;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

import java.util.Objects;

public class ModuleCoordinates extends BaseModule {
    public static final String xyzID = "xyz-display";
    @SerializedName(xyzID)
    protected boolean xyz;

    @Override
    public String[] getSettingIDs() {
        return new String[]{xyzID};
    }

    public ModuleCoordinates() {
        super(one.oth3r.directionhud.common.hud.module.Module.COORDINATES);
        this.order = 1;
        this.state = true;
        this.xyz = true;
    }

    public ModuleCoordinates(Integer order, boolean state, boolean xyz) {
        super(Module.COORDINATES, order, state);
        this.xyz = xyz;
    }

    public boolean isXyz() {
        return xyz;
    }

    public void setXyz(boolean xyz) {
        this.xyz = xyz;
    }

    @Override
    public BaseModule clone() {
        return new ModuleCoordinates(this.order, this.state, this.xyz);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModuleCoordinates that = (ModuleCoordinates) o;
        return xyz == that.xyz;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), xyz);
    }
}
