package one.oth3r.directionhud.common.hud.module.modules;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

import java.util.Objects;

public class ModuleTracking extends BaseModule {
    public static final String hybridID = "hybrid";
    @SerializedName(hybridID)
    protected boolean hybrid;

    public static final String targetID = "target";
    @SerializedName(targetID)
    protected Target target;

    public static final String typeID = "display-type";
    @SerializedName(typeID)
    protected Type type;

    public static final String elevationID = "show-elevation";
    @SerializedName(elevationID)
    protected boolean elevation;

    @Override
    public String[] getSettingIDs() {
        return new String[] { targetID, hybridID, typeID, elevationID };
    }

    public ModuleTracking() {
        super(one.oth3r.directionhud.common.hud.module.Module.TRACKING);
        this.order = 2;
        this.state = true;
        this.hybrid = true;
        this.target = Target.player;
        this.type = Type.simple;
        this.elevation = false;
    }

    public ModuleTracking(int order, boolean state, boolean hybrid, Target target, Type type, boolean elevation) {
        super(Module.TRACKING, order, state);
        this.hybrid = hybrid;
        this.target = target;
        this.type = type;
        this.elevation = elevation;
    }

    public boolean isHybrid() {
        return hybrid;
    }

    public void setHybrid(boolean hybrid) {
        this.hybrid = hybrid;
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean hasElevation() {
        return elevation;
    }

    public void setElevation(boolean elevation) {
        this.elevation = elevation;
    }

    public enum Target {
        player,
        dest
    }
    public enum Type {
        simple,
        compact
    }

    @Override
    public BaseModule clone() {
        return new ModuleTracking(this.order, this.state, this.hybrid, this.target, this.type, this.elevation);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModuleTracking that = (ModuleTracking) o;
        return hybrid == that.hybrid && elevation == that.elevation && target == that.target && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hybrid, target, type, elevation);
    }
}
