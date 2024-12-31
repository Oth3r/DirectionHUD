package one.oth3r.directionhud.common.hud.module;

import one.oth3r.directionhud.common.files.playerdata.BasePData;

public class ModuleDistance extends BaseModule {
    public ModuleDistance() {
        super(Module.DISTANCE);
        // todo get default
        BasePData pData = new BasePData();
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
