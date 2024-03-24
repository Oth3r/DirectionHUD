
package one.oth3r.directionhud.common.files.playerdata.destination;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.utils.Loc;

import java.util.ArrayList;
import java.util.List;

public class pd_destination {

    @SerializedName("saved")
    private List<Loc> saved = new ArrayList<>();
    @SerializedName("dest")
    private Loc dest = new Loc();
    @SerializedName("tracking")
    private String tracking = null;
    @SerializedName("lastdeath")
    private List<Loc> lastdeath = new ArrayList<>();
    @SerializedName("setting")
    private pd_destination_setting setting = new pd_destination_setting();

    public List<Loc> getSaved() {
        return new ArrayList<>(saved);
    }

    public void setSaved(List<Loc> saved) {
        this.saved = saved;
    }

    public List<Loc> getLastdeath() {
        return new ArrayList<>(lastdeath);
    }

    public void setLastdeath(List<Loc> lastdeath) {
        this.lastdeath = lastdeath;
    }

    public Loc getDest() {
        return dest;
    }

    public void setDest(Loc dest) {
        this.dest = dest;
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
    }

    public pd_destination_setting getSetting() {
        return setting;
    }

    public Object getSetting(Destination.Setting setting) {
        return switch (setting) {
            case ylevel -> getSetting().getYlevel();
            case autoclear -> getSetting().getAutoclear();
            case autoclear_rad -> getSetting().getAutoclearRad();
            case autoconvert -> getSetting().getAutoconvert();
            case features__send -> getSetting().getFeatures().getSend();
            case features__track -> getSetting().getFeatures().getTrack();
            case features__track_request_mode -> getSetting().getFeatures().getTrackRequestMode();
            case features__lastdeath -> getSetting().getFeatures().getLastdeath();
            case particles__dest -> getSetting().getParticles().getDest();
            case particles__dest_color -> getSetting().getParticles().getDestColor();
            case particles__line -> getSetting().getParticles().getLine();
            case particles__line_color -> getSetting().getParticles().getLineColor();
            case particles__tracking -> getSetting().getParticles().getTracking();
            case particles__tracking_color -> getSetting().getParticles().getTrackingColor();
            default -> null;
        };
    }

    public void setSetting(Destination.Setting setting, Object state) {
        switch (setting) {
            case ylevel -> getSetting().setYlevel((Boolean) state);
            case autoclear -> getSetting().setAutoclear((Boolean) state);
            case autoclear_rad -> getSetting().setAutoclearRad((int) state);
            case autoconvert -> getSetting().setAutoconvert((Boolean) state);
            case features__send -> getSetting().getFeatures().setSend((Boolean) state);
            case features__track -> getSetting().getFeatures().setTrack((Boolean) state);
            case features__track_request_mode -> getSetting().getFeatures().setTrackRequestMode(state.toString());
            case features__lastdeath -> getSetting().getFeatures().setLastdeath((Boolean) state);
            case particles__dest -> getSetting().getParticles().setDest((Boolean) state);
            case particles__dest_color -> getSetting().getParticles().setDestColor(state.toString());
            case particles__line -> getSetting().getParticles().setLine((Boolean) state);
            case particles__line_color -> getSetting().getParticles().setLineColor(state.toString());
            case particles__tracking -> getSetting().getParticles().setTracking((Boolean) state);
            case particles__tracking_color -> getSetting().getParticles().setTrackingColor(state.toString());
        }
    }

    public void setSetting(pd_destination_setting setting) {
        this.setting = setting;
    }
}
