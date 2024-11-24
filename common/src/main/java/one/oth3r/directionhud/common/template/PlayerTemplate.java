package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.common.utils.ParticleType;
import one.oth3r.directionhud.common.utils.Vec;
import one.oth3r.directionhud.utils.CTxT;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class PlayerTemplate {
    @Override
    public String toString() {
        return "DirectionHUD Player: "+getName();
    }
    public abstract boolean isValid();
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
    public abstract long getWorldTime();

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
    public abstract Vec getVec();
    public abstract Loc getLoc();
    public abstract int getBlockX();
    public abstract int getBlockY();
    public abstract int getBlockZ();
    public abstract void performCommand(String cmd);
    public abstract void sendMessage(CTxT message);
    public abstract void sendActionBar(CTxT message);
    public abstract void displayBossBar(CTxT message);
    public abstract void removeBossBar();

    public abstract PData getPData();
    public abstract CachedPData getPCache();

    public void updateHUD() {
        // if toggled off
        if (!(boolean) this.getPCache().getHud().getSetting(Hud.Setting.state)) {
            //if actionbar send empty to clear else remove bossbar
            if (this.getPCache().getHud().getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString()))
                this.sendActionBar(CTxT.of(""));
            else this.removeBossBar();
        }
        // if actionbar make sure no bossbar
        if (this.getPCache().getHud().getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString())) {
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
            if (this.getPCache().getMsg("hud.enabled_but_off") == 0) {
                this.getPCache().setMsg("hud.enabled_but_off", 1);
                // if actionbar, clear once, if bossbar remove player
                if ((Hud.Setting.DisplayType.get((String) this.getPCache().getHud().getSetting(Hud.Setting.type)).equals(Hud.Setting.DisplayType.actionbar))) {
                    sendActionBar(CTxT.of(""));
                } else removeBossBar();
            }
            return;
        } else if (this.getPCache().getMsg("hud.enabled_but_off") != 0) {
            // hud isn't blank but the blank tag was still enabled
            this.getPCache().setMsg("hud.enabled_but_off", 0);
        }
        // if actionbar send actionbar, if bossbar update the bar
        if ((Hud.Setting.DisplayType.get((String) this.getPCache().getHud().getSetting(Hud.Setting.type)).equals(Hud.Setting.DisplayType.actionbar)))
            sendActionBar(message);
        else displayBossBar(message);
    }

    /// PARTICLES

    /**
     * spawns a particle at the given location
     * @param particleType the type of particle
     * @param position the position
     */
    public abstract void spawnParticle(ParticleType particleType, Vec position);

    /**
     * spawns a line of particles, starting at the player pos
     * @param end the end of the line
     * @param particleType the particle type
     */
    public void spawnParticleLine(Vec end, ParticleType particleType) {
        spawnParticleLine(this.getVec(), end, particleType);
    }

    /**
     * spawns a line of particles for the player
     * @param start start of the line
     * @param end end of the line
     * @param particleType the type of particle
     */
    public void spawnParticleLine(Vec start, Vec end, ParticleType particleType) {
        Vec playerVec = this.getVec();

        double distance = start.distanceTo(end);
        Vec particlePos = start.add(0, -0.2, 0);

        // the spacing between 2 particle points
        double spacing = 1;

        // the vec that is added to the last pos to complete the line
        Vec segment = end.subtract(start).normalize().multiply(spacing,spacing,spacing);

        double distCovered = 0;
        // loop through the distance between the 2 lines
        for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
            distCovered += spacing;
            // don't spawn if player is further than 50 blocks
            if (playerVec.distanceTo(particlePos) >= 50) break;
            // keep trying if the player is too close
            if (playerVec.distanceTo(particlePos) < 0.5) continue;
            // spawn the particle
            this.spawnParticle(particleType, particlePos);
        }
    }
}
