package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class PlayerTemplate {
    @Override
    public String toString() {
        return "DirectionHUD Player: "+getName();
    }
    public abstract String getName();
    /**
     * makes a CTxT with the players name in the secondary color
     * @return the highlighted player name
     */
    public CTxT getHighlightedName() {
        return CTxT.of(getName()).color(CUtl.s());
    }
    public abstract String getUUID();
    public abstract String getSpawnDimension();
    public abstract String getDimension();

    /**
     * gets the time of day in ticks, ranging from 0 to 23999
     * @return the world time in ticks
     */
    public abstract int getTimeOfDay();

    /**
     * the storm status of a world
     */
    public abstract boolean hasStorm();

    /**
     * the thunderstorm status of a world
     */
    public abstract boolean hasThunderstorm();

    public abstract float getYaw();
    public abstract float getPitch();
    public abstract ArrayList<Double> getVec();
    public abstract Loc getLoc();
    public abstract int getBlockX();
    public abstract int getBlockY();
    public abstract int getBlockZ();
    public abstract void spawnParticleLine(ArrayList<Double> end, String particleType);
    public abstract void performCommand(String cmd);
    public abstract void sendMessage(CTxT message);
    public abstract void sendActionBar(CTxT message);
    public abstract void displayBossBar(CTxT message);
    public abstract void removeBossBar();
    public abstract PData getPData();
    public void updateHUD() {
        // if toggled off
        if (!(boolean) this.getPData().getHud().getSetting(Hud.Setting.state)) {
            //if actionbar send empty to clear else remove bossbar
            if (this.getPData().getHud().getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString()))
                this.sendActionBar(CTxT.of(""));
            else this.removeBossBar();
        }
        // if actionbar make sure no bossbar
        if (this.getPData().getHud().getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString())) {
            this.removeBossBar();
        }
        // else clear the actionBar
        else this.sendActionBar(CTxT.of(""));
    }
    public abstract void sendPDataPackets();
    public abstract void sendHUDPackets(HashMap<Hud.Module, ArrayList<String>> hudData);
    public void displayHUD(CTxT message) {
        if (message.toString().isEmpty()) {
            //if the HUD is enabled but there is no output, flip the tag
            if (this.getPData().getMsg("hud.enabled_but_off").isBlank()) {
                this.getPData().setMsg("hud.enabled_but_off","true");
                // if actionbar, clear once, if bossbar remove player
                if ((Hud.Setting.DisplayType.get((String) this.getPData().getHud().getSetting(Hud.Setting.type)).equals(Hud.Setting.DisplayType.actionbar))) {
                    sendActionBar(CTxT.of(""));
                } else removeBossBar();
            }
            return;
        } else if (!this.getPData().getMsg("hud.enabled_but_off").isBlank()) {
            // hud isn't blank but the blank tag was still enabled
            this.getPData().clearMsg("hud.enabled_but_off");
        }
        // if actionbar send actionbar, if bossbar update the bar
        if ((Hud.Setting.DisplayType.get((String) this.getPData().getHud().getSetting(Hud.Setting.type)).equals(Hud.Setting.DisplayType.actionbar)))
            sendActionBar(message);
        else displayBossBar(message);
    }
}
