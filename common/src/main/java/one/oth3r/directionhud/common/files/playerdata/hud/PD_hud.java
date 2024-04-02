
package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Helper.*;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.List;

public class PD_hud {
    public void setPlayer(Player player) {
        this.player = player;
        primary.setPlayer(player);
        secondary.setPlayer(player);
    }
    private transient Player player;
    @SerializedName("module")
    private PD_hud_module module = new PD_hud_module();
    @SerializedName("setting")
    private PD_hud_setting setting = new PD_hud_setting();
    @SerializedName("order")
    private List<String> order = Enums.toStringList(config.hud.Order);
    @SerializedName("primary")
    private PD_hud_color primary = new PD_hud_color(player,config.hud.primary.Color,config.hud.primary.Bold,config.hud.primary.Italics,config.hud.primary.Rainbow);
    @SerializedName("secondary")
    private PD_hud_color secondary = new PD_hud_color(player,config.hud.secondary.Color,config.hud.secondary.Bold,config.hud.secondary.Italics,config.hud.secondary.Rainbow);

    public PD_hud_module getModule() {
        return module;
    }

    public boolean getModule(HUD.Module module) {
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

    public void setModule(PD_hud_module module) {
        this.module = module;
        player.getPData().save();
    }

    public void setModule(HUD.Module module, boolean state) {
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
        player.getPData().save();
    }

    public PD_hud_setting getSetting() {
        return setting;
    }

    public Object getSetting(HUD.Setting type) {
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

    public void setSetting(HUD.Setting type, Object setting) {
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
        player.getPData().save();
    }

    public void setSetting(PD_hud_setting setting) {
        this.setting = setting;
        player.getPData().save();
    }

    public ArrayList<HUD.Module> getOrder() {
        ArrayList<HUD.Module> types = new ArrayList<>();
        for (String m : order) types.add(Enums.get(m , HUD.Module.class));
        return types;
    }

    public void setOrder(ArrayList<HUD.Module> order) {
        this.order = Enums.toStringList(order);
        player.getPData().save();
    }

    public PD_hud_color getPrimary() {
        return primary;
    }

    public void setPrimary(PD_hud_color primary) {
        this.primary = primary;
        player.getPData().save();
    }

    public PD_hud_color getSecondary() {
        return secondary;
    }

    public void setSecondary(PD_hud_color secondary) {
        this.secondary = secondary;
        player.getPData().save();
    }
}
