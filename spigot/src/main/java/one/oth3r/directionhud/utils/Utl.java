package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper.Dim;
import one.oth3r.directionhud.common.utils.Helper.Num;
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
    public static void setTime() {
        World world = Bukkit.getWorlds().get(0);
        long timeTicks = world.getTime();
        HUD.hour = (int) ((timeTicks / 1000 + 6) % 24);
        HUD.minute = (int) ((timeTicks % 1000) * 60 / 1000);
        if (world.hasStorm()) {
            //https://minecraft.fandom.com/wiki/Daylight_cycle
            String str;
            if (Num.inBetween((int) timeTicks, 12010,23992)) str = Assets.symbols.moon;
            else str = Assets.symbols.sun;
            // if thundering always can sleep
            if (world.isThundering()) HUD.weatherIcon = Assets.symbols.moon + Assets.symbols.thunder;
            else HUD.weatherIcon = str + Assets.symbols.rain;
        } else if (Num.inBetween((int) timeTicks, 12542,23460)) HUD.weatherIcon = Assets.symbols.moon;
        else HUD.weatherIcon = Assets.symbols.sun;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>();
        for (org.bukkit.entity.Player p : Bukkit.getOnlinePlayers())
            array.add(Player.of(p));
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
    public static class checkEnabled {
        public static boolean customPresets(Player player) {
            return config.MAXColorPresets > 0;
        }
        public static boolean destination(Player player) {
            return player.getPlayer().hasPermission("directionhud.destination");
        }
        public static boolean hud(Player player) {
            return player.getPlayer().hasPermission("directionhud.hud") && config.HUDEditing;
        }
        public static boolean reload(Player player) {
            return player.getPlayer().hasPermission("directionhud.reload");
        }
        public static boolean global(Player player) {
            return config.globalDESTs && player.getPlayer().hasPermission("directionhud.destination.global-saving");
        }
        public static boolean saving(Player player) {
            return player.getPlayer().hasPermission("directionhud.destination.saving");
        }
        public static boolean lastdeath(Player player) {
            return (boolean)player.getPData().getDEST().getSetting(Destination.Setting.features__lastdeath) && config.LastDeathSaving;
        }
        public static boolean send(Player player) {
            return (boolean)player.getPData().getDEST().getSetting(Destination.Setting.features__send) && config.social;
        }
        public static boolean track(Player player) {
            return (boolean)player.getPData().getDEST().getSetting(Destination.Setting.features__track) && config.social;
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
                player.getPlayer().spawnParticle(Particle.REDSTONE,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,getParticle(particleType,player));
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
        public static final List<HashMap<String, String>> DEFAULT_DIMENSIONS = List.of(
                new HashMap<>() {{ put("dimension", "overworld"); put("name", "Overworld"); put("color", "#55FF55"); }},
                new HashMap<>() {{ put("dimension", "nether"); put("name", "Nether"); put("color", "#e8342e"); }},
                new HashMap<>() {{ put("dimension", "end"); put("name", "End"); put("color", "#edffb0"); }}
        );
        public static final List<HashMap<String,Double>> DEFAULT_RATIOS = List.of(
                new HashMap<>() {{ put("overworld", 1.0); put("nether", 8.0);}}
        );
        /**
         * formats the un-formatted dimension received from the game
         * @param dimension the un-formatted dimension
         * @return the formatted dimension
         */
        public static String format(String dimension) {
            String defaultWorld = Bukkit.getWorlds().get(0).getName();
            if (dimension.equals(defaultWorld)) return "overworld";
            String[] split = dimension.split("_");
            return split[split.length-1];
        }
        /**
         * adds the dimensions that are loaded in game but aren't in the config yet
         */
        public static void addMissing() {
            Random random = new Random();
            // ADD MISSING DIMENSIONS TO MAP
            String defaultWorld = Bukkit.getWorlds().get(0).getName();
            for (World world : Bukkit.getWorlds()) {
                String currentDIM = format(world.getName());
                // if already exist or the default, continue
                if (config.dimensions.stream()
                        .anyMatch(dimension -> dimension.get("dimension").equals(currentDIM)) || currentDIM.equals(defaultWorld)) continue;
                // make the entry
                HashMap<String,String> entry = new HashMap<>();
                // add the dimension name
                entry.put("dimension",currentDIM);
                // format the dimension name by capitalizing the first letter
                entry.put("name",currentDIM.substring(0,1).toUpperCase()+currentDIM.substring(1));
                //make a random color to spice things up
                entry.put("color",String.format("#%02x%02x%02x",
                        random.nextInt(100,256),random.nextInt(100,256),random.nextInt(100,256)));
                config.dimensions.add(entry);
            }
        }
    }
}