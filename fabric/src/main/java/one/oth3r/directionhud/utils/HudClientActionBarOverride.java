package one.oth3r.directionhud.utils;

/**
 * helper for hud actionbar override when the client gets a non-directionhud actionbar <br>
 * when the client gets a bar, the timer starts to count down to when the directionhud bars can show up again
 */
public class HudClientActionBarOverride {
    /**
     * the time in ticks that a custom actionbar should show up for
     */
    private int overrideTickTime;

    private CTxT overrideTxT;
    private int overrideTick;

    public HudClientActionBarOverride(int overrideTickTime) {
        this.overrideTickTime = overrideTickTime;
    }

    public void clear() {
        overrideTxT = null;
        overrideTickTime = 0;
    }

    public void setOverrideTickTime(int overrideTickTime) {
        this.overrideTickTime = overrideTickTime;
    }

    public void tick() {
        if (overrideTick > 0) overrideTick--;
    }

    public void setOverride(CTxT overrideTxT) {
        this.overrideTxT = overrideTxT;
        this.overrideTick = overrideTickTime;
    }

    public CTxT getOverrideTxT() {
        return overrideTxT;
    }

    /**
     * if the HUD can display - (no override)
     */
    public boolean canDisplay() {
        return overrideTick == 0;
    }
}
