package one.oth3r.directionhud.common.hud.module;

public class ModuleWeather extends BaseModule {
    public ModuleWeather() {
        super(Module.WEATHER);
        this.order = 1;
        this.state = true;
    }

    public ModuleWeather(int order, boolean state) {
        super(Module.WEATHER, order, state);
    }

    @Override
    public ModuleWeather clone() {
        return new ModuleWeather(this.order, this.state);
    }
}
