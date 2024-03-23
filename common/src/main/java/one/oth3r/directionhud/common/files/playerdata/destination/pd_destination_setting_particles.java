
package one.oth3r.directionhud.common.files.playerdata.destination;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class pd_destination_setting_particles {
    @SerializedName("dest")
    private Boolean dest = config.dest.particles.Dest;
    @SerializedName("dest_color")
    private String destColor = config.dest.particles.DestColor;
    @SerializedName("line")
    private Boolean line = config.dest.particles.Line;
    @SerializedName("line_color")
    private String lineColor = config.dest.particles.LineColor;
    @SerializedName("tracking")
    private Boolean tracking = config.dest.particles.Tracking;
    @SerializedName("tracking_color")
    private String trackingColor = config.dest.particles.TrackingColor;

    public Boolean getLine() {
        return line;
    }

    public void setLine(Boolean line) {
        this.line = line;
    }

    public String getTrackingColor() {
        return trackingColor;
    }

    public void setTrackingColor(String trackingColor) {
        this.trackingColor = trackingColor;
    }

    public String getDestColor() {
        return destColor;
    }

    public void setDestColor(String destColor) {
        this.destColor = destColor;
    }

    public Boolean getDest() {
        return dest;
    }

    public void setDest(Boolean dest) {
        this.dest = dest;
    }

    public Boolean getTracking() {
        return tracking;
    }

    public void setTracking(Boolean tracking) {
        this.tracking = tracking;
    }

    public String getLineColor() {
        return lineColor;
    }

    public void setLineColor(String lineColor) {
        this.lineColor = lineColor;
    }

}
