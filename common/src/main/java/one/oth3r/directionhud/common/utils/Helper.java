package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.Player;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.Objects;

public class Helper {
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
