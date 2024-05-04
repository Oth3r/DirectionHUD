package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.Data;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.utils.Helper.Enums;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;
import java.util.HashMap;

public class LoopManager {

    public static int rainbowF;
    private static int secondTick;
    private static int HUDTick;
    private static int ParticleTick;

    public static void tick() {
        // tick the counters
        secondTick++;
        rainbowF += 10;
        HUDTick++;
        ParticleTick++;
        if (HUDTick >= Data.getConfig().getHud().getLoop()) {
            HUDTick = 0;
            for (Player player : Utl.getPlayers()) {
                HUDTickLogic(player);
            }
        }
        if (ParticleTick >= Data.getConfig().getDestination().getLoop()) {
            ParticleTick = 0;
            for (Player player :Utl.getPlayers()) particles(player);
        }
        // reset the rainbow at 360
        if (rainbowF >= 360) rainbowF = 0;
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
    
    private static void speedUpdate(Player player) {
        // only update the speed if the module and hud is on
        if ((boolean) player.getPCache().getHud().getSetting(Hud.Setting.state) && player.getPCache().getHud().getModule(Hud.Module.speed)) {
            ArrayList<Double> pos = player.getVec();
            ArrayList<Double> oldPos = player.getPCache().getSpeedData().getVec();
            // replace with players current speed
            player.getPCache().getSpeedData().setVec(pos);
            // only do x and y if 3d is off
            if (!(boolean) player.getPCache().getHud().getSetting(Hud.Setting.module__speed_3d)) {
                pos.set(1,0.0);
                oldPos.set(1,0.0);
            }
            // update the speed
            player.getPCache().getSpeedData().setSpeed(Utl.vec.distance(oldPos, pos) * 10);
        }
    }

    private static void HUDTickLogic(Player player) {
        // if the HUD is enabled
        if ((boolean) player.getPCache().getHud().getSetting(Hud.Setting.state)) {
            HashMap<Hud.Module, ArrayList<String>> HUDData = Hud.build.getHUDInstructions(player);
            // if the client has directionhud and the hud type is the actionBar, send as a packet
            if (DirectionHUD.clientPlayers.contains(player) &&
                    Enums.get(player.getPCache().getHud().getSetting(Hud.Setting.type), Hud.Setting.DisplayType.class).equals(Hud.Setting.DisplayType.actionbar))
                player.sendHUDPackets(HUDData);
            // if not do a normal display
            else player.displayHUD(Hud.build.compile(player,HUDData));
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
            // destination particles
            if (player.getPCache().getDEST().getDestSettings().getParticles().getDest()) {
                ArrayList<Double> destVec1 = Destination.dest.get(player).getVec(player);
                ArrayList<Double> destVec2 = new ArrayList<>(destVec1);
                destVec1.set(1,destVec1.get(1)+3);
                destVec2.set(1,destVec2.get(1)-3);
                Utl.particle.spawnLine(player, destVec1, destVec2, Utl.particle.DEST);
            }
            // line particles
            if (player.getPCache().getDEST().getDestSettings().getParticles().getLine())
                player.spawnParticleLine(Destination.dest.get(player).getVec(player),Utl.particle.LINE);
        }
        // track particles
        if (player.getPCache().getDEST().getDestSettings().getParticles().getTracking()) {
            // make sure there's a target
            Player target = Destination.social.track.getTarget(player);
            if (target != null) {
                boolean sendParticles = true;
                ArrayList<Double> targetVec = target.getVec();
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
                    }
                }
                // actually send the particles
                if (sendParticles) player.spawnParticleLine(targetVec,Utl.particle.TRACKING);
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