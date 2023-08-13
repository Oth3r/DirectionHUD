package one.oth3r.directionhud.common.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

public class PlayerData {
    public static Map<Player,Map<String,Object>> playerMap = new HashMap<>();
    public static Map<Player,Map<String,String>> oneTimeMap = new HashMap<>();
    public static File getFile(Player player) {
        if (config.online) return new File(DirectionHUD.playerData+player.getUUID()+".json");
        else return new File(DirectionHUD.playerData+player.getName()+".json");
    }
    public static Map<String, Object> fileToMap(Player player) {
        File file = getFile(player);
        if (!file.exists()) mapToFile(player,defaults.get(player));
        try (FileReader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
            return gson.fromJson(reader,new TypeToken<Map<String, Object>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    public static void mapToFile(Player player, Map<String, Object> map) {
        try (FileWriter writer = new FileWriter(getFile(player))){
            Gson gson = new GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create();
            writer.write(gson.toJson(addExpires(player,map)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("unchecked")
    public static Map<String, Object> updater(Player player, Map<String,Object> map) {
        map.put("name", player.getName());
        if (map.get("version").equals(1.0)) {
            map.put("version",1.1);
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
            dSet.put("lastdeath", config.DESTLastdeath);
            dest.put("setting",dSet);
            map.put("destination",dest);
        }
        if (map.get("version").equals(1.1)) {
            map.put("version",1.2);
            Map<String,Object> hud = (Map<String, Object>) map.get("hud");
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
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
            map.put("destination",dest);
            map.put("hud",hud);
        }
        if (map.get("version").equals(1.2)) {
            map.put("version",1.3);
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            dest.computeIfAbsent("saved", k -> new ArrayList<String>());
            if (((ArrayList<String>) dest.get("saved")).size() != 0) {
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
            map.put("dest",dest);
        }
        if (map.get("version").equals(1.3)) {
            // dest logic, add new tracking
            map.put("version",1.4);
            map.remove("lastdeath");
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            Map<String,Object> hud = (Map<String, Object>) map.get("hud");
            Map<String,Object> hudModule = (Map<String, Object>) hud.get("module");
            hudModule.put("tracking",hudModule.get("compass"));
            hudModule.put("compass",null);
            hud.put("module",hudModule);
            //ORDER FIX
            String order = (String) hud.get("order");
            hud.put("order",order.replace("compass","tracking"));
            map.put("hud",hud);
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
                dest.put("dest",loc.getLocC());
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
                Loc loc = new Loc(Utl.tryInt(coordS[0]),Utl.tryInt(coordS[1]),Utl.tryInt(coordS[2]),split[2]);
                savedN.add(saved.indexOf(s), Arrays.asList(split[0],loc.getLocC(),split[3]));
            }
            dest.put("saved",savedN);
            //LASTDEATH FIX
            ArrayList<String> lastdeath = (ArrayList<String>) dest.get("lastdeath");
            for (String s:lastdeath) {
                String[] split = s.split("\\|");
                lastdeath.set(lastdeath.indexOf(s),new Loc(split[1],split[0]).getLocC());
            }
            dest.put("lastdeath",lastdeath);
            //ADD NEW PARTICLES & AUTOCONVERT
            Map<String,Object> setting = (Map<String, Object>) dest.get("setting");
            Map<String,Object> particles = (Map<String, Object>) setting.get("particles");
            particles.put("tracking", config.DESTTrackingParticles);
            particles.put("trackingcolor", config.DESTTrackingParticleColor);
            setting.put("autoconvert", config.DESTAutoConvert);
            setting.put("particles",particles);
            dest.put("setting",setting);
            map.put("destination",dest);
        }
        if (map.get("version").equals(1.4)) {
            map.put("version",1.43);
            Map<String,Object> temp = new HashMap<>();
            Map<String,Object> dest = (Map<String, Object>) map.get("destination");
            //MOVE COUNTDOWN FROM DEST TO TEMP
            if (dest.get("track") != null) temp.put("track",dest.get("track"));
            map.put("temp",temp);
            //UPDATE DEST PARTICLE COLORS TO NEW SYSTEM
            Map<String,Object> destSetting = (Map<String, Object>) dest.get("setting");
            Map<String,Object> particles = (Map<String, Object>) destSetting.get("particles");
            particles.put("dest_color",CUtl.color.updateOld((String) particles.get("destcolor"),config.DESTDestParticleColor));
            particles.put("line_color",CUtl.color.updateOld((String) particles.get("linecolor"),config.DESTLineParticleColor));
            particles.put("tracking_color",CUtl.color.updateOld((String) particles.get("trackingcolor"),config.DESTTrackingParticleColor));
            particles.remove("destcolor");
            particles.remove("linecolor");
            particles.remove("trackingcolor");
            destSetting.put("particles",particles);
            destSetting.put("autoclear_rad",destSetting.get("autoclearradius"));
            destSetting.remove("autoclearradius");
            Map<String,Object> destFeatures = new HashMap<>();
            destFeatures.put("send",destSetting.get("send"));
            destFeatures.put("track",destSetting.get("track"));
            destFeatures.put("lastdeath",destSetting.get("lastdeath"));
            destSetting.remove("send");
            destSetting.remove("track");
            destSetting.remove("lastdeath");
            destSetting.put("features",destFeatures);
            dest.put("setting",destSetting);
            //UPDATE HUD COLORS TO NEW SYSTEM
            Map<String,Object> hud = (Map<String, Object>) map.get("hud");
            String[] primary = ((String) hud.get("primary")).split("-");
            primary[0] = CUtl.color.updateOld(primary[0],config.HUDPrimaryColor);
            hud.put("primary",String.join("-",primary));
            String[] secondary = ((String) hud.get("secondary")).split("-");
            secondary[0] = CUtl.color.updateOld(secondary[0],config.HUDSecondaryColor);
            hud.put("secondary",String.join("-",secondary));
            //ADD NEW HUD SETTINGS
            Map<String,Object> hudSetting = (Map<String, Object>) hud.get("setting");
            hudSetting.put("type",config.HUDType);
            hudSetting.put("bossbar",defaults.hudBossBar());
            Map<String,Object> hudSettingModule = new HashMap<>();
            hudSettingModule.put("time_24hr",hudSetting.get("time24h"));
            hudSetting.put("time24h",null);
            hudSettingModule.put("tracking_target",config.HUDTrackingTarget);
            hudSetting.put("module",hudSettingModule);
            hud.put("setting",hudSetting);
            map.put("destination",dest);
            map.put("hud",hud);
        }
        return map;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> removeUnnecessary(Map<String,Object> map) {
        Map<String,Object> dest = (Map<String, Object>) map.get("destination");
        // todo remove the hud presets when they get done
        dest.remove("saved");
        dest.remove("lastdeath");
        map.put("destination",dest);
        map.remove("name");
        //removes map.name, map.destination.saved, map.destination.setting.send, map.destination.lastdeath
        return map;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> addExpires(Player player, Map<String,Object> map) {
        //since the counters are stored in the map, when the file gets saved, it updates the file.
        Map<String,Object> cache = playerMap.get(player);
        if (cache == null) return map;
        Map<String,Object> cTemp = (Map<String, Object>) cache.get("temp");
        Map<String,Object> mTemp = (Map<String, Object>) map.get("temp");
        //if the count-down is still in the map, make sure the target player is online, then save it to file
        //  if not, don't save & remove the countdown if there is one already in the file
        if (cTemp.get("track")!=null) {
            Map<String,Object> track = (Map<String, Object>) cTemp.get("track");
            if (Player.of((String) track.get("target")) == null) {
                mTemp.put("track", null);
            } else mTemp.put("track",cTemp.get("track"));
        } else if (mTemp.get("track") != null) mTemp.put("track", null);
        map.put("temp",mTemp);
        return map;
    }
    public static void updatePlayerMap(Player player) {
        playerMap.put(player,removeUnnecessary(fileToMap(player)));
    }
    public static void addPlayer(Player player) {
        Map<String, Object> map = updater(player, fileToMap(player));
        mapToFile(player, map);
        updatePlayerMap(player);
        oneTimeMap.put(player,new HashMap<>());
    }
    public static void removePlayer(Player player) {
        mapToFile(player, fileToMap(player));
        playerMap.remove(player);
        oneTimeMap.remove(player);
    }
    public static String getOneTime(Player player, String key) {
        return oneTimeMap.get(player).get(key);
    }
    public static void setOneTime(Player player, String key, String value) {
        Map<String,String> map = oneTimeMap.get(player);
        map.put(key,value);
        oneTimeMap.put(player,map);
    }
    public static class defaults {
        public static Map<String,Object> get(Player player) {
            Map<String,Object> map = new HashMap<>();
            //hud
            Map<String,Object> hud = new HashMap<>();
            hud.put("enabled", config.HUDEnabled);
            hud.put("setting", defaults.hudSetting());
            hud.put("module", defaults.hudModule());
            hud.put("order", config.HUDOrder);
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
            map.put("version", 1.43);
            map.put("name", player.getName());
            map.put("hud", hud);
            map.put("destination", destination);
            map.put("temp", new HashMap<>());
            return map;
        }
        public static Map<String,Object> hudSetting() {
            Map<String,Object> hudSetting = new HashMap<>();
            hudSetting.put("type", config.HUDType);
            hudSetting.put("bossbar",hudBossBar());
            hudSetting.put("module",hudSettingModule());
            return hudSetting;
        }
        public static Map<String,Object> hudBossBar() {
            Map<String,Object> hudBossBar = new HashMap<>();
            hudBossBar.put("color", config.HUDBarColor);
            hudBossBar.put("distance",config.HUDBarShowDistance);
            hudBossBar.put("distance_max",config.HUDBarDistanceMax);
            return hudBossBar;
        }
        public static Map<String,Object> hudSettingModule() {
            Map<String,Object> module = new HashMap<>();
            module.put("time_24hr", config.HUDTime24HR);
            module.put("tracking_target",config.HUDTrackingTarget);
            return module;
        }
        public static Map<String,Object> hudModule() {
            Map<String,Object> hudModule = new HashMap<>();
            hudModule.put("coordinates", config.HUDCoordinates);
            hudModule.put("distance", config.HUDDistance);
            hudModule.put("destination", config.HUDDestination);
            hudModule.put("direction", config.HUDDirection);
            hudModule.put("tracking", config.HUDTracking);
            hudModule.put("time", config.HUDTime);
            hudModule.put("weather", config.HUDWeather);
            return hudModule;
        }
        public static Map<String,Object> destSetting() {
            Map<String,Object> destSetting = new HashMap<>();
            destSetting.put("autoclear", config.DESTAutoClear);
            destSetting.put("autoclear_rad",(long) config.DESTAutoClearRad);
            destSetting.put("autoconvert", config.DESTAutoConvert);
            destSetting.put("ylevel", config.DESTYLevel);
            destSetting.put("features", destFeatures());
            destSetting.put("particles", destParticles());
            return destSetting;
        }
        public static Map<String,Object> destFeatures() {
            Map<String,Object> destFeatures = new HashMap<>();
            destFeatures.put("send", config.DESTSend);
            destFeatures.put("track", config.DESTTrack);
            destFeatures.put("lastdeath", config.DESTLastdeath);
            return destFeatures;
        }
        public static Map<String,Object> destParticles() {
            Map<String,Object> destParticles = new HashMap<>();
            destParticles.put("line", config.DESTLineParticles);
            destParticles.put("line_color", config.DESTLineParticleColor);
            destParticles.put("dest", config.DESTDestParticles);
            destParticles.put("dest_color", config.DESTDestParticleColor);
            destParticles.put("tracking", config.DESTTrackingParticles);
            destParticles.put("tracking_color", config.DESTTrackingParticleColor);
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
            private static Map<String,Object> get(Player player) {
                return (Map<String, Object>) fromMap(player).get("hud");
            }
            public static Map<String,Object> getModule(Player player) {
                return (Map<String,Object>) get(player).get("module");
            }
            public static String order(Player player) {
                return (String) get(player).get("order");
            }
            public static boolean state(Player player) {
                return (boolean) get(player).get("enabled");
            }
            public static String primary(Player player) {
                return (String) get(player).get("primary");
            }
            public static String secondary(Player player) {
                return (String) get(player).get("secondary");
            }
            public static class setting {
                public static Map<String,Object> map(Player player) {
                    return (Map<String,Object>) hud.get(player).get("setting");
                }
                public static Object get(Player player, HUD.Settings type) {
                    String string = type.toString();
                    if (string.contains(".")) {
                        String base = string.substring(0,string.indexOf('.'));
                        Map<String,Object> bar = (Map<String,Object>) map(player).get(base);
                        return bar.get(string.substring(string.indexOf('.')+1));
                    }
                    return map(player).get(string);
                }
            }
            public static class module {
                public static boolean coordinates(Player player) {
                    return (boolean) getModule(player).get("coordinates");
                }
                public static boolean distance(Player player) {
                    return (boolean) getModule(player).get("distance");
                }
                public static boolean destination(Player player) {
                    return (boolean) getModule(player).get("destination");
                }
                public static boolean direction(Player player) {
                    return (boolean) getModule(player).get("direction");
                }
                public static boolean tracking(Player player) {
                    return (boolean) getModule(player).get("tracking");
                }
                public static boolean time(Player player) {
                    return (boolean) getModule(player).get("time");
                }
                public static boolean weather(Player player) {
                    return (boolean) getModule(player).get("weather");
                }
            }
        }
        public static class dest {
            private static Map<String,Object> get(Player player, boolean map) {
                if (map) return (Map<String,Object>) fromMap(player).get("destination");
                return (Map<String, Object>) fileToMap(player).get("destination");
            }
            public static ArrayList<String> getLastdeaths(Player player) {
                return (ArrayList<String>) get(player,false).get("lastdeath");
            }
            public static Loc getDest(Player player) {
                return new Loc((String) get(player,true).get("dest"));
            }
            public static String getTracking(Player player) {
                return (String) get(player,true).get("tracking");
            }
            public static List<List<String>> getSaved(Player player) {
                return (List<List<String>>) get(player,false).get("saved");
            }
            public static class setting {
                private static Map<String,Object> map(Player player) {
                    return (Map<String,Object>) dest.get(player,true).get("setting");
                }
                public static Object get(Player player, Destination.Settings settings) {
                    String string = settings.toString();
                    if (string.contains(".")) {
                        String base = string.substring(0,string.indexOf('.'));
                        Map<String,Object> bar = (Map<String,Object>) map(player).get(base);
                        return bar.get(string.substring(string.indexOf('.')+1));
                    }
                    return map(player).get(string);
                }
            }
        }
        public static class temp {
            private static Map<String,Object> get(Player player) {
                if (fromMap(player).get("temp") == null) return new HashMap<>();
                return (Map<String,Object>) fromMap(player).get("temp");
            }
            public static class track {
                public static boolean exists(Player player) {
                    return get(player).get("track") != null;
                }
                private static Map<String,Object> map(Player player) {
                    if (get(player).get("track") == null) return new HashMap<>();
                    return (Map<String,Object>) get(player).get("track");
                }
                public static String id(Player player) {
                    return (String) map(player).get("id");
                }
                public static int expire(Player player) {
                    return ((Long) map(player).get("expire")).intValue();
                }
                public static String target(Player player) {
                    return (String) map(player).get("target");
                }
            }
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
            public static void order(Player player, String order) {
                Map<String,Object> data = get.hud.get(player);
                data.put("order", order);
                map(player, data);
            }
            public static void state(Player player, boolean b) {
                Map<String,Object> data = get.hud.get(player);
                data.put("enabled", b);
                map(player, data);
            }
            public static void primary(Player player, String color) {
                Map<String,Object> data = get.hud.get(player);
                data.put("primary", color);
                map(player, data);
            }
            public static void secondary(Player player, String color) {
                Map<String,Object> data = get.hud.get(player);
                data.put("secondary", color);
                map(player, data);
            }
            public static class setting {
                private static void map(Player player, Map<String,Object> setting) {
                    Map<String,Object> data = get.hud.get(player);
                    data.put("setting", setting);
                    hud.map(player, data);
                }
                public static void set(Player player, HUD.Settings type, Object setting) {
                    String string = type.toString();
                    Map<String,Object> data = get.hud.setting.map(player);
                    if (string.contains(".")) {
                        String base = string.substring(0,string.indexOf('.'));
                        Map<String,Object> bar = (Map<String,Object>) get.hud.setting.map(player).get(base);
                        bar.put(string.substring(string.indexOf('.')+1),setting);
                        data.put(base,bar);
                    } else {
                        data.put(string.substring(string.indexOf('.')+1),setting);
                    }
                    map(player,data);
                }
            }
            public static class module {
                public static void map(Player player, Map<String,Object> module) {
                    Map<String,Object> data = get.hud.get(player);
                    data.put("module", module);
                    hud.map(player, data);
                }
                public static void fromString(Player player, String moduleName, boolean b) {
                    Map<String,Object> data = get.hud.getModule(player);
                    data.put(moduleName, b);
                    map(player, data);
                }
            }
        }
        public static class dest {
            public static void set(Player player, Map<String,Object> dest) {
                Map<String,Object> map = fileToMap(player);
                map.put("destination",dest);
                mapToFile(player,map);
                updatePlayerMap(player);
            }
            public static void setDest(Player player, Loc loc) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("dest", loc.getLocC());
                set(player, data);
            }
            public static void setTracking(Player player, String s) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("tracking", s);
                set(player, data);
            }
            public static void setLastdeaths(Player player, ArrayList<String> lastdeath) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("lastdeath", lastdeath);
                set(player, data);
            }
            public static void setSaved(Player player, List<List<String>> saved) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("saved", saved);
                set(player, data);
            }
            public static class setting {
                private static void map(Player player, Map<String,Object> setting) {
                    Map<String,Object> data = get.dest.get(player,false);
                    data.put("setting", setting);
                    dest.set(player, data);
                }
                public static void set(Player player, Destination.Settings settings, Object setting) {
                    String string = settings.toString();
                    Map<String,Object> data = get.dest.setting.map(player);
                    if (string.contains(".")) {
                        String base = string.substring(0,string.indexOf('.'));
                        Map<String,Object> bar = (Map<String,Object>) get.dest.setting.map(player).get(base);
                        bar.put(string.substring(string.indexOf('.')+1),setting);
                        data.put(base,bar);
                    } else {
                        data.put(string.substring(string.indexOf('.')+1),setting);
                    }
                    map(player,data);
                }
            }
        }
        public static class temp {
            public static void setM(Player player, Map<String,Object> temp) {
                Map<String,Object> map = get.fromMap(player);
                map.put("temp",temp);
                playerMap.put(player,map);
            }
            public static class track {
                private static void set(Player player, Map<String,Object> setting) {
                    Map<String,Object> data = get.temp.get(player);
                    data.put("track", setting);
                    setM(player, data);
                }
                public static void remove(Player player) {
                    Map<String,Object> data = get.temp.get(player);
                    data.put("track", null);
                    setM(player, data);
                }
                public static void id(Player player, String b) {
                    Map<String,Object> data = get.temp.track.map(player);
                    data.put("id", b);
                    set(player, data);
                }
                public static void expire(Player player, long b) {
                    Map<String,Object> data = get.temp.track.map(player);
                    data.put("expire", b);
                    set(player, data);
                }
                public static void target(Player player, String b) {
                    Map<String,Object> data = get.temp.track.map(player);
                    data.put("target", b);
                    set(player, data);
                }
            }
        }
    }
}