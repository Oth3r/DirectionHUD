package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.*;

public class Utl {
    public static class Pair<A, B> {
        private final A first;
        private final B second;
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
        public String toString() {
            return "("+this.first+", "+this.second+")";
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Pair<?, ?> otherPair = (Pair<?, ?>) obj;
            return Objects.equals(first, otherPair.first) && Objects.equals(second, otherPair.second);
        }
        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }
    public static CTxT getTranslation(String key,Object... args) {
        return LangReader.of("key.directionhud."+key, args).getTxT();
    }
    public static boolean isInt(String string) {
        try {
            Integer.parseInt(string);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public static Integer tryInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean inBetween(int i, int min, int max) {
        return i >= min && i <= max;
    }
    public static boolean inBetweenD(double i, double min, double max) {
        if (min > max) {
            return i >= min || i <= max;
        }
        return i >= min && i <= max;
    }
    public static double sub(double i, double sub, double max) {
        double s = i - sub;
        if (s < 0) s = max - (s*-1);
        return s;
    }
    public static String createID() {
        String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(CHARS.length());
            char randomChar = CHARS.charAt(randomIndex);
            sb.append(randomChar);
        }
        return sb.toString();
    }
    public static String[] trimStart(String[] arr, int numToRemove) {
        if (numToRemove > arr.length) {
            return new String[0];
        }
        String[] result = new String[arr.length - numToRemove];
        System.arraycopy(arr, numToRemove, result, 0, result.length);
        return result;
    }
    public static String capitalizeFirst(String string) {
        return string.toUpperCase().charAt(0)+string.substring(1);
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
            if (Utl.inBetween((int) timeTicks, 12010,23992)) str = Assets.symbols.moon;
            else str = Assets.symbols.sun;
            // if thundering always can sleep
            if (world.isThundering()) HUD.weatherIcon = Assets.symbols.moon + Assets.symbols.thunder;
            else HUD.weatherIcon = str + Assets.symbols.rain;
        } else if (Utl.inBetween((int) timeTicks, 12542,23460)) HUD.weatherIcon = Assets.symbols.moon;
        else HUD.weatherIcon = Assets.symbols.sun;
    }
    public static ArrayList<String> xyzSuggester(Player player, String type) {
        ArrayList<String> arr = new ArrayList<>();
        if (type.equalsIgnoreCase("x")) {
            arr.add(String.valueOf(player.getBlockX()));
            arr.add(player.getBlockX()+" "+player.getBlockZ());
            arr.add(player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
        }
        if (type.equalsIgnoreCase("y")) {
            arr.add(String.valueOf(player.getBlockY()));
            arr.add(player.getBlockY()+" "+player.getBlockZ());
        }
        if (type.equalsIgnoreCase("z")) arr.add(String.valueOf(player.getBlockZ()));
        return arr;
    }
    public static ArrayList<String> formatSuggestions(ArrayList<String> suggester, String[] args) {
        ArrayList<String> filteredCompletions = new ArrayList<>();
        String currentInput = args[args.length - 1].toLowerCase();
        for (String completion : suggester) {
            if (completion.toLowerCase().startsWith(currentInput)) {
                filteredCompletions.add(completion);
            }
        }
        return filteredCompletions;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>(List.of());
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
        public static boolean destination(Player player) {
            return player.getPlayer().hasPermission("directionhud.destination");
        }
        public static boolean hud(Player player) {
            return player.getPlayer().hasPermission("directionhud.hud");
        }
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean dirhud(Player player) {
            return player.getPlayer().hasPermission("directionhud.directionhud");
        }
        public static boolean reload(Player player) {
            return player.getPlayer().hasPermission("directionhud.reload");
        }
        public static boolean defaults(Player player) {
            return player.getPlayer().hasPermission("directionhud.defaults");
        }
        public static boolean saving(Player player) {
            return player.getPlayer().hasPermission("directionhud.destination.saving");
        }
        public static boolean lastdeath(Player player) {
            return (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__lastdeath) && config.deathsaving;
        }
        public static boolean send(Player player) {
            return (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__send) && config.social;
        }
        public static boolean track(Player player) {
            return (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track) && config.social;
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
            int[] i = CUtl.color.RGB((String) PlayerData.get.dest.setting.get(player, Destination.Settings.particles__line_color));
            if (particleType.equals(LINE)) return new Particle.DustOptions(Color.fromRGB(i[0],i[1],i[2]), 1);
            i = CUtl.color.RGB((String) PlayerData.get.dest.setting.get(player, Destination.Settings.particles__dest_color));
            if (particleType.equals(DEST)) return new Particle.DustOptions(Color.fromRGB(i[0],i[1],i[2]), 3);
            i = CUtl.color.RGB((String) PlayerData.get.dest.setting.get(player, Destination.Settings.particles__tracking_color));
            if (particleType.equals(TRACKING)) return new Particle.DustOptions(Color.fromRGB(i[0],i[1],i[2]), 0.5f);
            return new Particle.DustOptions(Color.BLACK,1);
        }
    }
    public static class dim {
        public static final List<String> DEFAULT_DIMENSIONS = List.of("overworld|Overworld|#55FF55","nether|Nether|#e8342e","end|End|#edffb0");
        public static final List<String> DEFAULT_RATIOS = List.of("overworld=1|nether=8");
        public static String format(String name) {
            String defaultWorld = Bukkit.getWorlds().get(0).getName();
            if (name.equals(defaultWorld)) return "overworld";
            String[] split = name.split("_");
            return split[split.length-1];
        }
        public static boolean checkValid(String s) {
            return dims.containsKey(s);
        }
        public static String getName(String dim) {
            if (!dims.containsKey(dim)) return "unknown";
            HashMap<String,String> map = dims.get(dim);
            return map.get("name");
        }
        public static boolean canConvert(String DIM1, String DIM2) {
            // both in same dim, cant convert
            if (DIM1.equalsIgnoreCase(DIM2)) return false;
            Pair<String, String> key = new Pair<>(DIM1, DIM2);
            Pair<String, String> flippedKey = new Pair<>(DIM2, DIM1);
            // if the ratio exists, show the button
            return conversionRatios.containsKey(key) || conversionRatios.containsKey(flippedKey);
        }
        public static List<String> getList() {
            return new ArrayList<>(dims.keySet());
        }
        public static String getHEX(String dim) {
            if (!dims.containsKey(dim)) return "#FF0000";
            HashMap<String,String> map = dims.get(dim);
            return map.get("color");
        }
        public static CTxT getLetterButton(String dim) {
            if (!dims.containsKey(dim)) return CTxT.of("X").btn(true).hEvent(CTxT.of("???"));
            HashMap<String,String> map = dims.get(dim);
            return CTxT.of(map.get("name").charAt(0)+"".toUpperCase()).btn(true).color(map.get("color"))
                    .hEvent(CTxT.of(map.get("name").toUpperCase()).color(map.get("color")));
        }
        public static HashMap<Pair<String, String>, Double> conversionRatios = new HashMap<>();
        public static HashMap<String,HashMap<String,String>> dims = new HashMap<>();
        //only works when the server is on, loads server dimensions into the config.
        public static void loadConfig() {
            //LOAD DIM RATIOS
            HashMap<Pair<String, String>, Double> configRatios = new HashMap<>();
            for (String s : config.dimensionRatios) {
                String[] split = s.split("\\|");
                if (split.length != 2) continue;
                double ratio = Double.parseDouble(split[0].split("=")[1])/Double.parseDouble(split[1].split("=")[1]);
                configRatios.put(new Pair<>(split[0].split("=")[0],split[1].split("=")[0]), ratio);
            }
            conversionRatios = configRatios;
            //CONFIG TO MAP
            HashMap<String,HashMap<String,String>> configDims = new HashMap<>();
            for (String s : config.dimensions) {
                String[] split = s.split("\\|");
                if (split.length != 3) continue;
                HashMap<String,String> data = new HashMap<>();
                data.put("name",split[1]);
                data.put("color",split[2]);
                configDims.put(split[0],data);
            }
            dims = configDims;
            //ADD MISSING DIMS TO MAP
            String defaultWorld = Bukkit.getWorlds().get(0).getName();
            for (World world : Bukkit.getWorlds()) {
                String currentDIM = format(world.getName());
                if (!dims.containsKey(currentDIM) && !currentDIM.equals(defaultWorld)) {
                    HashMap<String,String> map = new HashMap<>();
                    // try to make it look better, remove all "_" and "the" and capitalizes the first word. CANT IN SPIGOT SMH
                    String formatted = currentDIM.substring(0,1).toUpperCase()+currentDIM.substring(1);
                    //make random color to spice things up
                    Random random = new Random();
                    int red = random.nextInt(256);
                    int green = random.nextInt(256);
                    int blue = random.nextInt(256);
                    map.put("name",formatted);
                    map.put("color",String.format("#%02x%02x%02x", red, green, blue));
                    dims.put(currentDIM,map);
                }
            }
            //MAP TO CONFIG
            List<String> output = new ArrayList<>();
            for (Map.Entry<String, HashMap<String, String>> entry : dims.entrySet()) {
                String key = entry.getKey();
                HashMap<String, String> data = entry.getValue();
                output.add(key+"|"+data.get("name")+"|"+data.get("color"));
            }
            config.dimensions = output;
        }
    }
}