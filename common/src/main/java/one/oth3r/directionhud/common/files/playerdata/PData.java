
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.files.playerdata.destination.pd_destination;
import one.oth3r.directionhud.common.files.playerdata.hud.pd_hud;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PData {
    @SerializedName("version")
    private Double version = 2.0;
    @SerializedName("hud")
    private pd_hud hud = new pd_hud();
    @SerializedName("name")
    private String name;
    @SerializedName("destination")
    private pd_destination destination = new pd_destination();
    @SerializedName("inbox")
    private List<HashMap<String,Object>> inbox = new ArrayList<>();
    @SerializedName("color_presets")
    private List<String> colorPresets = config.colorPresets;
    @SerializedName("social_cooldown")
    private Double socialCooldown;
    private transient Map<String,Object> dataMap = new HashMap<>();

    public PData(Player player) {
        this.name = player.getName();
    }

    public pd_hud getHud() {
        return hud;
    }

    public void setHud(pd_hud hud) {
        this.hud = hud;
    }

    public String getName() {
        return name;
    }

    public void setName(Player player) {
        this.name = player.getName();
    }

    public pd_destination getDEST() {
        return destination;
    }

    public void setDestination(pd_destination destination) {
        this.destination = destination;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public ArrayList<HashMap<String,Object>> getInbox() {
        return (ArrayList<HashMap<String, Object>>) inbox;
    }

    public void setInbox(List<HashMap<String,Object>> inbox) {
        this.inbox = inbox;
    }

    public ArrayList<String> getColorPresets() {
        return (ArrayList<String>) colorPresets;
    }

    public void setColorPresets(List<String> colorPresets) {
        this.colorPresets = colorPresets;
    }

    public Double getSocialCooldown() {
        return socialCooldown;
    }

    public void setSocialCooldown(Double socialCooldown) {
        this.socialCooldown = socialCooldown;
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public ArrayList<String> getMsgKeys() {
        ArrayList<String> keys = new ArrayList<>();
        for (String s: getDataMap().keySet())
            if (s.startsWith("msg.")) keys.add(s);
        return keys;
    }
    public String getMsg(String key) {
        // casting to string gets rid of nulls, so if null return empty string
        String value = String.valueOf(getDataMap().get("msg."+key));
        return value.equals("null")?"":value;
    }
    public void setMsg(String key, String value) {
        dataMap.put("msg."+key,value);
    }
    public void clearMsg(String key) {
        dataMap.remove("msg."+key);
    }
}
