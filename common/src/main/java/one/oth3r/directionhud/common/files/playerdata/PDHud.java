
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;

public class PDHud {

    public void setPlayer(Player player) {
        this.player = player;
        primary.setPlayer(player);
        secondary.setPlayer(player);
    }
    
    private void save() {
        if (player == null) return;
        player.getPData().save();
    }
    
    private transient Player player;

    @SerializedName("module")
    private Modules module = new Modules();
    @SerializedName("setting")
    private Settings setting = new Settings();
    @SerializedName("order")
    private ArrayList<Hud.Module> order = Hud.modules.getDefaultOrder();
    @SerializedName("primary")
    private Color primary = new PDHud.Color(
            DirectionHUD.PRIMARY,false,false,false);
    @SerializedName("secondary")
    private Color secondary = new PDHud.Color(
            DirectionHUD.SECONDARY,false,false,false);

    public PDHud() {}

    public PDHud(Modules module, Settings setting, ArrayList<Hud.Module> order, Color primary, Color secondary) {
        this.module = module;
        this.setting = setting;
        this.order = order;
        this.primary = primary;
        this.secondary = secondary;
    }

    public Modules getModule() {
        return module;
    }

    public boolean getModule(Hud.Module module) {
        return switch (module) {
            case destination -> getModule().getDestination();
            case time -> getModule().getTime();
            case angle -> getModule().getAngle();
            case speed -> getModule().getSpeed();
            case weather -> getModule().getWeather();
            case distance -> getModule().getDistance();
            case tracking -> getModule().getTracking();
            case direction -> getModule().getDirection();
            case coordinates -> getModule().getCoordinates();
            default -> false;
        };
    }

    public void setModule(Modules module) {
        this.module = module;
        save();
    }

    public void setModule(Hud.Module module, boolean state) {
        switch (module) {
            case coordinates -> getModule().setCoordinates(state);
            case destination -> getModule().setDestination(state);
            case direction -> getModule().setDirection(state);
            case distance -> getModule().setDistance(state);
            case speed -> getModule().setSpeed(state);
            case angle -> getModule().setAngle(state);
            case tracking -> getModule().setTracking(state);
            case weather -> getModule().setWeather(state);
            case time -> getModule().setTime(state);
        }
        save();
    }

    public Settings getSetting() {
        return setting;
    }

    public Object getSetting(Hud.Setting type) {
        return switch (type) {
            case type -> getSetting().getType();
            case state -> getSetting().getState();
            case bossbar__color -> getSetting().getBossbar().getColor();
            case bossbar__distance -> getSetting().getBossbar().getDistance();
            case bossbar__distance_max -> getSetting().getBossbar().getDistanceMax();
            case module__angle_display -> getSetting().getModule().getAngleDisplay();
            case module__speed_3d -> getSetting().getModule().getSpeed3d();
            case module__speed_pattern -> getSetting().getModule().getSpeedPattern();
            case module__tracking_target -> getSetting().getModule().getTrackingTarget();
            case module__tracking_hybrid -> getSetting().getModule().getTrackingHybrid();
            case module__tracking_type -> getSetting().getModule().getTrackingType();
            case module__time_24hr -> getSetting().getModule().getTime24hr();
            default -> null;
        };
    }

    public void setSetting(Hud.Setting type, Object setting) {
        switch (type) {
            case type -> getSetting().setType(setting.toString());
            case state -> getSetting().setState((Boolean) setting);
            case bossbar__color -> getSetting().getBossbar().setColor(setting.toString());
            case bossbar__distance -> getSetting().getBossbar().setDistance((Boolean) setting);
            case bossbar__distance_max -> getSetting().getBossbar().setDistanceMax((int) setting);
            case module__angle_display -> getSetting().getModule().setAngleDisplay(setting.toString());
            case module__speed_3d -> getSetting().getModule().setSpeed3d((Boolean) setting);
            case module__speed_pattern -> getSetting().getModule().setSpeedPattern((String) setting);
            case module__tracking_hybrid -> getSetting().getModule().setTrackingHybrid((Boolean) setting);
            case module__tracking_type -> getSetting().getModule().setTrackingType(setting.toString());
            case module__tracking_target -> getSetting().getModule().setTrackingTarget(setting.toString());
            case module__time_24hr -> getSetting().getModule().setTime24hr((Boolean) setting);
        }
        save();
    }

    public void setSetting(Settings setting) {
        this.setting = setting;
        save();
    }

