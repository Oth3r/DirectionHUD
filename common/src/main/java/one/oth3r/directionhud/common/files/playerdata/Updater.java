package one.oth3r.directionhud.common.files.playerdata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHUD;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class Updater {
    public static void run(Player player) {
        double version = player.getPData().getVersion();
        if (version < 2.0) {
            legacy.update(player);
            PlayerData.loadFromFile(player);
        } else {
            update();
            PlayerData.toFile(player);
        }
    }
    private static void update() {

    }

    public static class legacy {
        public static void update(Player player) {
            mapUpdate(player,fileToMap(player));
        }
        private static Map<String, Object> fileToMap(Player player) {
            File file = PlayerData.getFile(player);
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new GsonBuilder().create();
                return gson.fromJson(reader,new TypeToken<Map<String, Object>>() {}.getType());
            } catch (Exception e) {
                DirectionHUD.LOGGER.info("ERROR READING PLAYER DATA (LEGACY) - PLEASE REPORT WITH THE ERROR LOG");
                DirectionHUD.LOGGER.info(e.getMessage());
            }
            return new HashMap<>();
        }
        private static void mapToFile(Player player, Map<String, Object> map) {
            try (FileWriter writer = new FileWriter(PlayerData.getFile(player))){
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
            base.put("name", player.getName());
            if (base.get("version").equals(1.0)) {
                base.put("version",1.1);
                Map<String,Object> dest = (Map<String, Object>) base.get("destination");
                Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
                dSet.put("lastdeath", config.dest.Lastdeath);
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
                particles.put("tracking", config.dest.particles.Tracking);
                particles.put("trackingcolor", config.dest.particles.TrackingColor);
                setting.put("autoconvert", config.dest.AutoConvert);
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
                particles.put("dest_color", CUtl.color.updateOld((String) particles.get("destcolor"), config.dest.particles.DestColor));
                particles.put("line_color",CUtl.color.updateOld((String) particles.get("linecolor"), config.dest.particles.LineColor));
                particles.put("tracking_color",CUtl.color.updateOld((String) particles.get("trackingcolor"), config.dest.particles.TrackingColor));
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
                destFeatures.put("track_request_mode", config.dest.TrackingRequestMode);
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
                primary[0] = CUtl.color.updateOld(primary[0], config.hud.primary.Color);
                hud.put("primary",String.join("-",primary));
                String[] secondary = ((String) hud.get("secondary")).split("-");
                secondary[0] = CUtl.color.updateOld(secondary[0], config.hud.secondary.Color);
                hud.put("secondary",String.join("-",secondary));
                //ADD NEW HUD SETTINGS
                Map<String,Object> hudSetting = (Map<String, Object>) hud.get("setting");
                hudSetting.put("type", config.hud.DisplayType);
//                hudSetting.put("bossbar", PlayerData.defaults.hudBossBar()); //todo find
                Map<String,Object> hudSettingModule = new HashMap<>();
                hudSettingModule.put("time_24hr",hudSetting.get("time24h"));
                hudSetting.put("time24h",null);
                hudSettingModule.put("tracking_target", config.hud.TrackingTarget);
                hudSetting.put("module",hudSettingModule);
                hud.put("order", HUD.modules.fixOrder(Helper.Enums.toEnumList(new ArrayList<>(List.of(((String) hud.get("order")).split(" "))),HUD.Module.class)));
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
                hudModuleSetting.put("tracking_hybrid",config.hud.TrackingHybrid);
                hudModuleSetting.put("tracking_type",config.hud.TrackingType);
                hudModuleSetting.put("speed_pattern", config.hud.SpeedPattern);
                hudModuleSetting.put("speed_3d", config.hud.Speed3D);
                hudModuleSetting.put("angle_display", config.hud.AngleDisplay);
                hudSetting.put("module",hudModuleSetting);
                hud.put("setting",hudSetting);
                // new hud modules
                Map<String,Object> hudModule = (Map<String, Object>) hud.get("module");
                hudModule.put("speed",config.hud.Speed);
                hudModule.put("angle",config.hud.Angle);
                hud.put("module",hudModule);
                base.put("hud",hud);
                // new preset system
                base.put("color_presets", DHUD.preset.custom.updateTo1_7((ArrayList<String>) base.get("color_presets")));
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
                hud.put("order",HUD.modules.fixOrder(Helper.Enums.toEnumList((ArrayList<String>) hud.get("order"),HUD.Module.class)));
                base.put("hud",hud);
                // remove the extra module tab found in the base from a broken past update
                base.put("module",null);
                // reload the file after updating a version
                base = saveLoad(player,base);
            }
            if (base.get("version").equals(1.72)) {
                //todo
            }
            // save at the end
            mapToFile(player,base);
        }
    }

}
