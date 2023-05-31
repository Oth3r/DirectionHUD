package one.oth3r.directionhud.spigot.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import com.google.gson.reflect.TypeToken;
import one.oth3r.directionhud.spigot.DirectionHUD;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.spigot.utils.Player;
import one.oth3r.directionhud.spigot.utils.Loc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerData {
    public static Map<Player,Map<String,Object>> playerMap = new HashMap<>();
    public static Map<Player,Map<String,String>> oneTimeMap = new HashMap<>();
    public static File getFile(Player player) {
        if (config.online) return new File(DirectionHUD.playerData+player.getUUID()+".json");
        else return new File(DirectionHUD.playerData+player.getName()+".json");
    }
    public static Map<String, Object> fileToMap(Player player) {
        File file = getFile(player);
        if (!file.exists()) return getDefaults(player);
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
        return map;
    }
    @SuppressWarnings("unchecked")
    public static Map<String,Object> removeUnnecessary(Map<String,Object> map) {
        Map<String,Object> dest = (Map<String, Object>) map.get("destination");
        Map<String,Object> dSet = (Map<String, Object>) dest.get("setting");
        dSet.remove("send");
        dSet.remove("lastdeath");
        dest.remove("saved");
        dest.remove("lastdeath");
        dest.put("setting",dSet);
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
        Map<String,Object> cdest = (Map<String, Object>) cache.get("destination");
        Map<String,Object> mdest = (Map<String, Object>) map.get("destination");
        if (cdest.get("track")!=null) {
            Map<String,Object> track = (Map<String, Object>) cdest.get("track");
            if (Player.of((String) track.get("target")) == null) {
                mdest.put("track", null);
            } else mdest.put("track",cdest.get("track"));
        } else if (mdest.get("track") != null) mdest.put("track", null);
        map.put("destination",mdest);
        return map;
    }
    public static void addPlayer(Player player) {
        Map<String, Object> map = updater(player, fileToMap(player));
        mapToFile(player, map);
        playerMap.put(player,removeUnnecessary(map));
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
    public static Map<String,Object> getDefaults(Player player) {
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
        destination.put("track", null);
        //base
        map.put("version", 1.0);
        map.put("name", player.getName());
        map.put("hud", hud);
        map.put("destination", destination);
        return map;
    }
    public static class defaults {
        public static Map<String,Object> hudSetting() {
            Map<String,Object> hudSetting = new HashMap<>();
            hudSetting.put("time24h", config.HUD24HR);
            return hudSetting;
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
            destSetting.put("autoclearradius",(long) config.DESTAutoClearRad);
            destSetting.put("autoconvert", config.DESTAutoConvert);
            destSetting.put("ylevel", config.DESTYLevel);
            destSetting.put("send", config.DESTSend);
            destSetting.put("track", config.DESTTrack);
            destSetting.put("lastdeath", config.DESTLastdeath);
            destSetting.put("particles", destParticles());
            return destSetting;
        }
        public static Map<String,Object> destParticles() {
            Map<String,Object> destParticles = new HashMap<>();
            destParticles.put("line", config.DESTLineParticles);
            destParticles.put("linecolor", config.DESTLineParticleColor);
            destParticles.put("dest", config.DESTDestParticles);
            destParticles.put("destcolor", config.DESTDestParticleColor);
            destParticles.put("tracking", config.DESTTrackingParticles);
            destParticles.put("trackingcolor", config.DESTTrackingParticleColor);
            return destParticles;
        }
    }
    @SuppressWarnings("unchecked")
    public static class get {
        public static Map<String,Object> fromMap(Player player) {
            return playerMap.get(player);
        }
        public static class hud {
            private static Map<String,Object> get(Player player) {
                return (Map<String, Object>) fromMap(player).get("hud");
            }
            public static Map<String,Object> getSetting(Player player) {
                return (Map<String,Object>) get(player).get("setting");
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
                public static boolean time24h(Player player) {
                    return (boolean) getSetting(player).get("time24h");
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
            private static Map<String,Object> getSetting(Player player, boolean map) {
                return (Map<String,Object>) get(player,map).get("setting");
            }
            private static Map<String,Object> getParticleSetting(Player player) {
                return (Map<String,Object>) dest.getSetting(player, true).get("particles");
            }
            private static Map<String,Object> getTrack(Player player) {
                if (get(player,true).get("track") == null) return new HashMap<>();
                return (Map<String,Object>) get(player,true).get("track");
            }
            public static ArrayList<String> getLastdeaths(Player player) {
                return (ArrayList<String>) get(player,false).get("lastdeath");
            }
            public static boolean getTrackPending(Player player) {
                return get(player,true).get("track") != null;
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
                public static boolean autoclear(Player player) {
                    return (boolean) getSetting(player, true).get("autoclear");
                }
                public static int autoclearrad(Player player) {
                    return ((Long) getSetting(player, true).get("autoclearradius")).intValue();
                }
                public static boolean autoconvert(Player player) {
                    return (boolean) getSetting(player, true).get("autoconvert");
                }
                public static boolean ylevel(Player player) {
                    return (boolean) getSetting(player, true).get("ylevel");
                }
                public static boolean send(Player player) {
                    return (boolean) getSetting(player, false).get("send");
                }
                public static boolean track(Player player) {
                    return (boolean) getSetting(player, true).get("track");
                }
                public static boolean lastdeath(Player player) {
                    return (boolean) getSetting(player, false).get("lastdeath");
                }
                public static class particle {
                    public static boolean line(Player player) {
                        return (boolean) getParticleSetting(player).get("line");
                    }
                    public static String linecolor(Player player) {
                        return (String) getParticleSetting(player).get("linecolor");
                    }
                    public static boolean dest(Player player) {
                        return (boolean) getParticleSetting(player).get("dest");
                    }
                    public static String destcolor(Player player) {
                        return (String) getParticleSetting(player).get("destcolor");
                    }
                    public static boolean tracking(Player player) {
                        return (boolean) getParticleSetting(player).get("tracking");
                    }
                    public static String trackingcolor(Player player) {
                        return (String) getParticleSetting(player).get("trackingcolor");
                    }
                }
            }
            public static class track {
                public static String id(Player player) {
                    return (String) getTrack(player).get("id");
                }
                public static int expire(Player player) {
                    return ((Long) getTrack(player).get("expire")).intValue();
                }
                public static String target(Player player) {
                    return (String) getTrack(player).get("target");
                }
            }
        }
    }
    public static class set {
        public static class hud {
            public static void set(Player player, Map<String,Object> hud) {
                Map<String,Object> map = fileToMap(player);
                map.put("hud",hud);
                mapToFile(player,map);
                playerMap.put(player,removeUnnecessary(map));
            }
            private static void setSetting(Player player, Map<String,Object> setting) {
                Map<String,Object> data = get.hud.get(player);
                data.put("setting", setting);
                set(player, data);
            }
            public static void setModule(Player player, Map<String,Object> module) {
                Map<String,Object> data = get.hud.get(player);
                data.put("module", module);
                set(player, data);
            }
            public static void order(Player player, String order) {
                Map<String,Object> data = get.hud.get(player);
                data.put("order", order);
                set(player, data);
            }
            public static void state(Player player, boolean b) {
                Map<String,Object> data = get.hud.get(player);
                data.put("enabled", b);
                set(player, data);
            }
            public static void primary(Player player, String color) {
                Map<String,Object> data = get.hud.get(player);
                data.put("primary", color);
                set(player, data);
            }
            public static void secondary(Player player, String color) {
                Map<String,Object> data = get.hud.get(player);
                data.put("secondary", color);
                set(player, data);
            }
            public static class setting {
                public static void time24h(Player player, boolean b) {
                    Map<String,Object> data = get.hud.getSetting(player);
                    data.put("time24h", b);
                    setSetting(player, data);
                }
            }
            public static class module {
                public static void byName(Player player, String moduleName, boolean b) {
                    Map<String,Object> data = get.hud.getModule(player);
                    data.put(moduleName, b);
                    setModule(player, data);
                }
            }
        }
        public static class dest {
            public static void set(Player player, Map<String,Object> dest) {
                Map<String,Object> map = fileToMap(player);
                map.put("destination",dest);
                mapToFile(player,map);
                playerMap.put(player,map);
            }
            public static void setM(Player player, Map<String,Object> dest) {
                Map<String,Object> map = get.fromMap(player);
                map.put("destination",dest);
                playerMap.put(player,map);
            }
            private static void setSetting(Player player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.get(player,false);
                data.put("setting", setting);
                set(player, data);
            }
            private static void setParticleSetting(Player player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.getSetting(player,false);
                data.put("particles", setting);
                setSetting(player, data);
            }
            private static void setTrack(Player player, Map<String,Object> setting) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("track", setting);
                setM(player, data);
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
            public static void setTrackNull(Player player) {
                Map<String,Object> data = get.dest.get(player,true);
                data.put("track", null);
                setM(player, data);
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
                public static void autoclear(Player player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("autoclear", b);
                    setSetting(player, data);
                }
                public static void autoclearrad(Player player, long b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("autoclearradius", b);
                    setSetting(player, data);
                }
                public static void autoconvert(Player player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("autoconvert", b);
                    setSetting(player, data);
                }
                public static void ylevel(Player player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("ylevel", b);
                    setSetting(player, data);
                }
                public static void send(Player player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("send", b);
                    setSetting(player, data);
                }
                public static void track(Player player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("track", b);
                    setSetting(player, data);
                }
                public static void lastdeath(Player player, boolean b) {
                    Map<String,Object> data = get.dest.getSetting(player,false);
                    data.put("lastdeath", b);
                    setSetting(player, data);
                }
                public static class particles {
                    public static void line(Player player, boolean b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("line", b);
                        setParticleSetting(player, data);
                    }
                    public static void linecolor(Player player, String b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("linecolor", b);
                        setParticleSetting(player, data);
                    }
                    public static void dest(Player player, boolean b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("dest", b);
                        setParticleSetting(player, data);
                    }
                    public static void destcolor(Player player, String b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("destcolor", b);
                        setParticleSetting(player, data);
                    }
                    public static void tracking(Player player, boolean b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("tracking", b);
                        setParticleSetting(player, data);
                    }
                    public static void trackingcolor(Player player, String b) {
                        Map<String,Object> data = get.dest.getParticleSetting(player);
                        data.put("trackingcolor", b);
                        setParticleSetting(player, data);
                    }
                }
            }
            public static class track {
                public static void id(Player player, String b) {
                    Map<String,Object> data = get.dest.getTrack(player);
                    data.put("id", b);
                    setTrack(player, data);
                }
                public static void expire(Player player, long b) {
                    Map<String,Object> data = get.dest.getTrack(player);
                    data.put("expire", b);
                    setTrack(player, data);
                }
                public static void target(Player player, String b) {
                    Map<String,Object> data = get.dest.getTrack(player);
                    data.put("target", b);
                    setTrack(player, data);
                }
            }
        }
    }
}
