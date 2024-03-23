
package one.oth3r.directionhud.common.files.playerdata.destination;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class pd_destination_setting {

    @SerializedName("ylevel")
    private Boolean ylevel = config.dest.YLevel;
    @SerializedName("autoclear")
    private Boolean autoclear = config.dest.AutoClear;
    @SerializedName("autoclear_rad")
    private Double autoclearRad = (double) config.dest.AutoClearRad;
    @SerializedName("autoconvert")
    private Boolean autoconvert = config.dest.AutoConvert;
    @SerializedName("features")
    private pd_destination_setting_features features = new pd_destination_setting_features();
    @SerializedName("particles")
    private pd_destination_setting_particles particles = new pd_destination_setting_particles();

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

    public pd_destination_setting_features getFeatures() {
        return features;
    }

    public void setFeatures(pd_destination_setting_features features) {
        this.features = features;
    }

    public Boolean getAutoclear() {
        return autoclear;
    }

    public void setAutoclear(Boolean autoclear) {
        this.autoclear = autoclear;
    }

    public Double getAutoclearRad() {
        return autoclearRad;
    }

    public void setAutoclearRad(Double autoclearRad) {
        this.autoclearRad = autoclearRad;
    }

    public pd_destination_setting_particles getParticles() {
        return particles;
    }

    public void setParticles(pd_destination_setting_particles particles) {
        this.particles = particles;
    }

}
