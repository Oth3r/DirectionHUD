package one.oth3r.directionhud.common.hud.module;

import one.oth3r.directionhud.common.files.playerdata.BasePData;

public class ModuleDestination extends BaseModule {
    public ModuleDestination() {
        super(Module.DESTINATION);
        this.order = 2;
        this.state = true;
    }

    public ModuleDestination(int order, boolean state) {
        super(Module.DESTINATION, order, state);
    }

    @Override
    public ModuleDestination clone() {
        return new ModuleDestination(this.order, this.state);
    }
}
