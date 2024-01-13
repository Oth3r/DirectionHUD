package one.oth3r.directionhud.utils;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;
import java.util.Map;

public class BossBarManager {
    private final Map<Player, BossBar> bossBars = new HashMap<>();
    public void addPlayer(Player player) {
        BossBar bossBar = Bukkit.createBossBar("DirectionHUD", BarColor.WHITE, BarStyle.SOLID);
        bossBar.addPlayer(player.getPlayer());
        bossBars.put(player, bossBar);
    }
    public void removePlayer(Player player) {
        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) bossBar.removePlayer(player.getPlayer());
    }
    public void display(Player player, CTxT hud) {
        if (!bossBars.containsKey(player)) addPlayer(player);
        BossBar bossBar = bossBars.get(player);
        bossBar.setTitle(hud.b().toLegacyText());
        bossBar.setColor(BarColor.valueOf(((String) PlayerData.get.hud.setting(player, HUD.Setting.bossbar__color)).toUpperCase()));
        if (Destination.get(player).hasXYZ() && (boolean) PlayerData.get.hud.setting(player, HUD.Setting.bossbar__distance)) {
            int dist = Destination.getDist(player);
            double progress = getProgress(dist,(double) PlayerData.get.hud.setting(player, HUD.Setting.bossbar__distance_max));
            bossBar.setStyle(BarStyle.SEGMENTED_10);
            if ((double) PlayerData.get.hud.setting(player, HUD.Setting.bossbar__distance_max) == 0) {
                progress = getProgress(dist,1000);
                StringBuilder s = new StringBuilder();
                for (int i = 1;i<5;i++) {
                    if (dist > Integer.parseInt(750+s.toString())) {
                        bossBar.setStyle(BarStyle.SEGMENTED_20);
                        progress = getProgress(dist,Integer.parseInt(2000+s.toString()));
                    }
                    if (dist > Integer.parseInt(1750+s.toString())) {
                        bossBar.setStyle(BarStyle.SEGMENTED_10);
                        progress = getProgress(dist,Integer.parseInt(10000+s.toString()));
                    }
                    s.append("0");
                }
            }
            bossBar.setProgress(progress);
        } else {
            bossBar.setProgress(1);
            bossBar.setStyle(BarStyle.SOLID);
        }
    }
    private double getProgress(int current, double max) {
        double progress = (double) current/max;
        if (current > max) progress = 1.0;
        progress = Math.max(Math.min(progress,1.0),0.0);
        progress = (progress-1)*-1;
        return progress;
    }
}
