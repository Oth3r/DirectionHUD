package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

public abstract class BaseModule implements Cloneable {
    @SerializedName("module")
    protected final Module moduleType;
    @SerializedName("order")
    protected int order;
    @SerializedName("state")
    protected boolean state;


    public BaseModule(Module moduleType) {
        this.moduleType = moduleType;
    }

    public BaseModule(Module moduleType, int order, boolean state) {
        this.moduleType = moduleType;
        this.order = order;
        this.state = state;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isEnabled() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public Module getModuleType() {
        return moduleType;
    }

    @Override
    public abstract BaseModule clone();

    @Override
    public String toString() {
        return moduleType.getName();
    }
}
