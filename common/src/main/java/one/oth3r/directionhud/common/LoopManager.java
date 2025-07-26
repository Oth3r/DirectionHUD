package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.hud.module.ModuleInstructions;
import one.oth3r.directionhud.common.hud.module.modules.ModuleSpeed;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.common.utils.ParticleType;
import one.oth3r.directionhud.common.utils.Vec;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

public class LoopManager {

    public static int rainbowF;
    private static int secondTick;
    private static int HUDTick;
    private static int ParticleTick;

    public static void tickRainbow() {
        rainbowF += 10;
        // reset the rainbow at 360
        if (rainbowF >= 360) rainbowF = 0;
    }

    public static void tick() {
        // tick the counters
        secondTick++;
        tickRainbow();

        HUDTick++;
        ParticleTick++;

        if (HUDTick >= FileData.getConfig().getHud().getLoop()) {
            HUDTick = 0;
            for (Player player : Utl.getPlayers()) {
                HUDTickLogic(player);
            }
        }

        if (ParticleTick >= FileData.getConfig().getDestination().getLoop()) {
            ParticleTick = 0;
            for (Player player :Utl.getPlayers()) particles(player);
        }



        // tick every 2
        if (secondTick % 2 == 0) {
            for (Player player : Utl.getPlayers()) {
                speedUpdate(player);
            }
        }
        
        // every 20 ticks
        if (secondTick >= 20) {
            secondTick = 0;
            for (Player player :Utl.getPlayers()) secondLoop(player);
            // tick the playerdata queue
            PlayerData.Queue.tick();
        }
    }

    /**
     * updates the player speed by seeing how far the player went since the last check
     */
    private static void speedUpdate(Player player) {
        /*
        still having consistency issues - FABRIC TESTING ONLY it seems like the player's location isnt being properly updated every tick
        - leading to the player speed jumping around... making the check faster makes the issue worse, and slowing down the checks lessons the effect
        BUT the display looks laggier. averaging out the speed makes the display not immedatly update, so stopping still displays 0.23123 ect... still stuck on this :P
        10/12/24
        */

        // only update the speed if the module and hud is on AND the speed module is enabled
        if (FileData.getConfig().getHud().getEnabledModules().get(Module.SPEED) &&
                (boolean) player.getPCache().getHud().getSetting(Hud.Setting.state) && player.getPCache().getHud().getModule(Module.SPEED).isEnabled()) {
            CachedPData.SpeedData speedData = player.getPCache().getSpeedData();

            Vec pos = player.getVec(), oldPos = speedData.getVec();
            long worldTime = player.getWorldTime(), oldWorldTime = speedData.getWorldTime();

            // replace with players current speed
            speedData.setVec(pos);
            speedData.setWorldTime(player.getWorldTime());

            // only do x and y if 3d is off
            if (player.getPCache().getHud().getModule(Module.SPEED).getSettingValue(ModuleSpeed.calculation2DID)) {
                pos = new Vec(pos.getX(),0,pos.getZ());
                oldPos = new Vec(oldPos.getX(),0,oldPos.getZ());
            }

            // update the speed
            player.getPCache().getSpeedData().setSpeed((pos.distanceTo(oldPos) / (worldTime-oldWorldTime))*20);
        }
    }

    private static void HUDTickLogic(Player player) {
        // if the HUD is enabled
        if ((boolean) player.getPCache().getHud().getSetting(Hud.Setting.state)) {
            ModuleInstructions instructions = Hud.build.getModuleInstructions(player);
            // if the client has directionhud and the hud type is the actionBar, send as a packet
          if (DirectionHUD.getData().getClientPlayers().contains(player) &&
                    Helper.Enums.get(player.getPCache().getHud().getSetting(Hud.Setting.type), Hud.Setting.DisplayType.class).equals(Hud.Setting.DisplayType.actionbar))
                player.sendHUDPackets(instructions);
            // if not do a normal display
            else
                player.displayHUD(Hud.build.compile(player,instructions));
        }
        // if player has a DEST, AutoClear is on, and the distance is in the AutoClear range, clear
        if (Destination.dest.get(player).hasXYZ() && player.getPCache().getDEST().getDestSettings().getAutoclear() &&
                Destination.dest.getDist(player) <= player.getPCache().getDEST().getDestSettings().getAutoclearRad()) {
            Destination.dest.clear(player, 2);
        }
    }

    private static void particles(Player player) {
        // spawn all the particles
        if (Destination.dest.get(player).hasXYZ()) {
            /// destination particles
            if (player.getPCache().getDEST().getDestSettings().getParticles().getDest()) {
                // make a vertical line at the destination, marking it
                Vec destinationVec = Destination.dest.get(player).getVec(player);
                // spawn the line of particles
                player.spawnParticleLine(destinationVec.add(0,2,0),destinationVec.add(0,-2,0), ParticleType.DEST);
            }

            /// line particles
            if (player.getPCache().getDEST().getDestSettings().getParticles().getLine()) {
                // particle line from the player to the destination
                player.spawnParticleLine(Destination.dest.get(player).getVec(player),ParticleType.LINE);
            }
        }

        /// tracking particles
        // if tracking particles are enabled
        if (player.getPCache().getDEST().getDestSettings().getParticles().getTracking()) {
            // make sure there's a target
            Player target = Destination.social.track.getTarget(player);
            if (target.isValid()) {
                boolean sendParticles = true;
                Vec targetVec = target.getVec();
                if (!target.getDimension().equals(player.getDimension())) {
                    sendParticles = false;
                    // if convertible and autoconvert is enabled, send the particles
                    if (Dimension.canConvert(player.getDimension(), target.getDimension()) &&
                            player.getPCache().getDEST().getDestSettings().getAutoconvert()) {
                        sendParticles = true;
                        // update the vec to the converted loc
                        Loc targetLoc = target.getLoc();
                        targetLoc.convertTo(player.getDimension());
                        targetVec = targetLoc.getVec(player);

                        // add one to the y to point towards the body of the target player
                        targetVec = targetVec.add(0,1,0);
                    }
                }
                // actually send the particles
                if (sendParticles) player.spawnParticleLine(targetVec,ParticleType.TRACKING);
            }
        }
    }
    private static void secondLoop(Player player) {
        DHud.inbox.tick(player);
        // count down the social cooldown
        Integer timer = player.getPCache().getSocialCooldown();
        if (timer != null) {
            player.getPCache().setSocialCooldown(timer-1);
            if (timer<=1) player.getPCache().setSocialCooldown(null);
        }
        // tracker message logic
        Destination.social.track.logic(player);
    }
}