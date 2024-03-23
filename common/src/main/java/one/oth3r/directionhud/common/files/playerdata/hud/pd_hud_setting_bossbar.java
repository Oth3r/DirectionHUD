
package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class pd_hud_setting_bossbar {

    @SerializedName("color")
    private String color = config.hud.BarColor;
    @SerializedName("distance")
    private Boolean distance = config.hud.Distance;
    @SerializedName("distance_max")
    private Double distanceMax = (double) config.hud.ShowDistanceMAX;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getDistance() {
        return distance;
    }

    public void setDistance(Boolean distance) {
        this.distance = distance;
    }

    public Double getDistanceMax() {
        return distanceMax;
    }

    public void setDistanceMax(Double distanceMax) {
        this.distanceMax = distanceMax;
    }
}
