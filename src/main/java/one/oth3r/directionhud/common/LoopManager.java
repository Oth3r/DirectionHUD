package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;

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
                if (PlayerData.get.hud.state(player)) HUD.build(player);
                if (Destination.get(player).hasXYZ() && PlayerData.get.dest.setting.autoclear(player) &&
                        Destination.getDist(player) <= PlayerData.get.dest.setting.autoclearrad(player)) {
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
            if (PlayerData.get.dest.setting.particle.dest(player)) {
                ArrayList<Double> destVec1 = Destination.get(player).getVec(player);
                ArrayList<Double> destVec2 = new ArrayList<>(destVec1);
                destVec1.set(1,destVec1.get(1)+3);
                destVec2.set(1,destVec2.get(1)-3);
                Utl.particle.spawnLine(player, destVec1, destVec2, Utl.particle.DEST);
            }

            if (PlayerData.get.dest.setting.particle.line(player))
                player.spawnParticleLine(Destination.get(player).getVec(player),Utl.particle.LINE);
        }
        if (PlayerData.get.dest.getTracking(player) != null && !PlayerData.get.dest.setting.track(player))
            Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off").color('7').italic(true));
        Player trackingP = Destination.social.track.getTarget(player);
        //TRACKING
        if (trackingP != null && PlayerData.get.dest.setting.track(trackingP)) {
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
                if (PlayerData.get.dest.setting.autoconvert(player) && Utl.dim.canConvert(player.getDimension(),trackingP.getDimension())) {
                    if (PlayerData.getOneTime(player, "tracking.converted") == null) {
                        //SEND MSG IF HAVENT B4
                        player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                                .append(CUtl.lang("dest.autoconvert.info",
                                                CTxT.of(Utl.dim.getName(trackingP.getDimension())).italic(true).color(Utl.dim.getHEX(trackingP.getDimension())),
                                                CTxT.of(Utl.dim.getName(player.getDimension())).italic(true).color(Utl.dim.getHEX(player.getDimension())))
                                        .italic(true).color('7')));
                        PlayerData.setOneTime(player, "tracking.converted",player.getDimension());
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
                            .append(CUtl.lang("dest.track.dimension_2",
                                    CTxT.of(trackingP.getName()).color(CUtl.s())).color('7').italic(true))));
                    PlayerData.setOneTime(player, "tracking.dimension", "1");
                }
            } else if (PlayerData.getOneTime(player, "tracking.converted") != null) {
                //SAME DIM & RESET CONVERT MSG
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                        .append(CUtl.lang("dest.autoconvert.info",
                                        CTxT.of(Utl.dim.getName(PlayerData.getOneTime(player, "tracking.converted"))).italic(true).color(Utl.dim.getHEX(PlayerData.getOneTime(player, "tracking.converted"))),
                                        CTxT.of(Utl.dim.getName(player.getDimension())).italic(true).color(Utl.dim.getHEX(player.getDimension())))
                                .italic(true).color('7')));
                PlayerData.setOneTime(player, "tracking.converted", null);
            } else if (PlayerData.getOneTime(player, "tracking.dimension") != null) {
                //SAME DIM, RESET DIM MSG
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")));
                PlayerData.setOneTime(player, "tracking.dimension", null);
            }
            //PARTICLES
            if (PlayerData.get.dest.setting.particle.tracking(player) && particleState) {
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
        if (PlayerData.get.dest.getTrackPending(player)) {
            //REMOVE IF TRACKING IS OFF
            if (!PlayerData.get.dest.setting.track(player)) {
                PlayerData.set.dest.setTrackNull(player);
            } else if (PlayerData.get.dest.track.expire(player) == 0) { //RAN OUT OF TIME
                player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")));
                PlayerData.set.dest.setTrackNull(player);
            } else if (PlayerData.get.dest.track.expire(player) > 0) { //TICK DOWN
                PlayerData.set.dest.track.expire(player, PlayerData.get.dest.track.expire(player) - 1);
                if (Player.of(PlayerData.get.dest.track.target(player)) == null) { //TARGET PLAYER LEFT
                    player.sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")));
                    PlayerData.set.dest.setTrackNull(player);
                }
            }
        }
    }
}