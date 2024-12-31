
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.List;

public class PDDestination {

    public void setPlayer(Player player) {
        this.player = player;
    }
    
    private void save() {
        if (player == null) return;
        player.getPData().queueSave();
    }
    
    private transient Player player;

    @SerializedName("saved")
    private ArrayList<Dest> saved = new ArrayList<>();
    @SerializedName("dest")
    private Dest dest = new Dest();
    @SerializedName("tracking")
    private String tracking = null;
    @SerializedName("lastdeath")
    private List<Loc> lastdeath = new ArrayList<>();
    @SerializedName("setting")
    private Settings setting = new Settings();

    public PDDestination() {}

    public PDDestination(ArrayList<Dest> saved, Dest dest, String tracking, List<Loc> lastdeath, Settings setting) {
        this.saved = saved;
        this.dest = dest;
        this.tracking = tracking;
        this.lastdeath = lastdeath;
        this.setting = setting;
    }

    public ArrayList<Dest> getSaved() {
        return new ArrayList<>(saved);
    }

    public void setSaved(ArrayList<Dest> saved) {
        this.saved = saved;
        save();
    }

    public List<Loc> getLastdeath() {
        return new ArrayList<>(lastdeath);
    }

    public void setLastdeath(List<Loc> lastdeath) {
        this.lastdeath = lastdeath;
        save();
    }

    public Dest getDest() {
        return dest;
    }

    public void setDest(Dest dest) {
        this.dest = dest;
        save();
    }

    public String getTracking() {
        return tracking;
    }

    public void setTracking(String tracking) {
        this.tracking = tracking;
        save();
    }

    public Settings getSetting() {
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
        save();
    }

    public void setSetting(Settings setting) {
        this.setting = setting;
        save();
    }

    public static class Settings {

        @SerializedName("ylevel")
        private Boolean ylevel = false;
        @SerializedName("autoclear")
        private Boolean autoclear = true;
        @SerializedName("autoclear_rad")
        private Integer autoclearRad = 2;
        @SerializedName("autoconvert")
        private Boolean autoconvert = false;
        @SerializedName("features")
        private Features features = new Features();
        @SerializedName("particles")
        private Particles particles = new Particles();

        public Settings() {}

        public Settings(Settings settings) {
            this.ylevel = settings.ylevel;
            this.autoclear = settings.autoclear;
            this.autoclearRad = settings.autoclearRad;
            this.autoconvert = settings.autoconvert;
            this.features = new Features(settings.features);
            this.particles = new Particles(settings.particles);
        }

        public Boolean getYlevel() {
            return ylevel;
        }

        public void setYlevel(Boolean ylevel) {
            this.ylevel = ylevel;
        }

        public Boolean getAutoconvert() {
            return autoconvert;
        }

        public void setAutoconvert(Boolean autoconvert) {
            this.autoconvert = autoconvert;
        }

        public Features getFeatures() {
            return features;
        }

        public void setFeatures(Features features) {
            this.features = features;
        }

        public Boolean getAutoclear() {
            return autoclear;
        }

        public void setAutoclear(Boolean autoclear) {
            this.autoclear = autoclear;
        }

        public Integer getAutoclearRad() {
            return autoclearRad;
        }

        public void setAutoclearRad(Integer autoclearRad) {
            this.autoclearRad = autoclearRad;
        }

        public Particles getParticles() {
            return particles;
        }

        public void setParticles(Particles particles) {
            this.particles = particles;
        }

        public static class Features {

            @SerializedName("track")
            private Boolean track = true;
            @SerializedName("track_request_mode")
            private Destination.Setting.TrackingRequestMode trackRequestMode = Destination.Setting.TrackingRequestMode.request;
            @SerializedName("send")
            private Boolean send = true;
            @SerializedName("lastdeath")
            private Boolean lastdeath = true;

            public Features() {}

            public Features(Features features) {
                this.trackRequestMode = features.trackRequestMode;
                this.track = features.track;
                this.send = features.send;
                this.lastdeath = features.lastdeath;
            }

            public String getTrackRequestMode() {
                return trackRequestMode.toString();
            }

            public void setTrackRequestMode(String trackRequestMode) {
                this.trackRequestMode = Helper.Enums.get(trackRequestMode,Destination.Setting.TrackingRequestMode.class);
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

        public static class Particles {

            @SerializedName("dest")
            private Boolean dest = true;
            @SerializedName("dest_color")
            private String destColor = DirectionHUD.PRIMARY;
            @SerializedName("line")
            private Boolean line = true;
            @SerializedName("line_color")
            private String lineColor = DirectionHUD.SECONDARY;
            @SerializedName("tracking")
            private Boolean tracking = true;
            @SerializedName("tracking_color")
            private String trackingColor = Assets.mainColors.track;

            public Particles() {}

            public Particles(Particles particles) {
//              note to Pookie
//              You will never see this but mocha update soon
                this.dest = particles.dest;
                this.destColor = particles.destColor;
                this.line = particles.line;
                this.lineColor = particles.lineColor;
                this.tracking = particles.tracking;
                this.trackingColor = particles.trackingColor;
            }

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
    }
}
