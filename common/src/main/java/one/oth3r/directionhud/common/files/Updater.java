package one.oth3r.directionhud.common.files;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.playerdata.*;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Updater {
    public static class PlayerFile {

        /**
         * runs the playerdata updater from the file reader
         * @param player the player for the file
         * @param reader the file reader
         * @throws NullPointerException if the file is null
         * @throws UnsupportedVersionException if the file's version is unsupported
         */
        public static void run(Player player, BufferedReader reader)
                throws NullPointerException, UnsupportedVersionException {
            // try to read the json
            PData fileData;
            try {
                fileData = Helper.getGson().fromJson(reader, PData.class);
            } catch (JsonSyntaxException | JsonIOException e) {
                throw new NullPointerException();
            }

            // throw null if the fileData is null
            if (fileData == null) throw new NullPointerException();

            // get the file version
            Double version = fileData.getVersion();

            // if the version is unsupported, (<2.0 or no version, throw an error)
            if (version == null || version < 2.0) throw new UnsupportedVersionException();

            // update the base player data using the default player data updater
            PData pData = new PData(player,DefaultPlayerData.update(fileData));

            // update anything else player based here
            pData = update(pData, version);

            // set the PlayerData Arraylist
            PlayerData.setPlayerData(player,pData);
        }

        /**
         * updates the pData file, from the version number provided
         * @return the updated pData
         */
        public static PData update(PData old, double version) {
            PData pData = new PData(old);
            return pData;
        }

        public static class legacy {
            /**
             * runs the playerdata legacy updater
             */
            public static void update(Player player) {
                mapUpdate(player,fileToMap(player));
            }

            /**
             * converts the playerdata file to a Map
             */
            private static Map<String, Object> fileToMap(Player player) {
                try (BufferedReader reader = Files.newBufferedReader(PData.getPlayerFile(player).toPath())) {
                    Gson gson = new GsonBuilder().create();
                    return gson.fromJson(reader,new TypeToken<Map<String, Object>>() {}.getType());
                } catch (Exception e) {
                    DirectionHUD.LOGGER.info("ERROR READING PLAYER DATA (LEGACY) - PLEASE REPORT WITH THE ERROR LOG");
                    DirectionHUD.LOGGER.info(e.getMessage());
                }
                return new HashMap<>();
            }

            /**
             * writes a plyerdata Map to file
             */
            private static void mapToFile(Player player, Map<String, Object> map) {
                try (BufferedWriter writer = Files.newBufferedWriter(PData.getPlayerFile(player).toPath())) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    writer.write(gson.toJson(map));
                } catch (Exception e) {
                    DirectionHUD.LOGGER.info("ERROR WRITING PLAYER DATA - PLEASE REPORT WITH THE ERROR LOG");
                    DirectionHUD.LOGGER.info(e.getMessage());
                }
            }

            /**
             * saves the Map to file and loads it again, resetting custom types
             */
            private static Map<String, Object> saveLoad(Player player, Map<String, Object> map) {
                // saves the current map and sends back an updated map
                mapToFile(player,map);
                return fileToMap(player);
            }

            /**
             * updates the player data Map to be able to be read by the new Object system
             */
            @SuppressWarnings("unchecked")
            private static void mapUpdate(Player player, Map<String,Object> base) {
                PDDestination DESTINATION = PlayerData.getDefaults().getDEST();
                PDDestination.Settings DESTSETTINGS = DESTINATION.getSetting();
                PDHud HUD_defaults = PlayerData.getDefaults().getHud();
                PDHud.Settings HUDSETTINGS = HUD_defaults.getSetting();
                base.put("name", player.getName());
                if (base.get("version").equals(1.0)) {
                    base.put("version",1.1);
                    Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                    Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
                    dSet.put("lastdeath", DESTINATION.getLastdeath());
                    dest.put("setting",dSet);
                    base.put("destination",dest);
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.1)) {
                    base.put("version",1.2);
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                    String death = (String) dest.get("lastdeath");
                    String[] deaths = death.split(" ");
                    ArrayList<String> newDeaths = new ArrayList<>();
                    for (int i = 0;i<deaths.length;i++) {
                        if (deaths[i].equals("f")) continue;
                        String xyz = deaths[i].replace("_"," ");
                        if (i == 0) newDeaths.add("minecraft.overworld|"+xyz);
                        if (i == 1) newDeaths.add("minecraft.the_nether|"+xyz);
                        if (i == 2) newDeaths.add("minecraft.the_end|"+xyz);
                    }
                    String pri = (String) hud.get("primary");
                    String sec = (String) hud.get("secondary");
                    String[] priS = pri.split("-");
                    String[] secS = sec.split("-");
                    if (priS[0].equals("rainbow")) pri = "white-"+priS[1]+"-"+priS[2]+"-true";
                    else pri = pri+"-false";
                    if (secS[0].equals("rainbow")) sec = "white-"+secS[1]+"-"+secS[2]+"-true";
                    else sec = sec+"-false";
                    hud.put("primary",pri);
                    hud.put("secondary",sec);
                    dest.put("lastdeath",newDeaths);
                    base.put("destination",dest);
                    base.put("hud",hud);
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.2)) {
                    base.put("version",1.3);
                    Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                    dest.computeIfAbsent("saved", k -> new ArrayList<String>());
                    if (!((ArrayList<String>) dest.get("saved")).isEmpty()) {
                        ArrayList<String> saved = (ArrayList<String>) dest.get("saved");
                        for (String s: saved) {
                            String[] split = s.split(" ");
                            switch (split[2]) {
                                case "overworld" -> saved.set(saved.indexOf(s), split[0] + " " + split[1] + " minecraft.overworld " + split[3]);
                                case "the_nether" -> saved.set(saved.indexOf(s), split[0] + " " + split[1] + " minecraft.the_nether " + split[3]);
                                case "the_end" -> saved.set(saved.indexOf(s), split[0] + " " + split[1] + " minecraft.the_end " + split[3]);
                            }
                        }
                        dest.put("saved",saved);
                    }
                    dest.computeIfAbsent("lastdeath", k -> new ArrayList<String>());
                    ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
                    for (String s:new ArrayList<>(lastdeath)) {
                        if (s.startsWith("overworld|") || s.startsWith("the_nether|")) {
                            String prefix = s.startsWith("overworld|") ? "overworld|" : "the_nether|";
                            int count = 0;
                            for (String z : lastdeath) if (z.startsWith("minecraft." + prefix)) count++;
                            if (count == 0) lastdeath.set(lastdeath.indexOf(s),"minecraft."+s);
                            else lastdeath.remove(s);
                        }
                    }
                    dest.put("lastdeath",lastdeath);
                    base.put("dest",dest);
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.3)) {
                    // dest logic, add new tracking
                    base.put("version",1.4);
                    base.remove("lastdeath");
                    Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    Map<String,Object> hudModule = (Map<String, Object>) hud.get("module");
                    hudModule.put("tracking",hudModule.get("compass"));
                    hudModule.put("compass",null);
                    hud.put("module",hudModule);
                    //ORDER FIX
                    String order = (String) hud.get("order");
                    hud.put("order",order.replace("compass","tracking"));
                    base.put("hud",hud);
                    //NEW TRACKING & XYZ FIX
                    dest.put("tracking",null);
                    String xyz = (String) dest.get("xyz");
                    if (xyz.equals("f")) dest.put("dest","null");
                    else if (xyz.split(" ").length == 1) {
                        dest.put("dest", "null");
                        dest.put("tracking",xyz);
                    } else {
                        String[] sp = xyz.split(" ");
                        Loc loc = new Loc(xyz);
                        if (sp[1].equals("n")) loc = new Loc(sp[0]+" "+sp[2]);
                        dest.put("dest",loc.toString());
                    }
                    dest.remove("xyz");
                    //REMOVE SUSPENDED
                    Map<String,Object> suspended = (Map<String, Object>) dest.get("suspended");
                    if (suspended != null) {
                        dest.put("tracking",suspended.get("target"));
                    }
                    dest.remove("suspended");
                    //SAVED FIX
                    ArrayList<String> saved = (ArrayList<String>) dest.get("saved");
                    List<List<String>> savedN = new ArrayList<>();
                    for (String s: saved) {
                        String[] split = s.split(" ");
                        String[] coordS = split[1].split("_");
                        Loc loc = new Loc(Helper.Num.toInt(coordS[0]), Helper.Num.toInt(coordS[1]), Helper.Num.toInt(coordS[2]),split[2]);
                        savedN.add(saved.indexOf(s), Arrays.asList(split[0],loc.toString(),split[3]));
                    }
                    dest.put("saved",savedN);
                    //LASTDEATH FIX
                    ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
                    for (String s:lastdeath) {
                        // xyz|dimension
                        String[] split = s.split("\\|");
                        // x,y,z
                        String[] xyzArray = split[1].split(" ");
                        lastdeath.set(lastdeath.indexOf(s),new Loc(
                                Integer.valueOf(xyzArray[0]),
                                Integer.valueOf(xyzArray[1]),
                                Integer.valueOf(xyzArray[2]),
                                split[0]).toString());
                    }
                    dest.put("lastdeath",lastdeath);
                    //ADD NEW PARTICLES & AUTOCONVERT
                    Map<String,Object> setting = (Map<String, Object>) dest.get("setting");
                    Map<String,Object> particles = (Map<String, Object>) setting.get("particles");
                    particles.put("tracking", DESTSETTINGS.getParticles().getTracking());
                    particles.put("trackingcolor", DESTSETTINGS.getParticles().getTrackingColor());
                    setting.put("autoconvert", DESTSETTINGS.getAutoconvert());
                    setting.put("particles",particles);
                    dest.put("setting",setting);
                    base.put("destination",dest);
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.4)) {
                    base.put("version",1.5);
                    Map<String,Object> temp = new HashMap<>();
                    Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                    //MOVE COUNTDOWN FROM DEST TO TEMP
                    if (dest.get("track") != null) temp.put("track",dest.get("track"));
                    base.put("temp",temp);
                    //UPDATE DEST PARTICLE COLORS TO NEW SYSTEM
                    Map<String,Object> destSetting = (Map<String, Object>) dest.get("setting");
                    Map<String,Object> particles = (Map<String, Object>) destSetting.get("particles");
                    particles.put("dest_color", CUtl.color.updateOld((String) particles.get("destcolor"), DESTSETTINGS.getParticles().getDestColor()));
                    particles.put("line_color",CUtl.color.updateOld((String) particles.get("linecolor"), DESTSETTINGS.getParticles().getLineColor()));
                    particles.put("tracking_color",CUtl.color.updateOld((String) particles.get("trackingcolor"), DESTSETTINGS.getParticles().getTrackingColor()));
                    particles.remove("destcolor");
                    particles.remove("linecolor");
                    particles.remove("trackingcolor");
                    destSetting.put("particles",particles);
                    destSetting.put("autoclear_rad",destSetting.get("autoclearradius"));
                    destSetting.remove("autoclearradius");
                    Map<String,Object> destFeatures = new HashMap<>();
                    destFeatures.put("send",destSetting.get("send"));
                    destFeatures.put("track",destSetting.get("track"));
                    //ADD NEW DEST SETTING
                    destFeatures.put("track_request_mode", DESTSETTINGS.getFeatures().getTrackRequestMode());
                    destFeatures.put("lastdeath",destSetting.get("lastdeath"));
                    destSetting.remove("send");
                    destSetting.remove("track");
                    destSetting.remove("lastdeath");
                    destSetting.put("features",destFeatures);
                    dest.put("setting",destSetting);
                    //UPDATE SAVED DEST COLORS
                    List<List<String>> destSaved = (List<List<String>>) dest.get("saved");
                    for (List<String> list:destSaved) {
                        list.set(2,CUtl.color.updateOld(list.get(2),"#ffffff"));
                        destSaved.set(destSaved.indexOf(list),list);
                    }
                    dest.put("saved",destSaved);
                    //UPDATE HUD COLORS TO NEW SYSTEM
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    String[] primary = ((String) hud.get("primary")).split("-");
                    primary[0] = CUtl.color.updateOld(primary[0], HUD_defaults.getPrimary().getColor());
                    hud.put("primary",String.join("-",primary));
                    String[] secondary = ((String) hud.get("secondary")).split("-");
                    secondary[0] = CUtl.color.updateOld(secondary[0], HUD_defaults.getSecondary().getColor());
                    hud.put("secondary",String.join("-",secondary));
                    //ADD NEW HUD SETTINGS
                    Map<String,Object> hudSetting = (Map<String, Object>) hud.get("setting");
                    hudSetting.put("type", HUDSETTINGS.getType());
                    hudSetting.put("bossbar", HUDSETTINGS.getBossbar());
                    Map<String,Object> hudSettingModule = new HashMap<>();
                    hudSettingModule.put("time_24hr",hudSetting.get("time24h"));
                    hudSetting.put("time24h",null);
                    hudSettingModule.put("tracking_target", HUDSETTINGS.getModule().getTrackingTarget());
                    hudSetting.put("module",hudSettingModule);
                    hud.put("order", Hud.modules.fixOrder(Helper.Enums.toEnumList(new ArrayList<>(List.of(((String) hud.get("order")).split(" "))), Hud.Module.class)));
                    hud.put("setting",hudSetting);
                    base.put("destination",dest);
                    base.put("hud",hud);
                    base.put("color_presets", PlayerData.getDefaults().getColorPresets());
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.5)) {
                    base.put("version",1.6);
                    // new inbox system
                    base.put("temp",null);
                    base.put("inbox",new ArrayList<>());
                    // move hud.enabled to hud.setting.state
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    Map<String,Object> hudSetting = (Map<String, Object>) hud.get("setting");
                    hudSetting.put("state",hud.get("enabled"));
                    hud.put("setting",hudSetting);
                    hud.put("enabled",null);
                    base.put("hud",hud);
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.6)) {
                    base.put("version",1.7);
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    // new hud module settings
                    Map<String,Object> hudSetting = (Map<String, Object>) hud.get("setting");
                    Map<String,Object> hudModuleSetting = (Map<String, Object>) hudSetting.get("module");
                    hudModuleSetting.put("tracking_hybrid", HUDSETTINGS.getModule().getTrackingHybrid());
                    hudModuleSetting.put("tracking_type", HUDSETTINGS.getModule().getTrackingType());
                    hudModuleSetting.put("speed_pattern", HUDSETTINGS.getModule().getSpeedPattern());
                    hudModuleSetting.put("speed_3d", HUDSETTINGS.getModule().getSpeed3d());
                    hudModuleSetting.put("angle_display", HUDSETTINGS.getModule().getAngleDisplay());
                    hudSetting.put("module",hudModuleSetting);
                    hud.put("setting",hudSetting);
                    // new hud modules
                    Map<String,Object> hudModule = (Map<String, Object>) hud.get("module");
                    hudModule.put("speed", HUD_defaults.getModule().getSpeed());
                    hudModule.put("angle", HUD_defaults.getModule().getAngle());
                    hud.put("module",hudModule);
                    base.put("hud",hud);
                    // new preset system
                    base.put("color_presets", DHud.preset.custom.updateTo1_7((ArrayList<String>) base.get("color_presets")));
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.71)) {
                    // revert
                    base.put("version",1.7);
                    base.put("order",null);
                }
                if (base.get("version").equals(1.7)) {
                    // skip to 1.72 as 1.71 was reverted
                    base.put("version",1.72);
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    // update the HUD order, forgot to do that in 1.7
                    hud.put("order", Hud.modules.fixOrder(Helper.Enums.toEnumList((ArrayList<String>) hud.get("order"), Hud.Module.class)));
                    base.put("hud",hud);
                    // remove the extra module tab found in the base from a broken past update
                    base.put("module",null);
                    // reload the file after updating a version
                    base = saveLoad(player,base);
                }
                if (base.get("version").equals(1.72)) {
                    base.put("version",2.0);

                    // update color presets to record system
                    base.put("color_presets", DHud.preset.custom.updateTo2_0((ArrayList<String>) base.get("color_presets")));

                    // clear the inbox
                    base.put("inbox", new ArrayList<>());

                    // update the HUD styles to the new system
                    Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                    // get the old styles, split by the old delimiter
                    String[] primary = ((String) hud.get("primary")).split("-"), secondary = ((String) hud.get("secondary")).split("-");
                    // set to the new PD_hud_color system
                    hud.put("primary",new PDHud.Color(player,primary[0],primary[1].equals("true"),primary[2].equals("true"),primary[3].equals("true")));
                    hud.put("secondary",new PDHud.Color(player,secondary[0],secondary[1].equals("true"),secondary[2].equals("true"),secondary[3].equals("true")));

                    // update all destination Locs
                    Map<String,Object> dest = (Map<String, Object>) base.get("destination");

                    // update dest
                    Loc destination = new Loc(true,(String)dest.get("dest"));
                    // update the dimension
                    if (destination.getDimension() != null) {
                        destination.setDimension(Utl.dim.updateLegacy(destination.getDimension()));
                    }
                    dest.put("dest",destination);

                    // update lastdeath
                    ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
                    ArrayList<Loc> newLastdeath = lastdeath.stream().map(string -> {
                        Loc loc = new Loc(true, string);
                        // update dimension
                        loc.setDimension(Utl.dim.updateLegacy(loc.getDimension()));
                        return loc;
                            }).collect(Collectors.toCollection(ArrayList::new));
                    dest.put("lastdeath", newLastdeath);

                    // update saved
                    ArrayList<ArrayList<String>> saved = (ArrayList<ArrayList<String>>) dest.get("saved");
                    ArrayList<Dest> newSaved = saved.stream().map(entry -> {
                        // ENTRY: NAME, LOC, COLOR
                        Loc loc = new Loc(true,entry.get(1));
                        Dest destLoc = new Dest(loc,entry.get(0),entry.get(2));
                        // update dimension
                        destLoc.setDimension(Utl.dim.updateLegacy(loc.getDimension()));
                        return destLoc;
                    }).collect(Collectors.toCollection(ArrayList::new));
                    dest.put("saved",newSaved);
                }
                // save at the end
                mapToFile(player,base);
                // load the prepared PlayerData
                PData.loadPlayer(player);
            }
        }
    }

    public static class DefaultPlayerData {

        /**
         * runs the default playerdata updater from the file reader and sets the defaults when finished
         * @param reader the file reader
         * @throws NullPointerException if the file is null
         */
        public static void run(BufferedReader reader)
                throws NullPointerException {
            // try to read the json
            DefaultPData fileData;
            try {
                fileData = Helper.getGson().fromJson(reader, DefaultPData.class);
            } catch (Exception e) {
                throw new NullPointerException();
            }

            // throw null if the fileData is null or version is null
            if (fileData == null) throw new NullPointerException();

            // get the file version
            Double version = fileData.getVersion();

            // if there's no version, throw
            if (version == null) throw new NullPointerException();

            // get the default pData
            DefaultPData defaultPData = new DefaultPData(fileData);

            // update the default pData
            defaultPData = update(defaultPData);

            // set the DefaultPData
            PlayerData.setDefaults(defaultPData);
        }

        /**
         * updates the base pData
         */
        public static DefaultPData update(DefaultPData defaultPData) {
            DefaultPData pData = new DefaultPData(defaultPData);
            // verify the order
            pData.getHud().setOrder(Hud.modules.fixOrder(pData.getHud().getOrder()));

            return pData;
        }
    }

    public static class DimSettings {

        /**
         * runs the updater from the file reader and sets the loaded settings when finished, adding the missing dimensions
         * @param reader the file reader
         * @throws NullPointerException if the file is null
         */
        public static void run(BufferedReader reader)
                throws NullPointerException {
            // try to read the json
            DimensionSettings dimensionSettings;
            try {
                dimensionSettings = Helper.getGson().fromJson(reader, DimensionSettings.class);
            } catch (Exception e) {
                throw new NullPointerException();
            }

            // throw null if the fileData is null or version is null
            if (dimensionSettings == null) throw new NullPointerException();

            // get the file version
            Double version = dimensionSettings.getVersion();

            // if there's no version, throw
            if (version == null) throw new NullPointerException();

            // update the default pData
            dimensionSettings = update(dimensionSettings);

            // add missing dimensions
            Utl.dim.addMissing(dimensionSettings);

            // set the DefaultPData
            Dimension.setDimensionSettings(dimensionSettings);
        }

        /**
         * updates the file
         */
        public static DimensionSettings update(DimensionSettings old) {
            DimensionSettings dimensionSettings = new DimensionSettings(old);
            return dimensionSettings;
        }
    }

    public static class ConfigFile {

        /**
         * runs the updater from the file reader and sets the loaded settings when finished, adding the missing dimensions
         * @param reader the file reader
         * @throws NullPointerException if the file is null
         */
        public static void run(BufferedReader reader)
                throws NullPointerException {
            // try to read the json
            Config config;
            try {
                config = Helper.getGson().fromJson(reader, Config.class);
            } catch (Exception e) {
                throw new NullPointerException();
            }

            // throw null if the fileData is null or version is null
            if (config == null) throw new NullPointerException();

            // get the file version
            Double version = config.getVersion();

            // if there's no version, throw
            if (version == null) throw new NullPointerException();

            // update the config
            config = update(config);

            // set the DefaultPData
            Data.setConfig(config);
        }

        /**
         * updates the file
         */
        public static Config update(Config old) {
            Config config = new Config(old);
            return config;
        }

        public static class Legacy {

            /**
             * gets the legacy file, from the old directory for fabric, and the same one for spigot
             */
            public static File getLegacyFile() {
                // spigot file
                if (!DirectionHUD.isMod) return new File(DirectionHUD.CONFIG_DIR+"DirectionHUD.properties");
                // fabric file, strip the new directory
                return new File(DirectionHUD.CONFIG_DIR.substring(0,DirectionHUD.CONFIG_DIR.length()-13)+"DirectionHUD.properties");
            }

            /**
             * updates the old DirectionHUD.properties to config.json
             */
            public static void run() {
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

            public static void loadVersion(Properties properties, float version) {
                Config config = Data.getConfig();

                DefaultPData DEFAULTS = PlayerData.getDefaults();
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
                    config.getLocation().setMaxXZ(Integer.parseInt((String) properties.computeIfAbsent("max-xz", a -> String.valueOf(config.getLocation().getMaxXZ()))));
                    config.getLocation().setMaxY(Integer.parseInt((String) properties.computeIfAbsent("max-y", a -> String.valueOf(config.getLocation().getMaxY()))));
                    config.getDestination().setGloabal(Boolean.parseBoolean((String) properties.computeIfAbsent("global-destinations", a -> String.valueOf(config.getDestination().getGloabal()))));
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
                    hudSModule.setTrackingTarget(Helper.Enums.get(properties.computeIfAbsent("hud.settings.module.tracking_target", a -> hudSModule.getTrackingTarget()), Hud.Setting.ModuleTrackingTarget.class).toString());
                    hudSModule.setTrackingType(Helper.Enums.get(properties.computeIfAbsent("hud.settings.module.tracking_type", a -> hudSModule.getTrackingType()), Hud.Setting.ModuleTrackingType.class).toString());
                    hudSModule.setTime24hr(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.time_24hr", a -> String.valueOf(hudSModule.getTime24hr()))));
                    hudSModule.setSpeed3d(Boolean.parseBoolean((String) properties.computeIfAbsent("hud.settings.module.speed_3d", a -> String.valueOf(hudSModule.getSpeed3d()))));
                    String pattern = (String) properties.computeIfAbsent("hud.settings.module.speed_pattern", a -> hudSModule.getSpeedPattern());
                    try {
                        new DecimalFormat(pattern);
                        hudSModule.setSpeedPattern(pattern);
                    } catch (IllegalArgumentException ignored) {}
                    hudSModule.setAngleDisplay(Helper.Enums.get(properties.computeIfAbsent("hud.settings.module.angle_display", a -> hudSModule.getAngleDisplay()), Hud.Setting.ModuleAngleDisplay.class).toString());

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
                    hudSBossbar.setColor(Helper.Enums.get(properties.computeIfAbsent("hud.settings.bossbar.color", a -> hudSBossbar.getColor()), Hud.Setting.BarColor.class).toString());
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
                    destSFeatures.setTrackRequestMode(Helper.Enums.get(properties.computeIfAbsent("dest.settings.features.track_request_mode", a -> destSFeatures.getTrackRequestMode()), Destination.Setting.TrackingRequestMode.class).toString());
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

                    // moving to the new defaults system & config system
                    Data.setConfig(config);
                    PlayerData.setDefaults(DEFAULTS);
                    DefaultPData.saveDefaults();
                } catch (Exception e) {
                    DirectionHUD.LOGGER.info("ERROR LOADING CONFIG - PLEASE REPORT WITH THE ERROR LOG");
                    e.printStackTrace();
                }
            }
        }
    }
}
