package one.oth3r.directionhud.common.hud.module;

import one.oth3r.directionhud.common.files.playerdata.BasePData;

public class ModuleCoordinates extends BaseModule {
    public ModuleCoordinates() {
        super(Module.COORDINATES);
        this.order = 1;
        this.state = true;
    }

    public ModuleCoordinates(int order, boolean state) {
        super(Module.COORDINATES, order, state);
    }

    @Override
    public BaseModule clone() {
        return new ModuleCoordinates(this.order, this.state);
    }
}
