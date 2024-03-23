
package one.oth3r.directionhud.common.files.playerdata.destination;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class pd_destination_setting_features {

    @SerializedName("track_request_mode")
    private String trackRequestMode = config.dest.TrackingRequestMode;
    @SerializedName("track")
    private Boolean track = config.dest.Track;
    @SerializedName("send")
    private Boolean send = config.dest.Send;
    @SerializedName("lastdeath")
    private Boolean lastdeath = config.dest.Lastdeath;

    public String getTrackRequestMode() {
        return trackRequestMode;
    }

    public void setTrackRequestMode(String trackRequestMode) {
        this.trackRequestMode = trackRequestMode;
    }

    public Boolean getTrack() {
        return track;
    }

    public void setTrack(Boolean track) {
        this.track = track;
    }

    public Boolean getSend() {
        return send;
    }

    public void setSend(Boolean send) {
        this.send = send;
    }

    public Boolean getLastdeath() {
        return lastdeath;
    }

    public void setLastdeath(Boolean lastdeath) {
        this.lastdeath = lastdeath;
    }

}
