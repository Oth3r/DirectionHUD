package one.oth3r.directionhud.common.hud.module;

public class ModuleDirection extends BaseModule {
    public ModuleDirection() {
        super(Module.DIRECTION);
        this.order = 1;
        this.state = true;
    }

    public ModuleDirection(int order, boolean state) {
        super(Module.DIRECTION, order, state);
    }

    @Override
    public ModuleDirection clone() {
        return new ModuleDirection(this.order, this.state);
    }
}
