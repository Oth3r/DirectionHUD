
package one.oth3r.directionhud.common.files.playerdata;

import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PData extends DefaultPData {

    private transient Map<String,Object> dataMap = new HashMap<>();

    public PData(Player player) {
        super(player);
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
