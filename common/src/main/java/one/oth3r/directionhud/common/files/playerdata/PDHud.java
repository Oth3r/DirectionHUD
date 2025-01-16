
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.*;
import one.oth3r.directionhud.common.hud.module.Module;
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
        player.getPData().queueSave();
    }
    
    private transient Player player;

    @SerializedName("modules")
    private ArrayList<BaseModule> modules = new ArrayList<>() {{
        add(new ModuleCoordinates(0, true));
        add(new ModuleDestination(1, true));
        add(new ModuleDistance(2, true));
        add(new ModuleTracking(3, false, true, ModuleTracking.Target.player, ModuleTracking.Type.simple));
        add(new ModuleDirection(4, true));
        add(new ModuleWeather(5, true));
        add(new ModuleTime(6, true, false));
        add(new ModuleAngle(7, false, ModuleAngle.Display.both));
        add(new ModuleSpeed(8, false, false, "0.00"));
    }};
    @SerializedName("setting")
    private Settings setting = new Settings();
    @SerializedName("primary")
    private Color primary = new Color(
            DirectionHUD.PRIMARY,false,false,false);
    @SerializedName("secondary")
    private Color secondary = new Color(
            "#ffffff",false,false,false);

    public PDHud() {}

    public PDHud(PDHud hud) {
        ArrayList<BaseModule> baseModules = new ArrayList<>();
        for (BaseModule module : hud.modules) baseModules.add(module.clone());
        this.modules = baseModules;
        this.setting = new Settings(hud.setting);
        this.primary = new Color(hud.getPrimary());
        this.secondary = new Color(hud.getSecondary());
    }

    public ArrayList<BaseModule> getModules() {
        return modules;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseModule> T getModule(Module module) {
        return (T) BaseModule.findInArrayList(modules,module).orElseThrow(() ->
                new RuntimeException("Invalid HUD Module playerdata for "+(player == null ? "a file with no player set" : player.getName())+"!"));
    }

    public void setModule(Module module, BaseModule setModule) {
        for (int i = 0; i < modules.size(); i++) {
            if (module.getModuleClass().isInstance(modules.get(i))) {
                modules.set(i,setModule);
            }
        }
        save();
    }

    public void setModules(ArrayList<BaseModule> module) {
        this.modules = module;
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
            player.getPData().queueSave();
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

        public Color(Color color) {
            this.color = color.color;
            this.bold = color.bold;
            this.italics = color.italics;
            this.rainbow = color.rainbow;
            this.player = color.player;
        }

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

        public Settings(Settings settings) {
            this.bossbar = new Bossbar(settings.getBossbar());
            this.module = new Module(settings.getModule());
            this.state = settings.state;
            this.type = settings.type;
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

            public Bossbar(Bossbar bossbar) {
                this.color = bossbar.color;
                this.distance = bossbar.distance;
                this.distanceMax = bossbar.distanceMax;
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
            private Boolean time24hr = false;
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

            public Module(Module module) {
                this.time24hr = module.time24hr;
                this.trackingType = module.trackingType;
                this.trackingTarget = module.trackingTarget;
                this.trackingHybrid = module.trackingHybrid;
                this.speed3d = module.speed3d;
                this.speedPattern = module.speedPattern;
                this.angleDisplay = module.angleDisplay;
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
}
