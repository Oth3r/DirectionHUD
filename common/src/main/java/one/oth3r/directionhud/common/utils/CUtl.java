package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.DHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.LangReader;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CUtl {
    public static CTxT LARGE = CTxT.of("\n                                             ").strikethrough(true);
    public static CTxT LINE_35 = CTxT.of("\n                                   ").strikethrough(true);
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
        return tag().append(lang("error").color(Assets.mainColors.error)).append(" ").append(lang("error."+key, args));
    }
    public static CTxT usage(String s) {
        return tag().append(lang("usage").color(Assets.mainColors.usage)).append(" ").append(s);
    }
    public static CTxT lang(String key, Object... args) {
        if (DirectionHUD.isClient) {
            Object[] fixedArgs = new Object[args.length];
            for (var i = 0;i < args.length;i++) {
                if (args[i] instanceof CTxT) fixedArgs[i] = ((CTxT) args[i]).b();
                else fixedArgs[i] = args[i];
            }
            return Utl.getTranslation("key.directionhud."+key,fixedArgs);
        } else {
            return LangReader.of("key.directionhud."+key, args).getTxT();
        }
    }
    // OFF/ON (COLOR)
    public static CTxT toggleTxT(boolean button) {
        return CUtl.TBtn(button?"off":"on").color(button?'c':'a');
    }
    // [OFF/ON] (COLOR & FUNCTIONALITY)
    public static CTxT toggleBtn(boolean button, String cmd) {
        return CUtl.TBtn(button?"on":"off").btn(true).color(button?'a':'c').hEvent(CUtl.TBtn("state.hover",
                toggleTxT(button))).cEvent(1,cmd+(button?"off":"on"));
    }
    public static String formatCMD(String cmd) {
        return cmd.substring(1).replace(" ", "-");
    }
    public static String unFormatCMD(String cmd) {
        return "/"+cmd.replace("-"," ");
    }
    public static CTxT TBtn(String key, Object... args) {
        return lang("button."+key,args);
    }
    public static class CButton {
        public static CTxT back(String cmd) {
            return TBtn("back").btn(true).color(Assets.mainColors.back).cEvent(1,cmd).hEvent(CTxT.of(cmd).color(Assets.mainColors.back).append("\n").append(TBtn("back.hover")));
        }
        public static class dest {
            public static CTxT convert(String cmd) {
                return CTxT.of(Assets.symbols.convert).btn(true).color(Assets.mainColors.convert).cEvent(1,cmd).hEvent(
                        CTxT.of(cmd).color(Assets.mainColors.convert).append("\n").append(TBtn("dest.convert.hover")));
            }
            public static CTxT set(String cmd) {
                return TBtn("dest.set").btn(true).color(Assets.mainColors.set).cEvent(1,cmd).hEvent(
                        CTxT.of(cmd).color(Assets.mainColors.set).append("\n").append(TBtn("dest.set.hover")));
            }
            public static CTxT edit(int t, String cmd) {
                return CTxT.of(Assets.symbols.pencil).btn(true).color(Assets.mainColors.edit).cEvent(t,cmd).hEvent(TBtn("dest.edit.hover").color(Assets.mainColors.edit)).color(Assets.mainColors.edit);
            }
            public static CTxT settings() {
                return TBtn("settings").btn(true).color(Assets.mainColors.setting).cEvent(1,"/dest settings")
                        .hEvent(CTxT.of(Assets.cmdUsage.destSettings).color(Assets.mainColors.setting).append("\n").append(TBtn("settings.hover",lang("hud.module.destination"))));
            }
            public static CTxT saved() {
                return TBtn("dest.saved").btn(true).color(Assets.mainColors.saved).cEvent(1,"/dest saved").hEvent(
                        CTxT.of(Assets.cmdUsage.destSaved).color(Assets.mainColors.saved).append("\n").append(TBtn("dest.saved.hover")));
            }
            public static CTxT add() {
                return CTxT.of("+").btn(true).color(Assets.mainColors.add).cEvent(2,"/dest add ").hEvent(
                        CTxT.of(Assets.cmdUsage.destAdd).color(Assets.mainColors.add).append("\n").append(TBtn("dest.add.hover",TBtn("dest.add.hover_2").color(Assets.mainColors.add))));
            }
            public static CTxT add(String cmd) {
                return CTxT.of("+").btn(true).color(Assets.mainColors.add).cEvent(2,cmd).hEvent(
                        TBtn("dest.add.hover_save",TBtn("dest.add.hover_2").color(Assets.mainColors.add)));
            }
            public static CTxT set() {
                return TBtn("dest.set").btn(true).color(Assets.mainColors.set).cEvent(2,"/dest set ").hEvent(
                        CTxT.of(Assets.cmdUsage.destSet).color(Assets.mainColors.set).append("\n").append(TBtn("dest.set.hover_info")));
            }
            public static CTxT clear(Player player) {
                boolean o = Destination.get(player).hasXYZ();
                return CTxT.of(Assets.symbols.x).btn(true).color(o?'c':'7').cEvent(o?1:0,"/dest clear").hEvent(
                        CTxT.of(Assets.cmdUsage.destClear).color(o?'c':'7').append("\n").append(TBtn("dest.clear.hover")));
            }
            public static CTxT lastdeath() {
                return TBtn("dest.lastdeath").btn(true).color(Assets.mainColors.lastdeath).cEvent(1,"/dest lastdeath").hEvent(
                        CTxT.of(Assets.cmdUsage.destLastdeath).color(Assets.mainColors.lastdeath).append("\n").append(TBtn("dest.lastdeath.hover")));
            }
            public static CTxT send() {
                return TBtn("dest.send").btn(true).color(Assets.mainColors.send).cEvent(2,"/dest send ").hEvent(
                        CTxT.of(Assets.cmdUsage.destSend).color(Assets.mainColors.send).append("\n").append(TBtn("dest.send.hover")));
            }
            public static CTxT track() {
                return TBtn("dest.track").btn(true).color(Assets.mainColors.track).cEvent(2,"/dest track set").hEvent(
                        CTxT.of(Assets.cmdUsage.destTrack).color(Assets.mainColors.track).append("\n").append(TBtn("dest.track.hover")));
            }
            public static CTxT trackX() {
                return CTxT.of(Assets.symbols.x).btn(true).color('c').cEvent(1,"/dest track clear").hEvent(
                        CTxT.of(Assets.cmdUsage.destTrackClear).color('c').append("\n").append(TBtn("dest.track_clear.hover")));
            }
        }
        public static class hud {
            public static CTxT color() {
                return TBtn("hud.color").rainbow(true,15f,45f).btn(true).cEvent(1,"/hud color")
                        .hEvent(CTxT.of("").append(CTxT.of(Assets.cmdUsage.hudColor).rainbow(true,10f,23f)).append("\n").append(TBtn("hud.color.hover")));
            }
            public static CTxT modules() {
                return TBtn("hud.modules").btn(true).color(Assets.mainColors.edit).cEvent(1,"/hud modules").hEvent(
                        CTxT.of(Assets.cmdUsage.hudModules).color(Assets.mainColors.edit).append("\n").append(TBtn("hud.modules.hover")));
            }
            public static CTxT settings() {
                return TBtn("settings").btn(true).color(Assets.mainColors.setting).cEvent(1,"/hud settings").hEvent(
                        CTxT.of(Assets.cmdUsage.hudSettings).color(Assets.mainColors.setting).append("\n").append(TBtn("settings.hover",lang("hud"))));
            }
        }
        public static class DHUD {
            public static CTxT hud() {
                return TBtn("hud").btn(true).color(Assets.mainColors.hud).cEvent(1,"/hud").hEvent(
                        CTxT.of(Assets.cmdUsage.hud).color(Assets.mainColors.hud).append("\n").append(TBtn("hud.hover")));
            }
            public static CTxT dest() {
                return TBtn("dest").btn(true).color(Assets.mainColors.dest).cEvent(1,"/dest").hEvent(
                        CTxT.of(Assets.cmdUsage.dest).color(Assets.mainColors.dest).append("\n").append(TBtn("dest.hover")));
            }
            public static CTxT inbox() {
                return TBtn("dhud.inbox").btn(true).color(Assets.mainColors.inbox).cEvent(1,"/dhud inbox").hEvent(
                        CTxT.of(Assets.cmdUsage.inbox).color(Assets.mainColors.inbox).append("\n").append(TBtn("dhud.inbox.hover")));
            }
            public static CTxT reload() {
                return TBtn("dhud.reload").btn(true).color(Assets.mainColors.reload).cEvent(1,"/dhud reload").hEvent(
                        CTxT.of(Assets.cmdUsage.reload).color(Assets.mainColors.reload).append("\n").append(TBtn("dhud.reload.hover")));
            }
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
            if (color != null && color.contains("preset-")) {
                String name = color.split("-")[1];
                // find the right preset and get the color
                for (String preset : PlayerData.get.colorPresets(player)) {
                    if (DHUD.preset.custom.getName(preset).equals(name)) {
                        color = DHUD.preset.custom.getColor(preset);
                        break;
                    }
                }
            }
            if (color != null)
                switch (color.toLowerCase()) {
                    case "red" -> color = DEFAULT_COLORS.get(1);
                    case "orange" -> color = DEFAULT_COLORS.get(4);
                    case "yellow" -> color = DEFAULT_COLORS.get(7);
                    case "green" -> color = DEFAULT_COLORS.get(10);
                    case "blue" -> color = DEFAULT_COLORS.get(13);
                    case "purple" -> color = DEFAULT_COLORS.get(16);
                    case "gray" -> color = DEFAULT_COLORS.get(19);
                }
            color = format(color,defaultColor);
            return color;
        }
    }
}
