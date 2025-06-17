
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.HudColor;
import one.oth3r.directionhud.common.hud.module.BaseModule;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.modules.*;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<BaseModule> modules = new ArrayList<>(List.of(
            new ModuleCoordinates(0, true, true),
            new ModuleDestination(1, true,true),
            new ModuleDistance(2, true),
            new ModuleTracking(3, false, true, ModuleTracking.Target.player, ModuleTracking.Type.simple, false),
            new ModuleDirection(4, true),
            new ModuleWeather(5, true),
            new ModuleTime(6, true, false),
            new ModuleAngle(7, false, ModuleAngle.Display.both),
            new ModuleSpeed(8, false, false, "0.00"),
            new ModuleLight(9,false, ModuleLight.Target.eye, ModuleLight.Display.block)
    ));
    @SerializedName("setting")
    private Settings setting = new Settings();
    @SerializedName("primary")
    private Color primary = new Color(
            DirectionHUD.getData().getPrimary(),false,false,false);
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

    /**
     * gets the module via the Module enum from the BaseModule arraylist
     * @param module the module type to get
     * @return the module that was fetched
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseModule> T getModule(Module module) {
        return (T) BaseModule.findInArrayList(modules,module).orElseThrow(() ->
                new RuntimeException("Invalid HUD Module playerdata for "+(player == null ? "a file with no player set" : player.getName())+"!"));
    }

    /**
     * replaces the old module in the BaseModule ArrayList with the new BaseModule type provided
     *
     * @param setModule the module to replace
     */
    public void setModule(BaseModule setModule) {
        for (int i = 0; i < modules.size(); i++) {
            if (setModule.getModuleType().equals(modules.get(i).getModuleType())) {
                modules.set(i,setModule);
            }
        }
        // save after editing any player data object
        save();
    }

    /**
     * sets the whole BaseModule Arraylist
     * @param module the new ArrayList of BaseModules
     */
    public void setModules(ArrayList<BaseModule> module) {
        this.modules = module;
        // save after editing any player data object
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
        }
        save();
    }

    public void setSetting(Settings setting) {
        this.setting = setting;
        save();
    }

    public Color getColor(HudColor color) {
        return color.equals(HudColor.PRIMARY) ? primary : secondary;
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
        @SerializedName("state")
        private Boolean state = true;
        @SerializedName("type")
        private Hud.Setting.DisplayType type = Hud.Setting.DisplayType.actionbar;

        public Settings() {}

        public Settings(Settings settings) {
            this.bossbar = new Bossbar(settings.getBossbar());
            this.state = settings.state;
            this.type = settings.type;
        }

        public Bossbar getBossbar() {
            return bossbar;
        }

        public void setBossbar(Bossbar bossbar) {
            this.bossbar = bossbar;
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
    }
}
