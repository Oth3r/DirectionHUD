package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.HUD;
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
    public static boolean HUDEnabled = defaults.HUDEnabled;
    public static String HUDOrder = defaults.HUDOrder;
    public static boolean HUDCoordinates = defaults.HUDCoordinates;
    public static boolean HUDDistance = defaults.HUDDistance;
    public static boolean HUDTracking = defaults.HUDTracking;
    public static boolean HUDDestination = defaults.HUDDestination;
    public static boolean HUDDirection = defaults.HUDDirection;
    public static boolean HUDTime = defaults.HUDTime;
    public static boolean HUDWeather = defaults.HUDWeather;
    public static boolean HUD24HR = defaults.HUD24HR;
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
        HUD24HR = defaults.HUD24HR;
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
        //one.oth3r.directionhud.common.HUD SETTINGS
        HUDEnabled = PlayerData.get.hud.state(player);
        HUDOrder = PlayerData.get.hud.order(player);
        HUDCoordinates = PlayerData.get.hud.module.coordinates(player);
        HUDDistance = PlayerData.get.hud.module.distance(player);
        HUDTracking = PlayerData.get.hud.module.tracking(player);
        HUDDestination = PlayerData.get.hud.module.destination(player);
        HUDDirection = PlayerData.get.hud.module.direction(player);
        HUDTime = PlayerData.get.hud.module.time(player);
        HUDWeather = PlayerData.get.hud.module.weather(player);
        HUD24HR = PlayerData.get.hud.setting.time24h(player);
        //HUD COLORS
        HUDPrimaryColor = HUD.color.getHUDColors(player)[0];
        HUDPrimaryBold = HUD.color.getHUDBold(player,1);
        HUDPrimaryItalics = HUD.color.getHUDItalics(player, 1);
        HUDPrimaryRainbow = HUD.color.getHUDRGB(player,1);
        //SEC
        HUDSecondaryColor = HUD.color.getHUDColors(player)[1];
        HUDSecondaryBold = HUD.color.getHUDBold(player,2);
        HUDSecondaryItalics = HUD.color.getHUDItalics(player, 2);
        HUDSecondaryRainbow = HUD.color.getHUDRGB(player,2);
        //DEST SETTINGS
        DESTAutoClear = PlayerData.get.dest.setting.autoclear(player);
        DESTAutoClearRad = PlayerData.get.dest.setting.autoclearrad(player);
        DESTAutoConvert = PlayerData.get.dest.setting.autoconvert(player);
        DESTYLevel = PlayerData.get.dest.setting.ylevel(player);
        DESTSend = PlayerData.get.dest.setting.send(player);
        DESTTrack = PlayerData.get.dest.setting.track(player);
        DESTLastdeath = PlayerData.get.dest.setting.lastdeath(player);
        //DEST PARTICLES
        DESTLineParticles = PlayerData.get.dest.setting.particles.line(player);
        DESTLineParticleColor = PlayerData.get.dest.setting.particles.linecolor(player);
        DESTDestParticles = PlayerData.get.dest.setting.particles.dest(player);
        DESTDestParticleColor = PlayerData.get.dest.setting.particles.destcolor(player);
        DESTTrackingParticles = PlayerData.get.dest.setting.particles.tracking(player);
        DESTTrackingParticleColor = PlayerData.get.dest.setting.particles.trackingcolor(player);
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
        DESTSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("destination-saving", a -> defaults.DESTSaving+""));
        MAXSaved = Integer.parseInt((String) properties.computeIfAbsent("destination-max-saved", a -> defaults.MAXSaved+""));
        deathsaving = Boolean.parseBoolean((String) properties.computeIfAbsent("death-saving", a -> defaults.deathsaving +""));
        HUDEditing = Boolean.parseBoolean((String) properties.computeIfAbsent("hud-editing", a -> defaults.HUDEditing +""));
        HUDRefresh = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-refresh", a -> defaults.HUDRefresh+""))));
        online = Boolean.parseBoolean((String) properties.computeIfAbsent("online-mode", a -> defaults.online +""));
        //one.oth3r.directionhud.common.HUD
        HUDEnabled = Boolean.parseBoolean((String) properties.computeIfAbsent("enabled", a -> defaults.HUDEnabled+""));
        HUDOrder = HUD.order.fixOrder((String) properties.computeIfAbsent("order", a -> defaults.HUDOrder));
        HUD24HR = Boolean.parseBoolean((String) properties.computeIfAbsent("time24hr", a -> defaults.HUD24HR+""));
        HUDPrimaryColor = Utl.color.fix((String) properties.computeIfAbsent("primary-color", a -> defaults.HUDPrimaryColor),true,defaults.HUDPrimaryColor);
        HUDPrimaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-bold", a -> defaults.HUDPrimaryBold+""));
        HUDPrimaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-italics", a -> defaults.HUDPrimaryItalics+""));
        HUDPrimaryRainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("primary-rainbow", a -> defaults.HUDPrimaryRainbow+""));
        HUDSecondaryColor = Utl.color.fix((String) properties.computeIfAbsent("secondary-color", a -> defaults.HUDSecondaryColor),true,defaults.HUDSecondaryColor);
        HUDSecondaryBold = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-bold", a -> defaults.HUDSecondaryBold+""));
        HUDSecondaryItalics = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-italics", a -> defaults.HUDSecondaryItalics+""));
        HUDSecondaryRainbow = Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-rainbow", a -> defaults.HUDSecondaryRainbow+""));
        //MODULES
        HUDCoordinates = Boolean.parseBoolean((String) properties.computeIfAbsent("coordinates", a -> defaults.HUDCoordinates+""));
        HUDDistance = Boolean.parseBoolean((String) properties.computeIfAbsent("distance", a -> defaults.HUDDistance+""));
        HUDDestination = Boolean.parseBoolean((String) properties.computeIfAbsent("destination", a -> defaults.HUDDestination+""));
        HUDDirection = Boolean.parseBoolean((String) properties.computeIfAbsent("direction", a -> defaults.HUDDirection+""));
        HUDTime = Boolean.parseBoolean((String) properties.computeIfAbsent("time", a -> defaults.HUDTime+""));
        HUDWeather = Boolean.parseBoolean((String) properties.computeIfAbsent("weather", a -> defaults.HUDWeather+""));
        //DEST
        DESTAutoClear = Boolean.parseBoolean((String) properties.computeIfAbsent("autoclear", a -> defaults.DESTAutoClear+""));
        DESTAutoClearRad = Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("autoclear-radius", a -> defaults.DESTAutoClearRad+""))));
        DESTYLevel = Boolean.parseBoolean((String) properties.computeIfAbsent("y-level", a -> defaults.DESTYLevel+""));
        DESTLineParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("line-particles", a -> defaults.DESTLineParticles+""));
        DESTLineParticleColor = Utl.color.fix((String) properties.computeIfAbsent("line-particle-color", a -> defaults.DESTLineParticleColor),false,defaults.DESTLineParticleColor);
        DESTDestParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("dest-particles", a -> defaults.DESTDestParticles+""));
        DESTDestParticleColor = Utl.color.fix((String) properties.computeIfAbsent("dest-particle-color", a -> defaults.DESTDestParticleColor),false,defaults.DESTDestParticleColor);
        DESTSend = Boolean.parseBoolean((String) properties.computeIfAbsent("send", a -> defaults.DESTSend+""));
        DESTTrack = Boolean.parseBoolean((String) properties.computeIfAbsent("track", a -> defaults.DESTTrack+""));
        //DIM
        Type mapType = new TypeToken<ArrayList<String>>() {}.getType();
        if (!DirectionHUD.isMod)
            dimensionRatios = new Gson().fromJson((String)
                    properties.computeIfAbsent("dimension-ratios", a -> defaults.dimensionRatios+""),mapType);
        dimensions = new Gson().fromJson((String)
                properties.computeIfAbsent("dimensions", a -> defaults.dimensions+""),mapType);

        if (version == 1.1) {
            HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("compass", a -> defaults.HUDTracking+""));
        }
        if (version >= 1.2) {
            MAXxz = Integer.parseInt((String) properties.computeIfAbsent("max-xz", a -> defaults.MAXxz+""));
            MAXy = Integer.parseInt((String) properties.computeIfAbsent("max-y", a -> defaults.MAXy+""));
            social = Boolean.parseBoolean((String) properties.computeIfAbsent("social-commands", a -> defaults.social+""));
            DESTAutoConvert = Boolean.parseBoolean((String) properties.computeIfAbsent("autoconvert", a -> defaults.DESTAutoConvert+""));
            HUDTracking = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking", a -> defaults.HUDTracking+""));
            DESTTrackingParticles = Boolean.parseBoolean((String) properties.computeIfAbsent("tracking-particles", a -> defaults.DESTTrackingParticles+""));
            DESTTrackingParticleColor = Utl.color.fix((String) properties.computeIfAbsent("tracking-particle-color", a -> defaults.DESTTrackingParticleColor),false,defaults.DESTDestParticleColor);
        }
        if (version == 1.21) {
            dimensionRatios = new Gson().fromJson((String)
                    properties.computeIfAbsent("dimension-ratios", a -> defaults.dimensionRatios+""),mapType);
        }
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
            file.write(("\ntime24hr=" + HUD24HR).getBytes());
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
            file.write(("\nsend=" + DESTSend).getBytes());
            file.write(("\ntrack=" + DESTTrack).getBytes());
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
        public static float version = 1.21f;
        public static String lang = "en_us";
        public static boolean DESTSaving = true;
        public static int MAXSaved = 50;
        public static int MAXy = 512;
        public static int MAXxz = 30000000;
        public static boolean deathsaving = true;
        public static boolean social = true;
        public static boolean HUDEditing = true;
        public static int HUDRefresh = 1;
        public static boolean online = true;
        public static boolean HUDEnabled = true;
        public static String HUDOrder = HUD.order.allModules();
        public static boolean HUDCoordinates = true;
        public static boolean HUDDistance = true;
        public static boolean HUDTracking = false;
        public static boolean HUDDestination = true;
        public static boolean HUDDirection = true;
        public static boolean HUDTime = true;
        public static boolean HUDWeather = true;
        public static boolean HUD24HR = false;
        public static String HUDPrimaryColor = Assets.mainColors.pri;
        public static boolean HUDPrimaryBold = false;
        public static boolean HUDPrimaryItalics = false;
        public static boolean HUDPrimaryRainbow = false;
        public static String HUDSecondaryColor = "white";
        public static boolean HUDSecondaryBold = false;
        public static boolean HUDSecondaryItalics = false;
        public static boolean HUDSecondaryRainbow = false;
        public static boolean DESTAutoClear = true;
        public static int DESTAutoClearRad = 2;
        public static boolean DESTAutoConvert = false;
        public static boolean DESTYLevel = false;
        public static boolean DESTLineParticles = true;
        public static String DESTLineParticleColor = Assets.mainColors.sec;
        public static boolean DESTDestParticles = true;
        public static String DESTDestParticleColor = Assets.mainColors.pri;
        public static boolean DESTTrackingParticles = true;
        public static String DESTTrackingParticleColor = Assets.mainColors.track;
        public static boolean DESTSend = true;
        public static boolean DESTTrack = true;
        public static boolean DESTLastdeath = true;
        public static List<String> dimensions = Utl.dim.DEFAULT_DIMENSIONS;
        public static List<String> dimensionRatios = Utl.dim.DEFAULT_RATIOS;
    }
}
