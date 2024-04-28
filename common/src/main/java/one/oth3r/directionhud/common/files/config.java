package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.Destination.Setting.*;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.*;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Lang;
import one.oth3r.directionhud.common.utils.Helper.*;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;

public class config {

    public static class defaults {
        public static final float version = 1.6f;
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
        public static final int MAXColorPresets = 14;
        public static final List<String> colorPresets = new ArrayList<>();
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
    public static int MAXColorPresets = defaults.MAXColorPresets;
    public static List<String> colorPresets = defaults.colorPresets;

    public static File configFile() {
        return new File(DirectionHUD.CONFIG_DIR+"DirectionHUD.properties");
    }

    public static void load() {
        if (!configFile().exists() || !configFile().canRead()) {
            // if moving config location failed / not plausible, save the file (make a new config) and try loading again
            if (!moveConfigLocation()) {
                DirectionHUD.LOGGER.info("Creating new DirectionHUD.properties");
                save();
            } else {
                DirectionHUD.LOGGER.info("Moved DirectionHUD.properties to new `/directionhud` directory");
            }
        }
        try (FileInputStream fileStream = new FileInputStream(configFile())) {
            Properties properties = new Properties();
            properties.load(fileStream);
            String version = (String) properties.computeIfAbsent("version", a -> String.valueOf(defaults.version));
            if (version.contains("v")) version = version.substring(1);
            loadVersion(properties,Float.parseFloat(version));
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR READING CONFIG - PLEASE REPORT WITH THE ERROR LOG");
            e.printStackTrace();
        }
        PData.loadDefaults();
        LangReader.loadLanguageFile();
        Dimension.load();
        if (globalDESTs) GlobalDest.load();
        save();
    }

    /**
     * tries to move the config file to the new config location for fabric
     *  - moved since 1.7.0
     */
    public static boolean moveConfigLocation() {
        // only for fabric versions
        if (!DirectionHUD.isMod) return false;
        // the file but back one directory
        File file = new File(DirectionHUD.CONFIG_DIR.substring(0,DirectionHUD.CONFIG_DIR.length()-13)+"DirectionHUD.properties");
        // if it doesnt exist, it wasn't there before
        if (!file.exists()) return false;
        // try moving the file, if not possible oh well
        try {
            return file.renameTo(configFile());
        } catch (Exception ignored) {
            DirectionHUD.LOGGER.info("CONFIG FILE MIGRATION FAILED. creating a new config file in the new config directory.");
        }
        return false;
    }

    public static void loadVersion(Properties properties, float version) {
        DefaultPData DEFAULTS = PlayerData.DEFAULTS;
        PDDestination.Settings destSettings = DEFAULTS.getDEST().getSetting();
        PDDestination.Settings.Particles destSParticles = destSettings.getParticles();
        PDDestination.Settings.Features destSFeatures = destSettings.getFeatures();
        PDHud hud = DEFAULTS.getHud();
        PDHud.Modules hudModules = hud.getModule();
        PDHud.Settings hudSettings = hud.getSetting();
        PDHud.Settings.Bossbar hudSBossbar = hudSettings.getBossbar();
        PDHud.Settings.Module hudSModule = hudSettings.getModule();

        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            // json maps
            Type arrayListMap = new TypeToken<ArrayList<String>>() {}.getType();
            Type moduleListMap = new TypeToken<ArrayList<Hud.Module>>() {}.getType();
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
            // COLOR PRESETS
            try {
                colorPresets = DHud.preset.custom.validate(gson.fromJson((String) properties.computeIfAbsent("color-presets", a -> gson.toJson(defaults.colorPresets)), arrayListMap));
            } catch (JsonSyntaxException ignored) {}
            MAXColorPresets = Integer.parseInt((String) properties.computeIfAbsent("max-color-presets", a -> String.valueOf(defaults.MAXColorPresets)));

            // CONFIG UPDATER, if the version is lower than the current, load from the old config
            if (version <= 1.5f) {
                // update destinations to new system
                try {
                    Dimension.getDimensionSettings().setDimensions(Dimension.convertLegacyDimensions(gson.fromJson((String) properties.computeIfAbsent("dimensions", a -> ""), arrayListMap)));
                } catch (JsonSyntaxException ignored) {}
                // update ratios to new system
                try {
                    Dimension.getDimensionSettings().setRatios(Dimension.convertLegacyRatios(gson.fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> ""), arrayListMap)));
                } catch (JsonSyntaxException ignored) {}
                Dimension.save();

