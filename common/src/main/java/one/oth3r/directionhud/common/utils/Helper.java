package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;

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
}
