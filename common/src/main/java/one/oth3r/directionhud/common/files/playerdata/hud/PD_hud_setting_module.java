
package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class PD_hud_setting_module {

    @SerializedName("speed_3d")
    private Boolean speed3d = config.hud.Speed3D;
    @SerializedName("tracking_type")
    private String trackingType = config.hud.TrackingType;
    @SerializedName("tracking_target")
    private String trackingTarget = config.hud.TrackingTarget;
    @SerializedName("speed_pattern")
    private String speedPattern = config.hud.SpeedPattern;
    @SerializedName("angle_display")
    private String angleDisplay = config.hud.AngleDisplay;
    @SerializedName("tracking_hybrid")
    private Boolean trackingHybrid = config.hud.TrackingHybrid;
    @SerializedName("time_24hr")
    private Boolean time24hr = config.hud.Time24HR;

    public Boolean getSpeed3d() {
        return speed3d;
    }

    public void setSpeed3d(Boolean speed3d) {
        this.speed3d = speed3d;
    }

    public String getTrackingType() {
        return trackingType;
    }

    public void setTrackingType(String trackingType) {
        this.trackingType = trackingType;
    }

    public String getTrackingTarget() {
        return trackingTarget;
    }

    public void setTrackingTarget(String trackingTarget) {
        this.trackingTarget = trackingTarget;
    }

    public String getSpeedPattern() {
        return speedPattern;
    }

    public void setSpeedPattern(String speedPattern) {
        this.speedPattern = speedPattern;
    }

    public String getAngleDisplay() {
        return angleDisplay;
    }

    public void setAngleDisplay(String angleDisplay) {
        this.angleDisplay = angleDisplay;
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
