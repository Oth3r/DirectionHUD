package one.oth3r.directionhud.common.files.playerdata;

import one.oth3r.directionhud.common.hud.HudColor;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.hud.HudRainbow;
import one.oth3r.directionhud.common.utils.Vec;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.otterlib.chat.Rainbow;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * cached PData
 * <p>
 * player data that needs to be accessed frequently
 * <p>
 * currently everything in HUD and Destination settings
 * <p>
 * inbox and social cooldown are also in the cache, and can be written to
 * <p>
 * when saving pData the writing data gets merged
 */
public class CachedPData {
    // READ ONLY
    private PDHud hud;
    private CachedDestination destination;
    private final SpeedData speedData;
    private final HashMap<String,Integer> msgMap = new HashMap<>();
    private final HudRainbow hudRainbow;
    // READ AND WRITE
    private Integer socialCooldown;
    private ArrayList<HashMap<String,String>> inbox;

    public CachedPData(PData pData) {
        this.hud = new PDHud(pData.getHud());
        this.destination = new CachedDestination(pData.getDEST());
        this.speedData = new SpeedData(pData.getPlayer());
        this.hudRainbow = new HudRainbow(pData.getPlayer());
        this.inbox = pData.getInbox();
        this.socialCooldown = pData.getSocialCooldown();
    }

    /**
     * update the cache with new data
     * @param pData updated data
     */
    public void update(BasePData pData) {
        this.hud = new PDHud(pData.getHud());
        this.destination = new CachedDestination(pData.getDEST());
    }

    public PDHud getHud() {
        return hud;
    }

    public CachedDestination getDEST() {
        return destination;
    }

    public SpeedData getSpeedData() {
        return speedData;
    }

    /**
     * get rainbow based on hud color type
     * @return rainbow for the hud color type
     */
    public Rainbow getRainbow(HudColor color) {
        return this.hudRainbow.select(color);
    }

    public ArrayList<String> getMsgKeys() {
        return new ArrayList<>(msgMap.keySet());
    }

    public int getMsg(String key) {
        return msgMap.getOrDefault(key, 0);
    }

    public void setMsg(String key, int value) {
        msgMap.put(key,value);
    }

    public ArrayList<HashMap<String, String>> getInbox() {
        return inbox;
    }

    public void setInbox(ArrayList<HashMap<String, String>> inbox) {
        this.inbox = inbox;
    }

    public Integer getSocialCooldown() {
        return socialCooldown;
    }

    public void setSocialCooldown(Integer socialCooldown) {
        this.socialCooldown = socialCooldown;
    }

    public static class SpeedData {
        // all data since last update
        private Vec vec;
        private Long worldTime;
        private Double speed = 0.0;

        public SpeedData(Player player) {
            vec = player.getVec();
            worldTime = player.getWorldTime();
        }

        public Vec getVec() {
            return vec;
        }

        public void setVec(Vec vec) {
            this.vec = vec;
        }

        public Long getWorldTime() {
            return worldTime;
        }

        public void setWorldTime(Long worldTime) {
            this.worldTime = worldTime;
        }

        public Double getSpeed() {
            return speed;
        }

        public void setSpeed(Double speed) {
            this.speed = speed;
        }
    }

    public static class CachedDestination {
        private final Dest destination;
        private final PDDestination.Settings destSettings;
        private final String tracking;

        public CachedDestination(PDDestination destination) {
            this.destination = new Dest(destination.getDest());
            this.destSettings = new PDDestination.Settings(destination.getSetting());
            this.tracking = destination.getTracking();
        }

        public Dest getDestination() {
            return destination;
        }

        public PDDestination.Settings getDestSettings() {
            return destSettings;
        }

        public String getTracking() {
            return tracking;
        }
    }
}
