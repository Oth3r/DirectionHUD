package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.Player;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
    public static Integer forceInt(String s) {
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
    public static class command {
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
            String[] arr = new String[output.size()];
            return output.toArray(arr);
        }
    }
    public static boolean inBetween(double i, double min, double max) {
        // if min is greater than max, flip
        if (min > max) return i >= min || i <= max;
        return i >= min && i <= max;
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
    public static String createID() {
        return RandomStringUtils.random(8, true, true);
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
