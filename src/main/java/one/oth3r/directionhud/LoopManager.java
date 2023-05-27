package one.oth3r.directionhud;

import one.oth3r.directionhud.commands.Destination;
import one.oth3r.directionhud.commands.HUD;
import one.oth3r.directionhud.files.PlayerData;
import one.oth3r.directionhud.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.CUtl;
import one.oth3r.directionhud.utils.Loc;
import one.oth3r.directionhud.utils.Utl;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class LoopManager {
    public static int hour;
    public static int minute;
    public static long timeTicks;
    public static String weatherIcon;
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
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (PlayerData.get.hud.state(player)) {
                    HUD.build(player);
                }
                if (Destination.getDist(player) <= PlayerData.get.dest.setting.autoclearrad(player)
                        && PlayerData.get.dest.setting.autoclear(player) && Destination.get(player).hasXYZ())
                    Destination.clear(player, CUtl.lang("dest.changed.cleared.reached").color('7').italic(true));
            }
        }
        if (rainbowF >= 360) rainbowF = 0;
        if (tickH >= 5) {
            World world = Bukkit.getWorlds().get(0);
            tickH = 0;
            timeTicks = world.getTime();
            hour = (int) ((timeTicks / 1000 + 6) % 24);
            minute = (int) ((timeTicks % 1000) * 60 / 1000);
            if (world.hasStorm()) {
                String str;
                if (Utl.inBetween((int) timeTicks, 12010,23992)) str = CUtl.symbols.moon();
                else str = CUtl.symbols.sun();
                if (world.isThundering()) weatherIcon = str + CUtl.symbols.thunder();
                else weatherIcon = str + CUtl.symbols.rain();
            } else if (Utl.inBetween((int) timeTicks, 12010,23992)) weatherIcon = CUtl.symbols.moon();
            else weatherIcon = CUtl.symbols.sun();
        }
        if (tickS >= 20) {
            tickS = 0;
            for (Player player : Bukkit.getOnlinePlayers()) {
                secondLoop(player);
            }
        }
    }
    private static void secondLoop(Player player) {
        //PARTICLES
        if (Destination.get(player).hasXYZ()) {
            if (PlayerData.get.dest.setting.particle.dest(player)) particles.dest(player);
            if (PlayerData.get.dest.setting.particle.line(player)) particles.line(player);
        }
        if (PlayerData.get.dest.getTracking(player) != null && !PlayerData.get.dest.setting.track(player))
            Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off").color('7').italic(true));
        Player trackingP = Destination.social.track.getTarget(player);
        //TRACKING
        if (trackingP != null && PlayerData.get.dest.setting.track(trackingP)) {
            //TRACKING OFFLINE MSG RESET
            if (PlayerData.getOneTime(player, "tracking.offline") != null) {
                player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")).b());
                PlayerData.setOneTime(player, "tracking.offline", null);
            }
            Vector trackingVec = trackingP.getLocation().toVector();
            boolean particleState = true;
            //IF NOT IN SAME DIM
            if (!Utl.player.dim(trackingP).equals(Utl.player.dim(player))) {
                particleState = false;
                // AUTOCONVERT ON AND CONVERTIBLE
                if (PlayerData.get.dest.setting.autoconvert(player) && Utl.dim.canConvert(Utl.player.dim(player), Utl.player.dim(trackingP))) {
                    if (PlayerData.getOneTime(player, "tracking.converted") == null) {
                        //SEND MSG IF HAVENT B4
                        player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                                .append(CUtl.lang("dest.autoconvert.info",
                                                CTxT.of(Utl.dim.getName(Utl.player.dim(trackingP))).italic(true).color(Utl.dim.getHEX(Utl.player.dim(trackingP))),
                                                CTxT.of(Utl.dim.getName(Utl.player.dim(player))).italic(true).color(Utl.dim.getHEX(Utl.player.dim(player))))
                                        .italic(true).color('7')).b());
                        PlayerData.setOneTime(player, "tracking.converted", Utl.player.dim(player));
                    }
                    particleState = true;
                    Loc tLoc = new Loc(trackingP);
                    tLoc.convertTo(Utl.player.dim(player));
                    trackingVec = tLoc.getVec3d(player);
                } else if (PlayerData.getOneTime(player, "tracking.dimension") == null) {
                    //NOT CONVERTIBLE OR AUTOCONVERT OFF -- SEND DIM MSG
                    //RESET CONVERT
                    PlayerData.setOneTime(player, "tracking.converted", null);
                    player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.track.dimension").append("\n ")
                            .append(CUtl.lang("dest.track.dimension_2",
                                    CTxT.of(Utl.player.name(trackingP)).color(CUtl.sTC())).color('7').italic(true))).b());
                    PlayerData.setOneTime(player, "tracking.dimension", "1");
                }
            } else if (PlayerData.getOneTime(player, "tracking.converted") != null) {
                //SAME DIM & RESET CONVERT MSG
                player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.autoconvert.tracking")).append("\n ")
                        .append(CUtl.lang("dest.autoconvert.info",
                                        CTxT.of(Utl.dim.getName(PlayerData.getOneTime(player, "tracking.converted"))).italic(true).color(Utl.dim.getHEX(PlayerData.getOneTime(player, "tracking.converted"))),
                                        CTxT.of(Utl.dim.getName(Utl.player.dim(player))).italic(true).color(Utl.dim.getHEX(Utl.player.dim(player))))
                                .italic(true).color('7')).b());
                PlayerData.setOneTime(player, "tracking.converted", null);
            } else if (PlayerData.getOneTime(player, "tracking.dimension") != null) {
                //SAME DIM, RESET DIM MSG
                player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.track.back")).b());
                PlayerData.setOneTime(player, "tracking.dimension", null);
            }
            //PARTICLES
            if (PlayerData.get.dest.setting.particle.tracking(player) && particleState) {
                particles.tracking(player, trackingVec);
            }
        } else if (trackingP != null) {
            //TRACKING PLAYER TURNED OFF TRACKING
            Destination.social.track.clear(player, CUtl.lang("dest.track.clear.tracking_off_tracked").color('7').italic(true));
        } else if (PlayerData.getOneTime(player, "tracking.offline") == null && PlayerData.get.dest.getTracking(player) != null) {
            //TRACKING PLAYER OFFLINE
            player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.track.offline")).append(" ")
                    .append(CUtl.CButton.dest.clear()).b());
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
                player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")).b());
                PlayerData.set.dest.setTrackNull(player);
            } else if (PlayerData.get.dest.track.expire(player) > 0) { //TICK DOWN
                PlayerData.set.dest.track.expire(player, PlayerData.get.dest.track.expire(player) - 1);
                if (Utl.player.getFromIdentifier(PlayerData.get.dest.track.target(player)) == null) { //TARGET PLAYER LEFT
                    player.spigot().sendMessage(CUtl.tag().append(CUtl.lang("dest.track.expired")).b());
                    PlayerData.set.dest.setTrackNull(player);
                }
            }
        }
    }
    private static class particles {
        private static void dest(Player player) {
            Vector pVec = player.getLocation().toVector().add(new Vector(0, 1, 0));
            if (player.getVehicle() != null) pVec.add(new Vector(0,-0.2,0));
            Vector destVec = Destination.get(player).getVec3d(player).add(new Vector(0.5,0.5,0.5));
            if (PlayerData.get.dest.setting.particle.dest(player)) {
                Vector particlePos = Destination.get(player).getVec3d(player).add(new Vector(0.5,3.5,0.5));
                double spacing = 1;
                Vector segment = Destination.get(player).getVec3d(player).add(new Vector(0.5, 0.5, 0.5))
                        .subtract(particlePos).normalize().multiply(spacing);
                double distCovered = 0;
                for (; distCovered <= 6; particlePos = particlePos.add(segment)) {
                    if (pVec.distance(destVec) > 0.5 && pVec.distance(destVec) < 50) {
                        Particle.DustOptions dustOptions =
                                new Particle.DustOptions(Color.fromRGB(Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.destcolor(player))), 3);
                        player.spawnParticle(Particle.REDSTONE,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,dustOptions);
                    }
                    distCovered += spacing;
                }
            }
        }
        private static void line(Player player) {
            Vector pVec = player.getLocation().toVector().add(new Vector(0, 1, 0));
            if (player.getVehicle() != null) pVec.add(new Vector(0,-0.2,0));
            Vector destVec = Destination.get(player).getVec3d(player).add(new Vector(0.5,0.5,0.5));
            if (PlayerData.get.dest.setting.particle.line(player)) {
                double distance = pVec.distance(destVec);
                Vector particlePos = pVec.subtract(new Vector(0, 0.2, 0));
                double spacing = 1;
                Vector segment = destVec.subtract(pVec).normalize().multiply(spacing);
                double distCovered = 0;
                for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
                    distCovered += spacing;
                    if (pVec.distance(destVec) < 2) continue;
                    if (distCovered >= 50) break;
                    Particle.DustOptions dustOptions =
                            new Particle.DustOptions(Color.fromRGB(Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.linecolor(player))), 1);
                    player.spawnParticle(Particle.REDSTONE,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,dustOptions);
                }
            }
        }
        private static void tracking(Player player, Vector trackingVec) {
            Vector pVec = player.getLocation().toVector().add(new Vector(0, 1, 0));
            if (player.getVehicle() != null) pVec.add(new Vector(0,-0.2,0));
            if (PlayerData.get.dest.setting.ylevel(player))
                trackingVec = new Vector(trackingVec.getX(),player.getLocation().getY(),trackingVec.getZ());
            Vector tVec = trackingVec.add(new Vector(0,1,0));
            double distance = pVec.distance(tVec);
            Vector particlePos = pVec.subtract(new Vector(0, 0.2, 0));
            double spacing = 0.5;
            //space between each particle
            Vector segment = tVec.subtract(pVec).normalize().multiply(spacing);
            double distanceCovered = 0;
            for (; distanceCovered <= distance; particlePos = particlePos.add(segment)) {
                distanceCovered += spacing;
                //min particle spawning distance
                if (pVec.distance(tVec) < 2) continue;
                //if more than x blocks away
                if (distanceCovered >= 50) break;
                Particle.DustOptions dustOptions =
                        new Particle.DustOptions(Color.fromRGB(Utl.color.getCodeRGB(PlayerData.get.dest.setting.particle.trackingcolor(player))), 0.5f);
                player.spawnParticle(Particle.REDSTONE,particlePos.getX(),particlePos.getY(),particlePos.getZ(),2,dustOptions);
            }
        }
    }
}
