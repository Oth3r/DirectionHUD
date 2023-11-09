package one.oth3r.directionhud.common;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.common.utils.CUtl;
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

        if (HUDTick >= config.HUDLoop) {
            HUDTick = 0;
            for (Player player : Utl.getPlayers()) {
                if ((boolean)PlayerData.get.hud.setting.get(player, HUD.Setting.state)) {
                    HashMap<HUD.Module, ArrayList<String>> HUDData = HUD.getRawHUDText(player);
                    // if the client has directionhud and the hud type is the actionBar send as a packet
                    if (DirectionHUD.clientPlayers.contains(player) && HUD.Setting.DisplayType.get((String) PlayerData.get.hud.setting.get(player, HUD.Setting.type)).equals(HUD.Setting.DisplayType.actionbar)) player.sendHUDPackets(HUDData);
                    else player.displayHUD(HUD.build(player,HUDData));
                }
                // if player has DEST, AutoClear is on, and the distance is in the AutoClear range, clear
                if (Destination.get(player).hasXYZ() && (boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.autoclear) &&
                        Destination.getDist(player) <= (double)PlayerData.get.dest.setting.get(player, Destination.Setting.autoclear_rad)) {
                    Destination.clear(player, CUtl.lang("dest.changed.cleared.reached").color('7').italic(true));
                }
            }
        }
        if (ParticleTick >= config.ParticleLoop) {
            ParticleTick = 0;
            for (Player player :Utl.getPlayers()) particles(player);
        }
        // reset the rainbow at 360
        if (rainbowF >= 360) rainbowF = 0;
        // update the time every five ticks
        if (secondTick%5==0) Utl.setTime();
        // every 20 ticks
        if (secondTick >= 20) {
            secondTick = 0;
            for (Player player :Utl.getPlayers()) secondLoop(player);
        }
    }
    private static void particles(Player player) {
        // spawn all the particles
        if (Destination.get(player).hasXYZ()) {
            // destination particles
            if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.particles__dest)) {
                ArrayList<Double> destVec1 = Destination.get(player).getVec(player);
                ArrayList<Double> destVec2 = new ArrayList<>(destVec1);
                destVec1.set(1,destVec1.get(1)+3);
                destVec2.set(1,destVec2.get(1)-3);
                Utl.particle.spawnLine(player, destVec1, destVec2, Utl.particle.DEST);
            }
            // line particles
            if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.particles__line))
                player.spawnParticleLine(Destination.get(player).getVec(player),Utl.particle.LINE);
        }
        // track particles
        if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.particles__tracking)) {
            // make sure there's a target
            Player target = Destination.social.track.getTarget(player);
            if (target != null) {
                boolean sendParticles = true;
                ArrayList<Double> targetVec = target.getVec();
                if (!target.getDimension().equals(player.getDimension())) {
                    sendParticles = false;
                    // if convertible and autoconvert is enabled, send the particles
                    if (Utl.dim.canConvert(player.getDimension(), target.getDimension()) &&
                            (boolean) PlayerData.get.dest.setting.get(player, Destination.Setting.autoconvert)) {
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
        DHUD.inbox.tick(player);
        Double timer = PlayerData.get.socialCooldown(player);
        if (timer != null) {
            PlayerData.set.socialCooldown(player,timer-1);
            if (timer<=1) PlayerData.set.socialCooldown(player,null);
        }
        // TRACKING MESSAGE HANDLER
        // INFO (null if not sent, not if otherwise)
        // tracking.offline = target offline
        // tracking.dimension = not in same dimension & cant convert (trail cold)
        // tracking.converted = tracker converted message

        // if there is an entry in the tracking
        if (PlayerData.get.dest.getTracking(player) != null) {
            // player turned off tracking
            if (!(boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.features__track))
                Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off").color('7').italic(true));
            // if the server turned social off, just clear
            if (!Utl.checkEnabled.track(player)) Destination.social.track.clear(player);
        }
        Player target = Destination.social.track.getTarget(player);
        // if the target isn't null and the target has tracking on
        if (target != null && (boolean)PlayerData.get.dest.setting.get(target, Destination.Setting.features__track)) {
            // if the offline message was sent, reset it and send the back message
            if (PlayerData.getOneTime(player, "tracking.offline") != null) {
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")));
                PlayerData.setOneTime(player, "tracking.offline", null);
            }
            // target isn't in the same dimension as the player
            if (!target.getDimension().equals(player.getDimension())) {
                // AUTOCONVERT ON AND CONVERTIBLE
                if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.autoconvert) &&
                        Utl.dim.canConvert(player.getDimension(),target.getDimension())) {
                    // send the tracking resumed message if not reset
                    if (PlayerData.getOneTime(player, "tracking.dimension") != null) {
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")));
                        PlayerData.setOneTime(player, "tracking.dimension", null);
                    }
                    // send the convert message if it hasn't been sent
                    if (PlayerData.getOneTime(player, "tracking.converted") == null) {
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                                .append(CUtl.lang("dest.autoconvert.tracking.info",
                                                CTxT.of(Utl.dim.getName(target.getDimension())).italic(true).color(Utl.dim.getHEX(target.getDimension())))
                                        .italic(true).color('7')));
                        // change the status on the convert message
                        PlayerData.setOneTime(player, "tracking.converted",target.getDimension());
                    }
                } else if (PlayerData.getOneTime(player, "tracking.dimension") == null) {
                    // if not convertible or AutoConvert is off, & the dimension message hasn't been sent,
                    // send the dimension message
                    player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.dimension").append("\n ")
                            .append(CUtl.lang("dest.autoconvert.tracking.info",
                                            CTxT.of(Utl.dim.getName(target.getDimension())).italic(true).color(Utl.dim.getHEX(target.getDimension())))
                                    .italic(true).color('7'))));
                    PlayerData.setOneTime(player, "tracking.dimension", "1");
                    // make sure converted is reset
                    PlayerData.setOneTime(player, "tracking.converted", null);
                }
            } else if (PlayerData.getOneTime(player, "tracking.converted") != null) {
                // in the same dimension, but has been converted before
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                        .append(CUtl.lang("dest.autoconvert.tracking.info",
                                        CTxT.of(Utl.dim.getName(target.getDimension())).italic(true).color(Utl.dim.getHEX(target.getDimension())))
                                .italic(true).color('7')));
                PlayerData.setOneTime(player, "tracking.converted", null);
            } else if (PlayerData.getOneTime(player, "tracking.dimension") != null) {
                // in the same dimension, but tracking was stopped
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")));
                PlayerData.setOneTime(player, "tracking.dimension", null);
            }
        } else if (target != null) {
            // if tracking player isn't null, but they turned off tracking
            Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off_tracked").color('7').italic(true));
        } else if (PlayerData.getOneTime(player, "tracking.offline") == null && PlayerData.get.dest.getTracking(player) != null) {
            // if the target is null, means the player cant be found, probably offline
            // AND the offline message hasn't been sent yet
            player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.offline")).append(" ")
                    .append(CUtl.CButton.dest.clear()));
            PlayerData.setOneTime(player, "tracking.offline", "1");
            // reset all other messages
            PlayerData.setOneTime(player, "tracking.converted", null);
            PlayerData.setOneTime(player, "tracking.dimension", null);
        }
    }
}