    public ArrayList<Hud.Module> getOrder() {
        return order;
    }

    public void setOrder(ArrayList<Hud.Module> order) {
        this.order = order;
        save();
    }

    public Color getPrimary() {
        return primary;
    }

    public void setPrimary(Color primary) {
        this.primary = primary;
        save();
    }

    public Color getSecondary() {
        return secondary;
    }

    public void setSecondary(Color secondary) {
        this.secondary = secondary;
        save();
    }

    public static class Color {

        public void setPlayer(Player player) {
            this.player = player;
        }

        private void save() {
            if (player == null) return;
            player.getPData().save();
        }

        private transient Player player;

        @SerializedName("color")
        private String color;
        @SerializedName("bold")
        private Boolean bold;
        @SerializedName("italics")
        private Boolean italics;
        @SerializedName("rainbow")
        private Boolean rainbow;

        public Color(Player player, String color, Boolean bold, Boolean italics, Boolean rainbow) {
            this.color = color;
            this.bold = bold;
            this.italics = italics;
            this.rainbow = rainbow;
            this.player = player;
        }

        public Color(String color, Boolean bold, Boolean italics, Boolean rainbow) {
            this.color = color;
            this.bold = bold;
            this.italics = italics;
            this.rainbow = rainbow;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
            save();
        }

        public Boolean getBold() {
            return bold;
        }

        public void setBold(Boolean bold) {
            this.bold = bold;
            save();
        }

        public Boolean getItalics() {
            return italics;
        }

        public void setItalics(Boolean italics) {
            this.italics = italics;
            save();
        }

        public Boolean getRainbow() {
            return rainbow;
        }

        public void setRainbow(Boolean rainbow) {
            this.rainbow = rainbow;
            save();
        }
    }

    public static class Settings {

        @SerializedName("bossbar")
        private Bossbar bossbar = new Bossbar();
        @SerializedName("module")
        private Module module = new Module();
        @SerializedName("state")
        private Boolean state = true;
        @SerializedName("type")
        private Hud.Setting.DisplayType type = Hud.Setting.DisplayType.actionbar;

        public Settings() {}

        public Settings(Bossbar bossbar, Module module, Boolean state, Hud.Setting.DisplayType type) {
            this.bossbar = bossbar;
            this.module = module;
            this.state = state;
            this.type = type;
        }

        public Bossbar getBossbar() {
            return bossbar;
        }

        public void setBossbar(Bossbar bossbar) {
            this.bossbar = bossbar;
        }

        public Module getModule() {
            return module;
        }

        public void setModule(Module module) {
            this.module = module;
        }

        public Boolean getState() {
            return state;
        }

        public void setState(Boolean state) {
            this.state = state;
        }

        public String getType() {
            return type.toString();
        }

        public void setType(String type) {
            this.type = Helper.Enums.get(type, Hud.Setting.DisplayType.class);
        }

        public static class Bossbar {

            @SerializedName("color")
            private Hud.Setting.BarColor color = Hud.Setting.BarColor.white;
            @SerializedName("distance")
            private Boolean distance = true;
            @SerializedName("distance_max")
            private Integer distanceMax = 0;

            public Bossbar() {}

            public Bossbar(Hud.Setting.BarColor color, Boolean distance, Integer distanceMax) {
                this.color = color;
                this.distance = distance;
                this.distanceMax = distanceMax;
            }

            public String getColor() {
                return color.toString();
            }

            public void setColor(String color) {
                this.color = Helper.Enums.get(color, Hud.Setting.BarColor.class);
            }

            public Boolean getDistance() {
                return distance;
            }

            public void setDistance(Boolean distance) {
                this.distance = distance;
            }

            public Integer getDistanceMax() {
                return distanceMax;
            }

            public void setDistanceMax(Integer distanceMax) {
                this.distanceMax = distanceMax;
            }
        }

        public static class Module {

            @SerializedName("time_24hr")
            private Boolean time24hr = true;
            @SerializedName("tracking_type")
            private Hud.Setting.ModuleTrackingType trackingType = Hud.Setting.ModuleTrackingType.simple;
            @SerializedName("tracking_target")
            private Hud.Setting.ModuleTrackingTarget trackingTarget = Hud.Setting.ModuleTrackingTarget.player;
            @SerializedName("tracking_hybrid")
            private Boolean trackingHybrid = true;
            @SerializedName("speed_3d")
            private Boolean speed3d = true;
            @SerializedName("speed_pattern")
            private String speedPattern = "0.00";
            @SerializedName("angle_display")
            private Hud.Setting.ModuleAngleDisplay angleDisplay = Hud.Setting.ModuleAngleDisplay.both;

