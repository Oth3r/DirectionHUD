package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

public class ModuleDistance extends BaseModule {
    public ModuleDistance() {
        super(one.oth3r.directionhud.common.hud.module.Module.DISTANCE);
        this.order = 1;
        this.state = true;
    }

    public ModuleDistance(int order, boolean state) {
        super(Module.DISTANCE, order, state);
    }

    @Override
    public ModuleDistance clone() {
        return new ModuleDistance(this.order, this.state);
    }
}
