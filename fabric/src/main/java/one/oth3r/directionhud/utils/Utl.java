package one.oth3r.directionhud.utils;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import org.apache.commons.lang3.RandomStringUtils;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.regex.Pattern;

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
        return RandomStringUtils.random(8, true, true);
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
        else if (obj instanceof Text) txt.append((Text) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static void setTime() {
        World world = DirectionHUD.server.getOverworld();
        long timeTicks = world.getTimeOfDay();
        HUD.hour = (int) ((timeTicks / 1000 + 6) % 24);
        HUD.minute = (int) ((timeTicks % 1000) * 60 / 1000);
        if (world.isRaining()) {
            String str;
            if (world.isNight()) str = CUtl.symbols.moon();
            else str = CUtl.symbols.sun();
            if (world.isThundering()) HUD.weatherIcon = str + CUtl.symbols.thunder();
            else HUD.weatherIcon = str + CUtl.symbols.rain();
        } else if (world.isNight()) HUD.weatherIcon = CUtl.symbols.moon();
        else HUD.weatherIcon = CUtl.symbols.sun();
    }
    public static ArrayList<String> xyzSuggester(Player player, String type) {
        ArrayList<String> arr = new ArrayList<>();
        if (type.equalsIgnoreCase("x")) {
            arr.add(player.getBlockX()+"");
            arr.add(player.getBlockX()+" "+player.getBlockZ());
            arr.add(player.getBlockX()+" "+player.getBlockY()+" "+player.getBlockZ());
        }
        if (type.equalsIgnoreCase("y")) {
            arr.add(player.getBlockY()+"");
            arr.add(player.getBlockY()+" "+player.getBlockZ());
        }
        if (type.equalsIgnoreCase("z")) arr.add(player.getBlockZ()+"");
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
        public static boolean destination(Player player) {
            return true;
        }
        public static boolean hud(Player player) {
            return config.HUDEditing;
        }
        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public static boolean dirhud(Player player) {
            return true;
        }
        public static boolean reload(Player player) {
            return DirectionHUD.server.isRemote() && player.getPlayer().hasPermissionLevel(2);
        }
        public static boolean defaults(Player player) {
            return !DirectionHUD.server.isRemote() && player.getPlayer().hasPermissionLevel(2);
        }
        public static boolean saving(Player player) {
            return config.DESTSaving;
        }
        public static boolean lastdeath(Player player) {
            return PlayerData.get.dest.setting.lastdeath(player) && config.deathsaving;
        }
        public static boolean send(Player player) {
            return PlayerData.get.dest.setting.send(player) && config.social && DirectionHUD.server.isRemote();
        }
        public static boolean track(Player player) {
            return PlayerData.get.dest.setting.track(player) && config.social && DirectionHUD.server.isRemote();
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
            if (particleType.equals(LINE))
                return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(
                        Utl.color.hexToRGB(Utl.color.getFromTextString(PlayerData.get.dest.setting.particle.linecolor(player)))).toVector3f()),1);
            if (particleType.equals(DEST))
                return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(
                        Utl.color.hexToRGB(Utl.color.getFromTextString(PlayerData.get.dest.setting.particle.destcolor(player)))).toVector3f()),3);
            if (particleType.equals(TRACKING))
                return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(
                        Utl.color.hexToRGB(Utl.color.getFromTextString(PlayerData.get.dest.setting.particle.trackingcolor(player)))).toVector3f()),0.5f);
            return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Utl.color.hexToRGB("#000000")).toVector3f()),1);
        }
    }
    public static class dim {
        public static String format(Identifier identifier) {
            return identifier.toString().replace(":",".");
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
            if (DirectionHUD.server == null) return;
            //LOAD DIM RATIOS
            HashMap<Pair<String, String>, Double> configRatios = new HashMap<>();
            for (String s : config.dimensionRatios) {
                String[] split = s.split("\\|");
                if (split.length != 2) continue;
                double ratio = Double.parseDouble(split[0].split("=")[1])/Double.parseDouble(split[1].split("=")[1]);
                configRatios.put(new Pair<>(split[0].split("=")[0], split[1].split("=")[0]), ratio);
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
            for (ServerWorld world : DirectionHUD.server.getWorlds()) {
                String currentDIM = world.getRegistryKey().getValue().toString().replace(":",".");
                String currentDIMp = world.getRegistryKey().getValue().getPath();
                if (!dims.containsKey(currentDIM)) {
                    HashMap<String,String> map = new HashMap<>();
                    // try to make it look better, remove all "_" and "the" and capitalizes the first word.
                    String formatted = currentDIMp.replaceAll("_"," ");
                    formatted = formatted.replaceFirst("the ","");
                    formatted = formatted.substring(0,1).toUpperCase()+formatted.substring(1);
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

    public static class color {
        // red, dark_red, gold, yellow, green, dark_green, aqua, dark_aqua, blue, dark_blue, pink, purple, white, gray, dark_gray, black
        public static List<String> getList() {
            return new ArrayList<>(Arrays.asList(
                    "red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
                    "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black","ffffff"));
        }
        //todo maybe change this
        public static String getFromTextString(String color) {
            if (color.equals("red")) return "#FF5555";
            if (color.equals("dark_red")) return "#AA0000";
            if (color.equals("gold")) return "#FFAA00";
            if (color.equals("yellow")) return "#FFFF55";
            if (color.equals("green")) return "#55FF55";
            if (color.equals("dark_green")) return "#00AA00";
            if (color.equals("aqua")) return "#55FFFF";
            if (color.equals("dark_aqua")) return "#00AAAA";
            if (color.equals("blue")) return "#5555FF";
            if (color.equals("dark_blue")) return "#0000AA";
            if (color.equals("pink")) return "#FF55FF";
            if (color.equals("purple")) return "#AA00AA";
            if (color.equals("white")) return "#FFFFFF";
            if (color.equals("gray")) return "#AAAAAA";
            if (color.equals("dark_gray")) return "#555555";
            if (color.equals("black")) return "#000000";
            if (color.charAt(0)=='#') return color;
            if (color.length()==6) return "#"+color;
            return "ffffff";
        }
        public static int hexToRGB(String hexColor) {
            // Remove the # symbol if it exists
            if (hexColor.charAt(0) == '#') {
                hexColor = hexColor.substring(1);
            }
            // Convert the hex string to an integer
            int colorValue = Integer.parseInt(hexColor, 16);
            // Separate the red, green, and blue values from the integer
            int red = (colorValue >> 16) & 0xFF;
            int green = (colorValue >> 8) & 0xFF;
            int blue = colorValue & 0xFF;
            // Combine the values into an RGB integer
            return (red << 16) | (green << 8) | blue;
        }
        private static boolean checkValid(String s) {
            List<String> colors = new ArrayList<>(Arrays.asList(
                    "red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
                    "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black"));
            if (s.charAt(0) == '#') return true;
            return colors.contains(s);
        }
        public static String fix(String s,boolean enableRainbow, String Default) {
            if (checkValid(s)) return s.toLowerCase();
            if (s.equals("rainbow") && enableRainbow) return s;
            if (s.equalsIgnoreCase("light_purple")) return "pink";
            if (s.equalsIgnoreCase("dark_purple")) return "purple";
            if (s.length() == 6) s = "#"+s;
            if (s.length() == 7) {
                String regex = "^#([A-Fa-f0-9]{6})$";
                Pattern pattern = Pattern.compile(regex);
                java.util.regex.Matcher matcher = pattern.matcher(s);
                if (matcher.matches()) return s;
            }
            return Default;
        }
        public static String formatPlayer(String s, boolean caps) {
            if (caps) s=s.toUpperCase();
            else s=s.toLowerCase();
            if (checkValid(s.toLowerCase())) return s.replace('_', ' ');
            if (s.length() == 6) return "#"+s;
            if (s.equalsIgnoreCase("rainbow")) return s;
            return caps? "WHITE":"white";
        }
        public static MutableText rainbow(String string, float start, float step) {
            float hue = start % 360f;
            MutableText text = Text.literal("");
            for (int i = 0; i < string.codePointCount(0, string.length()); i++) {
                if (string.charAt(i) == ' ') {
                    text.append(Text.literal(" "));
                    continue;
                }
                Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                text.append(Text.literal(Character.toString(string.codePointAt(i))).styled(style -> style.withColor(TextColor.parse(hexColor))));
                hue = ((hue % 360f)+step)%360f;
            }
            return text;
        }
        public static Color toColor(String string) {
            if (string.equals("red")) return Color.decode("#FF5555");
            if (string.equals("dark_red")) return Color.decode("#AA0000");
            if (string.equals("gold")) return Color.decode("#FFAA00");
            if (string.equals("yellow")) return Color.decode("#FFFF55");
            if (string.equals("green")) return Color.decode("#55FF55");
            if (string.equals("dark_green")) return Color.decode("#00AA00");
            if (string.equals("aqua")) return Color.decode("#55FFFF");
            if (string.equals("dark_aqua")) return Color.decode("#00AAAA");
            if (string.equals("blue")) return Color.decode("#5555FF");
            if (string.equals("dark_blue")) return Color.decode("#0000AA");
            if (string.equals("pink")) return Color.decode("#FF55FF");
            if (string.equals("purple")) return Color.decode("#AA00AA");
            if (string.equals("white")) return Color.decode("#FFFFFF");
            if (string.equals("gray")) return Color.decode("#AAAAAA");
            if (string.equals("dark_gray")) return Color.decode("#555555");
            if (string.equals("black")) return Color.decode("#000000");
            if (string.charAt(0)=='#') return Color.decode(string);
            return Color.WHITE;
        }
    }
}
