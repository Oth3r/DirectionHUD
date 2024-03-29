package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.DHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.utils.Player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class PlayerData {
    public static Map<Player,Map<String,Object>> playerMap = new HashMap<>();
    public static Map<Player,Map<String,Object>> dataMap = new HashMap<>();
    public static File getFile(Player player) {
        if (config.online) return new File(DirectionHUD.DATA_DIR+"playerdata/" +player.getUUID()+".json");
        else return new File(DirectionHUD.DATA_DIR+"playerdata/"+player.getName()+".json");
    }
    public static Map<String, Object> fileToMap(Player player) {
        File file = getFile(player);
        if (!file.exists()) mapToFile(player,defaults.get(player));
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(reader,new TypeToken<Map<String, Object>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    public static void mapToFile(Player player, Map<String, Object> map) {
        try (FileWriter writer = new FileWriter(getFile(player))){
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer.write(gson.toJson(addExpires(player,map)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> updater(Player player, Map<String,Object> base) {
        base.put("name", player.getName());
        if (base.get("version").equals(1.0)) {
            base.put("version",1.1);
            Map<String,Object> dest = (Map<String, Object>) base.get("destination");
            Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
            dSet.put("lastdeath", config.dest.Lastdeath);
            dest.put("setting",dSet);
            base.put("destination",dest);
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
                dest.put("dest",loc.toArray());
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
                savedN.add(saved.indexOf(s), Arrays.asList(split[0],loc.toArray(),split[3]));
            }
            dest.put("saved",savedN);
            //LASTDEATH FIX
            ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
            for (String s:lastdeath) {
                String[] split = s.split("\\|");
                lastdeath.set(lastdeath.indexOf(s),new Loc(split[1],split[0]).toArray());
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
            particles.put("dest_color",CUtl.color.updateOld((String) particles.get("destcolor"), config.dest.particles.DestColor));
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
            hudSetting.put("bossbar",defaults.hudBossBar());
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
            base.put("color_presets", DHUD.preset.custom.update((ArrayList<String>) base.get("color_presets")));
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
        }
        return base;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> removeUnnecessary(Map<String,Object> map) {
        Map<String,Object> dest = (Map<String, Object>) map.get("destination");
        dest.remove("saved");
        dest.remove("lastdeath");
        map.put("destination",dest);
        map.remove("name");
        map.remove("presets");
        //removes map.presets, map.name, map.destination.saved, map.destination.lastdeath
        return map;
    }
    public static Map<String,Object> addExpires(Player player, Map<String,Object> map) {
        //since the counters are stored in the map to reduce load, when the file gets saved, it updates the file with the current counter times.
        Map<String,Object> cache = playerMap.get(player);
        if (cache == null) return map;
        // add the inbox & trackCooldown to the map before saving
        map.put("inbox",cache.get("inbox"));
        map.put("social_cooldown",cache.get("social_cooldown"));
        return map;
    }
    public static void updatePlayerMap(Player player) {
        playerMap.put(player,removeUnnecessary(fileToMap(player)));
        // send packets to update player settings
        player.sendSettingPackets();
    }
    public static void addPlayer(Player player) {
        Map<String, Object> map = updater(player, fileToMap(player));
        mapToFile(player, map);
        updatePlayerMap(player);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("speed_data", player.getVec());
        hashMap.put("speed", 0.0);
        dataMap.put(player, hashMap);
    }
    public static void removePlayer(Player player) {
        mapToFile(player, fileToMap(player));
        playerMap.remove(player);
        dataMap.remove(player);
    }
    public static String getMsgData(Player player, String key) {
        return String.valueOf(dataMap.get(player).get("msg."+key));
    }
    public static void setMsgData(Player player, String key, String value) {
        dataMap.get(player).put("msg."+key,value);
    }
    public static class defaults {
        public static Map<String,Object> get(Player player) {
            Map<String,Object> map = new HashMap<>();
            //hud
            Map<String,Object> hud = new HashMap<>();
            hud.put("setting", defaults.hudSetting());
            hud.put("module", defaults.hudModule());
            hud.put("order", config.hud.Order);
            hud.put("primary", HUD.color.defaultFormat(1));
            hud.put("secondary", HUD.color.defaultFormat(2));
            //dest
            Map<String,Object> destination = new HashMap<>();
            destination.put("dest", "null");
            destination.put("setting", defaults.destSetting());
            destination.put("saved", new ArrayList<String>());
            destination.put("lastdeath", new ArrayList<String>());
            destination.put("tracking", null);
            //base
            map.put("version", 1.72);
            map.put("name", player.getName());
            map.put("hud", hud);
            map.put("destination", destination);
            map.put("color_presets",config.colorPresets);
            map.put("inbox",new ArrayList<>());
            return map;
        }
        public static Map<String,Object> hudSetting() {
            Map<String,Object> hudSetting = new HashMap<>();
            hudSetting.put("state", config.hud.State);
            hudSetting.put("type", config.hud.DisplayType);
            hudSetting.put("bossbar",hudBossBar());
            hudSetting.put("module",hudSettingModule());
            return hudSetting;
        }
        public static Map<String,Object> hudBossBar() {
            Map<String,Object> hudBossBar = new HashMap<>();
            hudBossBar.put("color", config.hud.BarColor);
            hudBossBar.put("distance", config.hud.BarShowDistance);
            hudBossBar.put("distance_max",(double) config.hud.ShowDistanceMAX);
            return hudBossBar;
        }
        public static Map<String,Object> hudSettingModule() {
            Map<String,Object> module = new HashMap<>();
            module.put("time_24hr", config.hud.Time24HR);
            module.put("tracking_hybrid", config.hud.TrackingHybrid);
            module.put("tracking_target", config.hud.TrackingTarget);
            module.put("tracking_type", config.hud.TrackingType);
            module.put("speed_pattern", config.hud.SpeedPattern);
            module.put("speed_3d", config.hud.Speed3D);
            module.put("angle_display", config.hud.AngleDisplay);
            return module;
        }
        public static Map<String,Object> hudModule() {
            Map<String,Object> hudModule = new HashMap<>();
            hudModule.put("coordinates", config.hud.Coordinates);
            hudModule.put("distance", config.hud.Distance);
            hudModule.put("destination", config.hud.Destination);
            hudModule.put("direction", config.hud.Direction);
            hudModule.put("tracking", config.hud.Tracking);
            hudModule.put("time", config.hud.Time);
            hudModule.put("weather", config.hud.Weather);
            hudModule.put("speed", config.hud.Speed);
            hudModule.put("angle", config.hud.Angle);
            return hudModule;
        }
        public static Map<String,Object> destSetting() {
            Map<String,Object> destSetting = new HashMap<>();
            destSetting.put("autoclear", config.dest.AutoClear);
            destSetting.put("autoclear_rad",(double) config.dest.AutoClearRad);
            destSetting.put("autoconvert", config.dest.AutoConvert);
            destSetting.put("ylevel", config.dest.YLevel);
            destSetting.put("features", destFeatures());
            destSetting.put("particles", destParticles());
            return destSetting;
        }
        public static Map<String,Object> destFeatures() {
            Map<String,Object> destFeatures = new HashMap<>();
            destFeatures.put("send", config.dest.Send);
            destFeatures.put("track", config.dest.Track);
            destFeatures.put("track_request_mode", config.dest.TrackingRequestMode);
            destFeatures.put("lastdeath", config.dest.Lastdeath);
            return destFeatures;
        }
        public static Map<String,Object> destParticles() {
            Map<String,Object> destParticles = new HashMap<>();
            destParticles.put("line", config.dest.particles.Line);
            destParticles.put("line_color", config.dest.particles.LineColor);
            destParticles.put("dest", config.dest.particles.Dest);
            destParticles.put("dest_color", config.dest.particles.DestColor);
            destParticles.put("tracking", config.dest.particles.Tracking);
            destParticles.put("tracking_color", config.dest.particles.TrackingColor);
            return destParticles;
        }
    }
    @SuppressWarnings("unchecked")
    public static class get {
        public static Map<String,Object> fromMap(Player player) {
            if (playerMap.get(player) == null) addPlayer(player);
            return playerMap.get(player);
        }
        public static class hud {
            private static Map<String,Object> map(Player player) {
                return (Map<String, Object>) fromMap(player).get("hud");
            }
            public static boolean module(Player player, HUD.Module module) {
                Map<String,Object> map = (Map<String,Object>) map(player).get("module");
                return (boolean) map.get(module.toString());
            }
            public static ArrayList<HUD.Module> order(Player player) {
                ArrayList<String> modules = (ArrayList<String>) map(player).get("order");
                ArrayList<HUD.Module> types = new ArrayList<>();
                for (String m:modules) types.add(HUD.Module.get(m));
                return types;
            }
            public static String color(Player player, int typ) {
                return (String) map(player).get(typ==1?"primary":"secondary");
            }
            public static Object setting(Player player, HUD.Setting type) {
                Map<String,Object> settings = (Map<String,Object>) hud.map(player).get("setting");
                String string = type.toString();
                if (string.contains(".")) {
                    String base = string.substring(0,string.indexOf('.'));
                    Map<String,Object> bar = (Map<String,Object>) settings.get(base);
                    return bar.get(string.substring(string.indexOf('.')+1));
                }
                return settings.get(string);
            }
        }
        public static class dest {
            private static Map<String,Object> map(Player player, boolean map) {
                if (map) return (Map<String,Object>) fromMap(player).get("destination");
                return (Map<String, Object>) fileToMap(player).get("destination");
            }
            public static ArrayList<String> lastdeaths(Player player) {
                return (ArrayList<String>) map(player,false).get("lastdeath");
            }
            public static Loc loc(Player player) {
                return new Loc((String) map(player,true).get("dest"));
            }
            public static String tracking(Player player) {
                return (String) map(player,true).get("tracking");
            }
            public static List<List<String>> saved(Player player) {
                return (List<List<String>>) map(player,false).get("saved");
            }
            public static Object setting(Player player, Destination.Setting setting) {
                Map<String,Object> settings = (Map<String,Object>) dest.map(player,true).get("setting");
                String string = setting.toString();
                // if nested
                if (string.contains(".")) {
                    String base = string.substring(0,string.indexOf('.'));
                    Map<String,Object> bar = (Map<String,Object>) settings.get(base);
                    return bar.get(string.substring(string.indexOf('.')+1));
                }
                return settings.get(string);
            }
        }
        public static ArrayList<String> colorPresets(Player player) {
            return (ArrayList<String>) fileToMap(player).get("color_presets");
        }
        public static Double socialCooldown(Player player) {
            return (Double) fromMap(player).get("social_cooldown");
        }
        public static ArrayList<HashMap<String, Object>> inbox(Player player) {
            Type inboxType = new TypeToken<ArrayList<HashMap<String, Object>>>() {}.getType();
            String json = String.valueOf(fromMap(player).get("inbox"));
            return new Gson().fromJson(json,inboxType);
        }
    }
    @SuppressWarnings("unchecked")
    public static class set {
        public static class hud {
            public static void map(Player player, Map<String,Object> hud) {
                Map<String,Object> map = fileToMap(player);
                map.put("hud",hud);
                mapToFile(player,map);
                updatePlayerMap(player);
            }
            public static void order(Player player, ArrayList<HUD.Module> order) {
                Map<String,Object> data = get.hud.map(player);
                data.put("order", order);
                map(player, data);
            }
            public static void color(Player player, int typ, String color) {
                Map<String,Object> data = get.hud.map(player);
                data.put(typ==1?"primary":"secondary", color);
                map(player, data);
            }
            public static void setting(Player player, HUD.Setting type, Object setting) {
                String string = type.toString();
                Map<String,Object> settings = (Map<String,Object>) get.hud.map(player).get("setting");
                if (string.contains(".")) { // if there's a dot, go deeper in the tree
                    String base = string.substring(0,string.indexOf('.'));
                    Map<String,Object> bar = (Map<String,Object>) settings.get(base);
                    bar.put(string.substring(string.indexOf('.')+1),setting);
                    settings.put(base,bar);
                } else // else put directly
                    settings.put(string,setting);
                // save the data
                Map<String,Object> data = get.hud.map(player);
                data.put("setting", settings);
                hud.map(player, data);
            }
            public static void module(Player player, HUD.Module module, boolean b) {
                Map<String,Object> modules = (Map<String, Object>) get.hud.map(player).get("module");
                modules.put(module.toString(),b);
                moduleMap(player,modules);
            }
            public static void moduleMap(Player player, Map<String,Object> module) {
                // set the modules all at once using a map
                Map<String,Object> data = get.hud.map(player);
                data.put("module", module);
                hud.map(player, data);
            }
        }
        public static class dest {
            public static void map(Player player, Map<String,Object> dest) {
                Map<String,Object> map = fileToMap(player);
                map.put("destination",dest);
                mapToFile(player,map);
                updatePlayerMap(player);
            }
            public static void loc(Player player, Loc loc) {
                Map<String,Object> data = get.dest.map(player,false);
                data.put("dest", loc.toArray());
                map(player, data);
            }
            public static void tracking(Player player, String s) {
                Map<String,Object> data = get.dest.map(player,false);
                data.put("tracking", s);
                map(player, data);
            }
            public static void lastdeaths(Player player, ArrayList<String> lastdeath) {
                Map<String,Object> data = get.dest.map(player,false);
                data.put("lastdeath", lastdeath);
                map(player, data);
            }
            public static void saved(Player player, List<List<String>> saved) {
                Map<String,Object> data = get.dest.map(player,false);
                data.put("saved", saved);
                map(player, data);
            }
            public static void setting(Player player, Destination.Setting type, Object setting) {
                Map<String,Object> settings = (Map<String,Object>) get.dest.map(player,true).get("setting");
                String string = type.toString();
                if (string.contains(".")) {
                    String base = string.substring(0,string.indexOf('.'));
                    Map<String,Object> bar = (Map<String,Object>) settings.get(base);
                    bar.put(string.substring(string.indexOf('.')+1),setting);
                    settings.put(base,bar);
                } else settings.put(string,setting);
                // save the data
                Map<String,Object> data = get.dest.map(player,false);
                data.put("setting", settings);
                dest.map(player, data);
            }
        }
        public static void colorPresets(Player player, ArrayList<String> preset) {
            Map<String,Object> map = fileToMap(player);
            map.put("color_presets", preset);
            mapToFile(player,map);
        }
        public static void socialCooldown(Player player, Double d) {
            Map<String,Object> map = get.fromMap(player);
            map.put("social_cooldown", d);
            playerMap.put(player,map);
        }
        public static void inbox(Player player, ArrayList<HashMap<String, Object>> inbox) {
            Map<String, Object> map = get.fromMap(player);
            map.put("inbox",inbox);
            playerMap.put(player,map);
        }
    }
}