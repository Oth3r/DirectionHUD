package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.hud.module.ModuleInstructions;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.common.utils.ParticleType;
import one.oth3r.directionhud.common.utils.Vec;
import one.oth3r.directionhud.utils.CTxT;

public abstract class PlayerTemplate {
    @Override
    public String toString() {
        return "DirectionHUD Player: "+getName();
    }

    /**
     * checks if a player is valid to use other methods on.
     */
    public abstract boolean isValid();

    /**
     * gets the player's name as a plaintext string
     */
    public abstract String getName();

    /**
     * makes a CTxT with the player's name in the secondary color
     * @return the highlighted player name
     */
    public CTxT getHighlightedName() {
        return new CTxT(getName()).color(CUtl.s());
    }

    /**
     * gets the player's UUID
     */
    public abstract String getUUID();

    /**
     * gets the player's spawn dimension (if possible, overworld if not)
     */
    public abstract String getSpawnDimension();

    /**
     * gets the player's current dimension
     */
    public abstract String getDimension();

    /**
     * gets the time of day in ticks, ranging from 0 to 23999
     * @return the world time in ticks
     */
    public abstract int getTimeOfDay();

    /**
     * gets the world time as a long
     */
    public abstract long getWorldTime();

    /**
     * the storm status of the player's world
     */
    public abstract boolean hasStorm();

    /**
     * the thunderstorm status of the player's world
     */
    public abstract boolean hasThunderstorm();

    /**
     * gets the light level.
     * @param lookTarget if enabled, it will get the light level of the next closest target after the player's target-look block
     * @return an int array, 2 in length, first entry for the skylight, second entry for the block light. -1 is returned if not found
     */
    public abstract int[] getLightLevels(boolean lookTarget);

    /// Location Methods
    public abstract Vec getVec();
    public abstract Loc getLoc();

    /// Player Display Methods
    public abstract void performCommand(String cmd);
    public abstract void sendMessage(CTxT message);
    public abstract void sendActionBar(CTxT message);
    public abstract void displayBossBar(CTxT message);
    public abstract void removeBossBar();

    /// PlayerData Methods
    /**
     * gets the player's data from file
     * @return retrieves the player data file
     */
    public abstract PData getPData();

    /**
     * gets the player data from cache
     * @return a smaller, cached version of {@link PData}
     */
    public abstract CachedPData getPCache();

    public void updateHUD() {
        // if toggled off
        if (!(boolean) this.getPCache().getHud().getSetting(Hud.Setting.state)) {
            //if actionbar send empty to clear else remove bossbar
            if (this.getPCache().getHud().getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString()))
                this.sendActionBar(new CTxT());
            else this.removeBossBar();
        }
        // if actionbar make sure no bossbar
        if (this.getPCache().getHud().getSetting(Hud.Setting.type).equals(Hud.Setting.DisplayType.actionbar.toString())) {
            this.removeBossBar();
        }
        // else clear the actionBar
        else this.sendActionBar(new CTxT());
    }

    /// Packet Methods
    public abstract void sendPDataPackets();
    public abstract void sendHUDPackets(ModuleInstructions instructions);

    public void displayHUD(CTxT message) {
        if (message.toString().isEmpty()) {
            //if the HUD is enabled but there is no output, flip the tag
            if (this.getPCache().getMsg("hud.enabled_but_off") == 0) {
                this.getPCache().setMsg("hud.enabled_but_off", 1);
                // if actionbar, clear once, if bossbar remove player
                if ((Hud.Setting.DisplayType.get((String) this.getPCache().getHud().getSetting(Hud.Setting.type)).equals(Hud.Setting.DisplayType.actionbar))) {
                    sendActionBar(new CTxT());
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

    /// ROTATION
    /**
     * gets the player's yaw
     */
    public abstract float getYaw();

    /**
     * gets the player's pitch
     */
    public abstract float getPitch();

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
