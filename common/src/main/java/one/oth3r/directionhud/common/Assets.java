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
        public static final String inbox = "#7a6ef0";
        public static final String reload = "#69ff29";
        public static final String back = "#ff9500";
        public static final String usage = "#ff8b38";
        public static final String error = "#ff4646";
        public static final String gray = "#8d8d8d";
        public static final String custom = "#c4ff14";
        public static final String presets = "#2dedff";
        public static final String global = "#60a4fc";
    }
    public static String barColor(HUD.Setting.BarColor color) {
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
        public static final String hudColor = "/hud color";
        public static final String hudModules = "/hud modules (order, toggle, reset)";
        public static final String hudSettings = "/hud settings";
        public static final String dest = "/dest | /destination";
        public static final String destAdd = "/dest (saved) add <name> (x) (y) (z) (dimension) (color)";
        public static String destSet = "/dest set <x> (y) <z> (dimension) (convert) | /dest set "+(config.globalDESTs?"(saved, global)":"saved")+" <name> (convert)";
        public static final String destLastdeath = "/dest lastdeath";
        public static final String destClear = "/dest clear";
        public static String destSaved = "/dest saved (add, edit, delete, send"+(config.globalDESTs?", global)":")");
        public static final String destSettings = "/dest settings";
        public static final String destSend = "/dest send <IGN> saved <name> | /dest send <IGN> (name) (x) (y) (z) (dimension) (color)";
        public static final String destTrack = "/dest track (set, accept, deny, cancel) <IGN> | /dest track clear";
        public static final String destTrackClear = "/dest track clear";
        public static final String reload = "/dhud reload";
        public static final String inbox = "/dhud inbox";
    }

    /**
     * gets all the options for a config entry, better than hard coding into the lang file.
     */
    public static class configOptions {
        @SafeVarargs
        private static <T extends Enum<T>> String fromEnum(T[] enums, T... exclude) {
            StringBuilder output = new StringBuilder();
            // get every enum except for the exclude list
            for (T entry: enums) {
                boolean stop = false;
                // check if the entry is excluded, then skip the entry
                for (T ex: exclude) {
                    if (ex.equals(entry)) {
                        stop = true;
                        break;
                    }
                }
                // skip entry
                if (stop) continue;
                output.append(entry).append(", ");
            }
            return output.substring(0,output.toString().length()-2); // remove the last ", "
        }
        public static String moduleOrder() {
            return fromEnum(HUD.Module.values(), HUD.Module.unknown);
        }
        public static String DisplayType() {
            return fromEnum(HUD.Setting.DisplayType.values());
        }
        public static String BossBarColor() {
            return fromEnum(HUD.Setting.BarColor.values());
        }
        public static String TrackingTarget() {
            return fromEnum(HUD.Setting.ModuleTrackingTarget.values());
        }
        public static String TrackingType() {
            return fromEnum(HUD.Setting.ModuleTrackingType.values());
        }
        public static String AngleDisplay() {
            return fromEnum(HUD.Setting.ModuleAngleDisplay.values());
        }
    }
    public static class symbols {
        public static class arrows {
            public static final String north = "\u2b06";
            public static final String north_west = "\u2b09";
            public static final String west = "\u2b05";
            public static final String south_west = "\u2b0b";
            public static final String south = "\u2b07";
            public static final String south_east = "\u2b0a";
            public static final String east = "\u2b95";
            public static final String north_east = "\u2b08";
            public static final String up = "\u25b2";
            public static final String down = "\u25bc";
            public static final String left = "\u25c0";
            public static final String right = "\u25b6";
            public static final String leftRight = "\u2b0c";
            public static final String upDown = "\u2b0d";
        }
        public static final String square = "\u2588";
        public static final String x = "\u2715";
        public static final String pencil = "\u270e";
        public static final String sun = "\u2600";
        public static final String moon = "\u263d";
        public static final String rain = "\ud83c\udf27";
        public static final String thunder = "\u26c8";
        public static final String link = "\u29c9";
        public static final String envelope = "\u2709";
        public static final String lighting_bolt = "\u26a1";
        public static final String convert = "\u2194";
        public static final String toggle = "\u21C4";
        public static final String local = "\uD83D\uDCDA";
        public static final String global = "\uD83E\uDDED";
    }
    public enum packets {
        INITIALIZATION("initialize_v1.0"),
        SETTINGS("player_settings_v1.0"),
        HUD("hud_v1.0");
        private final String identifier;
        packets(String key) {
            this.identifier = key;
        }
        public String getIdentifier() {
            return identifier;
        }
    }
}
