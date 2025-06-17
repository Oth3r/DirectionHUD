package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.hud.HudColor;
import one.oth3r.directionhud.common.utils.Helper.*;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CUtl {
    public static final Lang LANG = new Lang(""), DLANG = new Lang("directionhud."); // need to migrate everything back to dlang
    public static CTxT tag() {
        return CTxT.of("").append(CTxT.of("DirectionHUD").btn(true).color(p())).append(" ");
    }
    public static String p() {
        return DirectionHUD.getData().getPrimary();
    }
    public static String s() {
        return DirectionHUD.getData().getSecondary();
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
        // if the directionHUD client, use the built-in lang reader
        if (DirectionHUD.getData().isClient()) {
            // we have to first convert all the CTxT's to the built version because minecraft lang reader doesn't know how to process it
            // make a array with the same size of the args
            Object[] fixedArgs = new Object[args.length];
            // for every arg, build & add if CTxT or just add if not
            for (var i = 0; i < args.length; i++) {
                if (args[i] instanceof CTxT) fixedArgs[i] = ((CTxT) args[i]).b();
                else fixedArgs[i] = args[i];
            }
            // return the translated text
            return Utl.getTranslation(key,fixedArgs);
        }
        // else use the custom lang reader, for compatibility, and the lack of a lang reader on the server on fabric
        else {
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
        return LANG.btn(button?"on":"off").btn(true).color(button?'a':'c').hover(LANG.hover("state",
                toggleTxT(!button))).click(1,cmd+(button?"off":"on"));
    }

    /**
     * makes UI line based on the length provided
     */
    public static CTxT makeLine(int length) {
        // add a space for the length of the int
        // return the CTxT
        return CTxT.of("\n"+ " ".repeat(Math.max(0, length))).strikethrough(true);
    }

    /**
     * parses a coded string into a colored CTxT <br>
     * <p>
     *     &1 - primary color <br>
     *     &2 - secondary color <br>
     *     &b - bold <br>
     *     &i - italic <br>
     *     &s - strikethrough <br>
     *     &u - underline <br>
     *     &o - obfuscated <br>
     *     &r - resets all modifiers, but doesn't reset the color <br>
     * </p>
     * @return colored CTxT
     */
    public static CTxT parse(Player player, String input) {
        // assets
        ArrayList<Character> selectors = new ArrayList<>(List.of('1','2','b','i','s','u','o','r'));
        CTxT output = CTxT.of("");

        HudColor color = HudColor.PRIMARY;
        StringBuilder current = new StringBuilder();
        boolean bold = false,
                italic = false,
                strikethrough = false,
                underline = false,
                obfuscated = false;

        int i = 0;
        while (i < input.length()) {
            int ahead = i + 1;
            // if there is a code
            if (input.charAt(i) == '&' && ahead < input.length() &&
                    selectors.contains(input.charAt(ahead))) {
                char code = input.charAt(ahead);

                // append the currently built string
                output.append(colorize(player,current.toString(),color,bold,italic,strikethrough,underline,obfuscated));

                // clear the currently built string
                current.setLength(0);

                // apply the code
                switch (code) {
                    case '1' -> color = HudColor.PRIMARY;
                    case '2' -> color = HudColor.SECONDARY;
                    case 'b' -> bold = true;
                    case 'i' -> italic = true;
                    case 's' -> strikethrough = true;
                    case 'u' -> underline = true;
                    case 'o' -> obfuscated = true;
                    case 'r' -> {
                        bold = false;
                        italic = false;
                        strikethrough = false;
                        underline = false;
                        obfuscated = false;
                    }
                }

                i += 2;
            } else {
                current.append(input.charAt(i));
                i++;
            }
        }

        // build what's left at the end of the loop
        output.append(colorize(player,current.toString(),color,bold,italic,strikethrough,underline,obfuscated));


        return output;
    }

    /**
     * colorizes the string using the settings provided + player hud colors
     * @return colored CTxT
     */
    public static CTxT colorize(Player player, String text, HudColor color,
                                boolean bold, boolean italic, boolean strikethrough,
                                boolean underline, boolean obfuscated) {
        // append the currently built string using the hud rainbow object
        Rainbow hudRainbow = player.getPCache().getRainbow(color);
        CTxT build = Hud.color.addColor(player,new CTxT(text),color,new Rainbow(hudRainbow));

        // only apply bold and italic if enabled
        if (bold) build.bold(true);
        if (italic) build.italic(true);

        // apply the rest of the modifiers
        build.strikethrough(strikethrough).underline(underline).obfuscate(obfuscated);

        return build;
    }

    public static class CButton {
        public static CTxT back(String cmd) {
            return LANG.btn("back").btn(true).color(Assets.mainColors.back).click(1,cmd).hover(CTxT.of(cmd).color(Assets.mainColors.back).append("\n").append(LANG.hover("back")));
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
            return switch (string) {
                case "red" -> "#FF5555";
                case "dark_red" -> "#AA0000";
                case "gold" -> "#FFAA00";
                case "yellow" -> "#FFFF55";
                case "green" -> "#55FF55";
                case "dark_green" -> "#00AA00";
                case "aqua" -> "#55FFFF";
                case "dark_aqua" -> "#00AAAA";
                case "blue" -> "#5555FF";
                case "dark_blue" -> "#0000AA";
                case "pink" -> "#FF55FF";
                case "purple" -> "#AA00AA";
                case "white" -> "#FFFFFF";
                case "gray" -> "#AAAAAA";
                case "dark_gray" -> "#555555";
                case "black" -> "#000000";
                default -> {
                    if (string.isEmpty() || string.charAt(0) != '#') yield format(defaultColor);
                    else yield format(string);
                }
            };
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

        /**
         * converts the character into the corresponding hex color <br>
         * currently using minecraft colors, but todo change later
         */
        public static String format(char character) {
            return switch (character) {
                case 'c' -> Assets.mainColors.off;
                case 'e' -> "#FFFF55";
                case 'a' -> Assets.mainColors.on;
                case '7' -> "#AAAAAA";
                default -> "#FFFFFF";
            };
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
                    for (ColorPreset preset : player.getPData().getColorPresets()) {
                        if (preset.name().equals(name)) {
                            color = preset.color();
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
