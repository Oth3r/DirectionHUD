
package one.oth3r.directionhud.common.files.playerdata.destination;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class PD_destination_setting {

    @SerializedName("ylevel")
    private Boolean ylevel = config.dest.YLevel;
    @SerializedName("autoclear")
    private Boolean autoclear = config.dest.AutoClear;
    @SerializedName("autoclear_rad")
    private Integer autoclearRad = config.dest.AutoClearRad;
    @SerializedName("autoconvert")
    private Boolean autoconvert = config.dest.AutoConvert;
    @SerializedName("features")
    private PD_destination_setting_features features = new PD_destination_setting_features();
    @SerializedName("particles")
    private PD_destination_setting_particles particles = new PD_destination_setting_particles();

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

    public PD_destination_setting_features getFeatures() {
        return features;
    }

    public void setFeatures(PD_destination_setting_features features) {
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

    public PD_destination_setting_particles getParticles() {
        return particles;
    }

    public void setParticles(PD_destination_setting_particles particles) {
        this.particles = particles;
    }

}
