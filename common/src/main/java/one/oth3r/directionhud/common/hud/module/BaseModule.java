package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseModule implements Cloneable {
    @SerializedName("module")
    protected final Module moduleType;
    @SerializedName("order")
    protected Integer order;
    @SerializedName("state")
    protected boolean state;

    public BaseModule(Module moduleType) {
        this.moduleType = moduleType;
    }

    public BaseModule(Module moduleType, Integer order, boolean state) {
        this.moduleType = moduleType;
        this.order = order;
        this.state = state;
    }

    public Integer getOrder() {
        return order;
    }

    /**
     * returns the settings IDs for the module, empty if no extra settings are in the module
     * @return an array of IDs
     */
    public String[] getSettingIDs() {
        return new String[0];
    }

    /**
     * if the module has extra settings apart from the base settings <br>
     * checks if {@link #getSettingIDs()} is empty
     */
    public boolean hasExtraSettings() {
        return getSettingIDs().length > 0;
    }

    public void setOrder(Integer order) {
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BaseModule that)) return false;
        return order == that.order && state == that.state && moduleType == that.moduleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleType, order, state);
    }
}
