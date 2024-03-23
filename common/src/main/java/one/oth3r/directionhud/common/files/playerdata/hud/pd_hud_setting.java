
package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;

public class pd_hud_setting {
    @SerializedName("bossbar")
    private pd_hud_setting_bossbar bossbar = new pd_hud_setting_bossbar();
    @SerializedName("module")
    private pd_hud_setting_module module = new pd_hud_setting_module();
    @SerializedName("state")
    private Boolean state = config.hud.State;
    @SerializedName("type")
    private String type = config.hud.DisplayType;

    public pd_hud_setting_bossbar getBossbar() {
        return bossbar;
    }

    public void setBossbar(pd_hud_setting_bossbar bossbar) {
        this.bossbar = bossbar;
    }

    public pd_hud_setting_module getModule() {
        return module;
    }

    public void setModule(pd_hud_setting_module module) {
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
