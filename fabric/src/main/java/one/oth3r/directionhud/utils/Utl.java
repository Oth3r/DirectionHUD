package one.oth3r.directionhud.utils;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry.*;
import one.oth3r.directionhud.common.files.dimension.RatioEntry;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper.*;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Utl {
    public static CTxT getTranslation(String key, Object... args) {
        return CTxT.of(Text.translatable(key, args));
    }
    public static CTxT getTxTFromObj(Object obj) {
        CTxT txt = CTxT.of("");
        if (obj instanceof CTxT) txt.append(((CTxT) obj).b());
        else if (obj instanceof Text) txt.append((Text) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>();
        for (ServerPlayerEntity p : DirectionHUD.server.getPlayerManager().getPlayerList())
            array.add(Player.of(p));
        return array;
    }
    public static class vec {
        public static double distance(ArrayList<Double> from, ArrayList<Double> to)  {
            return convertTo(from).distanceTo(convertTo(to));
        }
        public static Vec3d convertTo(ArrayList<Double> vec) {
            if (vec.size() == 3) return new Vec3d(vec.get(0),vec.get(1),vec.get(2));
            else return new Vec3d(0,0,0);
        }
    }
    public static class checkEnabled {
        //todo add a bool for singleplayer for perm checking
        public static boolean customPresets(Player player) {
            return config.MAXColorPresets > 0;
        }
        public static boolean destination(Player player) {
            return true;
        }
        public static boolean hud(Player player) {
            return config.HUDEditing;
        }
        public static boolean reload(Player player) {
            return player.getPlayer().hasPermissionLevel(2) || DirectionHUDClient.singleplayer;
        }
        public static boolean global(Player player) {
            return config.globalDESTs && (player.getPlayer().hasPermissionLevel(2) || DirectionHUDClient.singleplayer);
        }
        public static boolean saving(Player player) {
            return config.DestSaving;
        }
        public static boolean lastdeath(Player player) {
            return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__lastdeath) && config.LastDeathSaving;
        }
        public static boolean send(Player player) {
            return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__send) && config.social && DirectionHUD.server.isRemote();
        }
        public static boolean track(Player player) {
            return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__track) && config.social && DirectionHUD.server.isRemote();
        }
    }
    public static class particle {
        public static final String LINE = "LINE";
        public static final String DEST = "DEST";
        public static final String TRACKING = "TRACKING";
        public static void spawnLine(Player player, ArrayList<Double> start, ArrayList<Double> end, String particleType) {
            Vec3d startV = vec.convertTo(start);
            Vec3d endV = vec.convertTo(end);
            Vec3d playerV = vec.convertTo(player.getVec());
            double distance = startV.distanceTo(endV);
            Vec3d particlePos = startV.subtract(0, 0.2, 0);
            double spacing = 1;
            Vec3d segment = endV.subtract(startV).normalize().multiply(spacing);
            double distCovered = 0;
            for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
                distCovered += spacing;
                if (distCovered >= 50) break;
                if (!(playerV.distanceTo(particlePos) > 0.5 && playerV.distanceTo(particlePos) < 50)) continue;
                player.spawnParticle(particleType,particlePos);
            }
        }
        public static DustParticleEffect getParticle(String particleType, Player player) {
            String hex = (String) player.getPData().getDEST().getSetting(Destination.Setting.particles__dest_color);
            if (particleType.equals(DEST)) return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),3);
            hex = (String) player.getPData().getDEST().getSetting(Destination.Setting.particles__line_color);
            if (particleType.equals(LINE)) return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),1);
            hex = (String) player.getPData().getDEST().getSetting(Destination.Setting.particles__tracking_color);
            if (particleType.equals(TRACKING)) return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),0.5f);
            hex = "#000000";
            return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),5f);
        }
    }

    public static class dim {

        public static ArrayList<DimensionEntry> DEFAULT_DIMENSIONS = new ArrayList<>(Arrays.asList(
                new DimensionEntry("minecraft.overworld", "Overworld", "#55FF55",Dimension.OVERWORLD_TIME_ENTRY),
                new DimensionEntry("minecraft.the_nether", "Nether", "#e8342e", new Time(true)),
                new DimensionEntry("minecraft.the_end", "End", "#edffb0", new Time(true))
        ));

        public static ArrayList<RatioEntry> DEFAULT_RATIOS = new ArrayList<>(List.of(
                new RatioEntry(new Pair<>("minecraft.overworld", 1.0), new Pair<>("minecraft.the_nether", 8.0))
        ));

        /**
         * formats the un-formatted dimension received from the game
         * @param worldRegistryKey the worldRegistryKey
         * @return the formatted dimension string
         */
        public static String format(RegistryKey<World> worldRegistryKey) {
            return worldRegistryKey.getValue().toString().replace(":",".");
        }

        /**
         * adds the dimensions that are loaded in game but aren't in the config yet
         */
        public static void addMissing() {
            Random random = new Random();
            if (DirectionHUD.server == null) return;
            ArrayList<DimensionEntry> dimensions = Dimension.getDimensionSettings().getDimensions();
            //ADD MISSING DIMS TO MAP
            for (ServerWorld world : DirectionHUD.server.getWorlds()) {
                String currentDIM = format(world.getRegistryKey());
                // if already exist, continue
                if (dimensions.stream()
                        .anyMatch(dimension -> dimension.getId().equals(currentDIM)) ) continue;
                DimensionEntry entry = new DimensionEntry();
                // add the dimension name
                entry.setId(currentDIM);
                // add the formatted name
                entry.setName(getFormattedDim(world));
                //make a random color to spice things up
                entry.setColor(String.format("#%02x%02x%02x",
                        random.nextInt(100,256),random.nextInt(100,256),random.nextInt(100,256)));
                // add the entry
                dimensions.add(entry);
            }
        }

        /**
         * tries to generate a formatted name for the dimension
         * @param world the dimension world
         * @return formatted dimension string
         */
        @NotNull
        private static String getFormattedDim(ServerWorld world) {
            // get the path of the dimension, removes the "minecraft."
            String formatted = world.getRegistryKey().getValue().getPath();
            // remove all "_" "the_nether" -> "the nether"
            formatted = formatted.replaceAll("_"," ");
            // remove 'the' "the nether" -> "nether"
            formatted = formatted.replaceFirst("the ","");
            // captilize the key letter "nether" -> "Nether"
            formatted = formatted.substring(0,1).toUpperCase()+formatted.substring(1);
            return formatted;
        }
    }
}