                // PLAYER DEFAULTS
                // HUD
                try {
                    hud.setOrder(Hud.modules.fixOrder(gson.fromJson((String) properties.computeIfAbsent("hud.order", a -> gson.toJson(hud.getOrder())), moduleListMap)));
                } catch (JsonSyntaxException ignored) {}

                // HUD MODULE STATES
                hudModules.setCoordinates(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.coordinates", a -> String.valueOf(hudModules.getCoordinates()))));
                hudModules.setDistance(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.distance", a -> String.valueOf(hudModules.getDistance()))));
                hudModules.setTracking(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.tracking", a -> String.valueOf(hudModules.getTracking()))));
                hudModules.setDestination(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.destination", a -> String.valueOf(hudModules.getDestination()))));
                hudModules.setDirection(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.direction", a -> String.valueOf(hudModules.getDirection()))));
                hudModules.setTime(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.time", a -> String.valueOf(hudModules.getTime()))));
                hudModules.setWeather(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.weather", a -> String.valueOf(hudModules.getWeather()))));
                hudModules.setSpeed(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.speed", a -> String.valueOf(hudModules.getSpeed()))));
                hudModules.setAngle(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.module.angle", a -> String.valueOf(hudModules.getAngle()))));

                // HUD MODULE SETTINGS
                hudSModule.setTrackingHybrid(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.tracking_hybrid", a -> String.valueOf(hudSModule.getTrackingHybrid()))));
                hudSModule.setTrackingTarget(Enums.get(properties.computeIfAbsent("hud.settings.module.tracking_target", a -> hudSModule.getTrackingTarget()), Hud.Setting.ModuleTrackingTarget.class).toString());
                hudSModule.setTrackingType(Enums.get(properties.computeIfAbsent("hud.settings.module.tracking_type", a -> hudSModule.getTrackingType()), Hud.Setting.ModuleTrackingType.class).toString());
                hudSModule.setTime24hr(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.time_24hr", a -> String.valueOf(hudSModule.getTime24hr()))));
                hudSModule.setSpeed3d(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.speed_3d", a -> String.valueOf(hudSModule.getSpeed3d()))));
                String pattern = (String) properties.computeIfAbsent("hud.settings.module.speed_pattern", a -> hudSModule.getSpeedPattern());
                try {
                    new DecimalFormat(pattern);
                    hudSModule.setSpeedPattern(pattern);
                } catch (IllegalArgumentException ignored) {}
                hudSModule.setAngleDisplay(Enums.get(properties.computeIfAbsent("hud.settings.module.angle_display", a -> hudSModule.getAngleDisplay()), Hud.Setting.ModuleAngleDisplay.class).toString());

                // HUD COLOR
                hud.setPrimary(
                        new PDHud.Color(
                                CUtl.color.format((String) properties.computeIfAbsent("hud.color.primary", a -> ""), hud.getPrimary().getColor()),
                                Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-bold", a -> String.valueOf(hud.getPrimary().getBold()))),
                                Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-italics", a -> String.valueOf(hud.getPrimary().getItalics()))),
                                Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.primary-rainbow", a -> String.valueOf(hud.getPrimary().getRainbow())))
                        )
                );
                hud.setSecondary(
                        new PDHud.Color(
                                CUtl.color.format((String) properties.computeIfAbsent("hud.color.secondary", a -> ""), hud.getSecondary().getColor()),
                                Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-bold", a -> String.valueOf(hud.getSecondary().getBold()))),
                                Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-italics", a -> String.valueOf(hud.getSecondary().getItalics()))),
                                Boolean.parseBoolean((String) properties.computeIfAbsent("hud.color.secondary-rainbow", a -> String.valueOf(hud.getSecondary().getRainbow())))
                        )
                );

                // HUD SETTINGS
                hudSettings.setState(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.state", a -> String.valueOf(hudSettings.getState()))));
                hudSettings.setType(Hud.Setting.DisplayType.get((String) properties.computeIfAbsent("hud.settings.type", a -> hudSettings.getType())).toString());
                hudSBossbar.setColor(Enums.get(properties.computeIfAbsent("hud.settings.bossbar.color", a -> hudSBossbar.getColor()), Hud.Setting.BarColor.class).toString());
                hudSBossbar.setDistance(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.bossbar.distance", a -> String.valueOf(hudSBossbar.getDistance()))));
                hudSBossbar.setDistanceMax(Integer.parseInt((String) properties.computeIfAbsent("hud.settings.bossbar.distance_max", a -> String.valueOf(hudSBossbar.getDistanceMax()))));
                // DEST SETTINGS
                destSettings.setAutoclear(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.autoclear", a -> String.valueOf(destSettings.getAutoclear()))));
                destSettings.setAutoclearRad(Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("dest.settings.autoclear_rad", a -> String.valueOf(destSettings.getAutoclearRad()))))));
                destSettings.setAutoconvert(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.autoconvert", a -> String.valueOf(destSettings.getAutoconvert()))));
                destSettings.setYlevel(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.ylevel", a -> String.valueOf(destSettings.getYlevel()))));
                // DEST COLOR
                destSParticles.setDest(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.dest", a -> String.valueOf(destSParticles.getDest()))));
                destSParticles.setDestColor(CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.dest_color", a -> ""), destSParticles.getDestColor()));
                destSParticles.setLine(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.line", a -> String.valueOf(destSParticles.getLine()))));
                destSParticles.setLineColor(CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.line_color", a -> ""), destSParticles.getLineColor()));
                destSParticles.setTracking(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.particles.tracking", a -> String.valueOf(destSParticles.getTracking()))));
                destSParticles.setTrackingColor(CUtl.color.format((String) properties.computeIfAbsent("dest.settings.particles.tracking_color", a -> ""), destSParticles.getTrackingColor()));
                // DEST FEATURES
                destSFeatures.setSend(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.send", a -> String.valueOf(destSFeatures.getSend()))));
                destSFeatures.setTrack(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.track", a -> String.valueOf(destSFeatures.getTrack()))));
                destSFeatures.setTrackRequestMode(Enums.get(properties.computeIfAbsent("dest.settings.features.track_request_mode", a -> destSFeatures.getTrackRequestMode()),TrackingRequestMode.class).toString());
                destSFeatures.setLastdeath(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.lastdeath", a -> String.valueOf(destSFeatures.getLastdeath()))));
            }
            if (version <= 1.4f) {
                try {
                    colorPresets = DHud.preset.custom.updateTo1_7(
                            gson.fromJson((String)properties.computeIfAbsent("color-presets",a->gson.toJson(new ArrayList<>())),arrayListMap));
                } catch (JsonSyntaxException ignored) {}
            }
            // everything before & 1.3
            if (version <= 1.3f) {
                DestMAX = Integer.parseInt((String) properties.computeIfAbsent("destination-max-saved", a -> String.valueOf(defaults.DestMAX)));
                LastDeathSaving = Boolean.parseBoolean((String) properties.computeIfAbsent("death-saving", a -> String.valueOf(defaults.LastDeathSaving)));
                hudSettings.setState(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.enabled", a -> String.valueOf(hudSettings.getState()))));
                HUDLoop = Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-refresh", a -> String.valueOf(defaults.HUDLoop)))));
            }
            // everything before & 1.21
            if (version <= 1.21f) {
                // update colors to new system
                hud.getPrimary().setColor(CUtl.color.updateOld((String) properties.computeIfAbsent("primary-color", a -> ""), hud.getPrimary().getColor()));
                hud.getSecondary().setColor(CUtl.color.updateOld((String) properties.computeIfAbsent("secondary-color", a -> ""), hud.getSecondary().getColor()));
                destSParticles.setLineColor(CUtl.color.updateOld((String) properties.computeIfAbsent("line-particle-color", a -> ""), destSParticles.getLineColor()));
                destSParticles.setDestColor(CUtl.color.updateOld((String) properties.computeIfAbsent("dest-particle-color", a -> ""), destSParticles.getDestColor()));

                //HUD
                hudSettings.setState(Boolean.parseBoolean((String) properties.computeIfAbsent("enabled", a -> String.valueOf(hudSettings.getState()))));
                List<String> orderList = List.of(((String) properties.computeIfAbsent("order", a -> "")).split(" "));
                ArrayList<Hud.Module> moduleArray = new ArrayList<>();
                for (String entry : orderList) moduleArray.add(Hud.Module.get(entry));
                hud.setOrder(Hud.modules.fixOrder(moduleArray));
                hudSModule.setTime24hr(Boolean.parseBoolean((String) properties.computeIfAbsent("time24hr", a -> String.valueOf(hudSModule.getTime24hr()))));
                hud.getPrimary().setBold(Boolean.parseBoolean((String) properties.computeIfAbsent("primary-bold", a -> String.valueOf(hud.getPrimary().getBold()))));
                hud.getPrimary().setItalics(Boolean.parseBoolean((String) properties.computeIfAbsent("primary-italics", a -> String.valueOf(hud.getPrimary().getItalics()))));
                hud.getPrimary().setRainbow(Boolean.parseBoolean((String) properties.computeIfAbsent("primary-rainbow", a -> String.valueOf(hud.getPrimary().getRainbow()))));
                hud.getSecondary().setBold(Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-bold", a -> String.valueOf(hud.getSecondary().getBold()))));
                hud.getSecondary().setItalics(Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-italics", a -> String.valueOf(hud.getSecondary().getItalics()))));
                hud.getSecondary().setRainbow(Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-rainbow", a -> String.valueOf(hud.getSecondary().getRainbow()))));
                //MODULES
                hudModules.setCoordinates(Boolean.parseBoolean((String) properties.computeIfAbsent("coordinates", a -> String.valueOf(hudModules.getCoordinates()))));
                hudModules.setDistance(Boolean.parseBoolean((String) properties.computeIfAbsent("distance", a -> String.valueOf(hudModules.getDistance()))));
                hudModules.setDestination(Boolean.parseBoolean((String) properties.computeIfAbsent("destination", a -> String.valueOf(hudModules.getDestination()))));
                hudModules.setDirection(Boolean.parseBoolean((String) properties.computeIfAbsent("direction", a -> String.valueOf(hudModules.getDirection()))));
                hudModules.setTime(Boolean.parseBoolean((String) properties.computeIfAbsent("time", a -> String.valueOf(hudModules.getTime()))));
                hudModules.setWeather(Boolean.parseBoolean((String) properties.computeIfAbsent("weather", a -> String.valueOf(hudModules.getWeather()))));
                hudModules.setTracking(Boolean.parseBoolean((String) properties.computeIfAbsent("tracking", a -> String.valueOf(hudModules.getTracking()))));
                //DEST

                destSettings.setAutoclear(Boolean.parseBoolean((String) properties.computeIfAbsent("autoclear", a -> String.valueOf(destSettings.getAutoclear()))));
                destSettings.setAutoclearRad(Math.min(15, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("autoclear-radius", a -> String.valueOf(destSettings.getAutoclearRad()))))));
                destSettings.setYlevel(Boolean.parseBoolean((String) properties.computeIfAbsent("y-level", a -> String.valueOf(destSettings.getYlevel()))));
                destSettings.setAutoconvert(Boolean.parseBoolean((String) properties.computeIfAbsent("autoconvert", a -> String.valueOf(destSettings.getAutoconvert()))));

                destSFeatures.setSend(Boolean.parseBoolean((String) properties.computeIfAbsent("send", a -> String.valueOf(destSFeatures.getSend()))));
                destSFeatures.setTrack(Boolean.parseBoolean((String) properties.computeIfAbsent("track", a -> String.valueOf(destSFeatures.getTrack()))));

                destSParticles.setLine(Boolean.parseBoolean((String) properties.computeIfAbsent("line-particles", a -> String.valueOf(destSParticles.getLine()))));
                destSParticles.setDest(Boolean.parseBoolean((String) properties.computeIfAbsent("dest-particles", a -> String.valueOf(destSParticles.getDest()))));
                destSParticles.setTracking(Boolean.parseBoolean((String) properties.computeIfAbsent("tracking-particles", a -> String.valueOf(destSParticles.getTracking()))));
                destSParticles.setTrackingColor(CUtl.color.updateOld((String) properties.computeIfAbsent("tracking-particle-color", a -> ""), destSParticles.getTrackingColor()));
            }
            // only in 1.1
            if (version == 1.1f)
                hudModules.setTracking(Boolean.parseBoolean((String) properties.computeIfAbsent("compass", a -> String.valueOf(hudModules.getTracking()))));

            if (version <= 1.5f) {
                // save default when moving to the new defaults system
                DefaultPData.saveDefaults();
            }
        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR LOADING CONFIG - PLEASE REPORT WITH THE ERROR LOG");
            e.printStackTrace();
        }
    }
    public static final Lang LANG = new Lang("config.");
    public static void save() {
        try (var file = Files.newBufferedWriter(configFile().toPath(), StandardCharsets.UTF_8)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            file.write("# "+LANG.ui("main")+"\n");
            file.write("version="+defaults.version);

            file.write("\n\n# "+LANG.ui("max")+
                    "\n# "+LANG.desc("max.xyz"));
            // MAX XYZ
            file.write("\nmax-xz=" + MAXxz);
            file.write("\nmax-y=" + MAXy);
            // MAX SAVED AND LAST DEATH
            file.write("\n# "+LANG.desc("max.feature"));
            file.write("\ndestination-max=" + DestMAX);
            file.write("\nlastdeath-max=" + LastDeathMAX);
            file.write("\nmax-color-presets=" + MAXColorPresets);
            // ONLINE MODE
            file.write("\n\n# "+LANG.desc("online_mode"));
            file.write("\nonline-mode=" + online);
            // GLOBAL DEST
            file.write("\n\n# "+LANG.desc("global_dest")+
                    "\n#  "+LANG.desc("global_dest.2"));
            file.write("\nglobal-destinations=" + globalDESTs);
            // DEST SAVING
            file.write("\n\n# "+LANG.desc("dest_saving"));
            file.write("\ndestination-saving=" + DestSaving);
            // LASTDEATH SAVING
            file.write("\n\n# "+LANG.desc("lastdeath_saving"));
            file.write("\nlastdeath-saving=" + LastDeathSaving);
            // HUD EDITING
            file.write("\n\n# "+LANG.desc("hud_editing"));
            file.write("\nhud-editing=" + HUDEditing);

            // SOCIAL
            file.write("\n\n# "+LANG.ui("social")+
                    "\n# "+LANG.desc("social_commands"));
            // SOCIAL COMMANDS
            file.write("\nsocial-commands=" + social);
            // SOCIAL COOLDOWN
            file.write("\n# "+LANG.desc("social_cooldown"));
            file.write("\nsocial-cooldown=" + socialCooldown);

            // LOOP
            file.write("\n\n# "+LANG.ui("loop")+
                    "\n# "+LANG.desc("hud_loop"));
            file.write("\nhud-loop=" + HUDLoop);
            // PARTICLE LOOP
            file.write("\n# "+LANG.desc("particle_loop"));
            file.write("\nparticle-loop=" + ParticleLoop);

        } catch (Exception e) {
            DirectionHUD.LOGGER.info("ERROR WRITING CONFIG - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }
}
