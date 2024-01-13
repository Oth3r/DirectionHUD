package one.oth3r.directionhud.utils;

import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.CUtl;
import one.oth3r.directionhud.common.utils.Helper;
import org.joml.Vector3f;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Utl {
    public static CTxT getTranslation(String key, Object... args) {
        return CTxT.of(Text.translatable(key, args));
    }
    public static CTxT getTxTFromObj(Object obj) {
        CTxT txt = CTxT.of("");
        if (obj instanceof CTxT) txt.append(((CTxT) obj).b());
        else if (obj instanceof Text) txt.append((Text) obj);
        else txt.append(String.valueOf(obj));
        return txt;
    }
    public static void setTime() {
        World world = DirectionHUD.server.getOverworld();
        long timeTicks = world.getTimeOfDay();
        HUD.hour = (int) ((timeTicks / 1000 + 6) % 24);
        HUD.minute = (int) ((timeTicks % 1000) * 60 / 1000);
        if (world.isRaining()) {
            String str;
            if (world.isNight()) str = Assets.symbols.moon;
            else str = Assets.symbols.sun;
            // can sleep during thunderstorm
            if (world.isThundering()) HUD.weatherIcon = Assets.symbols.moon + Assets.symbols.thunder;
            else HUD.weatherIcon = str + Assets.symbols.rain;
        } else if (world.isNight()) HUD.weatherIcon = Assets.symbols.moon;
        else HUD.weatherIcon = Assets.symbols.sun;
    }
    public static List<Player> getPlayers() {
        ArrayList<Player> array = new ArrayList<>();
        for (ServerPlayerEntity p : DirectionHUD.server.getPlayerManager().getPlayerList())
            array.add(Player.of(p));
        return array;
    }
    public static class vec {
        public static double distance(ArrayList<Double> from, ArrayList<Double> to)  {
            return convertTo(from).distanceTo(convertTo(to));
        }
        public static Vec3d convertTo(ArrayList<Double> vec) {
            if (vec.size() == 3) return new Vec3d(vec.get(0),vec.get(1),vec.get(2));
            else return new Vec3d(0,0,0);
        }
    }
    public static class checkEnabled {
        //todo add a bool for singleplayer for perm checking
        public static boolean destination(Player player) {
            return true;
        }
        public static boolean hud(Player player) {
            return config.HUDEditing;
        }
        public static boolean reload(Player player) {
            return player.getPlayer().hasPermissionLevel(2) || DirectionHUDClient.singleplayer;
        }
        public static boolean global(Player player) {
            return config.globalDESTs && (player.getPlayer().hasPermissionLevel(2) || DirectionHUDClient.singleplayer);
        }
        public static boolean saving(Player player) {
            return config.DestSaving;
        }
        public static boolean lastdeath(Player player) {
            return (boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.features__lastdeath) && config.LastDeathSaving;
        }
        public static boolean send(Player player) {
            return (boolean)PlayerData.get.dest.setting.get(player, Destination.Setting.features__send) && config.social && DirectionHUD.server.isRemote();
        }
        public static boolean track(Player player) {
            return (boolean) PlayerData.get.dest.setting.get(player, Destination.Setting.features__track) && config.social && DirectionHUD.server.isRemote();
        }
    }
    public static class particle {
        public static final String LINE = "LINE";
        public static final String DEST = "DEST";
        public static final String TRACKING = "TRACKING";
        public static void spawnLine(Player player, ArrayList<Double> start, ArrayList<Double> end, String particleType) {
            Vec3d startV = vec.convertTo(start);
            Vec3d endV = vec.convertTo(end);
            Vec3d playerV = vec.convertTo(player.getVec());
            double distance = startV.distanceTo(endV);
            Vec3d particlePos = startV.subtract(0, 0.2, 0);
            double spacing = 1;
            Vec3d segment = endV.subtract(startV).normalize().multiply(spacing);
            double distCovered = 0;
            for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
                distCovered += spacing;
                if (distCovered >= 50) break;
                if (!(playerV.distanceTo(particlePos) > 0.5 && playerV.distanceTo(particlePos) < 50)) continue;
                player.spawnParticle(particleType,particlePos);
            }
        }
        public static DustParticleEffect getParticle(String particleType, Player player) {
            String hex = (String) PlayerData.get.dest.setting.get(player, Destination.Setting.particles__dest_color);
            if (particleType.equals(DEST)) return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),3);
            hex = (String) PlayerData.get.dest.setting.get(player, Destination.Setting.particles__line_color);
            if (particleType.equals(LINE)) return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),1);
            hex = (String) PlayerData.get.dest.setting.get(player, Destination.Setting.particles__tracking_color);
            if (particleType.equals(TRACKING)) return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),0.5f);
            hex = "#000000";
            return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(hex)).getRGB()).toVector3f()),5f);
        }
    }
    public static class dim {
        public static final List<String> DEFAULT_DIMENSIONS = List.of("minecraft.overworld|Overworld|#55FF55","minecraft.the_nether|Nether|#e8342e","minecraft.the_end|End|#edffb0");
        public static final List<String> DEFAULT_RATIOS = List.of("minecraft.overworld=1|minecraft.the_nether=8");
        public static String format(Identifier identifier) {
            return identifier.toString().replace(":",".");
        }
        public static boolean checkValid(String s) {
            return dims.containsKey(s);
        }
        public static String getName(String dim) {
            if (!dims.containsKey(dim)) return "unknown";
            HashMap<String,String> map = dims.get(dim);
            return map.get("name");
        }
        public static boolean canConvert(String DIM1, String DIM2) {
            // both in same dim, cant convert
            if (DIM1.equalsIgnoreCase(DIM2)) return false;
            Helper.Pair<String, String> key = new Helper.Pair<>(DIM1, DIM2);
            Helper.Pair<String, String> flippedKey = new Helper.Pair<>(DIM2, DIM1);
            // if the ratio exists, show the button
            return conversionRatios.containsKey(key) || conversionRatios.containsKey(flippedKey);
        }
        public static List<String> getList() {
            return new ArrayList<>(dims.keySet());
        }
        public static String getHEX(String dim) {
            if (!dims.containsKey(dim)) return "#FF0000";
            HashMap<String,String> map = dims.get(dim);
            return map.get("color");
        }
        public static CTxT getLetterButton(String dim) {
            if (!dims.containsKey(dim)) return CTxT.of("X").btn(true).hEvent(CTxT.of("???"));
            HashMap<String,String> map = dims.get(dim);
            return CTxT.of(map.get("name").charAt(0)+"".toUpperCase()).btn(true).color(map.get("color"))
                    .hEvent(CTxT.of(map.get("name").toUpperCase()).color(map.get("color")));
        }
        public static HashMap<Helper.Pair<String, String>, Double> conversionRatios = new HashMap<>();
        public static HashMap<String,HashMap<String,String>> dims = new HashMap<>();
        //only works when the server is on, loads server dimensions into the config.
        public static void loadConfig() {
            if (DirectionHUD.server == null) return;
            //LOAD DIM RATIOS
            HashMap<Helper.Pair<String, String>, Double> configRatios = new HashMap<>();
            for (String s : config.dimensionRatios) {
                String[] split = s.split("\\|");
                if (split.length != 2) continue;
                double ratio = Double.parseDouble(split[0].split("=")[1])/Double.parseDouble(split[1].split("=")[1]);
                configRatios.put(new Helper.Pair<>(split[0].split("=")[0], split[1].split("=")[0]), ratio);
            }
            conversionRatios = configRatios;
            //CONFIG TO MAP
            HashMap<String,HashMap<String,String>> configDims = new HashMap<>();
            for (String s : config.dimensions) {
                String[] split = s.split("\\|");
                if (split.length != 3) continue;
                HashMap<String,String> data = new HashMap<>();
                data.put("name",split[1]);
                data.put("color",split[2]);
                configDims.put(split[0],data);
            }
            dims = configDims;
            //ADD MISSING DIMS TO MAP
            for (ServerWorld world : DirectionHUD.server.getWorlds()) {
                String currentDIM = world.getRegistryKey().getValue().toString().replace(":",".");
                String currentDIMp = world.getRegistryKey().getValue().getPath();
                if (!dims.containsKey(currentDIM)) {
                    HashMap<String,String> map = new HashMap<>();
                    // try to make it look better, remove all "_" and "the" and capitalizes the first word.
                    String formatted = currentDIMp.replaceAll("_"," ");
                    formatted = formatted.replaceFirst("the ","");
                    formatted = formatted.substring(0,1).toUpperCase()+formatted.substring(1);
                    //make random color to spice things up
                    Random random = new Random();
                    int red = random.nextInt(256);
                    int green = random.nextInt(256);
                    int blue = random.nextInt(256);
                    map.put("name",formatted);
                    map.put("color",String.format("#%02x%02x%02x", red, green, blue));
                    dims.put(currentDIM,map);
                }
            }
            //MAP TO CONFIG
            List<String> output = new ArrayList<>();
            for (Map.Entry<String, HashMap<String, String>> entry : dims.entrySet()) {
                String key = entry.getKey();
                HashMap<String, String> data = entry.getValue();
                output.add(key+"|"+data.get("name")+"|"+data.get("color"));
            }
            config.dimensions = output;
        }
    }
}
