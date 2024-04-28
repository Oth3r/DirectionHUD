package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CUtl {
    public static final Lang LANG = new Lang("");
    public static CTxT tag() {
        return CTxT.of("").append(CTxT.of("DirectionHUD").btn(true).color(p())).append(" ");
    }
    public static String p() {
        return DirectionHUD.PRIMARY;
    }
    public static String s() {
        return DirectionHUD.SECONDARY;
    }
    public static CTxT error(String key, Object... args) {
        return error().append(getLangEntry("error."+key, args));
    }
    public static CTxT error() {
        return tag().append(getLangEntry("msg.error").color(Assets.mainColors.error)).append(" ");
    }
    public static CTxT usage(String s) {
        return tag().append(getLangEntry("msg.usage").color(Assets.mainColors.usage)).append(" ").append(s);
    }
    /**
     * get an entry from the lang file without a prefix
     * @param key lang key
     * @param args arguments
     * @return the CTxT of the entry
     */
    public static CTxT getLangEntry(String key, Object... args) {
        if (DirectionHUD.isClient) {
            Object[] fixedArgs = new Object[args.length];
            for (var i = 0;i < args.length;i++) {
                if (args[i] instanceof CTxT) fixedArgs[i] = ((CTxT) args[i]).b();
                else fixedArgs[i] = args[i];
            }
            return Utl.getTranslation(key,fixedArgs);
        } else {
            return LangReader.of(key, args).getTxT();
        }
    }
    public static String toggleColor(boolean button) {
        return button?"#55FF55":"#FF5555";
    }
    // OFF/ON w/COLOR
    public static CTxT toggleTxT(boolean button) {
        return LANG.btn(button?"on":"off").color(toggleColor(button));
    }
    // [OFF/ON] (COLOR & FUNCTIONALITY)
    public static CTxT toggleBtn(boolean button, String cmd) {
        return LANG.btn(button?"on":"off").btn(true).color(button?'a':'c').hEvent(LANG.hover("state",
                toggleTxT(!button))).cEvent(1,cmd+(button?"off":"on"));
    }
    public static class CButton {
        public static CTxT back(String cmd) {
            return LANG.btn("back").btn(true).color(Assets.mainColors.back).cEvent(1,cmd).hEvent(CTxT.of(cmd).color(Assets.mainColors.back).append("\n").append(LANG.hover("back")));
        }
    }
    public static class color {
        public static final List<String> DEFAULT_COLORS = List.of(
                "#ff5757","#d40000","#900000",
                "#ffa562","#ff9834","#e77400",
                "#ffff86","#ffff5b","#f9c517",
                "#9aff9a","#5dc836","#396a30",
                "#8ddfff","#0099ff","#004995",
                "#a38cff","#8c04dd","#5c00a7",
                "#d9d9d9","#808080","#404040");
        public static String updateOld(String string,String defaultColor) {
            if (string.equals("red")) return "#FF5555";
            if (string.equals("dark_red")) return "#AA0000";
            if (string.equals("gold")) return "#FFAA00";
            if (string.equals("yellow")) return "#FFFF55";
            if (string.equals("green")) return "#55FF55";
            if (string.equals("dark_green")) return "#00AA00";
            if (string.equals("aqua")) return "#55FFFF";
            if (string.equals("dark_aqua")) return "#00AAAA";
            if (string.equals("blue")) return "#5555FF";
            if (string.equals("dark_blue")) return "#0000AA";
            if (string.equals("pink")) return "#FF55FF";
            if (string.equals("purple")) return "#AA00AA";
            if (string.equals("white")) return "#FFFFFF";
            if (string.equals("gray")) return "#AAAAAA";
            if (string.equals("dark_gray")) return "#555555";
            if (string.equals("black")) return "#000000";
            if (string.charAt(0)=='#') return format(string);
            return format(defaultColor);
        }
        public static boolean checkValid(String color, String current) {
            //checks the validity of the color by seeing if it resets.
            //if color isn't current color, test if its valid
            if (!CUtl.color.format(color).equals(current)) {
                //format the color and set default to current
                color = CUtl.color.format(color, current);
                //if color is current (it reset), it's not valid
                return !color.equals(current);
            }
            return true;
        }
        public static String format(String hex, String defaultColor) {
            if (hex == null) return format(defaultColor);
            if (hex.length() == 6) hex = "#"+hex;
            if (hex.length() == 7) {
                String regex = "^#([A-Fa-f0-9]{6})$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(hex);
                if (matcher.matches()) return hex.toLowerCase();
            }
            return format(defaultColor);
        }
        public static String format(String hex) {
            return format(hex,"#ffffff");
        }
        public static CTxT getBadge(String hex) {
            return CTxT.of(Assets.symbols.square+" "+format(hex).toUpperCase()).color(hex);
        }
        public static float[] HSB(String hex) {
            Color color = Color.decode(format(hex));
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();
            float[] hsb = new float[3];
            Color.RGBtoHSB(r, g, b, hsb);
            return hsb;
        }
        public static int[] RGB(String hex) {
            Color color = Color.decode(format(hex));
            int[] i = new int[3];
            i[0] = color.getRed();
            i[1] = color.getGreen();
            i[2] = color.getBlue();
            return i;
        }
        public static String HSBtoHEX(float[] hsb) {
            Color color = new Color(Color.HSBtoRGB(hsb[0],hsb[1],hsb[2]));
            return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        }
        public static String editHSB(int type, String hex, float change) {
            float[] hsb = HSB(hex);
            hsb[type] = Math.max(Math.min(hsb[type]+change,1),0);
            return HSBtoHEX(hsb);
        }
        public static String colorHandler(Player player, String color) {
            return colorHandler(player, color, "#ffffff");
        }
        public static String colorHandler(Player player, String color, String defaultColor) {
            //if color is preset, get the preset color
            if (color != null) {
                if (color.contains("preset-")) {
                    String name = color.split("-")[1];
                    // find the right preset and get the color
                    for (String preset : player.getPData().getColorPresets()) {
                        if (DHud.preset.custom.getName(preset).equals(name)) {
                            color = DHud.preset.custom.getColor(preset);
                            break;
                        }
                    }
                }
                switch (color.toLowerCase()) {
                    case "red" -> color = DEFAULT_COLORS.get(1);
                    case "orange" -> color = DEFAULT_COLORS.get(4);
                    case "yellow" -> color = DEFAULT_COLORS.get(7);
                    case "green" -> color = DEFAULT_COLORS.get(10);
                    case "blue" -> color = DEFAULT_COLORS.get(13);
                    case "purple" -> color = DEFAULT_COLORS.get(16);
                    case "gray" -> color = DEFAULT_COLORS.get(19);
                }
            }
            color = format(color,defaultColor);
            return color;
        }
    }
}
