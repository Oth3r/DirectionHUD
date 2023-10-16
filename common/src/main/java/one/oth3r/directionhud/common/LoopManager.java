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
    private static int tickH;
    private static int tickS;
    private static int HUDRefresh;
    public static void tick() {
        tickH++;
        tickS++;
        rainbowF += 10;
        HUDRefresh++;
        if (HUDRefresh >= config.HUDRefresh) {
            HUDRefresh = 0;
            for (Player player : Utl.getPlayers()) {
                if (PlayerData.get.hud.state(player)) {
                    HashMap<HUD.modules.Types, ArrayList<String>> HUDData = HUD.getRawHUDText(player);
                    // if the client has directionhud and the hud type is the actionBar send as a packet
                    if (DirectionHUD.clientPlayers.contains(player) && config.HUDTypes.get((String) PlayerData.get.hud.setting.get(player, HUD.Settings.type)).equals(config.HUDTypes.actionbar)) player.sendHUDPackets(HUDData);
                    else player.displayHUD(HUD.build(player,HUDData));
                }
                if (Destination.get(player).hasXYZ() && (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear) &&
                        Destination.getDist(player) <= (double)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear_rad)) {
                    Destination.clear(player, CUtl.lang("dest.changed.cleared.reached").color('7').italic(true));
                }
            }
        }
        if (rainbowF >= 360) rainbowF = 0;
        if (tickH >= 5) {
            tickH = 0;
            Utl.setTime();
        }
        if (tickS >= 20) {
            tickS = 0;
            for (Player player :Utl.getPlayers()) {
                secondLoop(player);
            }
        }
    }
    private static void secondLoop(Player player) {
        //PARTICLES
        if (Destination.get(player).hasXYZ()) {
            if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__dest)) {
                ArrayList<Double> destVec1 = Destination.get(player).getVec(player);
                ArrayList<Double> destVec2 = new ArrayList<>(destVec1);
                destVec1.set(1,destVec1.get(1)+3);
                destVec2.set(1,destVec2.get(1)-3);
                Utl.particle.spawnLine(player, destVec1, destVec2, Utl.particle.DEST);
            }
            if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__line))
                player.spawnParticleLine(Destination.get(player).getVec(player),Utl.particle.LINE);
        }
        // INFO
        // tracking.offline = offline message, null if not sent
        // tracking.dimension = not in same dimension message, null if not sent
        // tracking.converted = tracker converted message, null if not sent
        if (PlayerData.get.dest.getTracking(player) != null) {
            //if they turned it off
            if (!(boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track))
                Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off").color('7').italic(true));
            //if server is off
            if (!Utl.checkEnabled.track(player)) Destination.social.track.clear(player);
        }
        Player trackingP = Destination.social.track.getTarget(player);
        //TRACKING
        if (trackingP != null && (boolean)PlayerData.get.dest.setting.get(trackingP, Destination.Settings.features__track)) {
            //TRACKING OFFLINE MSG RESET
            if (PlayerData.getOneTime(player, "tracking.offline") != null) {
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")));
                PlayerData.setOneTime(player, "tracking.offline", null);
            }
            ArrayList<Double> trackingVec = trackingP.getVec();
            boolean particleState = true;
            //IF NOT IN SAME DIM
            if (!trackingP.getDimension().equals(player.getDimension())) {
                particleState = false;
                // AUTOCONVERT ON AND CONVERTIBLE
                if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoconvert) &&
                        Utl.dim.canConvert(player.getDimension(),trackingP.getDimension())) {
                    if (PlayerData.getOneTime(player, "tracking.converted") == null) {
                        //SEND MSG IF HAVENT B4
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                                .append(CUtl.lang("dest.autoconvert.tracking.info",
                                                CTxT.of(Utl.dim.getName(trackingP.getDimension())).italic(true).color(Utl.dim.getHEX(trackingP.getDimension())))
                                        .italic(true).color('7')));
                        PlayerData.setOneTime(player, "tracking.converted",trackingP.getDimension());
                    }
                    particleState = true;
                    Loc tLoc = new Loc(trackingP);
                    tLoc.convertTo(player.getDimension());
                    trackingVec = tLoc.getVec(player);
                } else if (PlayerData.getOneTime(player, "tracking.dimension") == null) {
                    //NOT CONVERTIBLE OR AUTOCONVERT OFF -- SEND DIM MSG
                    //RESET CONVERT
                    PlayerData.setOneTime(player, "tracking.converted", null);
                    player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.dimension").append("\n ")
                            .append(CUtl.lang("dest.autoconvert.tracking.info",
                                            CTxT.of(Utl.dim.getName(trackingP.getDimension())).italic(true).color(Utl.dim.getHEX(trackingP.getDimension())))
                                    .italic(true).color('7'))));
                    PlayerData.setOneTime(player, "tracking.dimension", "1");
                }
            } else if (PlayerData.getOneTime(player, "tracking.converted") != null) {
                //SAME DIM & RESET CONVERT MSG
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                        .append(CUtl.lang("dest.autoconvert.tracking.info",
                                        CTxT.of(Utl.dim.getName(trackingP.getDimension())).italic(true).color(Utl.dim.getHEX(trackingP.getDimension())))
                                .italic(true).color('7')));
                PlayerData.setOneTime(player, "tracking.converted", null);
            } else if (PlayerData.getOneTime(player, "tracking.dimension") != null) {
                //SAME DIM, RESET DIM MSG
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")));
                PlayerData.setOneTime(player, "tracking.dimension", null);
            }
            //PARTICLES
            if ((boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__tracking) && particleState) {
                player.spawnParticleLine(trackingVec,Utl.particle.TRACKING);
            }
        } else if (trackingP != null) {
            //TRACKING PLAYER TURNED OFF TRACKING
            Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off_tracked").color('7').italic(true));
        } else if (PlayerData.getOneTime(player, "tracking.offline") == null && PlayerData.get.dest.getTracking(player) != null) {
            //TRACKING PLAYER OFFLINE
            player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.offline")).append(" ")
                    .append(CUtl.CButton.dest.clear()));
            PlayerData.setOneTime(player, "tracking.offline", "1");
            //RESET OTHER MSGS
            PlayerData.setOneTime(player, "tracking.converted", null);
            PlayerData.setOneTime(player, "tracking.dimension", null);
        }
        //TRACK TIMER
        if (PlayerData.get.temp.track.exists(player)) {
            //REMOVE IF TRACKING IS OFF
            if (!(boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track)) {
                PlayerData.set.temp.track.remove(player);
            } else if (PlayerData.get.temp.track.expire(player) == 0) { //RAN OUT OF TIME
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")));
                PlayerData.set.temp.track.remove(player);
            } else if (PlayerData.get.temp.track.expire(player) > 0) { //TICK DOWN
                PlayerData.set.temp.track.expire(player, PlayerData.get.temp.track.expire(player) - 1);
                if (Player.of(PlayerData.get.temp.track.target(player)) == null) { //TARGET PLAYER LEFT
                    player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")));
                    PlayerData.set.temp.track.remove(player);
                }
            }
        }
    }
}