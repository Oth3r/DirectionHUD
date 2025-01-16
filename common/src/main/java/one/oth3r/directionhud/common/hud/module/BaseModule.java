package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Optional;

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

    /**
     * searches for a specific BaseModule in the provided ArrayList based on the given Module type <br>
     * if there are multiple matches, the first match is returned
     *
     * @param modules ArrayList of BaseModules to search through
     * @param moduleType the Module type to search for
     * @return an Optional containing the first BaseModule matching the specified Module type, empty if not found
     */
    public static Optional<BaseModule> findInArrayList(ArrayList<BaseModule> modules, Module moduleType) {
        return modules.stream().filter(baseModule -> baseModule.getModuleType().equals(moduleType)).findFirst();
    }
}
