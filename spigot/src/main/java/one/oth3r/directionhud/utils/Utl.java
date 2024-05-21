package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionEntry;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.dimension.RatioEntry;
import one.oth3r.directionhud.common.template.FeatureChecker;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper.*;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.*;

public class Utl {
    public static CTxT getTranslation(String key,Object... args) {
        return LangReader.of("key.directionhud."+key, args).getTxT();
    }
    public static CTxT getTxTFromObj(Object obj) {
        CTxT txt = CTxT.of("");
        if (obj instanceof CTxT) txt.append(((CTxT) obj).b());
        else if (obj instanceof TextComponent) txt.append((TextComponent) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers())
            array.add(new Player(p));
        return array;
    }
    public static class vec {
        public static double distance(ArrayList<Double> from, ArrayList<Double> to)  {
            return convertTo(from).distance(convertTo(to));
        }
        public static Vector convertTo(ArrayList<Double> vec) {
            if (vec.size() == 3) return new Vector(vec.get(0),vec.get(1),vec.get(2));
            else return new Vector(0,0,0);
        }
    }

    public static class CheckEnabled extends FeatureChecker {

        public CheckEnabled(Player player) {
            super(player);
        }

        @Override
        public boolean reload() {
            return player.getPlayer().hasPermission("directionhud.reload");
        }

        @Override
        public boolean globalEditing() {
            return super.globalEditing() && player.getPlayer().hasPermission("directionhud.destination.global-saving");
        }

        @Override
        public boolean hud() {
            return super.hud() && player.getPlayer().hasPermission("directionhud.hud");
        }

        @Override
        public boolean destination() {
            return super.destination() && player.getPlayer().hasPermission("directionhud.destination");
        }

        @Override
        public boolean saving() {
            return super.saving() && player.getPlayer().hasPermission("directionhud.destination.saving");
        }
    }

    public static class particle {
        public static final String LINE = "LINE";
        public static final String DEST = "DEST";
        public static final String TRACKING = "TRACKING";
        public static void spawnLine(Player player, ArrayList<Double> start, ArrayList<Double> end, String particleType) {
            Vector startV = vec.convertTo(start);
            Vector endV = vec.convertTo(end);
            Vector playerV = vec.convertTo(player.getVec());
            double distance = startV.distance(endV);
            Vector particlePos = startV.subtract(new Vector(0, 0.2, 0));
            double spacing = 1;
            Vector segment = endV.subtract(startV).normalize().multiply(spacing);
            double distCovered = 0;
            for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
                distCovered += spacing;
                if (distCovered >= 50) break;
                if (!(playerV.distance(particlePos) > 0.5 && playerV.distance(particlePos) < 50)) continue;
                player.getPlayer().spawnParticle(Particle.CLOUD,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,getParticle(particleType,player));
            }
        }
        public static Particle.DustOptions getParticle(String particleType, Player player) {
            int[] i = CUtl.color.RGB((String) player.getPData().getDEST().getSetting(Destination.Setting.particles__line_color));
            if (particleType.equals(LINE)) return new Particle.DustOptions(Color.fromRGB(i[0],i[1],i[2]), 1);
            i = CUtl.color.RGB((String) player.getPData().getDEST().getSetting(Destination.Setting.particles__dest_color));
            if (particleType.equals(DEST)) return new Particle.DustOptions(Color.fromRGB(i[0],i[1],i[2]), 3);
            i = CUtl.color.RGB((String) player.getPData().getDEST().getSetting(Destination.Setting.particles__tracking_color));
            if (particleType.equals(TRACKING)) return new Particle.DustOptions(Color.fromRGB(i[0],i[1],i[2]), 0.5f);
            return new Particle.DustOptions(Color.BLACK,1);
        }
    }

    public static class dim {

        public static final ArrayList<DimensionEntry> DEFAULT_DIMENSIONS = new ArrayList<>(Arrays.asList(
                new DimensionEntry(format(Bukkit.getWorlds().get(0)), "Overworld", "#55FF55", Dimension.OVERWORLD_TIME_ENTRY),
                new DimensionEntry(format(Bukkit.getWorlds().get(0))+"_nether", "Nether", "#e8342e", new DimensionEntry.Time()),
                new DimensionEntry(format(Bukkit.getWorlds().get(0))+"_the_end", "End", "#edffb0", new DimensionEntry.Time())
        ));

        public static final ArrayList<RatioEntry> DEFAULT_RATIOS = new ArrayList<>(List.of(
                new RatioEntry(new Pair<>(format(Bukkit.getWorlds().get(0)), 1.0), new Pair<>(format(Bukkit.getWorlds().get(0))+"_nether", 8.0))
        ));

        /**
         * formats the un-formatted dimension received from the game
         * @param world the un-formatted dimension
         * @return the formatted dimension
         */
        public static String format(World world) {
            return world.getName();
        }

        public static String legacyFormatter(String dimension) {
            String defaultWorld = Bukkit.getWorlds().get(0).getName();
            if (dimension.equals(defaultWorld)) return "overworld";
            String[] split = dimension.split("_");
            return split[split.length-1];
        }

        /**
         * for spigot, return the old formatting back to normal, somewhat
         */
        public static String updateLegacy(String oldDimension) {
            // for each world, run the legacy formatter, and if the world name matches, use the new formatter for the world
            for (World world: Bukkit.getWorlds()) {
                if (legacyFormatter(world.getName()).equals(oldDimension)) {
                    return format(world);
                }
            }
            return oldDimension;
        }

        /**
         * adds the dimensions that are loaded in game but aren't in the config yet
         */
        public static void addMissing(DimensionSettings dimensionSettings) {
            Random random = new Random();
            ArrayList<DimensionEntry> dimensions = dimensionSettings.getDimensions();
            for (World world : Bukkit.getWorlds()) {
                String currentDIM = format(world);
                // if already exist or the default, continue
                if (dimensions.stream()
                        .anyMatch(dimension -> dimension.getId().equals(currentDIM))) continue;
                // make the entry
                DimensionEntry entry = new DimensionEntry();
                // add the dimension name
                entry.setId(currentDIM);
                // format the dimension name by capitalizing the first letter
                entry.setName(getFormattedDimName(currentDIM));
                //make a random color to spice things up
                entry.setColor(String.format("#%02x%02x%02x",
                        random.nextInt(100,256),random.nextInt(100,256),random.nextInt(100,256)));
                // add the entry
                dimensions.add(entry);
            }
        }

        /**
         * tries the format the dimension to a nice name
         */
        private static String getFormattedDimName(String worldName) {
            // remove all "_" "the_nether" -> "the nether"
            worldName = worldName.replaceAll("_"," ");
            // remove 'the' "the nether" -> "nether"
            worldName = worldName.replaceFirst("the ","");
            // capitalize the key letter "nether" -> "Nether"
            worldName = worldName.substring(0,1).toUpperCase()+worldName.substring(1);
            return worldName;
        }
    }
}