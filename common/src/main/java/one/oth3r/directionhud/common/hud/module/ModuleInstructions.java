package one.oth3r.directionhud.common.hud.module;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Set;

public class ModuleInstructions {
    @SerializedName("instructions")
    private final HashMap<Module, String> instructions = new HashMap<>();

    public void put(Module module, String instruction) {
        instructions.put(module, instruction);
    }

    public String get(Module module) {
        return instructions.getOrDefault(module, "");
    }

    public boolean isEmpty(Module module) {
        return get(module).isEmpty();
    }

    public Set<Module> getModules() {
        return instructions.keySet();
    }
}
