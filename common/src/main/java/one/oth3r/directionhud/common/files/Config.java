package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.playerdata.DefaultPData;
import one.oth3r.directionhud.common.files.playerdata.PDDestination;
import one.oth3r.directionhud.common.files.playerdata.PDHud;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.module.*;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.template.CustomFile;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Config implements CustomFile<Config> {
    private transient boolean legacyCheck = false;

    @SerializedName("version")
    private Double version = 1.6;
    @SerializedName("lang")
    private String lang = "en_us";
    @SerializedName("lang-options") @SuppressWarnings("unused")
    private final String[] lang_options = {"en_us","ru_ru","zh_cn","zh_hk"};
    @SerializedName("online-mode")
    private Boolean online = true;
    @SerializedName("location")
    private Location location = new Location();
    @SerializedName("hud")
    private Hud hud = new Hud();
    @SerializedName("destination")
    private Destination destination = new Destination();
    @SerializedName("social")
    private Social social = new Social();
    @SerializedName("max-color-presets")
    private Integer maxColorPresets = 14;

    public Config() {}

    public Config(Config config) {
        this.version = config.version;
        this.lang = config.lang;
        this.location = new Location(config.location);
        this.online = config.online;
        this.hud = new Hud(config.hud);
        this.destination = new Destination(config.destination);
        this.social = new Social(config.social);
        this.maxColorPresets = config.maxColorPresets;
    }

    public Social getSocial() {
        return social;
    }

    public void setSocial(Social social) {
        this.social = social;
    }

    public Double getVersion() {
        return version;
    }

    public void setVersion(Double version) {
        this.version = version;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public Hud getHud() {
        return hud;
    }

    public void setHud(Hud hud) {
        this.hud = hud;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Integer getMaxColorPresets() {
        return maxColorPresets;
    }

    public void setMaxColorPresets(Integer maxColorPresets) {
        this.maxColorPresets = maxColorPresets;
    }

    public static class Social {
        private Boolean enabled = true;
        private Integer cooldown = 10;

        public Social() {}

        public Social(Social social) {
            this.enabled = social.enabled;
            this.cooldown = social.cooldown;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getCooldown() {
            return cooldown;
        }

        public void setCooldown(Integer cooldown) {
            this.cooldown = cooldown;
        }
    }

    public static class Hud {
        @SerializedName("editing")
        private Boolean editing = true;
        @SerializedName("loop-ticks")
        private Integer loop = 1;

        public Hud() {}

        public Hud(Hud hud) {
            this.editing = hud.editing;
            this.loop = hud.loop;
        }

        public Boolean getEditing() {
            return editing;
        }

        public void setEditing(Boolean editing) {
            this.editing = editing;
        }

        public Integer getLoop() {
            return loop;
        }

        public void setLoop(Integer loop) {
            this.loop = loop;
        }
    }

    public static class Destination {
        @SerializedName("saving")
        private Boolean saving = true;
        @SerializedName("max-saved")
        private Integer maxSaved = 50;
        @SerializedName("global")
        private Boolean global = false;
        @SerializedName("lastdeath")
        private LastDeath lastDeath = new LastDeath();
        @SerializedName("loop-ticks")
        private Integer loop = 20;

        public Destination() {}

        public Destination(Destination destination) {
            this.saving = destination.saving;
            this.maxSaved = destination.maxSaved;
            this.global = destination.global;
            this.lastDeath = new LastDeath(destination.lastDeath);
            this.loop = destination.loop;
        }

        public Boolean getSaving() {
            return saving;
        }

        public void setSaving(Boolean saving) {
            this.saving = saving;
        }

        public Integer getMaxSaved() {
            return maxSaved;
        }

        public void setMaxSaved(Integer maxSaved) {
            this.maxSaved = maxSaved;
        }

        public Boolean getGlobal() {
            return global;
        }

        public void setGlobal(Boolean global) {
            this.global = global;
        }

        public LastDeath getLastDeath() {
            return lastDeath;
        }

        public void setLastDeath(LastDeath lastDeath) {
            this.lastDeath = lastDeath;
        }

        public Integer getLoop() {
            return loop;
        }

        public void setLoop(Integer loop) {
            this.loop = loop;
        }

        public static class LastDeath {
            @SerializedName("saving")
            private Boolean saving = true;
            @SerializedName("max-deaths")
            private Integer maxDeaths = 4;

            public LastDeath() {
            }

            public LastDeath(LastDeath lastDeath) {
                this.saving = lastDeath.saving;
                this.maxDeaths = lastDeath.maxDeaths;
            }

            public Boolean getSaving() {
                return saving;
            }

            public void setSaving(Boolean saving) {
                this.saving = saving;
            }

            public Integer getMaxDeaths() {
                return maxDeaths;
            }

            public void setMaxDeaths(Integer maxDeaths) {
                this.maxDeaths = maxDeaths;
            }
        }
    }

    public static class Location {
        @SerializedName("max-y")
        private int maxY = 512;
        @SerializedName("max-xz")
        private int maxXZ = 30000000;

        public Location(Location location) {
            this.maxY = location.maxY;
            this.maxXZ = location.maxXZ;
        }

        public Location() {
        }

        public int getMaxY() {
            return maxY;
        }

        public void setMaxY(int maxY) {
            this.maxY = maxY;
        }

        public int getMaxXZ() {
            return maxXZ;
        }

        public void setMaxXZ(int maxXZ) {
            this.maxXZ = maxXZ;
        }
    }

    @Override
    public void fileNotExist() {
        Legacy legacy = new Legacy();
        // if the check for the legacy file hasn't happened yet & the legacy file exists, update it to the new version
        if (!legacyCheck && legacy.getLegacyFile().exists()) {
            DirectionHUD.LOGGER.info("Updating DirectionHUD.properties to directionhud/config.json");
            // run the legacy updater
            legacy.run();
            // copy over the config that the legacy updater created
            copyFileData(legacy.getConfig());
        }
        // enable legacy check either way
        legacyCheck = true;

        // continue with base logic
        CustomFile.super.fileNotExist();
    }

    @Override
    public void reset() {
        // reset by copying a new blank config
        copyFileData(new Config());
    }

    /**
     * @return the class of the File
     */
    @Override
    public @NotNull Class<Config> getFileClass() {
        return Config.class;
    }

    /**
     * loads the data from the file object into the current object - DEEP COPY
     *
     * @param newFile the file to take the properties from
     */
    @Override
    public void copyFileData(Config newFile) {
        this.version = newFile.version;
        this.lang = newFile.lang;
        this.online = newFile.online;
        this.location = new Location(newFile.location);
        this.hud = new Hud(newFile.hud);
        this.destination = new Destination(newFile.destination);
        this.social = new Social(newFile.social);
        this.maxColorPresets = newFile.maxColorPresets;
    }

    /**
     * updates the file based on the version number of the current instance
     *
     * @param json
     */
    @Override
    public void update(JsonElement json) {

    }

    /**
     * gets the file name - including the extension
     *
     * @return ex. custom-file.json
     */
    @Override
    public String getFileName() {
        return "config.json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.CONFIG_DIR;
    }

    public static class Legacy {
        Config config = new Config();

        public Config getConfig() {
            return config;
        }

        public Legacy() {}

        /**
         * gets the legacy file, from the old directory for fabric, and the same one for spigot
         */
        public File getLegacyFile() {
            // spigot file
            if (!DirectionHUD.isMod) return new File(DirectionHUD.CONFIG_DIR+"DirectionHUD.properties");
            // fabric file, strip the new directory
            return new File(DirectionHUD.CONFIG_DIR.substring(0,DirectionHUD.CONFIG_DIR.length()-13)+"DirectionHUD.properties");
        }

        /**
         * updates the old DirectionHUD.properties to config.json
         */
        public void run() {
            // shouldn't happen, only call if the file exists
            File file = getLegacyFile();
            if (!file.exists()) return;

            try (FileInputStream fileStream = new FileInputStream(file)) {
                Properties properties = new Properties();
                properties.load(fileStream);
                String version = (String) properties.computeIfAbsent("version", a -> String.valueOf(1.5f));
                if (version.contains("v")) version = version.substring(1);

                // load the version
                loadVersion(properties,Float.parseFloat(version));

            } catch (Exception e) {
                DirectionHUD.LOGGER.info("ERROR LOADING LEGACY CONFIG: "+e.getMessage());
                e.printStackTrace();
            }

            // delete the old file
            try {
                Files.delete(file.toPath());
                DirectionHUD.LOGGER.info("Deleted " + file.getName());
            } catch (Exception e) {
                DirectionHUD.LOGGER.info("Failed to delete the old DirectionHUD config.");
            }
        }

        public void loadVersion(Properties properties, float version) {
            //todo test
            // 1.1 - done
            Config config = FileData.getConfig();

            DefaultPData DEFAULTS = PlayerData.getDefaults();
            PDDestination.Settings destSettings = DEFAULTS.getDEST().getSetting();
            PDDestination.Settings.Particles destSParticles = destSettings.getParticles();
            PDDestination.Settings.Features destSFeatures = destSettings.getFeatures();
            PDHud hud = DEFAULTS.getHud();
            PDHud.Settings hudSettings = hud.getSetting();
            PDHud.Settings.Bossbar hudSBossbar = hudSettings.getBossbar();

            try {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                // json maps
                Type arrayListMap = new TypeToken<ArrayList<String>>() {}.getType();
                // CONFIG
                config.getLocation().setMaxXZ(Integer.parseInt((String) properties.computeIfAbsent("max-xz", a -> String.valueOf(config.getLocation().getMaxXZ()))));
                config.getLocation().setMaxY(Integer.parseInt((String) properties.computeIfAbsent("max-y", a -> String.valueOf(config.getLocation().getMaxY()))));
                config.getDestination().setGlobal(Boolean.parseBoolean((String) properties.computeIfAbsent("global-destinations", a -> String.valueOf(config.getDestination().getGlobal()))));
                config.getDestination().setSaving(Boolean.parseBoolean((String) properties.computeIfAbsent("destination-saving", a -> String.valueOf(config.getDestination().getSaving()))));
                config.getDestination().setMaxSaved(Integer.parseInt((String) properties.computeIfAbsent("destination-max", a -> String.valueOf(config.getDestination().getMaxSaved()))));
                config.getDestination().getLastDeath().setSaving(Boolean.parseBoolean((String) properties.computeIfAbsent("lastdeath-saving", a -> String.valueOf(config.getDestination().getLastDeath().getSaving()))));
                config.getDestination().getLastDeath().setMaxDeaths(Integer.parseInt((String) properties.computeIfAbsent("lastdeath-max", a -> String.valueOf(config.getDestination().getLastDeath().getMaxDeaths()))));
                config.getHud().setEditing(Boolean.parseBoolean((String) properties.computeIfAbsent("hud-editing", a -> String.valueOf(config.getHud().getEditing()))));
                config.setOnline(Boolean.parseBoolean((String) properties.computeIfAbsent("online-mode", a -> String.valueOf(config.getOnline()))));
                // SOCIAL
                config.getSocial().setEnabled(Boolean.parseBoolean((String) properties.computeIfAbsent("social-commands", a -> String.valueOf(config.getSocial().getEnabled()))));
                config.getSocial().setCooldown(Integer.parseInt((String) properties.computeIfAbsent("social-cooldown", a -> String.valueOf(config.getSocial().getCooldown()))));
                // LOOPS
                config.getDestination().setLoop(Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("particle-loop", a -> String.valueOf(config.getDestination().getLoop()))))));
                config.getHud().setLoop(Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-loop", a -> String.valueOf(config.getHud().getLoop()))))));
                // COLOR PRESETS
                try {
                    DEFAULTS.setColorPresets(DHud.preset.custom.updateTo2_0(gson.fromJson((String) properties.computeIfAbsent("color-presets", a -> gson.toJson(DEFAULTS.getColorPresets())), arrayListMap)));
                } catch (JsonSyntaxException ignored) {}
                config.setMaxColorPresets(Integer.parseInt((String) properties.computeIfAbsent("max-color-presets", a -> String.valueOf(config.getMaxColorPresets()))));

                // update destinations to new system
                try {
                    Dimension.getDimensionSettings().setDimensions(Dimension.convertLegacyDimensions(gson.fromJson((String) properties.computeIfAbsent("dimensions", a -> ""), arrayListMap)));
                } catch (JsonSyntaxException ignored) {}
                // update ratios to new system
                try {
                    Dimension.getDimensionSettings().setRatios(Dimension.convertLegacyRatios(gson.fromJson((String) properties.computeIfAbsent("dimension-ratios", a -> ""), arrayListMap)));
                } catch (JsonSyntaxException ignored) {}
                DimensionSettings.save();

                // PLAYER DEFAULTS
                // HUD
                // MODULE UPDATER
                String[] order = properties.getProperty("hud.order", "").split(" ");
                ArrayList<BaseModule> hudModules = new ArrayList<>();
                int i = 1;
                for (String module : order) {
                    Module mod = Module.fromString(module);
                    // not valid
                    if (mod.equals(Module.UNKNOWN)) continue;

                    hudModules.add(switch (mod) {
                        case COORDINATES -> new ModuleCoordinates(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.coordinates", String.valueOf(true))),true);
                        case DESTINATION -> new ModuleDestination(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.destination", String.valueOf(true))));
                        case DISTANCE -> new ModuleDistance(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.distance", String.valueOf(true))));
                        case TRACKING -> new ModuleTracking(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.tracking", String.valueOf(false))),
                                Boolean.parseBoolean(properties.getProperty("hud.settings.module.tracking_hybrid", String.valueOf(true))),
                                Helper.Enums.get(properties.getProperty("hud.settings.module.tracking_target", ModuleTracking.Target.player.toString()), ModuleTracking.Target.class),
                                Helper.Enums.get(properties.getProperty("hud.settings.module.tracking_type", ModuleTracking.Type.simple.toString()), ModuleTracking.Type.class), false);
                        case DIRECTION -> new ModuleDirection(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.direction", String.valueOf(true))));
                        case WEATHER -> new ModuleWeather(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.weather", String.valueOf(true))));
                        case TIME -> new ModuleTime(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.time", String.valueOf(true))),
                                Boolean.parseBoolean(properties.getProperty("hud.settings.module.time_24hr", String.valueOf(false))));
                        case ANGLE -> new ModuleAngle(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.angle", String.valueOf(false))),
                                Helper.Enums.get(properties.getProperty("hud.settings.module.angle_display", ModuleAngle.Display.both.toString()), ModuleAngle.Display.class));
                        case SPEED -> new ModuleSpeed(i,
                                Boolean.parseBoolean(properties.getProperty("hud.module.speed", String.valueOf(false))),
                                !Boolean.parseBoolean(properties.getProperty("hud.settings.module.speed_3d", String.valueOf(false))),
                                properties.getProperty("hud.settings.module.speed_pattern", "0.00"));
                        default -> new ModuleCoordinates();
                    });

                    i++;
                }
                // set the updated module list
                hud.setModules(hudModules);

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
                hudSettings.setType(one.oth3r.directionhud.common.hud.Hud.Setting.DisplayType.get((String) properties.computeIfAbsent("hud.settings.type", a -> hudSettings.getType())).toString());
                hudSBossbar.setColor(Helper.Enums.get(properties.computeIfAbsent("hud.settings.bossbar.color", a -> hudSBossbar.getColor()), one.oth3r.directionhud.common.hud.Hud.Setting.BarColor.class).toString());
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
                destSFeatures.setTrackRequestMode(Helper.Enums.get(properties.computeIfAbsent("dest.settings.features.track_request_mode", a -> destSFeatures.getTrackRequestMode()), one.oth3r.directionhud.common.Destination.Setting.TrackingRequestMode.class).toString());
                destSFeatures.setLastdeath(Boolean.parseBoolean((String) properties.computeIfAbsent("dest.settings.features.lastdeath", a -> String.valueOf(destSFeatures.getLastdeath()))));

                // CONFIG UPDATER, if the version is lower than the current, load from the old config
                if (version <= 1.4f) {
                    try {
                        DEFAULTS.setColorPresets(DHud.preset.custom.updateTo2_0(DHud.preset.custom.updateTo1_7(
                                gson.fromJson((String)properties.computeIfAbsent("color-presets",a->gson.toJson(new ArrayList<>())),arrayListMap))));
                    } catch (JsonSyntaxException ignored) {}
                }
                // everything before & 1.3
                if (version <= 1.3f) {
                    config.getDestination().setMaxSaved(Integer.parseInt((String) properties.computeIfAbsent("destination-max-saved", a -> String.valueOf(config.getDestination().getMaxSaved()))));
                    config.getDestination().getLastDeath().setSaving(Boolean.parseBoolean((String) properties.computeIfAbsent("death-saving", a -> String.valueOf(config.getDestination().getLastDeath().getSaving()))));
                    hudSettings.setState(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.enabled", a -> String.valueOf(hudSettings.getState()))));
                    config.getHud().setLoop(Math.min(20, Math.max(1, Integer.parseInt((String) properties.computeIfAbsent("hud-refresh", a -> String.valueOf(config.getHud().getLoop()))))));
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

                    // MODULES
                    List<String> orderList = List.of(((String) properties.computeIfAbsent("order", a -> "")).split(" "));
                    hudModules = new ArrayList<>();
                    i = 1;
                    for (String module : orderList) {
                        Module mod = Module.fromString(module);
                        // not valid
                        if (mod.equals(Module.UNKNOWN)) continue;

                        hudModules.add(switch (mod) {
                            case COORDINATES -> new ModuleCoordinates(i,
                                    Boolean.parseBoolean(properties.getProperty("coordinates", String.valueOf(true))),true);
                            case DESTINATION -> new ModuleDestination(i,
                                    Boolean.parseBoolean(properties.getProperty("destination", String.valueOf(true))));
                            case DISTANCE -> new ModuleDistance(i,
                                    Boolean.parseBoolean(properties.getProperty("distance", String.valueOf(true))));
                            case TRACKING -> new ModuleTracking(i,
                                    Boolean.parseBoolean(properties.getProperty("tracking", String.valueOf(false))),
                                    true, ModuleTracking.Target.player, ModuleTracking.Type.simple, false);
                            case DIRECTION -> new ModuleDirection(i,
                                    Boolean.parseBoolean(properties.getProperty("direction", String.valueOf(true))));
                            case WEATHER -> new ModuleWeather(i,
                                    Boolean.parseBoolean(properties.getProperty("weather", String.valueOf(true))));
                            case TIME -> new ModuleTime(i,
                                    Boolean.parseBoolean(properties.getProperty("time", String.valueOf(true))),
                                    Boolean.parseBoolean(properties.getProperty("time24hr", String.valueOf(false))));
                            case ANGLE -> new ModuleAngle(i,false,ModuleAngle.Display.both);
                            case SPEED -> new ModuleSpeed(i,false,false,"0.00");
                            default -> new ModuleCoordinates();
                        });

                        i++;
                    }
                    // set the updated module list
                    hud.setModules(hudModules);

                    // HUD COLORS

                    hud.getPrimary().setBold(Boolean.parseBoolean((String) properties.computeIfAbsent("primary-bold", a -> String.valueOf(hud.getPrimary().getBold()))));
                    hud.getPrimary().setItalics(Boolean.parseBoolean((String) properties.computeIfAbsent("primary-italics", a -> String.valueOf(hud.getPrimary().getItalics()))));
                    hud.getPrimary().setRainbow(Boolean.parseBoolean((String) properties.computeIfAbsent("primary-rainbow", a -> String.valueOf(hud.getPrimary().getRainbow()))));
                    hud.getSecondary().setBold(Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-bold", a -> String.valueOf(hud.getSecondary().getBold()))));
                    hud.getSecondary().setItalics(Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-italics", a -> String.valueOf(hud.getSecondary().getItalics()))));
                    hud.getSecondary().setRainbow(Boolean.parseBoolean((String) properties.computeIfAbsent("secondary-rainbow", a -> String.valueOf(hud.getSecondary().getRainbow()))));

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
                if (version == 1.1f) {
                    // fix the name of the tracking module enabled setting, it was called compass back then

                    ArrayList<BaseModule> modules = hud.getModules();
                    // only replace an existing module if the tracking module is present
                    if (BaseModule.findInArrayList(modules,Module.TRACKING).isPresent()) {
                        hud.getModule(Module.TRACKING).setState(Boolean.parseBoolean(properties.getProperty("compass",
                                String.valueOf(hud.getModule(Module.TRACKING).isEnabled()))));
                    } else {
                        // the order number is redundant, it will be fixed later
                        modules.add(new ModuleTracking(9,Boolean.parseBoolean(properties.getProperty("compass",
                                String.valueOf(false))),true, ModuleTracking.Target.player, ModuleTracking.Type.simple, false));
                    }
                }


                // moving to the new defaults system & config system
                this.config.copyFileData(config);
                PlayerData.setDefaults(DEFAULTS, true);
            } catch (Exception e) {
                DirectionHUD.LOGGER.info("ERROR LOADING CONFIG - PLEASE REPORT WITH THE ERROR LOG");
                e.printStackTrace();
            }
        }
    }
}
