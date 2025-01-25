package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class ModuleTracking extends BaseModule {
    public static final String targetID = "target";
    @SerializedName("target")
    protected Target target;

    public static final String hybridID = "hybrid";
    @SerializedName("hybrid")
    protected boolean hybrid;

    public static final String typeID = "display-type";
    @SerializedName("display-type")
    protected Type type;

    @Override
    public String[] getSettingIDs() {
        return new String[] { targetID, hybridID, typeID };
    }

    public ModuleTracking() {
        super(Module.TRACKING);
        this.order = 2;
        this.state = true;
        this.target = Target.player;
        this.type = Type.simple;
        this.hybrid = true;
    }

    public ModuleTracking(int order, boolean state, boolean hybrid, Target target, Type type) {
        super(Module.TRACKING, order, state);
        this.hybrid = hybrid;
        this.target = target;
        this.type = type;
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
        return new ModuleTracking(this.order, this.state, this.hybrid, this.target, this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ModuleTracking that = (ModuleTracking) o;
        return hybrid == that.hybrid && target == that.target && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), target, hybrid, type);
    }
}
