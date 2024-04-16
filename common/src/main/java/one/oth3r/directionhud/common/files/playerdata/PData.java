
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.files.playerdata.destination.PD_destination;
import one.oth3r.directionhud.common.files.playerdata.hud.PD_hud;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PData {
    @SerializedName("version")
    private Double version = 2.0;
    @SerializedName("name")
    private String name;
    @SerializedName("hud")
    private PD_hud hud = new PD_hud();
    @SerializedName("destination")
    private PD_destination destination = new PD_destination();
    @SerializedName("inbox")
    private List<HashMap<String,Object>> inbox = new ArrayList<>();
    @SerializedName("color_presets")
    private List<String> colorPresets = config.colorPresets;
    @SerializedName("social_cooldown")
    private Double socialCooldown;
    private transient Map<String,Object> dataMap = new HashMap<>();
    private transient Player player;

    /**
     * sets pData to be saved and sends the updated pData to the player client if needed
     */
    public void save() {
        LoopManager.addSavePlayer(player);
        player.sendPDataPackets();
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.hud.setPlayer(player);
        this.destination.setPlayer(player);
        save();
    }

    public PData(Player player) {
        this.name = player.getName();
    }

    public PD_hud getHud() {
        return hud;
    }

    public String getName() {
        return name;
    }

    public PD_destination getDEST() {
        return destination;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
        // dont need to save, something else will save
    }

    public ArrayList<HashMap<String,Object>> getInbox() {
        return (ArrayList<HashMap<String, Object>>) inbox;
    }

    public void setInbox(List<HashMap<String,Object>> inbox) {
        this.inbox = inbox;
        save();
    }

    public ArrayList<String> getColorPresets() {
        return (ArrayList<String>) colorPresets;
    }

    public void setColorPresets(List<String> colorPresets) {
        this.colorPresets = colorPresets;
        save();
    }

    public Double getSocialCooldown() {
        return socialCooldown;
    }

    public void setSocialCooldown(Double socialCooldown) {
        this.socialCooldown = socialCooldown;
        save();
    }

    public Map<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
        // no saving for local
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
