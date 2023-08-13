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
    public static Object getDestSetting(Destination.Settings type) {
        Object output = false;
        switch (type) {
            case autoclear -> output = DESTAutoClear;
            case autoclear_rad -> output = DESTAutoClearRad;
            case autoconvert -> output = DESTAutoConvert;
            case ylevel -> output = DESTYLevel;
            case particles__dest -> output = DESTDestParticles;
            case particles__dest_color -> output = DESTDestParticleColor;
            case particles__line -> output = DESTLineParticles;
            case particles__line_color -> output = DESTLineParticleColor;
            case particles__tracking -> output =DESTTrackingParticles;
            case particles__tracking_color -> output=DESTTrackingParticleColor;
            case features__send -> output=DESTSend;
            case features__track -> output=DESTTrack;
            case features__lastdeath -> output=DESTLastdeath;
        }
        return output;
    }
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
    public enum HUDTypes {
        actionbar,
        bossbar;
        public static final HUDTypes[] values = values();
        public HUDTypes next() {
            return values[(ordinal() + 1) % values.length];
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
        white
    }
    public static String HUDBarColor = defaults.HUDBarColor;
    public static boolean HUDBarShowDistance = defaults.HUDBarShowDistance;
    public static int HUDBarDistanceMax = defaults.HUDBarDistanceMax;
    public static boolean HUDEnabled = defaults.HUDEnabled;
    public static String HUDOrder = defaults.HUDOrder;
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
    public static int DESTAutoClearRad = defaults.DESTAutoClearRad;
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
        //one.oth3r.directionhud.common.HUD SETTINGS
        HUDEnabled = defaults.HUDEnabled;
        HUDOrder = defaults.HUDOrder;
        HUDCoordinates = defaults.HUDCoordinates;
        HUDDistance = defaults.HUDDistance;
        HUDTracking = defaults.HUDTracking;
        HUDDestination = defaults.HUDDestination;
        HUDDirection = defaults.HUDDirection;
        HUDTime = defaults.HUDTime;
        HUDWeather = defaults.HUDWeather;
//        HUD24HR = defaults.HUD24HR;
        //one.oth3r.directionhud.common.HUD COLORS
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
        //todo this
        //HUD SETTINGS
        HUDEnabled = PlayerData.get.hud.state(player);
        HUDOrder = PlayerData.get.hud.order(player);
        HUDCoordinates = PlayerData.get.hud.module.coordinates(player);
        HUDDistance = PlayerData.get.hud.module.distance(player);
        HUDTracking = PlayerData.get.hud.module.tracking(player);
        HUDDestination = PlayerData.get.hud.module.destination(player);
        HUDDirection = PlayerData.get.hud.module.direction(player);
        HUDTime = PlayerData.get.hud.module.time(player);
        HUDWeather = PlayerData.get.hud.module.weather(player);
//        HUD24HR = PlayerData.get.hud.setting.time24h(player);
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
//        DESTAutoClear = PlayerData.get.dest.setting.autoclear(player);
//        DESTAutoClearRad = PlayerData.get.dest.setting.autoclearrad(player);
//        DESTAutoConvert = PlayerData.get.dest.setting.autoconvert(player);
//        DESTYLevel = PlayerData.get.dest.setting.ylevel(player);
//        DESTSend = PlayerData.get.dest.setting.send(player);
//        DESTTrack = PlayerData.get.dest.setting.track(player);
//        DESTLastdeath = PlayerData.get.dest.setting.lastdeath(player);
//        //DEST PARTICLES
//        DESTLineParticles = PlayerData.get.dest.setting.particles.line(player);
//        DESTLineParticleColor = PlayerData.get.dest.setting.particles.linecolor(player);
//        DESTDestParticles = PlayerData.get.dest.setting.particles.dest(player);
//        DESTDestParticleColor = PlayerData.get.dest.setting.particles.destcolor(player);
//        DESTTrackingParticles = PlayerData.get.dest.setting.particles.tracking(player);
//        DESTTrackingParticleColor = PlayerData.get.dest.setting.particles.trackingcolor(player);
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
        DESTSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("destination-saving", a -> String.valueOf(defaults.DESTSaving)));
        MAXSaved = Integer.parseInt((String) properties.computeIfAbsent("destination-max-saved", a -> String.valueOf(defaults.MAXSaved)));
        deathsaving = Boolean.parseBoolean((String) properties.computeIfAbsent("death-saving", a -> String.valueOf(defaults.deathsaving)));
        HUDEditing = Boolean.parseBoolean((String) properties.computeIfAbsent("hud-editing", a -> String.valueOf(defaults.HUDEditing)));
        HUDRefresh = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-refresh", a -> String.valueOf(defaults.HUDRefresh)))));
        online = Boolean.parseBoolean((String) properties.computeIfAbsent("online-mode", a -> String.valueOf(defaults.online)));
        //HUD
        HUDEnabled = Boolean.parseBoolean((String) properties.computeIfAbsent("enabled", a -> String.valueOf(defaults.HUDEnabled)));
        HUDOrder = HUD.modules.fixOrder((String) properties.computeIfAbsent("order", a -> defaults.HUDOrder));
