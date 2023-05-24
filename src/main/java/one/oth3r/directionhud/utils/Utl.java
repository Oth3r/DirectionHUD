package one.oth3r.directionhud.utils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import one.oth3r.directionhud.files.config;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Utl {
    public static class Pair<A, B> {
        private A first;
        private B second;
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
        public A getFirst() {
            return first;
        }
        public B getSecond() {
            return second;
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
    public static ArrayList<String> xyzSuggester(Player player, String type) {
        ArrayList<String> arr = new ArrayList<>();
        Location location = player.getLocation();
        if (type.equalsIgnoreCase("x")) {
            arr.add(location.getBlockX()+"");
            arr.add(location.getBlockX()+" "+location.getBlockZ());
            arr.add(location.getBlockX()+" "+location.getBlockY()+" "+location.getBlockZ());
        }
        if (type.equalsIgnoreCase("y")) {
            arr.add(location.getBlockY()+"");
            arr.add(location.getBlockY()+" "+location.getBlockZ());
        }
        if (type.equalsIgnoreCase("z")) arr.add(location.getBlockZ()+"");
        return arr;
    }
    public static class player {
        public static List<String> getList() {
            ArrayList<String> array = new ArrayList<>(List.of());
            for (Player p : Bukkit.getOnlinePlayers()) {
                array.add(name(p));
            }
            return array;
        }
        public static Player getFromIdentifier(String s) {
            if (s.contains("-")) Bukkit.getPlayer(UUID.fromString(s));
            return Bukkit.getPlayer(s);
        }
        public static String uuid(Player player) {
            return player.getUniqueId().toString();
        }
        public static String name(Player player) {
            return player.getName();
        }
        public static String dim(Player player) {
            return dim.format(player.getWorld().getName());
        }
        public static void sendAs(String command, Player player) {
            player.performCommand(command);
        }
    }
    public static class dim {
        public static String format(String name) {
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
            for (World world : Bukkit.getWorlds()) {
                //todo
                // ok so worlds are like world_overworld, world_nether, world_end...
                // so we can split by underscore and save the last split part and add it to a arraylist or sm
                // if already in list dont add, becuase spigot can load multiple worlds with the same dimensions
                String currentDIM = format(world.getName());
                if (!dims.containsKey(currentDIM)) {
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

    public static class color {
        // red, dark_red, gold, yellow, green, dark_green, aqua, dark_aqua, blue, dark_blue, pink, purple, white, gray, dark_gray, black
        public static List<String> getList() {
            return new ArrayList<>(Arrays.asList(
                    "red", "dark_red", "gold", "yellow", "green", "dark_green", "aqua", "dark_aqua",
                    "blue", "dark_blue", "pink", "purple", "white", "gray", "dark_gray", "black","ffffff"));
        }
        //todo maybe change this
        public static ChatColor getTC(String color) {
            if (color.equals("red")) return CUtl.TC('c');
            if (color.equals("dark_red")) return CUtl.TC('4');
            if (color.equals("gold")) return CUtl.TC('6');
            if (color.equals("yellow")) return CUtl.TC('e');
            if (color.equals("green")) return CUtl.TC('a');
            if (color.equals("dark_green")) return CUtl.TC('2');
            if (color.equals("aqua")) return CUtl.TC('b');
            if (color.equals("dark_aqua")) return CUtl.TC('3');
            if (color.equals("blue")) return CUtl.TC('9');
            if (color.equals("dark_blue")) return CUtl.TC('1');
            if (color.equals("pink")) return CUtl.TC('d');
            if (color.equals("purple")) return CUtl.TC('5');
            if (color.equals("white")) return CUtl.TC('f');
            if (color.equals("gray")) return CUtl.TC('7');
            if (color.equals("dark_gray")) return CUtl.TC('8');
            if (color.equals("black")) return CUtl.TC('0');
            if (color.charAt(0)=='#') return CUtl.HEX(color);
            return CUtl.TC('f');
        }
        public static int getCodeRGB(String color) {
            if (color.equals("red")) return 16733525;
            if (color.equals("dark_red")) return 11141120;
            if (color.equals("gold")) return 16755200;
            if (color.equals("yellow")) return 16777045;
            if (color.equals("green")) return 5635925;
            if (color.equals("dark_green")) return 43520;
            if (color.equals("aqua")) return 5636095;
            if (color.equals("dark_aqua")) return 43690;
            if (color.equals("blue")) return 5592575;
            if (color.equals("dark_blue")) return 170;
            if (color.equals("pink")) return 16733695;
            if (color.equals("purple")) return 11141290;
            if (color.equals("white")) return 16777215;
            if (color.equals("gray")) return 11184810;
            if (color.equals("dark_gray")) return 5592405;
            if (color.equals("black")) return 0;
            if (color.equals("rainbow")) return 16777215;
            if (color.charAt(0)=='#') return hexToRGB(color);
            return 16777215;
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
            if (s.length() == 6) return "#"+s;
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
        public static TextComponent rainbow(String string, float start, float step) {
            float hue = start % 360f;
            TextComponent text = new TextComponent();
            for (int i = 0; i < string.codePointCount(0, string.length()); i++) {
                if (string.charAt(i) == ' ') {
                    text.addExtra(new TextComponent(" "));
                    continue;
                }
                Color color = Color.getHSBColor(hue / 360.0f, 1.0f, 1.0f);
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();
                String hexColor = String.format("#%02x%02x%02x", red, green, blue);
                TextComponent letter = new TextComponent(Character.toString(string.codePointAt(i)));
                letter.setColor(CUtl.HEX(hexColor));
                text.addExtra(letter);
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
