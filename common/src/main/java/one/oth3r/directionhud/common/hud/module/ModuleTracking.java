package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

public class ModuleTracking extends BaseModule {
    @SerializedName("target")
    protected Target target;
    @SerializedName("hybrid")
    protected boolean hybrid;
    @SerializedName("display-type")
    protected Type type;

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
}
