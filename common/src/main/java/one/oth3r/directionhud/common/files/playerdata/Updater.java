package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHud;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class Updater {

    /**
     * runs the updater, only for v2.0+
     */
    public static void run(Player player) {
        PData pData = player.getPData();
        double version = pData.getVersion();
        pData.setPlayer(player);
    }

    public static class legacy {
        public static void update(Player player) {
            mapUpdate(player,fileToMap(player));
        }
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
        private static void mapToFile(Player player, Map<String, Object> map) {
            try (BufferedWriter writer = Files.newBufferedWriter(PData.getPlayerFile(player).toPath())) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                writer.write(gson.toJson(map));
            } catch (Exception e) {
                DirectionHUD.LOGGER.info("ERROR WRITING PLAYER DATA - PLEASE REPORT WITH THE ERROR LOG");
                DirectionHUD.LOGGER.info(e.getMessage());
            }
        }
        private static Map<String, Object> saveLoad(Player player, Map<String, Object> map) {
            // saves the current map and sends back an updated map
            mapToFile(player,map);
            return fileToMap(player);
        }
        @SuppressWarnings("unchecked")
        private static void mapUpdate(Player player, Map<String,Object> base) {
            PDDestination DESTINATION = PlayerData.DEFAULTS.getDEST();
            PDDestination.Settings DESTSETTINGS = DESTINATION.getSetting();
            PDHud HUD_defaults = PlayerData.DEFAULTS.getHud();
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
                base.put("color_presets",config.colorPresets);
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
                dest.put("dest",new Loc(true,(String)dest.get("dest")));
                // update lastdeath
                ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
                ArrayList<Loc> newLastdeath = lastdeath.stream().map(string -> new Loc(true, string))
                        .collect(Collectors.toCollection(ArrayList::new));
                dest.put("lastdeath", newLastdeath);
                // update saved
                ArrayList<ArrayList<String>> saved = (ArrayList<ArrayList<String>>) dest.get("saved");
                ArrayList<Loc> newSaved = saved.stream().map(entry -> {
                    Loc loc = new Loc(true,entry.get(1));
                    loc.setName(entry.get(0));
                    loc.setColor(entry.get(2));
                    return loc;
                }).collect(Collectors.toCollection(ArrayList::new));
                dest.put("saved",newSaved);
            }
            // save at the end
            mapToFile(player,base);
            // load the prepared PlayerData
            PData.loadPlayer(player,true);
        }
    }

}