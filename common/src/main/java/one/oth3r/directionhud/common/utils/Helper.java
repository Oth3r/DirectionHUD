package one.oth3r.directionhud.common.utils;

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
}
