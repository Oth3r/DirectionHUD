package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.*;

public class Helper {
    public static class Enums {
        public static <T extends Enum<T>> ArrayList<T> toArrayList(T[] array) {
            return new ArrayList<>(Arrays.asList(array));
        }
        public static <T extends Enum<T>> ArrayList<String> toStringList(ArrayList<T> enumList) {
            ArrayList<String> stringList = new ArrayList<>();
            for (T entry:enumList) stringList.add(entry.toString());
            return stringList;
        }
        public static <T extends Enum<T>> ArrayList<T> toEnumList(ArrayList<String> stringList, Class<T> enumType, boolean... setting) {
            boolean settingMode = setting != null;
            ArrayList<T> moduleList = new ArrayList<>();
            for (String module:stringList) {
                try {
                    T enumValue = Enum.valueOf(enumType, module);
                    if (settingMode) enumValue = Enum.valueOf(enumType, module.replace(".","__"));
                    moduleList.add(enumValue);
                } catch (IllegalArgumentException ignored) {}
            }
            return moduleList;
        }
        @SafeVarargs
        public static <T extends Enum<T>> T next(T current, Class<T> enumType, T... exclude) {
            T[] values = enumType.getEnumConstants();
            T next = values[(current.ordinal()+1)%values.length];
            if (exclude != null) {
                for (T item:exclude)
                    if (item.equals(next))
                        return next(next,enumType,exclude);
            }
            return next;
        }
    }
    public static class Num {
        public static boolean isInt(String string) {
            try {
                Integer.parseInt(string);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        }
        public static Integer toInt(String s) {
            // return an int no matter what
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                try {
                    return (int) Double.parseDouble(s);
                } catch (NumberFormatException e2) {
                    return 0;
                }
            }
        }
        public static boolean isNum(String s) {
            // checks if int or a double
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e1) {
                try {
                    Double.parseDouble(s);
                    return true;
                } catch (NumberFormatException e2) {
                    return false;
                }
            }
        }

        public static boolean inBetween(double num, double min, double max) {
            // if min is greater than max, flip
            if (min > max) return num >= min || num <= max;
            return num >= min && num <= max;
        }

        public static double wSubtract(double num, double sub, double max) {
            // wrapped subtract
            double output = num - sub;
            if (output < 0) output = max - (output*-1);
            return output;
        }

        public static double wAdd(double num, double add, double max) {
            // wrapped add
            return (num+add)%max;
        }
    }
    public static class Command {
        public static class Suggester {
            public static ArrayList<String> xyz(Player player, String type) {
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
            public static List<String> players(Player player) {
                //get player strings excluding the inputted player
                ArrayList<String> list = new ArrayList<>();
                for (Player entry: Utl.getPlayers())
                    if (!entry.equals(player)) list.add(entry.getName());
                return list;
            }
            public static String colorHandler(Player player, String color) {
                return colorHandler(player, color, "#ffffff");
            }
            public static String colorHandler(Player player, String color, String defaultColor) {
                //if color is preset, get the preset color
                if (color != null && color.contains("preset"))
                    color = PlayerData.get.colorPresets(player).get(Integer.parseInt(color.substring(7))-1);
                color = CUtl.color.format(color,defaultColor);
                return color;
            }
        }
        public static String[] quoteHandler(String[] args) {
            // put quoted items all in one arg
            boolean quote = false;
            ArrayList<String> output = new ArrayList<>();
            for (String s : args) {
                int lastIndex = output.size()-1;
                if (s.contains("\"")) {
                    // count how many quotes there are
                    int count = s.length() - s.replaceAll("\"","").length();
                    // remove the quote
                    s = s.replace("\"","");
                    if (quote) {
                        quote = false;
                        output.add(s); // start of the quote, so just add as normal
                    } else if (count == 1) quote = true; // only flip the quote if there's one quotation mark
                }
                if (quote) output.set(lastIndex,output.get(lastIndex)+" "+s); // add a space between entries while in a quote
                else output.add(s);
            }
            // remove all blank entries
            output.removeAll(Collections.singleton(""));
            String[] arr = new String[output.size()];
            return output.toArray(arr);
        }
    }
    public static String createID() {
        //spigot not having apache smh smh
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
        if (numToRemove > arr.length) return new String[0];
        String[] result = new String[arr.length - numToRemove];
        System.arraycopy(arr, numToRemove, result, 0, result.length);
        return result;
    }
    public static String capitalizeFirst(String string) {
        return string.toUpperCase().charAt(0)+string.substring(1);
    }
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
}
