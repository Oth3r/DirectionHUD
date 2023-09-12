package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class config {
    //todo
    // lang the config file, redo the YACL config
    // maybe enum system for config options or rename vars AT LEAST
    public static String lang = defaults.lang;
    public static boolean DESTSaving = defaults.DESTSaving;
    public static int MAXSaved = defaults.MAXSaved;
    public static int MAXy = defaults.MAXy;
    public static int MAXxz = defaults.MAXxz;
    public static boolean deathsaving = defaults.deathsaving;
    public static boolean social = defaults.social;
    public static boolean HUDEditing = defaults.HUDEditing;
    public static int HUDRefresh = defaults.HUDRefresh;
    public static boolean online = defaults.online;
    public static List<String> colorPresets = defaults.colorPresets;
    public enum HUDTypes {
        actionbar,
        bossbar;
        public static final HUDTypes[] values = values();
        public HUDTypes next() {
            return values[(ordinal() + 1) % values.length];
        }
        public static HUDTypes get(String s) {
            try {
                return HUDTypes.valueOf(s);

            } catch (IllegalArgumentException e) {
                return HUDTypes.valueOf(defaults.HUDType);
            }
        }
    }
    public static String HUDType = defaults.HUDType;
    public enum BarColors {
        pink,
        blue,
        red,
        green,
        yellow,
        purple,
        white;
        public static BarColors get(String s) {
            try {
                return BarColors.valueOf(s);

            } catch (IllegalArgumentException e) {
                return BarColors.valueOf(defaults.HUDBarColor);
            }
        }
    }
    public static String HUDBarColor = defaults.HUDBarColor;
    public static boolean HUDBarShowDistance = defaults.HUDBarShowDistance;
    public static long HUDBarDistanceMax = defaults.HUDBarDistanceMax;
    public static boolean HUDEnabled = defaults.HUDEnabled;
    public static ArrayList<String> HUDOrder = defaults.HUDOrder;
    public static boolean HUDCoordinates = defaults.HUDCoordinates;
    public static boolean HUDDistance = defaults.HUDDistance;
    public static boolean HUDTracking = defaults.HUDTracking;
    public static boolean HUDDestination = defaults.HUDDestination;
    public static boolean HUDDirection = defaults.HUDDirection;
    public static boolean HUDTime = defaults.HUDTime;
    public static boolean HUDWeather = defaults.HUDWeather;
    public static boolean HUDTime24HR = defaults.HUDTime24HR;
    public enum HUDTrackingTargets {
        player,
        dest;
        public static final HUDTrackingTargets[] values = values();
        public HUDTrackingTargets next() {
            return values[(ordinal() + 1) % values.length];
        }
        public static HUDTrackingTargets get(String s) {
            try {
                return HUDTrackingTargets.valueOf(s);

            } catch (IllegalArgumentException e) {
                return HUDTrackingTargets.valueOf(defaults.HUDTrackingTarget);
            }
        }
    }
    public static String HUDTrackingTarget = defaults.HUDTrackingTarget;
    public static String HUDPrimaryColor = defaults.HUDPrimaryColor;
    public static boolean HUDPrimaryBold = defaults.HUDPrimaryBold;
    public static boolean HUDPrimaryItalics = defaults.HUDPrimaryItalics;
    public static boolean HUDPrimaryRainbow = defaults.HUDPrimaryRainbow;
    public static String HUDSecondaryColor = defaults.HUDSecondaryColor;
    public static boolean HUDSecondaryBold = defaults.HUDSecondaryBold;
    public static boolean HUDSecondaryItalics = defaults.HUDSecondaryItalics;
    public static boolean HUDSecondaryRainbow = defaults.HUDSecondaryRainbow;
    public static boolean DESTAutoClear = defaults.DESTAutoClear;
    public static long DESTAutoClearRad = defaults.DESTAutoClearRad;
    public static boolean DESTAutoConvert = defaults.DESTAutoConvert;
    public static boolean DESTYLevel = defaults.DESTYLevel;
    public static boolean DESTLineParticles = defaults.DESTLineParticles;
    public static String DESTLineParticleColor = defaults.DESTLineParticleColor;
    public static boolean DESTDestParticles = defaults.DESTDestParticles;
    public static String DESTDestParticleColor = defaults.DESTDestParticleColor;
    public static boolean DESTTrackingParticles = defaults.DESTTrackingParticles;
    public static String DESTTrackingParticleColor = defaults.DESTTrackingParticleColor;
    public static boolean DESTSend = defaults.DESTSend;
    public static boolean DESTTrack = defaults.DESTTrack;
    public enum DESTTrackingRequestModes {
        request,
        instant;
        public static final DESTTrackingRequestModes[] values = values();
        public DESTTrackingRequestModes next() {
            return values[(ordinal() + 1) % values.length];
        }
        public static DESTTrackingRequestModes get(String s) {
            try {
                return DESTTrackingRequestModes.valueOf(s);

            } catch (IllegalArgumentException e) {
                return DESTTrackingRequestModes.valueOf(defaults.DESTTrackingRequestMode);
            }
        }
    }
    public static String DESTTrackingRequestMode = defaults.DESTTrackingRequestMode;
    public static boolean DESTLastdeath = defaults.DESTLastdeath;
    public static List<String> dimensions = defaults.dimensions;
    public static List<String> dimensionRatios = defaults.dimensionRatios;
    public static void resetDefaults() {
        //CONFIG SETTINGS
        DESTSaving = defaults.DESTSaving;
        MAXSaved = defaults.MAXSaved;
        MAXy = defaults.MAXy;
        MAXxz = defaults.MAXxz;
        deathsaving = defaults.deathsaving;
        social = defaults.social;
        HUDEditing = defaults.HUDEditing;
        HUDRefresh = defaults.HUDRefresh;
        dimensions = defaults.dimensions;
        dimensionRatios = defaults.dimensionRatios;
        //HUD SETTINGS
        HUDEnabled = defaults.HUDEnabled;
        HUDOrder = defaults.HUDOrder;
        HUDType = defaults.HUDType;
        HUDBarColor = defaults.HUDBarColor;
        HUDBarShowDistance = defaults.HUDBarShowDistance;
        HUDBarDistanceMax = defaults.HUDBarDistanceMax;
        HUDTime24HR = defaults.HUDTime24HR;
        HUDTrackingTarget = defaults.HUDTrackingTarget;
        //HUD MODULES
        HUDCoordinates = defaults.HUDCoordinates;
        HUDDistance = defaults.HUDDistance;
        HUDTracking = defaults.HUDTracking;
        HUDDestination = defaults.HUDDestination;
        HUDDirection = defaults.HUDDirection;
        HUDTime = defaults.HUDTime;
        HUDWeather = defaults.HUDWeather;
        //HUD COLORS
        HUDPrimaryColor = defaults.HUDPrimaryColor;
        HUDPrimaryBold = defaults.HUDPrimaryBold;
        HUDPrimaryItalics = defaults.HUDPrimaryItalics;
        HUDPrimaryRainbow = defaults.HUDPrimaryRainbow;
        //SEC
        HUDSecondaryColor = defaults.HUDSecondaryColor;
        HUDSecondaryBold = defaults.HUDSecondaryBold;
        HUDSecondaryItalics = defaults.HUDSecondaryItalics;
        HUDSecondaryRainbow = defaults.HUDSecondaryRainbow;
        //DEST SETTINGS
        DESTAutoClear = defaults.DESTAutoClear;
        DESTAutoClearRad = defaults.DESTAutoClearRad;
        DESTAutoConvert = defaults.DESTAutoConvert;
        DESTYLevel = defaults.DESTYLevel;
        DESTSend = defaults.DESTSend;
        DESTTrack = defaults.DESTTrack;
        DESTTrackingRequestMode = defaults.DESTTrackingRequestMode;
        DESTLastdeath = defaults.DESTLastdeath;
        //DEST PARTICLES
        DESTLineParticles = defaults.DESTLineParticles;
        DESTLineParticleColor = defaults.DESTLineParticleColor;
        DESTDestParticles = defaults.DESTDestParticles;
        DESTDestParticleColor = defaults.DESTDestParticleColor;
        DESTTrackingParticles = defaults.DESTTrackingParticles;
        DESTTrackingParticleColor = defaults.DESTTrackingParticleColor;
        save();
    }
    public static void setToPlayer(Player player) {
        //HUD SETTINGS
        HUDEnabled = PlayerData.get.hud.state(player);
        HUDType = (String)PlayerData.get.hud.setting.get(player, HUD.Settings.type);
        HUDBarColor = (String)PlayerData.get.hud.setting.get(player, HUD.Settings.bossbar__color);
        HUDBarShowDistance = (boolean)PlayerData.get.hud.setting.get(player, HUD.Settings.bossbar__distance);
        HUDBarDistanceMax = (long)PlayerData.get.hud.setting.get(player, HUD.Settings.bossbar__distance_max);
        HUDTime24HR = (boolean)PlayerData.get.hud.setting.get(player, HUD.Settings.module__time_24hr);
        HUDTrackingTarget = (String)PlayerData.get.hud.setting.get(player, HUD.Settings.module__tracking_target);
        //HUD MODULES
        HUDOrder = PlayerData.get.hud.order(player);
        HUDCoordinates = PlayerData.get.hud.getModule(player,"coordinates");
        HUDDistance = PlayerData.get.hud.getModule(player,"distance");
        HUDTracking = PlayerData.get.hud.getModule(player,"tracking");
        HUDDestination = PlayerData.get.hud.getModule(player,"destination");
        HUDDirection = PlayerData.get.hud.getModule(player,"direction");
        HUDTime = PlayerData.get.hud.getModule(player,"time");
        HUDWeather = PlayerData.get.hud.getModule(player,"weather");
        //HUD COLORS
        HUDPrimaryColor = HUD.color.getHUDColor(player,1);
        HUDPrimaryBold = HUD.color.getHUDBold(player,1);
        HUDPrimaryItalics = HUD.color.getHUDItalics(player, 1);
        HUDPrimaryRainbow = HUD.color.getHUDRGB(player,1);
        //SEC
        HUDSecondaryColor = HUD.color.getHUDColor(player,2);
        HUDSecondaryBold = HUD.color.getHUDBold(player,2);
        HUDSecondaryItalics = HUD.color.getHUDItalics(player, 2);
        HUDSecondaryRainbow = HUD.color.getHUDRGB(player,2);
        //DEST SETTINGS
        DESTAutoClear = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear);
        DESTAutoClearRad = (long)PlayerData.get.dest.setting.get(player, Destination.Settings.autoclear_rad);
        DESTAutoConvert = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.autoconvert);
        DESTYLevel = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.ylevel);
        DESTSend = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__send);
        DESTTrack = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track);
        DESTTrackingRequestMode = (String)PlayerData.get.dest.setting.get(player, Destination.Settings.features__track_request_mode);
        DESTLastdeath = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.features__lastdeath);
        //DEST PARTICLES
        DESTDestParticles = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__dest);
        DESTDestParticleColor = (String)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__dest_color);
        DESTLineParticles = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__line);
        DESTLineParticleColor = (String)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__line_color);
        DESTTrackingParticles = (boolean)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__tracking);
        DESTTrackingParticleColor = (String)PlayerData.get.dest.setting.get(player, Destination.Settings.particles__tracking_color);
        save();
    }
    public static File configFile() {
        return new File(DirectionHUD.configDir +"DirectionHUD.properties");
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
            Utl.dim.loadConfig();
            save();
        } catch (Exception f) {
            //read fail
            f.printStackTrace();
            resetDefaults();
        }
    }
    public static void loadVersion(Properties properties, float version) {
        //CONFIG
        MAXxz = Integer.parseInt((String) properties.computeIfAbsent("max-xz", a -> String.valueOf(defaults.MAXxz)));
        MAXy = Integer.parseInt((String) properties.computeIfAbsent("max-y", a -> String.valueOf(defaults.MAXy)));
        DESTSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("destination-saving", a -> String.valueOf(defaults.DESTSaving)));
        MAXSaved = Integer.parseInt((String) properties.computeIfAbsent("destination-max-saved", a -> String.valueOf(defaults.MAXSaved)));
        social = Boolean.parseBoolean((String) properties.computeIfAbsent("social-commands", a -> String.valueOf(defaults.social)));
        deathsaving = Boolean.parseBoolean((String) properties.computeIfAbsent("death-saving", a -> String.valueOf(defaults.deathsaving)));
        HUDEditing = Boolean.parseBoolean((String) properties.computeIfAbsent("hud-editing", a -> String.valueOf(defaults.HUDEditing)));
        HUDRefresh = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-refresh", a -> String.valueOf(defaults.HUDRefresh)))));
        online = Boolean.parseBoolean((String) properties.computeIfAbsent("online-mode", a -> String.valueOf(defaults.online)));
        //DIM
        Type arrayListMap = new TypeToken<ArrayList<String>>() {}.getType();
        dimensions = new Gson().fromJson((String) properties.computeIfAbsent("dimensions", a -> String.valueOf(defaults.dimensions)),arrayListMap);

        if (version == 1.1f) HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("compass", a -> String.valueOf(defaults.HUDTracking)));
        // old config entries
        if (version <= 1.2f) {
            if (!DirectionHUD.isMod)
                dimensionRatios = new Gson().fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> String.valueOf(defaults.dimensionRatios)),arrayListMap);
            // update colors to new system
            HUDPrimaryColor = CUtl.color.updateOld((String) properties.computeIfAbsent("primary-color", a -> defaults.HUDPrimaryColor),defaults.HUDPrimaryColor);
            HUDSecondaryColor = CUtl.color.updateOld((String) properties.computeIfAbsent("secondary-color", a -> defaults.HUDSecondaryColor),defaults.HUDSecondaryColor);
            DESTLineParticleColor = CUtl.color.updateOld((String) properties.computeIfAbsent("line-particle-color", a -> defaults.DESTLineParticleColor),defaults.DESTLineParticleColor);
            DESTDestParticleColor = CUtl.color.updateOld((String) properties.computeIfAbsent("dest-particle-color", a -> defaults.DESTDestParticleColor),defaults.DESTDestParticleColor);
            //HUD
            HUDEnabled = Boolean.parseBoolean((String) properties.computeIfAbsent("enabled", a -> String.valueOf(defaults.HUDEnabled)));
            HUDOrder = HUD.modules.fixOrder(new ArrayList<>(List.of(((String) properties.computeIfAbsent("order", a -> defaults.HUDOrder
                    .toString().substring(1).replace(",","").replace("]",""))).split(" "))));
            HUDTime24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("time24hr", a -> String.valueOf(defaults.HUDTime24HR)));
            HUDPrimaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-bold", a -> String.valueOf(defaults.HUDPrimaryBold)));
            HUDPrimaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-italics", a -> String.valueOf(defaults.HUDPrimaryItalics)));
            HUDPrimaryRainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-rainbow", a -> String.valueOf(defaults.HUDPrimaryRainbow)));
            HUDSecondaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-bold", a -> String.valueOf(defaults.HUDSecondaryBold)));
            HUDSecondaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-italics", a -> String.valueOf(defaults.HUDSecondaryItalics)));
            HUDSecondaryRainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-rainbow", a -> String.valueOf(defaults.HUDSecondaryRainbow)));
            //MODULES
            HUDCoordinates = Boolean.parseBoolean((String) properties.computeIfAbsent("coordinates", a -> String.valueOf(defaults.HUDCoordinates)));
            HUDDistance = Boolean.parseBoolean((String) properties.computeIfAbsent("distance", a -> String.valueOf(defaults.HUDDistance)));
            HUDDestination = Boolean.parseBoolean((String) properties.computeIfAbsent("destination", a -> String.valueOf(defaults.HUDDestination)));
            HUDDirection = Boolean.parseBoolean((String) properties.computeIfAbsent("direction", a -> String.valueOf(defaults.HUDDirection)));
            HUDTime = Boolean.parseBoolean((String) properties.computeIfAbsent("time", a -> String.valueOf(defaults.HUDTime)));
            HUDWeather = Boolean.parseBoolean((String) properties.computeIfAbsent("weather", a -> String.valueOf(defaults.HUDWeather)));
            //DEST
            DESTAutoClear = Boolean.parseBoolean((String) properties.computeIfAbsent("autoclear", a -> String.valueOf(defaults.DESTAutoClear)));
            DESTAutoClearRad = Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("autoclear-radius", a -> String.valueOf(defaults.DESTAutoClearRad)))));
            DESTYLevel = Boolean.parseBoolean((String) properties.computeIfAbsent("y-level", a -> String.valueOf(defaults.DESTYLevel)));
            DESTLineParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("line-particles", a -> String.valueOf(defaults.DESTLineParticles)));
            DESTDestParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("dest-particles", a -> String.valueOf(defaults.DESTDestParticles)));
            DESTSend = Boolean.parseBoolean((String) properties.computeIfAbsent("send", a -> String.valueOf(defaults.DESTSend)));
            DESTTrack = Boolean.parseBoolean((String) properties.computeIfAbsent("track", a -> String.valueOf(defaults.DESTTrack)));
        }
        if (version == 1.2f || version == 1.21f) {
            DESTAutoConvert = Boolean.parseBoolean((String) properties.computeIfAbsent("autoconvert", a -> String.valueOf(defaults.DESTAutoConvert)));
            HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking", a -> String.valueOf(defaults.HUDTracking)));
            DESTTrackingParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking-particles", a -> String.valueOf(defaults.DESTTrackingParticles)));
            DESTTrackingParticleColor = CUtl.color.updateOld((String) properties.computeIfAbsent("tracking-particle-color", a -> defaults.DESTTrackingParticleColor),defaults.DESTDestParticleColor);
        }
        if (version == 1.21f) dimensionRatios = new Gson().fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> String.valueOf(defaults.dimensionRatios)),arrayListMap);
        if (version >= 1.3f) {
            //PRESETS
            dimensionRatios = new Gson().fromJson((String)
                    properties.computeIfAbsent("color-presets", a -> String.valueOf(defaults.colorPresets)),arrayListMap);
            //HUD
            HUDEnabled = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.enabled", a -> String.valueOf(defaults.HUDEnabled)));
            HUDOrder = HUD.modules.fixOrder(new Gson().fromJson((String)
                    properties.computeIfAbsent("hud.order", a -> String.valueOf(defaults.HUDOrder)),arrayListMap));
            //MODULES
            HUDCoordinates = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.coordinates", a -> String.valueOf(defaults.HUDCoordinates)));
            HUDDistance = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.distance", a -> String.valueOf(defaults.HUDDistance)));
            HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.tracking", a -> String.valueOf(defaults.HUDTracking)));
            HUDDestination = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.destination", a -> String.valueOf(defaults.HUDDestination)));
            HUDDirection = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.direction", a -> String.valueOf(defaults.HUDDirection)));
            HUDTime = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.time", a -> String.valueOf(defaults.HUDTime)));
            HUDWeather = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.weather", a -> String.valueOf(defaults.HUDWeather)));
            //COLOR
            HUDPrimaryColor = CUtl.color.format((String) properties.computeIfAbsent("hud.color.primary", a -> defaults.HUDPrimaryColor),defaults.HUDPrimaryColor);
            HUDPrimaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-bold", a -> String.valueOf(defaults.HUDPrimaryBold)));
            HUDPrimaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-italics", a -> String.valueOf(defaults.HUDPrimaryItalics)));
            HUDPrimaryRainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-rainbow", a -> String.valueOf(defaults.HUDPrimaryRainbow)));
            HUDSecondaryColor = CUtl.color.format((String) properties.computeIfAbsent("hud.color.secondary", a -> defaults.HUDSecondaryColor),defaults.HUDSecondaryColor);
            HUDSecondaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-bold", a -> String.valueOf(defaults.HUDSecondaryBold)));
            HUDSecondaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-italics", a -> String.valueOf(defaults.HUDSecondaryItalics)));
            HUDSecondaryRainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-rainbow", a -> String.valueOf(defaults.HUDSecondaryRainbow)));
            //SETTINGS
            HUDType = HUDTypes.get((String) properties.computeIfAbsent("hud.settings.type", a -> defaults.HUDType)).toString();
            HUDBarColor = BarColors.get((String) properties.computeIfAbsent("hud.settings.bossbar.color", a -> defaults.HUDBarColor)).toString();
            HUDBarShowDistance = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.bossbar.distance", a -> String.valueOf(defaults.HUDBarShowDistance)));
            HUDBarDistanceMax = Integer.parseInt((String) properties.computeIfAbsent("hud.settings.bossbar.distance_max", a -> String.valueOf(defaults.HUDBarDistanceMax)));
            HUDTime24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.time_24hr", a -> String.valueOf(defaults.HUDTime24HR)));
            HUDTrackingTarget = HUDTrackingTargets.get((String) properties.computeIfAbsent("hud.settings.module.tracking_target", a -> defaults.HUDTrackingTarget)).toString();
            //DEST
            //SETTINGS
            DESTAutoClear = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.autoclear", a -> String.valueOf(defaults.DESTAutoClear)));
            DESTAutoClearRad = Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("dest.settings.autoclear_rad", a -> String.valueOf(defaults.DESTAutoClearRad)))));
            DESTAutoConvert = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.autoconvert", a -> String.valueOf(defaults.DESTAutoConvert)));
            DESTYLevel = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.ylevel", a -> String.valueOf(defaults.DESTYLevel)));
            //COLOR
            DESTDestParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.dest", a -> String.valueOf(defaults.DESTDestParticles)));
            DESTDestParticleColor = CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.dest_color", a -> defaults.DESTDestParticleColor),defaults.DESTDestParticleColor);
            DESTLineParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.line", a -> String.valueOf(defaults.DESTLineParticles)));
            DESTLineParticleColor = CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.line_color", a -> defaults.DESTLineParticleColor),defaults.DESTLineParticleColor);
            DESTTrackingParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.tracking", a -> String.valueOf(defaults.DESTTrackingParticles)));
            DESTTrackingParticleColor = CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.tracking_color", a -> defaults.DESTTrackingParticleColor),defaults.DESTDestParticleColor);
            //FEATURES
            DESTSend = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.send", a -> String.valueOf(defaults.DESTSend)));
            DESTTrack = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.track", a -> String.valueOf(defaults.DESTTrack)));
            DESTTrackingRequestMode = DESTTrackingRequestModes.get((String) properties.computeIfAbsent("dest.settings.features.track_request_mode", a -> defaults.DESTTrackingRequestMode)).toString();
            DESTLastdeath = Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.lastdeath", a -> String.valueOf(defaults.DESTLastdeath)));

            dimensionRatios = new Gson().fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> String.valueOf(defaults.dimensionRatios)),arrayListMap);
            colorPresets = new Gson().fromJson((String) properties.computeIfAbsent("color-presets", a -> String.valueOf(defaults.colorPresets)),arrayListMap);
        }
    }
    public static void save() {
        try (var file = new FileOutputStream(configFile(), false)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            file.write("# DirectionHUD Config\n".getBytes());
            file.write(("version="+defaults.version).getBytes());
            file.write("\n".getBytes());
            file.write(("\nmax-xz=" + MAXxz).getBytes());
            file.write(("\nmax-y=" + MAXy).getBytes());
            file.write(("\n# "+CUtl.lang("config.max.info",CUtl.lang("config.max.info_2")).getString()).getBytes());
            file.write(("\nonline-mode=" + online).getBytes());
            file.write(("\n# "+CUtl.lang("config.online_mode.info").getString()).getBytes());

            file.write(("\ndimensions="+gson.toJson(dimensions)).getBytes());
            file.write(("\n# "+CUtl.lang("config.dimensions.info").append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_3",CUtl.lang("config.dimensions.info_3.1"),
                            CUtl.lang("config.dimensions.info_3.2"),CUtl.lang("config.dimensions.info_3.3"))).append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_4")).append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_5")).append("\n# ")
                    .append(CUtl.lang("config.dimensions.info_6")).getString()).getBytes());
            file.write(("\ndimension-ratios="+gson.toJson(dimensionRatios)).getBytes());
            file.write(("\n# "+CUtl.lang("config.dimension_ratios.info").append("\n# ")
                    .append(CUtl.lang("config.dimension_ratios.info_2",CUtl.lang("config.dimension_ratios.info_2.1"),
                            CUtl.lang("config.dimension_ratios.info_2.2"))).getString()).getBytes());

            file.write(("\ndestination-saving=" + DESTSaving).getBytes());
            file.write(("\n# "+CUtl.lang("config.dest_saving.info").getString()).getBytes());
            file.write(("\ndestination-max-saved=" + MAXSaved).getBytes());
            file.write(("\n# "+CUtl.lang("config.dest_max_saved.info").getString()).getBytes());
            file.write(("\nsocial-commands=" + social).getBytes());
            file.write(("\n# "+CUtl.lang("config.social.info",CUtl.lang("config.social.info_2")).getString()).getBytes());
            file.write(("\ndeath-saving=" + deathsaving).getBytes());
            file.write(("\n# "+CUtl.lang("config.death_saving.info").getString()).getBytes());
            file.write(("\nhud-editing=" + HUDEditing).getBytes());
            file.write(("\n# "+CUtl.lang("config.hud_editing.info").getString()).getBytes());
            file.write(("\nhud-refresh=" + HUDRefresh).getBytes());
            file.write(("\n# "+CUtl.lang("config.hud_refresh.info").getString()).getBytes());

            file.write(("\n\n\n# "+CUtl.lang("config.default").getString()).getBytes());
            file.write(("\n# "+CUtl.lang("config.default.info").getString()).getBytes());
            file.write(("\ncolor-presets=" + gson.toJson(colorPresets)).getBytes());
            file.write(("\n# "+CUtl.lang("config.color_presets.info").getString()).getBytes());

            file.write(("\n\n# "+CUtl.lang("config.hud").getString()).getBytes());
            file.write(("\nhud.enabled=" + HUDEnabled).getBytes());
            file.write(("\nhud.order=" + HUDOrder).getBytes());
            file.write(("\n# "+CUtl.lang("config.hud.order.options").getString()).getBytes());
            file.write(("\n\n# "+CUtl.lang("config.hud.module").getString()).getBytes());
            file.write(("\nhud.module.coordinates=" + HUDCoordinates).getBytes());
            file.write(("\nhud.module.distance=" + HUDDistance).getBytes());
            file.write(("\nhud.module.tracking=" + HUDTracking).getBytes());
            file.write(("\nhud.module.destination=" + HUDDestination).getBytes());
            file.write(("\nhud.module.direction=" + HUDDirection).getBytes());
            file.write(("\nhud.module.time=" + HUDTime).getBytes());
            file.write(("\nhud.module.weather=" + HUDWeather).getBytes());
            file.write(("\n\n# "+CUtl.lang("config.settings").getString()).getBytes());
            file.write(("\nhud.settings.type=" + HUDType).getBytes());
            file.write(("\n# "+CUtl.lang("config.hud.settings.type.options").getString()).getBytes());
            file.write(("\nhud.settings.bossbar.color=" + HUDBarColor).getBytes());
            file.write(("\n# "+CUtl.lang("config.hud.settings.bossbar.color.options").getString()).getBytes());
            file.write(("\nhud.settings.bossbar.distance=" + HUDBarShowDistance).getBytes());
            file.write(("\nhud.settings.bossbar.distance_max=" + HUDBarDistanceMax).getBytes());
            file.write(("\nhud.settings.module.time_24hr=" + HUDTime24HR).getBytes());
            file.write(("\nhud.settings.module.tracking_target=" + HUDTrackingTarget).getBytes());
            file.write(("\n# "+CUtl.lang("config.hud.settings.module.tracking_target.options").getString()).getBytes());
            file.write(("\n\n# "+CUtl.lang("config.hud.color").getString()).getBytes());
            file.write(("\n# "+CUtl.lang("config.color.options").getString()).getBytes());
            file.write(("\nhud.color.primary=" + HUDPrimaryColor).getBytes());
            file.write(("\nhud.color.primary-bold=" + HUDPrimaryBold).getBytes());
            file.write(("\nhud.color.primary-italics=" + HUDPrimaryItalics).getBytes());
            file.write(("\nhud.color.primary-rainbow=" + HUDPrimaryRainbow).getBytes());
            file.write(("\nhud.color.secondary=" + HUDSecondaryColor).getBytes());
            file.write(("\nhud.color.secondary-bold=" + HUDSecondaryBold).getBytes());
            file.write(("\nhud.color.secondary-italics=" + HUDSecondaryItalics).getBytes());
            file.write(("\nhud.color.secondary-rainbow=" + HUDSecondaryRainbow).getBytes());

            file.write(("\n\n\n# "+CUtl.lang("config.dest").getString()).getBytes());
            file.write(("\n\n# "+CUtl.lang("config.settings").getString()).getBytes());
            file.write(("\ndest.settings.autoclear=" + DESTAutoClear).getBytes());
            file.write(("\ndest.settings.autoclear_rad=" + DESTAutoClearRad).getBytes());
            file.write(("\ndest.settings.autoconvert=" + DESTAutoConvert).getBytes());
            file.write(("\ndest.settings.ylevel=" + DESTYLevel).getBytes());
            file.write(("\n\n# "+CUtl.lang("config.hud.color").getString()).getBytes());
            file.write(("\n# "+CUtl.lang("config.color.options").getString()).getBytes());
            file.write(("\ndest.settings.particles.dest=" + DESTDestParticles).getBytes());
            file.write(("\ndest.settings.particles.dest_color=" + DESTDestParticleColor).getBytes());
            file.write(("\ndest.settings.particles.line=" + DESTLineParticles).getBytes());
            file.write(("\ndest.settings.particles.line_color=" + DESTLineParticleColor).getBytes());
            file.write(("\ndest.settings.particles.tracking=" + DESTTrackingParticles).getBytes());
            file.write(("\ndest.settings.particles.tracking_color=" + DESTTrackingParticleColor).getBytes());
            file.write(("\n\n# "+CUtl.lang("config.dest.settings.features").getString()).getBytes());
            file.write(("\ndest.settings.features.send=" + DESTSend).getBytes());
            file.write(("\ndest.settings.features.track=" + DESTTrack).getBytes());
            file.write(("\ndest.settings.features.track_request_mode=" + DESTTrackingRequestMode).getBytes());
            file.write(("\n# "+CUtl.lang("config.dest.settings.features.track_request_mode.options").getString()).getBytes());
            file.write(("\ndest.settings.features.lastdeath=" + DESTLastdeath).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static class defaults {
        public static final float version = 1.3f;
        public static final String lang = "en_us";
        public static final boolean DESTSaving = true;
        public static final int MAXSaved = 50;
        public static final int MAXy = 512;
        public static final int MAXxz = 30000000;
        public static final boolean deathsaving = true;
        public static final boolean social = true;
        public static final boolean HUDEditing = true;
        public static final int HUDRefresh = 1;
        public static final boolean online = true;
        public static final List<String> colorPresets = List.of("#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff","#ffffff");
        public static final String HUDType = HUDTypes.actionbar.toString();
        public static final String HUDBarColor = BarColors.white.toString();
        public static final boolean HUDBarShowDistance = true;
        public static final long HUDBarDistanceMax = 0;
        public static final boolean HUDEnabled = true;
        public static final ArrayList<String> HUDOrder = HUD.modules.DEFAULT;
        public static final boolean HUDCoordinates = true;
        public static final boolean HUDDistance = true;
        public static final boolean HUDTracking = false;
        public static final boolean HUDDestination = true;
        public static final boolean HUDDirection = true;
        public static final boolean HUDTime = true;
        public static final boolean HUDWeather = true;
        public static final boolean HUDTime24HR = false;
        public static final String HUDTrackingTarget = HUDTrackingTargets.player.toString();
        public static final String HUDPrimaryColor = DirectionHUD.PRIMARY;
        public static final boolean HUDPrimaryBold = false;
        public static final boolean HUDPrimaryItalics = false;
        public static final boolean HUDPrimaryRainbow = false;
        public static final String HUDSecondaryColor = "#ffffff";
        public static final boolean HUDSecondaryBold = false;
        public static final boolean HUDSecondaryItalics = false;
        public static final boolean HUDSecondaryRainbow = false;
        public static final boolean DESTAutoClear = true;
        public static final long DESTAutoClearRad = 2;
        public static final boolean DESTAutoConvert = false;
        public static final boolean DESTYLevel = false;
        public static final boolean DESTLineParticles = true;
        public static final String DESTLineParticleColor = DirectionHUD.SECONDARY;
        public static final boolean DESTDestParticles = true;
        public static final String DESTDestParticleColor = DirectionHUD.PRIMARY;
        public static final boolean DESTTrackingParticles = true;
        public static final String DESTTrackingParticleColor = Assets.mainColors.track;
        public static final boolean DESTSend = true;
        public static String DESTTrackingRequestMode = DESTTrackingRequestModes.request.toString();
        public static final boolean DESTTrack = true;
        public static final boolean DESTLastdeath = true;
        public static final List<String> dimensions = Utl.dim.DEFAULT_DIMENSIONS;
        public static final List<String> dimensionRatios = Utl.dim.DEFAULT_RATIOS;
    }
}
