package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSetting;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandler;
import one.oth3r.directionhud.common.hud.module.setting.ModuleSettingHandlerRegistry;
import one.oth3r.directionhud.common.utils.ActionResult;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Lang;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

import java.util.*;

public abstract class BaseModule implements Cloneable {
    @SerializedName("module")
    protected final Module moduleType;
    @SerializedName("order")
    protected Integer order;
    @SerializedName("state")
    protected boolean state;
    // dynamic settings for each module
    @SerializedName("settings")
    protected Map<String, ModuleSetting<?>> settings = new HashMap<>();

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

    public <V> void registerSetting(String settingID, V defaultValue, ModuleSettingHandler<V> validator) {
        settings.put(settingID, new ModuleSetting<>(settingID, defaultValue, validator));
        ModuleSettingHandlerRegistry.registerValidator(settingID, validator);
    }

    @SuppressWarnings("unchecked")
    public <V> ActionResult setSetting(String settingID, String strValue) {
        Lang LANG = ModuleManager.Setting.LANG;
        if (settings.containsKey(settingID)) {
            ModuleSetting<V> setting = (ModuleSetting<V>) settings.get(settingID);
            try {
                V newValue = setting.getValidator().convert(strValue);
                boolean status = setting.setValue(newValue);

                if (status) return new ActionResult(true, setting.getDisplay().getSetMsg(setting.getValue().toString()));
                // if the value is invalid, an exception will be thrown
                else throw new IllegalArgumentException();
            } catch (Exception e) {
                // setting value invalid
                return new ActionResult(false,LANG.error("invalid.setting_value",
                        new CTxT(settingID).color(CUtl.s())));
            }
        } else {
            return new ActionResult(false, LANG.error(
                    settings.isEmpty() ? "no_settings" : "invalid", moduleType.getCTxT()));
        }
    }

    @SuppressWarnings("unchecked")
    public <V> V getSetting(String settingID) {
        if (settings.containsKey(settingID)) {
            ModuleSetting<?> setting = settings.get(settingID);
            return (V) setting.getValue();
        }
        return null;
    }

    public boolean hasSettings() {
        return !settings.isEmpty();
    }

    public Map<String, ModuleSetting<?>> getSettings() {
        return new HashMap<>(settings);
    }

    /**
     * returns an array of setting IDs in the desired order
     * override this to customize the order
     */
    protected String[] getSettingOrder() {
        return new String[0];
    }

    /**
     * returns a list of settings in the order specified by {@link #getSettingOrder()}.
     * if {@link #getSettingOrder()} is empty, uses default order
     */
    public List<ModuleSetting<?>> getOrderedSettings() {
        String[] order = getSettingOrder();
        if (order.length == 0) {
            // Default: insertion order
            return new ArrayList<>(settings.values());
        } else {
            List<ModuleSetting<?>> ordered = new ArrayList<>();
            for (String id : order) {
                ModuleSetting<?> setting = settings.get(id);
                if (setting != null) ordered.add(setting);
            }
            // add any settings not in the order array at the end
            for (Map.Entry<String, ModuleSetting<?>> entry : settings.entrySet()) {
                if (Arrays.stream(order).noneMatch(id -> id.equals(entry.getKey()))) {
                    ordered.add(entry.getValue());
                }
            }
            return ordered;
        }
    }

    public CTxT getSettingButtons() {
        CTxT out = new CTxT();
        List<ModuleSetting<?>> settingList = getOrderedSettings();
        for (int i = 0; i < settingList.size(); i++) {
            ModuleSetting<?> setting = settingList.get(i);
            out.append(setting.getDisplay().getButton(setting.getValue().toString()));
            // only add a space if not the last element
            if (i < settingList.size() - 1) out.append(" ");
        }
        return out;
    }

    public void reassignValidators() {
        for (ModuleSetting<?> setting : settings.values()) {
            ModuleSettingHandler<?> validator = ModuleSettingHandlerRegistry.getHandler(setting.getId());
            if (validator != null) {
                setting.setValidator(validator);
            }
        }
    }


    /**
     * returns the settings IDs for the module, empty if no extra settings are in the module
     * @return an array of IDs
     */
    public String[] getSettingIDs() {
        return settings.keySet().toArray(new String[0]);
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
    public String toString() {
        return moduleType.getName();
    }

    /**
     * gets the module display string
     * @param args the valid arguments to display the module, arguments via {@link Module}
     * @return an unformatted string of the module display
     * @throws IllegalArgumentException if the arguments are not valid for the module
     */
    public String getDisplayString(Object... args) {
        if (!moduleType.verifyDisplayArguments(Arrays.stream(args).toList())) {
            throw new IllegalArgumentException("Invalid display arguments for module: " + moduleType.getName());
        }
        return display(args);
    }

    /**
     * gets the module display string as a CTxT
     * @param player the player to format against
     * @param args the valid arguments to display the module, arguments via {@link Module}
     * @return a CTxT of the module display
     * @throws IllegalArgumentException if the arguments are not valid for the module
     */
    public CTxT getDisplayTxT(Player player, Object... args) {
        return CUtl.parse(player, getDisplayString(args));
    }

    /**
     * the logic for getting the string for the module display
     * @param args the correct arguments for displaying the module
     * @return the module display
     */
    protected abstract String display(Object... args);

    // todo get setting button method

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
        return Objects.equals(order, that.order) && state == that.state && moduleType == that.moduleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleType, order, state);
    }

    @Override
    public BaseModule clone() {
        try {
            BaseModule clone = (BaseModule) super.clone();
            // Deep cloning the settings map
            clone.settings = new HashMap<>();
            for (Map.Entry<String, ModuleSetting<?>> entry : this.settings.entrySet()) {
                String key = entry.getKey();
                ModuleSetting<?> value = entry.getValue();

                // Assuming ModuleSetting class supports cloning
                ModuleSetting<?> valueClone = value.clone();
                clone.settings.put(key, valueClone);
            }

            // Return the fully cloned object
            return clone;

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
