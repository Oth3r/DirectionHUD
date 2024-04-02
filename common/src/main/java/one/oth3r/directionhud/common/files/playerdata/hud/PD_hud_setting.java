
package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class PD_hud_setting {
    @SerializedName("bossbar")
    private PD_hud_setting_bossbar bossbar = new PD_hud_setting_bossbar();
    @SerializedName("module")
    private PD_hud_setting_module module = new PD_hud_setting_module();
    @SerializedName("state")
    private Boolean state = config.hud.State;
    @SerializedName("type")
    private String type = config.hud.DisplayType;

    public PD_hud_setting_bossbar getBossbar() {
        return bossbar;
    }

    public void setBossbar(PD_hud_setting_bossbar bossbar) {
        this.bossbar = bossbar;
    }

    public PD_hud_setting_module getModule() {
        return module;
    }

    public void setModule(PD_hud_setting_module module) {
        this.module = module;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