            public Module() {}

            public Module(Boolean time24hr, Hud.Setting.ModuleTrackingType trackingType, Hud.Setting.ModuleTrackingTarget trackingTarget, Boolean trackingHybrid, Boolean speed3d, String speedPattern, Hud.Setting.ModuleAngleDisplay angleDisplay) {
                this.time24hr = time24hr;
                this.trackingType = trackingType;
                this.trackingTarget = trackingTarget;
                this.trackingHybrid = trackingHybrid;
                this.speed3d = speed3d;
                this.speedPattern = speedPattern;
                this.angleDisplay = angleDisplay;
            }

            public Boolean getSpeed3d() {
                return speed3d;
            }

            public void setSpeed3d(Boolean speed3d) {
                this.speed3d = speed3d;
            }

            public String getTrackingType() {
                return trackingType.toString();
            }

            public void setTrackingType(String trackingType) {
                this.trackingType = Helper.Enums.get(trackingType, Hud.Setting.ModuleTrackingType.class);
            }

            public String getTrackingTarget() {
                return trackingTarget.toString();
            }

            public void setTrackingTarget(String trackingTarget) {
                this.trackingTarget = Helper.Enums.get(trackingTarget, Hud.Setting.ModuleTrackingTarget.class);
            }

            public String getSpeedPattern() {
                return speedPattern;
            }

            public void setSpeedPattern(String speedPattern) {
                this.speedPattern = speedPattern;
            }

            public String getAngleDisplay() {
                return angleDisplay.toString();
            }

            public void setAngleDisplay(String angleDisplay) {
                this.angleDisplay = Helper.Enums.get(angleDisplay, Hud.Setting.ModuleAngleDisplay.class);
            }

            public Boolean getTrackingHybrid() {
                return trackingHybrid;
            }

            public void setTrackingHybrid(Boolean trackingHybrid) {
                this.trackingHybrid = trackingHybrid;
            }

            public Boolean getTime24hr() {
                return time24hr;
            }

            public void setTime24hr(Boolean time24hr) {
                this.time24hr = time24hr;
            }

        }
    }

    public static class Modules {

        @SerializedName("coordinates")
        private Boolean coordinates = true;
        @SerializedName("destination")
        private Boolean destination = true;
        @SerializedName("distance")
        private Boolean distance = true;
        @SerializedName("tracking")
        private Boolean tracking = false;
        @SerializedName("direction")
        private Boolean direction = true;
        @SerializedName("weather")
        private Boolean weather = true;
        @SerializedName("time")
        private Boolean time = true;
        @SerializedName("angle")
        private Boolean angle = false;
        @SerializedName("speed")
        private Boolean speed = false;

        public Modules() {}

        public Modules(Boolean coordinates, Boolean destination, Boolean distance, Boolean tracking, Boolean direction, Boolean weather, Boolean time, Boolean angle, Boolean speed) {
            this.coordinates = coordinates;
            this.destination = destination;
            this.distance = distance;
            this.tracking = tracking;
            this.direction = direction;
            this.weather = weather;
            this.time = time;
            this.angle = angle;
            this.speed = speed;
        }

        public Boolean getDistance() {
            return distance;
        }

        public void setDistance(Boolean distance) {
            this.distance = distance;
        }

        public Boolean getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(Boolean coordinates) {
            this.coordinates = coordinates;
        }

        public Boolean getDestination() {
            return destination;
        }

        public void setDestination(Boolean destination) {
            this.destination = destination;
        }

        public Boolean getWeather() {
            return weather;
        }

        public void setWeather(Boolean weather) {
            this.weather = weather;
        }

        public Boolean getAngle() {
            return angle;
        }

        public void setAngle(Boolean angle) {
            this.angle = angle;
        }

        public Boolean getTime() {
            return time;
        }

        public void setTime(Boolean time) {
            this.time = time;
        }

        public Boolean getTracking() {
            return tracking;
        }

        public void setTracking(Boolean tracking) {
            this.tracking = tracking;
        }

        public Boolean getSpeed() {
            return speed;
        }

        public void setSpeed(Boolean speed) {
            this.speed = speed;
        }

        public Boolean getDirection() {
            return direction;
        }

        public void setDirection(Boolean direction) {
            this.direction = direction;
        }
    }
}