//        HUD24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("time24hr", a -> String.valueOf(defaults.HUD24HR)));
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
        //DIM
        Type mapType = new TypeToken<ArrayList<String>>() {}.getType();
        dimensionRatios = new Gson().fromJson((String)
                properties.computeIfAbsent("dimension-ratios", a -> String.valueOf(defaults.dimensionRatios)),mapType);
        dimensions = new Gson().fromJson((String)
                properties.computeIfAbsent("dimensions", a -> String.valueOf(defaults.dimensions)),mapType);

        if (version == 1.1) {
            HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("compass", a -> String.valueOf(defaults.HUDTracking)));
        }
        if (version >= 1.2) {
            HUDPrimaryColor = CUtl.color.updateOld((String) properties.computeIfAbsent("primary-color", a -> defaults.HUDPrimaryColor),defaults.HUDPrimaryColor);
            HUDSecondaryColor = CUtl.color.updateOld((String) properties.computeIfAbsent("secondary-color", a -> defaults.HUDSecondaryColor),defaults.HUDSecondaryColor);
            DESTLineParticleColor = CUtl.color.updateOld((String) properties.computeIfAbsent("line-particle-color", a -> defaults.DESTLineParticleColor),defaults.DESTLineParticleColor);
            DESTDestParticleColor = CUtl.color.updateOld((String) properties.computeIfAbsent("dest-particle-color", a -> defaults.DESTDestParticleColor),defaults.DESTDestParticleColor);
            DESTTrackingParticleColor = CUtl.color.updateOld((String) properties.computeIfAbsent("tracking-particle-color", a -> defaults.DESTTrackingParticleColor),defaults.DESTDestParticleColor);
        }
        if (version >= 1.2) {
            MAXxz = Integer.parseInt((String) properties.computeIfAbsent("max-xz", a -> String.valueOf(defaults.MAXxz)));
            MAXy = Integer.parseInt((String) properties.computeIfAbsent("max-y", a -> String.valueOf(defaults.MAXy)));
            social = Boolean.parseBoolean((String) properties.computeIfAbsent("social-commands", a -> String.valueOf(defaults.social)));
            DESTAutoConvert = Boolean.parseBoolean((String) properties.computeIfAbsent("autoconvert", a -> String.valueOf(defaults.DESTAutoConvert)));
            HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking", a -> String.valueOf(defaults.HUDTracking)));
            DESTTrackingParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking-particles", a -> String.valueOf(defaults.DESTTrackingParticles)));
        }
        if (version >= 1.3) {
            HUDPrimaryColor = CUtl.color.format((String) properties.computeIfAbsent("primary-color", a -> defaults.HUDPrimaryColor),defaults.HUDPrimaryColor);
            HUDSecondaryColor = CUtl.color.format((String) properties.computeIfAbsent("secondary-color", a -> defaults.HUDSecondaryColor),defaults.HUDSecondaryColor);
            DESTLineParticleColor = CUtl.color.format((String) properties.computeIfAbsent("line-particle-color", a -> defaults.DESTLineParticleColor),defaults.DESTLineParticleColor);
            DESTDestParticleColor = CUtl.color.format((String) properties.computeIfAbsent("dest-particle-color", a -> defaults.DESTDestParticleColor),defaults.DESTDestParticleColor);
            DESTTrackingParticleColor = CUtl.color.format((String) properties.computeIfAbsent("tracking-particle-color", a -> defaults.DESTTrackingParticleColor),defaults.DESTDestParticleColor);
        }
        //todo
        // update config to update config
        //  lang file
    }
    public static void save() {
        try (var file = new FileOutputStream(configFile(), false)) {
            file.write("# DirectionHUD Config\n".getBytes());
            file.write(("version="+defaults.version).getBytes());
            file.write("\n".getBytes());
            file.write(("\nmax-xz=" + MAXxz).getBytes());
            file.write(("\nmax-y=" + MAXy).getBytes());
            file.write(("\ndestination-saving=" + DESTSaving).getBytes());
            file.write(("\ndestination-max-saved=" + MAXSaved).getBytes());
            file.write(("\nsocial-commands=" + social).getBytes());
            file.write(("\ndeath-saving=" + deathsaving).getBytes());
            file.write(("\nhud-editing=" + HUDEditing).getBytes());
            file.write(("\n# one.oth3r.directionhud.common.HUD refresh time in ticks:").getBytes());
            file.write(("\nhud-refresh=" + HUDRefresh).getBytes());
            file.write(("\n# Turn off for offline mode servers, uses a name based file system:").getBytes());
            file.write(("\nonline-mode=" + online).getBytes());
            file.write(("\n\n# DirectionHUD Player Defaults\n").getBytes());
            file.write("\n# one.oth3r.directionhud.common.HUD".getBytes());
            file.write(("\nenabled=" + HUDEnabled).getBytes());
            file.write(("\n# one.oth3r.directionhud.common.HUD Module order, all modules don't have to be listed:").getBytes());
            file.write(("\norder=" + HUDOrder).getBytes());
//            file.write(("\ntime24hr=" + HUDTime24HR).getBytes());
            file.write(("\nprimary-color=" + HUDPrimaryColor).getBytes());
            file.write(("\nprimary-bold=" + HUDPrimaryBold).getBytes());
            file.write(("\nprimary-italics=" + HUDPrimaryItalics).getBytes());
            file.write(("\nprimary-rainbow=" + HUDPrimaryRainbow).getBytes());
            file.write(("\nsecondary-color=" + HUDSecondaryColor).getBytes());
            file.write(("\nsecondary-bold=" + HUDSecondaryBold).getBytes());
            file.write(("\nsecondary-italics=" + HUDSecondaryItalics).getBytes());
            file.write(("\nsecondary-rainbow=" + HUDSecondaryRainbow).getBytes());
            file.write(("\n# VALID one.oth3r.directionhud.common.HUD COLORS: rainbow, hex colors, & all default minecraft colors. (light_purple -> pink & dark_purple -> purple)").getBytes());

            file.write("\n\n# Module State".getBytes());
            file.write(("\ncoordinates=" + HUDCoordinates).getBytes());
            file.write(("\ndistance=" + HUDDistance).getBytes());
            file.write(("\ntracking=" + HUDTracking).getBytes());
            file.write(("\ndestination=" + HUDDestination).getBytes());
            file.write(("\ndirection=" + HUDDirection).getBytes());
            file.write(("\ntime=" + HUDTime).getBytes());
            file.write(("\nweather=" + HUDWeather).getBytes());

            file.write("\n\n# Destination".getBytes());
            file.write(("\nautoclear=" + DESTAutoClear).getBytes());
            file.write(("\nautoclear-radius=" + DESTAutoClearRad).getBytes());
            file.write(("\nautoconvert=" + DESTAutoConvert).getBytes());
            file.write(("\ny-level=" + DESTYLevel).getBytes());
            file.write(("\nline-particles=" + DESTLineParticles).getBytes());
            file.write(("\nline-particle-color=" + DESTLineParticleColor).getBytes());
            file.write(("\ndest-particles=" + DESTDestParticles).getBytes());
            file.write(("\ndest-particle-color=" + DESTDestParticleColor).getBytes());
            file.write(("\ntracking-particles=" + DESTTrackingParticles).getBytes());
            file.write(("\ntracking-particle-color=" + DESTTrackingParticleColor).getBytes());
            file.write(("\nsend=" + HUDDirection).getBytes());
            file.write(("\ntrack=" + HUDTime).getBytes());
            file.write(("\nlastdeath=" + DESTLastdeath).getBytes());
            file.write(("\n# VALID DEST COLORS: hex colors, & all default minecraft colors. (light_purple -> pink & dark_purple -> purple)").getBytes());
            file.write("\n\n# Dimension".getBytes());
            file.write("\n# Add/edit custom dimensions and conversion ratios".getBytes());
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            file.write(("\ndimensions="+gson.toJson(dimensions)).getBytes());
            file.write(("\ndimension-ratios="+gson.toJson(dimensionRatios)).getBytes());
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
        public static final String HUDType = HUDTypes.actionbar.toString();
        public static final String HUDBarColor = BarColors.white.toString();
        public static final boolean HUDBarShowDistance = true;
        public static final int HUDBarDistanceMax = 0;
        public static final boolean HUDEnabled = true;
        public static final String HUDOrder = HUD.modules.allModules();
        public static final boolean HUDCoordinates = true;
        public static final boolean HUDDistance = true;
        public static final boolean HUDTracking = false;
        public static final boolean HUDDestination = true;
        public static final boolean HUDDirection = true;
        public static final boolean HUDTime = true;
        public static final boolean HUDWeather = true;
        public static final boolean HUDTime24HR = false;
        public static final String HUDTrackingTarget = HUDTrackingTargets.player.toString();
        public static final String HUDPrimaryColor = Assets.mainColors.pri;
        public static final boolean HUDPrimaryBold = false;
        public static final boolean HUDPrimaryItalics = false;
        public static final boolean HUDPrimaryRainbow = false;
        public static final String HUDSecondaryColor = "#ffffff";
        public static final boolean HUDSecondaryBold = false;
        public static final boolean HUDSecondaryItalics = false;
        public static final boolean HUDSecondaryRainbow = false;
        public static final boolean DESTAutoClear = true;
        public static final int DESTAutoClearRad = 2;
        public static final boolean DESTAutoConvert = false;
        public static final boolean DESTYLevel = false;
        public static final boolean DESTLineParticles = true;
        public static final String DESTLineParticleColor = Assets.mainColors.sec;
        public static final boolean DESTDestParticles = true;
        public static final String DESTDestParticleColor = Assets.mainColors.pri;
        public static final boolean DESTTrackingParticles = true;
        public static final String DESTTrackingParticleColor = Assets.mainColors.track;
        public static final boolean DESTSend = true;
        public static final boolean DESTTrack = true;
        public static final boolean DESTLastdeath = true;
        public static final List<String> dimensions = Utl.dim.DEFAULT_DIMENSIONS;
        public static final List<String> dimensionRatios = Utl.dim.DEFAULT_RATIOS;
    }
}
