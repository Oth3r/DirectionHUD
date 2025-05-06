package one.oth3r.directionhud.common.hud.module.modules;

import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;

public class ModuleWeather extends BaseModule {
    public ModuleWeather() {
        super(one.oth3r.directionhud.common.hud.module.Module.WEATHER);
        this.order = 1;
        this.state = true;
    }

    public ModuleWeather(Integer order, boolean state) {
        super(Module.WEATHER, order, state);
    }

    @Override
    public ModuleWeather clone() {
        return new ModuleWeather(this.order, this.state);
    }
}
