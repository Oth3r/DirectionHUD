
package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.module.Module;
import one.oth3r.directionhud.common.template.CustomFile;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Dest;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class PData extends BasePData implements CustomFile<PData> {

    @SerializedName("name")
    protected String name;
    @SerializedName("inbox")
    protected ArrayList<HashMap<String,String>> inbox = new ArrayList<>();
    @SerializedName("social_cooldown")
    protected Integer socialCooldown;

    protected transient Player player;

    public PData(Player player) {
        this.name = player.getName();
        this.hud = PlayerData.getDefaults().getHud();
        this.destination = PlayerData.getDefaults().getDEST();
        this.colorPresets = PlayerData.getDefaults().getColorPresets();
        setPlayer(player);
    }

    public PData(Player player, PData pData) {
        copyFileData(pData);
        setPlayer(player);
    }

    public PData(PData pData) {
        copyFileData(pData);
    }

    public String getName() {
        return name;
    }

    @Override
    public void setColorPresets(ArrayList<Helper.ColorPreset> colorPresets) {
        super.setColorPresets(colorPresets);
        // queue saving
        queueSave();
    }

    /**
     * sets pData to be saved and sends the updated pData to the player client if needed
     */
    public void queueSave() {
        if (player == null) return;

        // add to saving queue
        PlayerData.Queue.addSavePlayer(player);

        // update the cached variables
        CachedPData cachedPData = PlayerData.getPCache(player);
        inbox = cachedPData.getInbox();
        socialCooldown = cachedPData.getSocialCooldown();
        cachedPData.update(this);

        // send packets
        player.sendPDataPackets();
    }

    public void setPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.hud.setPlayer(player);
        this.destination.setPlayer(player);
    }

    public Player getPlayer() {
        return player;
    }

    // CACHE ITEMS (write in cache)

    public ArrayList<HashMap<String,String>> getInbox() {
        return inbox;
    }

    public Integer getSocialCooldown() {
        return socialCooldown;
    }

    @Override
    public void reset() {
        PData pData = new PData(this.player);
        copyFileData(pData);
    }

    /**
     * @return the class of the File
     */
    @Override
    public @NotNull Class<PData> getFileClass() {
        return PData.class;
    }

    /**
     * loads the data from the file object into the current object - DEEP COPY
     *
     * @param newFile the file to take the properties from
     */
    @Override
    public void copyFileData(PData newFile) {
        super.copyBaseFileData(newFile);
        // pData fields
        this.name = newFile.name;
        this.inbox = newFile.inbox.stream()
                .map(HashMap::new).collect(Collectors.toCollection(ArrayList::new));
        this.socialCooldown = newFile.socialCooldown;

        // update the player if the new file has a valid player
        if (newFile.player != null) this.player = newFile.player;
        // set the player no matter what, because the other objects have lost their player attribute
        setPlayer(this.player);
    }

    @Override
    public String getFileName() {
        if (FileData.getConfig().getOnline()) return player.getUUID()+".json";
        else return player.getName()+".json";
    }

    @Override
    public String getDirectory() {
        return DirectionHUD.DATA_DIR+"playerdata/";
    }

    @Override
    public void updateFromReader(BufferedReader reader) {
        // try to read the json
        PData file;
        JsonElement json = JsonParser.parseReader(reader);
        try {
            file = Helper.getGson().fromJson(json, PData.class);
        } catch (Exception e) {
            throw new NullPointerException();
        }

        // if the file couldn't be parsed, (null) try using the custom update method using the JsonElement on the current file
        // if not use the new file object that is loaded with the file data, and call update using that
        if (file == null) {
            this.update(json);
        } else {
            file.player = this.player;
            // update the instance
            file.update(json);
            // load the file to the current object
            copyFileData(file);
        }
    }

    /**
     * updates the file based on the version number of the current instance
     */
    @Override
    public void update(JsonElement json) {
        // todo test pre 2.0 updater
        //  1.0 - DONE
        //  1.1+ STILL TESTING
        if (this.version == null || this.version < 2) {
            // unsupported playerdata version
            DirectionHUD.LOGGER.info("Pre 2.0 PlayerData version detected! Trying to load from legacy...");
            new legacy(getFile()).update();
            // restart the file loading as the file was written to
            load();
            // end updater as the method will be called again
            return;
        }
        // update
        baseUpdater(json);
        // make sure the order and modules are valid (NOT FACTORY DEFAULT)
        Hud.modules.fixOrder(hud.getModules());
    }

    /**
     * the legacy updater for directionhud
     */
    public static class legacy {
        private final File file;

        public legacy(File file) {
            this.file = file;
        }

        /**
         * runs the playerdata legacy updater
         */
        public void update() {
            mapUpdate(fileToMap());
        }

        /**
         * converts the playerdata file to a Map
         */
        private Map<String, Object> fileToMap() {
            try (BufferedReader reader = Files.newBufferedReader(this.file.toPath())) {
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
        private void mapToFile(Map<String, Object> map) {
            try (BufferedWriter writer = Files.newBufferedWriter(this.file.toPath())) {
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
        private Map<String, Object> saveLoad(Map<String, Object> map) {
            // saves the current map and sends back an updated map
            mapToFile(map);
            return fileToMap();
        }

        /**
         * updates the player data Map to be able to be read by the new Object system
         */
        @SuppressWarnings("unchecked")
        private void mapUpdate(Map<String,Object> base) {
            // make sure this updates to the right json - it seems to be malformed
            PDDestination DESTINATION = PlayerData.getDefaults().getDEST();
            PDDestination.Settings DESTSETTINGS = DESTINATION.getSetting();
            PDHud HUD_defaults = PlayerData.getDefaults().getHud();
            PDHud.Settings HUDSETTINGS = HUD_defaults.getSetting();
            if (base.get("version").equals(1.0)) {
                base.put("version",1.1);
                Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
                dSet.put("lastdeath", DESTINATION.getSetting().getFeatures().getLastdeath());
                dest.put("setting",dSet);
                base.put("destination",dest);
                // reload the file after updating a version
                base = saveLoad(base);
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
                base = saveLoad(base);
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
                base.put("destination",dest);
                // reload the file after updating a version
                base = saveLoad(base);
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
                    Loc loc = new Loc(true, xyz);
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
                    Loc loc = new Loc(Helper.Num.toInt(coordS[0]), Helper.Num.toInt(coordS[1]), Helper.Num.toInt(coordS[2]),Utl.dim.updateLegacy(split[2]));
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
                            Utl.dim.updateLegacy(split[0])).toString());
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
                base = saveLoad(base);
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
                hudSettingModule.put("tracking_target", "player");
                hudSetting.put("module",hudSettingModule);
                hud.put("order",
                        Helper.Enums.toEnumList((a,b) -> Module.fromString(b),
                                new ArrayList<>(List.of(((String) hud.get("order")).split(" "))), Module.class));
                hud.put("setting",hudSetting);
                base.put("destination",dest);
                base.put("hud",hud);
                base.put("color_presets", PlayerData.getDefaults().getColorPresets());
                // reload the file after updating a version
                base = saveLoad(base);
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
                base = saveLoad(base);
            }
            if (base.get("version").equals(1.6)) {
                base.put("version",1.7);
                Map<String,Object> hud = (Map<String, Object>) base.get("hud");
                // new hud module settings
                Map<String,Object> hudSetting = (Map<String, Object>) hud.get("setting");
                Map<String,Object> hudModuleSetting = (Map<String, Object>) hudSetting.get("module");
                hudModuleSetting.put("tracking_hybrid", true);
                hudModuleSetting.put("tracking_type", "simple");
                hudModuleSetting.put("speed_pattern", "0.00");
                hudModuleSetting.put("speed_3d", true);
                hudModuleSetting.put("angle_display", "both");
                hudSetting.put("module",hudModuleSetting);
                hud.put("setting",hudSetting);
                // new hud modules
                Map<String,Object> hudModule = (Map<String, Object>) hud.get("module");
                hudModule.put("speed", HUD_defaults.getModule(Module.SPEED).isEnabled());
                hudModule.put("angle", HUD_defaults.getModule(Module.ANGLE).isEnabled());
                hud.put("module",hudModule);
                base.put("hud",hud);
                // new preset system
                base.put("color_presets", DHud.preset.custom.updateTo1_7((ArrayList<String>) base.get("color_presets")));
                // reload the file after updating a version
                base = saveLoad(base);
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
                hud.put("order", Helper.Enums.toEnumList((ArrayList<String>) hud.get("order"), Module.class));
                base.put("hud",hud);
                // remove the extra module tab found in the base from a broken past update
                base.put("module",null);
                // reload the file after updating a version
                base = saveLoad(base);
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
                hud.put("primary",new PDHud.Color(primary[0],primary[1].equals("true"),primary[2].equals("true"),primary[3].equals("true")));
                hud.put("secondary",new PDHud.Color(secondary[0],secondary[1].equals("true"),secondary[2].equals("true"),secondary[3].equals("true")));

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
                    if (loc.getDimension() != null) loc.setDimension(Utl.dim.updateLegacy(loc.getDimension()));
                    return loc;
                }).collect(Collectors.toCollection(ArrayList::new));
                dest.put("lastdeath", newLastdeath);

                // update saved
                ArrayList<ArrayList<String>> saved = (ArrayList<ArrayList<String>>) dest.get("saved");
                ArrayList<Dest> newSaved = saved.stream().map(entry -> {
                    // ENTRY: NAME, LOC, COLOR
                    Loc loc = new Loc(true, entry.get(1));
                    Dest destLoc = new Dest(loc,entry.get(0),entry.get(2));
                    // update dimension
                    destLoc.setDimension(Utl.dim.updateLegacy(loc.getDimension()));
                    return destLoc;
                }).collect(Collectors.toCollection(ArrayList::new));
                dest.put("saved",newSaved);

                // update tracked target
                dest.put("tracking", dest.get("track"));
                dest.put("track", null);
            }
            // save at the end
            mapToFile(base);
        }
    }
}
