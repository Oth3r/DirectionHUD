
package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class PD_hud_module {

    @SerializedName("distance")
    private Boolean distance = config.hud.Distance;
    @SerializedName("coordinates")
    private Boolean coordinates = config.hud.Coordinates;
    @SerializedName("destination")
    private Boolean destination = config.hud.Destination;
    @SerializedName("weather")
    private Boolean weather = config.hud.Weather;
    @SerializedName("angle")
    private Boolean angle = config.hud.Angle;
    @SerializedName("time")
    private Boolean time = config.hud.Time;
    @SerializedName("tracking")
    private Boolean tracking = config.hud.Tracking;
    @SerializedName("speed")
    private Boolean speed = config.hud.Speed;
    @SerializedName("direction")
    private Boolean direction = config.hud.Direction;

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
