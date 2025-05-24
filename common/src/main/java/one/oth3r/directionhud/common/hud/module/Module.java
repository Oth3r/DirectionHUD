package one.oth3r.directionhud.common.hud.module;

import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;

import java.util.List;

public enum Module {
    COORDINATES("coordinates", ModuleCoordinates.class, List.of(Loc.class)),
    DESTINATION("destination", ModuleDestination.class, List.of(Dest.class)),
    DISTANCE("distance", ModuleDistance.class, List.of(Integer.class)),
    TRACKING("tracking", ModuleTracking.class, List.of(Double.class, Loc.class,Loc.class)),
    DIRECTION("direction", ModuleDirection.class, List.of(Double.class)),
    WEATHER("weather", ModuleWeather.class, List.of(String.class, String.class)),
    TIME("time", ModuleTime.class, List.of(Integer.class, Integer.class)),
    ANGLE("angle", ModuleAngle.class, List.of(Float.class, Float.class)),
    SPEED("speed", ModuleSpeed.class, List.of(Double.class)),
    UNKNOWN("unknown", null, List.of());

    private final String name;
    private final Class<? extends BaseModule> moduleClass;
    private final List<Class<?>> displayArguments;

    Module(String name, Class<? extends BaseModule> moduleClass, List<Class<?>> displayArguments) {
        this.name = name;
        this.moduleClass = moduleClass;
        this.displayArguments = displayArguments == null ? List.of() : displayArguments;
    }

    public String getName() {
        return name;
    }

    public List<Class<?>> getDisplayArguments() {
        return displayArguments;
    }

    public boolean verifyDisplayArguments(List<Object> displayArguments) {
        if (displayArguments.size() != this.displayArguments.size()) {
            return false;
        }
        for (int i = 0; i < displayArguments.size(); i++) {
            Object actual = displayArguments.get(i);
            if (actual == null) return false;

            Class<?> expected = this.displayArguments.get(i);
            Class<?> actualClass = actual.getClass();

            if (!expected.isAssignableFrom(actualClass)) {
                return false;
            }
        }
        return true;
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
