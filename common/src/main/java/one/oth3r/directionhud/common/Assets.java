package one.oth3r.directionhud.common;

import one.oth3r.directionhud.common.files.config;

public class Assets {
    public static class mainColors {
        public static final String convert = "#ffa93f";
        public static final String set = "#fff540";
        public static final String saved = "#1ee16f";
        public static final String add = "#36ff89";
        public static final String setting = "#e9e9e9";
        public static final String lastdeath = "#ac4dff";
        public static final String send = "#52e1ff";
        public static final String track = "#ff6426";
        public static final String edit = "#665dff";
        public static final String dest = "#29a2ff";
        public static final String hud = "#29ff69";
        public static final String defaults = "#ff6629";
        public static final String reload = "#69ff29";
        public static final String back = "#ff9500";
        public static final String usage = "#ff8b38";
        public static final String error = "#ff4646";
        public static final String gray = "#8d8d8d";
        public static final String custom = "#c4ff14";
        public static final String presets = "#2dedff";
    }
    public static String barColor(config.BarColors color) {
        StringBuilder output = new StringBuilder();
        switch (color) {
            case pink -> output.append("#ec00b8");
            case blue -> output.append("#00b7ec");
            case red -> output.append("#ec3500");
            case green -> output.append("#1dec00");
            case yellow -> output.append("#e9ec00");
            case purple -> output.append("#7b00ec");
            default -> output.append("ececec");
        }
        return output.toString();
    }
    public static class cmdUsage {
        public static final String hud = "/hud";
        public static final String hudToggle = "/hud toggle";
        public static final String hudColor = "/hud color";
        public static final String hudModules = "/hud modules";
        public static final String hudSettings = "/hud settings";
        public static final String dest = "/dest | /destination";
        public static final String destAdd = "/dest (saved) add <name> <x> (y) <z> (dimension) (color)";
        public static final String destSet = "/dest set <x> (y) <z> (dimension) (convert) | /dest set saved <name> (convert)";
        public static final String destLastdeath = "/dest lastdeath";
        public static final String destClear = "/dest clear";
        public static final String destSaved = "/dest saved";
        public static final String destSettings = "/dest settings";
        public static final String destSend = "/dest send <IGN> saved <name> | /dest send <IGN> (name) <x> (y) <z> (dimension)";
        public static final String destTrack = "/dest track <IGN> | /dest track .clear";
        public static final String destTrackClear = "/dest track .clear";
        public static final String defaults = "/dirhud defaults";
        public static final String reload = "/dirhud reload";
    }
    public static class symbols {
        public static final String square = "\u2588";
        public static final String up = "\u25b2";
        public static final String down = "\u25bc";
        public static final String left = "\u25c0";
        public static final String right = "\u25b6";
        public static final String x = "\u2715";
        public static final String pencil = "\u270e";
        public static final String sun = "\u2600";
        public static final String moon = "\u263d";
        public static final String rain = "\ud83c\udf27";
        public static final String thunder = "\u26c8";
        public static final String link = "\u29c9";
        public static final String envelope = "\u2709";
        public static final String lighting_bolt = "\u26a1";
    }
}
