package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Utl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class config {
    public static class hud {
        public static class defaults {
            public static final String DisplayType = HUD.Setting.DisplayType.actionbar.toString();
            public static final String BarColor = HUD.Setting.BarColor.white.toString();
            public static final boolean BarShowDistance = true;
            public static final int ShowDistanceMAX = 0;
            public static final boolean State = true;
            public static final ArrayList<HUD.Module> Order = HUD.modules.getDefault();
            public static final boolean Coordinates = true;
            public static final boolean Distance = true;
            public static final boolean Tracking = false;
            public static final boolean Destination = true;
            public static final boolean Direction = true;
            public static final boolean Time = true;
            public static final boolean Weather = true;
            public static final boolean Time24HR = false;
            public static final String TrackingTarget = HUD.Setting.HUDTrackingTarget.player.toString();
            public static class primary {
                public static final String Color = DirectionHUD.PRIMARY;
                public static final boolean Bold = false;
                public static final boolean Italics = false;
                public static final boolean Rainbow = false;
            }
            public static class secondary {
                public static final String Color = "#ffffff";
                public static final boolean Bold = false;
                public static final boolean Italics = false;
                public static final boolean Rainbow = false;
            }
        }
        public static String DisplayType = defaults.DisplayType;
        public static String BarColor = defaults.BarColor;
        public static boolean BarShowDistance = defaults.BarShowDistance;
        public static int ShowDistanceMAX = defaults.ShowDistanceMAX;
        public static boolean State = defaults.State;
        public static ArrayList<HUD.Module> Order = defaults.Order;
        public static boolean Coordinates = defaults.Coordinates;
        public static boolean Distance = defaults.Distance;
        public static boolean Tracking = defaults.Tracking;
        public static boolean Destination = defaults.Destination;
        public static boolean Direction = defaults.Direction;
        public static boolean Time = defaults.Time;
        public static boolean Weather = defaults.Weather;
        public static boolean Time24HR = defaults.Time24HR;
        public static String TrackingTarget = defaults.TrackingTarget;
        public static class primary {
            public static String Color = defaults.primary.Color;
            public static boolean Bold = defaults.primary.Bold;
            public static boolean Italics = defaults.primary.Italics;
            public static boolean Rainbow = defaults.primary.Rainbow;
        }
        public static class secondary {
            public static String Color = defaults.secondary.Color;
            public static boolean Bold = defaults.secondary.Bold;
            public static boolean Italics = defaults.secondary.Italics;
            public static boolean Rainbow = defaults.secondary.Rainbow;
        }

    }
    public static class dest {
        public static class defaults {
            public static final boolean AutoClear = true;
            public static final int AutoClearRad = 2;
            public static final boolean AutoConvert = false;
            public static final boolean YLevel = false;
            public static class particles {
                public static final boolean Line = true;
                public static final String LineColor = DirectionHUD.SECONDARY;
                public static final boolean Dest = true;
                public static final String DestColor = DirectionHUD.PRIMARY;
                public static final boolean Tracking = true;
                public static final String TrackingColor = Assets.mainColors.track;
            }
            public static final boolean Send = true;
            public static final boolean Track = true;
            public static final boolean Lastdeath = true;
            public static String TrackingRequestMode = Destination.Setting.TrackingRequestMode.request.toString();
        }
        public static boolean AutoClear = defaults.AutoClear;
        public static int AutoClearRad = defaults.AutoClearRad;
        public static boolean AutoConvert = defaults.AutoConvert;
        public static boolean YLevel = defaults.YLevel;
        public static class particles {
            public static boolean Line = defaults.particles.Line;
            public static String LineColor = defaults.particles.LineColor;
            public static boolean Dest = defaults.particles.Dest;
            public static String DestColor = defaults.particles.DestColor;
            public static boolean Tracking = defaults.particles.Tracking;
            public static String TrackingColor = defaults.particles.TrackingColor;
        }
        public static boolean Send = defaults.Send;
        public static boolean Track = defaults.Track;
        public static boolean Lastdeath = defaults.Lastdeath;
        public static String TrackingRequestMode = defaults.TrackingRequestMode;
    }
    public static class defaults {
        public static final float version = 1.4f;
        public static final String lang = "en_us";
        public static final int MAXy = 512;
        public static final int MAXxz = 30000000;
        public static final boolean online = true;
        public static final boolean DestSaving = true;
        public static final int DestMAX = 50;
        public static final int LastDeathMAX = 4;
        public static final boolean LastDeathSaving = true;
        public static final boolean HUDEditing = true;
        public static final boolean social = true;
        public static final int socialCooldown = 10;
        public static final int HUDLoop = 1;
        public static final int ParticleLoop = 20;

        public static final boolean globalDESTs = false;
        public static final List<String> colorPresets = List.of("#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff");
        public static final List<String> dimensions = Utl.dim.DEFAULT_DIMENSIONS;
        public static final List<String> dimensionRatios = Utl.dim.DEFAULT_RATIOS;
    }
    public static String lang = defaults.lang;
    public static int MAXy = defaults.MAXy;
    public static int MAXxz = defaults.MAXxz;
    public static boolean online = defaults.online;
    public static boolean DestSaving = defaults.DestSaving;
    public static int DestMAX = defaults.DestMAX;
    public static boolean LastDeathSaving = defaults.LastDeathSaving;
    public static boolean HUDEditing = defaults.HUDEditing;
    public static int LastDeathMAX = defaults.LastDeathMAX;
    public static boolean social = defaults.social;
    public static Integer socialCooldown = defaults.socialCooldown;
    public static int HUDLoop = defaults.HUDLoop;
    public static int ParticleLoop = defaults.ParticleLoop;
    public static boolean globalDESTs = defaults.globalDESTs;
    public static List<String> colorPresets = defaults.colorPresets;
    public static List<String> dimensions = defaults.dimensions;
    public static List<String> dimensionRatios = defaults.dimensionRatios;
    public static File configFile() {
        return new File(DirectionHUD.CONFIG_DIR+"DirectionHUD.properties");
    }
    public static void load() {
        if (!configFile().exists() || !configFile().canRead()) {
            save();
            load();
            return;
        }
        try (FileInputStream fileStream = new FileInputStream(configFile())) {
            Properties properties = new Properties();
            properties.load(fileStream);
            String version = (String) properties.computeIfAbsent("version", a -> String.valueOf(defaults.version));
            if (version.contains("v")) version = version.substring(1);
            loadVersion(properties,Float.parseFloat(version));
            LangReader.loadLanguageFile();
            Utl.dim.loadConfig();
            save();
        } catch (Exception f) {
            //read fail
            f.printStackTrace();
        }
    }
    public static void loadVersion(Properties properties, float version) {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            //json maps
            Type arrayListMap = new TypeToken<ArrayList<String>>() {
            }.getType();
            Type moduleListMap = new TypeToken<ArrayList<HUD.Module>>() {
            }.getType();
            // CONFIG
            MAXxz = Integer.parseInt((String) properties.computeIfAbsent("max-xz", a -> String.valueOf(defaults.MAXxz)));
            MAXy = Integer.parseInt((String) properties.computeIfAbsent("max-y", a -> String.valueOf(defaults.MAXy)));
            globalDESTs = Boolean.parseBoolean((String) properties.computeIfAbsent("global-destinations", a -> String.valueOf(defaults.globalDESTs)));
            DestSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("destination-saving", a -> String.valueOf(defaults.DestSaving)));
            DestMAX = Integer.parseInt((String) properties.computeIfAbsent("destination-max", a -> String.valueOf(defaults.DestMAX)));
            LastDeathSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("lastdeath-saving", a -> String.valueOf(defaults.LastDeathSaving)));
            LastDeathMAX = Integer.parseInt((String) properties.computeIfAbsent("lastdeath-max", a -> String.valueOf(defaults.LastDeathMAX)));
            HUDEditing = Boolean.parseBoolean((String) properties.computeIfAbsent("hud-editing", a -> String.valueOf(defaults.HUDEditing)));
            online = Boolean.parseBoolean((String) properties.computeIfAbsent("online-mode", a -> String.valueOf(defaults.online)));
            // SOCIAL
            social = Boolean.parseBoolean((String) properties.computeIfAbsent("social-commands", a -> String.valueOf(defaults.social)));
            socialCooldown = Integer.parseInt((String) properties.computeIfAbsent("social-cooldown", a -> String.valueOf(defaults.socialCooldown)));
            // LOOPS
            ParticleLoop = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("particle-loop", a -> String.valueOf(defaults.ParticleLoop)))));
            HUDLoop = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-loop", a -> String.valueOf(defaults.HUDLoop)))));
            // DIM
            dimensions = new Gson().fromJson((String) properties.computeIfAbsent("dimensions", a -> gson.toJson(defaults.dimensions)), arrayListMap);
            dimensionRatios = new Gson().fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> gson.toJson(defaults.dimensionRatios)), arrayListMap);

            // PLAYER DEFAULTS
            colorPresets = new Gson().fromJson((String) properties.computeIfAbsent("color-presets", a -> gson.toJson(defaults.colorPresets)), arrayListMap);
            // HUD
            hud.Order = HUD.modules.fixOrder(new Gson().fromJson((String) properties.computeIfAbsent("hud.order", a -> gson.toJson(hud.defaults.Order)), moduleListMap));
            // HUD MODULES
            hud.Coordinates = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.coordinates", a -> String.valueOf(hud.defaults.Coordinates)));
            hud.Distance = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.distance", a -> String.valueOf(hud.defaults.Distance)));
            hud.Tracking = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.tracking", a -> String.valueOf(hud.defaults.Tracking)));
            hud.Destination = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.destination", a -> String.valueOf(hud.defaults.Destination)));
            hud.Direction = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.direction", a -> String.valueOf(hud.defaults.Direction)));
            hud.Time = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.time", a -> String.valueOf(hud.defaults.Time)));
            hud.Weather = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.weather", a -> String.valueOf(hud.defaults.Weather)));
            // HUD COLOR
            hud.primary.Color = CUtl.color.format((String) properties.computeIfAbsent("hud.color.primary", a -> hud.defaults.primary.Color), hud.defaults.primary.Color);
            hud.primary.Bold = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-bold", a -> String.valueOf(hud.defaults.primary.Bold)));
            hud.primary.Italics = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-italics", a -> String.valueOf(hud.defaults.primary.Italics)));
            hud.primary.Rainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-rainbow", a -> String.valueOf(hud.defaults.primary.Rainbow)));
            hud.secondary.Color = CUtl.color.format((String) properties.computeIfAbsent("hud.color.secondary", a -> hud.defaults.secondary.Color), hud.defaults.secondary.Color);
            hud.secondary.Bold = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-bold", a -> String.valueOf(hud.defaults.secondary.Bold)));
            hud.secondary.Italics = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-italics", a -> String.valueOf(hud.defaults.secondary.Italics)));
            hud.secondary.Rainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-rainbow", a -> String.valueOf(hud.defaults.secondary.Rainbow)));
            // HUD SETTINGS
            hud.State = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.state", a -> String.valueOf(hud.defaults.State)));
            hud.DisplayType = HUD.Setting.DisplayType.get((String) properties.computeIfAbsent("hud.settings.type", a -> hud.defaults.DisplayType)).toString();
            hud.BarColor = HUD.Setting.BarColor.get((String) properties.computeIfAbsent("hud.settings.bossbar.color", a -> hud.defaults.BarColor)).toString();
            hud.BarShowDistance = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.bossbar.distance", a -> String.valueOf(hud.defaults.BarShowDistance)));
            hud.ShowDistanceMAX = Integer.parseInt((String) properties.computeIfAbsent("hud.settings.bossbar.distance_max", a -> String.valueOf(hud.defaults.ShowDistanceMAX)));
            hud.Time24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.time_24hr", a -> String.valueOf(hud.defaults.Time24HR)));
            hud.TrackingTarget = HUD.Setting.HUDTrackingTarget.get((String) properties.computeIfAbsent("hud.settings.module.tracking_target", a -> hud.defaults.TrackingTarget)).toString();
            // DEST SETTINGS
            dest.AutoClear = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.autoclear", a -> String.valueOf(dest.defaults.AutoClear)));
            dest.AutoClearRad = Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("dest.settings.autoclear_rad", a -> String.valueOf(dest.defaults.AutoClearRad)))));
            dest.AutoConvert = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.autoconvert", a -> String.valueOf(dest.defaults.AutoConvert)));
            dest.YLevel = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.ylevel", a -> String.valueOf(dest.defaults.YLevel)));
            // DEST COLOR
            dest.particles.Dest = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.dest", a -> String.valueOf(dest.defaults.particles.Dest)));
            dest.particles.DestColor = CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.dest_color", a -> dest.defaults.particles.DestColor), dest.defaults.particles.DestColor);
            dest.particles.Line = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.line", a -> String.valueOf(dest.defaults.particles.Line)));
            dest.particles.LineColor = CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.line_color", a -> dest.defaults.particles.LineColor), dest.defaults.particles.LineColor);
            dest.particles.Tracking = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.tracking", a -> String.valueOf(dest.defaults.particles.Tracking)));
            dest.particles.TrackingColor = CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.tracking_color", a -> dest.defaults.particles.TrackingColor), dest.defaults.particles.DestColor);
            // DEST FEATURES
            dest.Send = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.send", a -> String.valueOf(dest.defaults.Send)));
            dest.Track = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.track", a -> String.valueOf(dest.defaults.Track)));
            dest.TrackingRequestMode = Destination.Setting.TrackingRequestMode.get((String) properties.computeIfAbsent("dest.settings.features.track_request_mode", a -> dest.defaults.TrackingRequestMode)).toString();
            dest.Lastdeath = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.lastdeath", a -> String.valueOf(dest.defaults.Lastdeath)));
            // CONFIG UPDATER, if the version is lower than the current, load from the old config
            // everything before & 1.3
            if (version <= 1.3f) {
                DestMAX = Integer.parseInt((String) properties.computeIfAbsent("destination-max-saved", a -> String.valueOf(defaults.DestMAX)));
                LastDeathSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("death-saving", a -> String.valueOf(defaults.LastDeathSaving)));
                hud.State = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.enabled", a -> String.valueOf(hud.defaults.State)));
                HUDLoop = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-refresh", a -> String.valueOf(defaults.HUDLoop)))));
            }
            // everything before & 1.21
            if (version <= 1.21f) {
                // I don't know why but oh well backwards compatibility
                if (!DirectionHUD.isMod || version == 1.21f)
                    dimensionRatios = new Gson().fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> gson.toJson(defaults.dimensionRatios)), arrayListMap);
                // update colors to new system
                hud.primary.Color = CUtl.color.updateOld((String) properties.computeIfAbsent("primary-color", a -> hud.defaults.primary.Color), hud.defaults.primary.Color);
                hud.secondary.Color = CUtl.color.updateOld((String) properties.computeIfAbsent("secondary-color", a -> hud.defaults.secondary.Color), hud.defaults.secondary.Color);
                dest.particles.LineColor = CUtl.color.updateOld((String) properties.computeIfAbsent("line-particle-color", a -> dest.defaults.particles.LineColor), dest.defaults.particles.LineColor);
                dest.particles.DestColor = CUtl.color.updateOld((String) properties.computeIfAbsent("dest-particle-color", a -> dest.defaults.particles.DestColor), dest.defaults.particles.DestColor);
                //HUD
                hud.State = Boolean.parseBoolean((String) properties.computeIfAbsent("enabled", a -> String.valueOf(hud.defaults.State)));
                List<String> orderList = List.of(((String) properties.computeIfAbsent("order", a -> hud.defaults.Order.toString().substring(1).replace(",", "").replace("]", ""))).split(" "));
                ArrayList<HUD.Module> moduleArray = new ArrayList<>();
                for (String entry : orderList) moduleArray.add(HUD.Module.get(entry));
                hud.Order = HUD.modules.fixOrder(moduleArray);
                hud.Time24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("time24hr", a -> String.valueOf(hud.defaults.Time24HR)));
                hud.primary.Bold = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-bold", a -> String.valueOf(hud.defaults.primary.Bold)));
                hud.primary.Italics = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-italics", a -> String.valueOf(hud.defaults.primary.Italics)));
                hud.primary.Rainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-rainbow", a -> String.valueOf(hud.defaults.primary.Rainbow)));
                hud.secondary.Bold = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-bold", a -> String.valueOf(hud.defaults.secondary.Bold)));
                hud.secondary.Italics = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-italics", a -> String.valueOf(hud.defaults.secondary.Italics)));
                hud.secondary.Rainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-rainbow", a -> String.valueOf(hud.defaults.secondary.Rainbow)));
                //MODULES
                hud.Coordinates = Boolean.parseBoolean((String) properties.computeIfAbsent("coordinates", a -> String.valueOf(hud.defaults.Coordinates)));
                hud.Distance = Boolean.parseBoolean((String) properties.computeIfAbsent("distance", a -> String.valueOf(hud.defaults.Distance)));
                hud.Destination = Boolean.parseBoolean((String) properties.computeIfAbsent("destination", a -> String.valueOf(hud.defaults.Destination)));
                hud.Direction = Boolean.parseBoolean((String) properties.computeIfAbsent("direction", a -> String.valueOf(hud.defaults.Direction)));
                hud.Time = Boolean.parseBoolean((String) properties.computeIfAbsent("time", a -> String.valueOf(hud.defaults.Time)));
                hud.Weather = Boolean.parseBoolean((String) properties.computeIfAbsent("weather", a -> String.valueOf(hud.defaults.Weather)));
                hud.Tracking = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking", a -> String.valueOf(hud.defaults.Tracking)));
                //DEST
                dest.AutoClear = Boolean.parseBoolean((String) properties.computeIfAbsent("autoclear", a -> String.valueOf(dest.defaults.AutoClear)));
                dest.AutoClearRad = Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("autoclear-radius", a -> String.valueOf(dest.defaults.AutoClearRad)))));
                dest.YLevel = Boolean.parseBoolean((String) properties.computeIfAbsent("y-level", a -> String.valueOf(dest.defaults.YLevel)));
                dest.particles.Line = Boolean.parseBoolean((String) properties.computeIfAbsent("line-particles", a -> String.valueOf(dest.defaults.particles.Line)));
                dest.particles.Dest = Boolean.parseBoolean((String) properties.computeIfAbsent("dest-particles", a -> String.valueOf(dest.defaults.particles.Dest)));
                dest.Send = Boolean.parseBoolean((String) properties.computeIfAbsent("send", a -> String.valueOf(dest.defaults.Send)));
                dest.Track = Boolean.parseBoolean((String) properties.computeIfAbsent("track", a -> String.valueOf(dest.defaults.Track)));
                dest.AutoConvert = Boolean.parseBoolean((String) properties.computeIfAbsent("autoconvert", a -> String.valueOf(dest.defaults.AutoConvert)));
                dest.particles.Tracking = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking-particles", a -> String.valueOf(dest.defaults.particles.Tracking)));
                dest.particles.TrackingColor = CUtl.color.updateOld((String) properties.computeIfAbsent("tracking-particle-color", a -> dest.defaults.particles.TrackingColor), dest.defaults.particles.DestColor);
            }
            // only in 1.1
            if (version == 1.1f)
                hud.Tracking = Boolean.parseBoolean((String) properties.computeIfAbsent("compass", a -> String.valueOf(hud.defaults.Tracking)));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR LOADING CONFIG - PLEASE REPORT WITH THE ERROR LOG");
            e.printStackTrace();
        }
    }
    public static void save() {
        try (var file = Files.newBufferedWriter(configFile().toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            file.write("# DirectionHUD Config\n");
            file.write("version="+defaults.version);
            file.write("\n");
            file.write("\nmax-xz=" + MAXxz);
            file.write("\nmax-y=" + MAXy);
            file.write("\n# "+CUtl.lang("config.max.info",CUtl.lang("config.max.info_2")).toString());
            file.write("\nonline-mode=" + online);
            file.write("\n# "+CUtl.lang("config.online_mode.info").toString());
            file.write("\nglobal-destinations=" + globalDESTs);
            file.write("\n# "+CUtl.lang("config.global_dest.info").toString());
            file.write("\n# "+CUtl.lang("config.global_dest.info_3").toString());
            file.write("\ndestination-saving=" + DestSaving);
            file.write("\n# "+CUtl.lang("config.dest_saving.info").toString());
            file.write("\ndestination-max=" + DestMAX);
            file.write("\n# "+CUtl.lang("config.dest_max.info").toString());
            file.write("\nlastdeath-saving=" + LastDeathSaving);
            file.write("\n# "+CUtl.lang("config.lastdeath_saving.info").toString());
            file.write("\nlastdeath-max=" + LastDeathMAX);
            file.write("\n# "+CUtl.lang("config.lastdeath_max.info").toString());
            file.write("\nhud-editing=" + HUDEditing);
            file.write("\n# "+CUtl.lang("config.hud_editing.info").toString());
            file.write("\n"); //SOCIAL
            file.write("\nsocial-commands=" + social);
            file.write("\n# "+CUtl.lang("config.social.info",CUtl.lang("config.social.info_2")).toString());
            file.write("\nsocial-cooldown=" + socialCooldown);
            file.write("\n# "+CUtl.lang("config.social_cooldown.info").toString());
            file.write("\n"); //LOOPS
            file.write("\nhud-loop=" + HUDLoop);
            file.write("\n# "+CUtl.lang("config.hud_loop.info").toString());
            file.write("\nparticle-loop=" + ParticleLoop);
            file.write("\n# "+CUtl.lang("config.particle_loop.info").toString());
            file.write("\n"); //DIMS
            file.write("\ndimensions="+gson.toJson(dimensions));
            file.write("\n# "+CUtl.lang("config.dimensions.info")
                    .append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_3",CUtl.lang("config.dimensions.info_3.1"),
                            CUtl.lang("config.dimensions.info_3.2"),CUtl.lang("config.dimensions.info_3.3"))).append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_4")).append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_5")).append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_6")).toString());
            file.write("\ndimension-ratios="+gson.toJson(dimensionRatios));
            file.write("\n# "+CUtl.lang("config.dimension_ratios.info").append("\n# ")
                    .append(CUtl.lang("config.dimension_ratios.info_2",CUtl.lang("config.dimension_ratios.info_2.1"),
                            CUtl.lang("config.dimension_ratios.info_2.2"))).toString());

            file.write("\n\n\n# "+CUtl.lang("config.default").toString());
            file.write("\n# "+CUtl.lang("config.default.info").toString());
            file.write("\ncolor-presets=" + gson.toJson(colorPresets));
            file.write("\n# "+CUtl.lang("config.color_presets.info").toString());

            file.write("\n\n# "+CUtl.lang("config.hud").toString());
            file.write("\nhud.order=" + hud.Order);
            file.write("\n# "+CUtl.lang("config.hud.order.options").toString());
            file.write("\n\n# "+CUtl.lang("config.hud.module").toString());
            file.write("\nhud.module.coordinates=" + hud.Coordinates);
            file.write("\nhud.module.distance=" + hud.Distance);
            file.write("\nhud.module.tracking=" + hud.Tracking);
            file.write("\nhud.module.destination=" + hud.Destination);
            file.write("\nhud.module.direction=" + hud.Direction);
            file.write("\nhud.module.time=" + hud.Time);
            file.write("\nhud.module.weather=" + hud.Weather);
            file.write("\n\n# "+CUtl.lang("config.settings").toString());
            file.write("\nhud.settings.state=" + hud.State);
            file.write("\nhud.settings.type=" + hud.DisplayType);
            file.write("\n# "+CUtl.lang("config.hud.settings.type.options").toString());
            file.write("\nhud.settings.bossbar.color=" + hud.BarColor);
            file.write("\n# "+CUtl.lang("config.hud.settings.bossbar.color.options").toString());
            file.write("\nhud.settings.bossbar.distance=" + hud.BarShowDistance);
            file.write("\nhud.settings.bossbar.distance_max=" + hud.ShowDistanceMAX);
            file.write("\nhud.settings.module.time_24hr=" + hud.Time24HR);
            file.write("\nhud.settings.module.tracking_target=" + hud.TrackingTarget);
            file.write("\n# "+CUtl.lang("config.hud.settings.module.tracking_target.options").toString());
            file.write("\n\n# "+CUtl.lang("config.hud.color").toString());
            file.write("\n# "+CUtl.lang("config.color.options").toString());
            file.write("\nhud.color.primary=" + hud.primary.Color);
            file.write("\nhud.color.primary-bold=" + hud.primary.Bold);
            file.write("\nhud.color.primary-italics=" + hud.primary.Italics);
            file.write("\nhud.color.primary-rainbow=" + hud.primary.Rainbow);
            file.write("\nhud.color.secondary=" + hud.secondary.Color);
            file.write("\nhud.color.secondary-bold=" + hud.secondary.Bold);
            file.write("\nhud.color.secondary-italics=" + hud.secondary.Italics);
            file.write("\nhud.color.secondary-rainbow=" + hud.secondary.Rainbow);

            file.write("\n\n\n# "+CUtl.lang("config.dest").toString());
            file.write("\n# "+CUtl.lang("config.settings").toString());
            file.write("\ndest.settings.autoclear=" + dest.AutoClear);
            file.write("\ndest.settings.autoclear_rad=" + dest.AutoClearRad);
            file.write("\ndest.settings.autoconvert=" + dest.AutoConvert);
            file.write("\ndest.settings.ylevel=" + dest.YLevel);
            file.write("\n\n# "+CUtl.lang("config.hud.color").toString());
            file.write("\n# "+CUtl.lang("config.color.options").toString());
            file.write("\ndest.settings.particles.dest=" + dest.particles.Dest);
            file.write("\ndest.settings.particles.dest_color=" + dest.particles.DestColor);
            file.write("\ndest.settings.particles.line=" + dest.particles.Line);
            file.write("\ndest.settings.particles.line_color=" + dest.particles.LineColor);
            file.write("\ndest.settings.particles.tracking=" + dest.particles.Tracking);
            file.write("\ndest.settings.particles.tracking_color=" + dest.particles.TrackingColor);
            file.write("\n\n# "+CUtl.lang("config.dest.settings.features").toString());
            file.write("\ndest.settings.features.send=" + dest.Send);
            file.write("\ndest.settings.features.track=" + dest.Track);
            file.write("\ndest.settings.features.track_request_mode=" + dest.TrackingRequestMode);
            file.write("\n# "+CUtl.lang("config.dest.settings.features.track_request_mode.options").toString());
            file.write("\ndest.settings.features.lastdeath=" + dest.Lastdeath);
        } catch (Exception e) {
            DirectionHUD.LOGGER.error("ERROR WRITING CONFIG!");
            e.printStackTrace();
        }
    }
}
