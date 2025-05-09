package one.oth3r.directionhud.common.hud.module;

import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.CTxT;

public enum Module {
    COORDINATES("coordinates", ModuleCoordinates.class),
    DESTINATION("destination", ModuleDestination.class),
    DISTANCE("distance", ModuleDistance.class),
    TRACKING("tracking", ModuleTracking.class),
    DIRECTION("direction", ModuleDirection.class),
    WEATHER("weather", ModuleWeather.class),
    TIME("time", ModuleTime.class),
    ANGLE("angle", ModuleAngle.class),
    SPEED("speed", ModuleSpeed.class),
    UNKNOWN("unknown", null);

    private final String name;
    private final Class<? extends BaseModule> moduleClass;

    Module(String name, Class<? extends BaseModule> moduleClass) {
        this.name = name;
        this.moduleClass = moduleClass;
    }

    public String getName() {
        return name;
    }

    /**
     * gets a CTxT of the name of the module colored to DirectionHUD's secondary color
     */
    public CTxT getCTxT() {
        return new CTxT(name).color(CUtl.s());
    }

    public Class<? extends BaseModule> getModuleClass() {
        return moduleClass;
    }

    public static Module fromString(String fieldName) {
        for (Module field : Module.values()) {
            if (field.name.equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return UNKNOWN;
    }

    public static Module fromClass(Class<? extends BaseModule> moduleClass) {
        for (Module field : Module.values()) {
            if (field.moduleClass == moduleClass) {
                return field;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return getName();
    }
}
