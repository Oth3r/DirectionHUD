package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

public class ModuleDirection extends BaseModule {
    public ModuleDirection() {
        super(one.oth3r.directionhud.common.hud.module.Module.DIRECTION);
        this.order = 1;
        this.state = true;
    }

    public ModuleDirection(Integer order, boolean state) {
        super(Module.DIRECTION, order, state);
    }

    @Override
    public ModuleDirection clone() {
        return new ModuleDirection(this.order, this.state);
    }

    @Override
    public boolean settingEquals(BaseModule module) {
        return true;
    }
}
