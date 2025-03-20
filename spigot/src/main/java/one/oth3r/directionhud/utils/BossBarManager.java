package one.oth3r.directionhud.utils;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.hud.Hud;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.HashMap;
import java.util.Map;

public class BossBarManager {
    private final Map<Player, BossBar> bossBars = new HashMap<>();

    /**
     * creates a bossbar for the player
     */
    public void addPlayer(Player player) {
        if (bossBars.get(player) == null) {
            BossBar bossBar = Bukkit.createBossBar("DirectionHUD", BarColor.WHITE, BarStyle.SOLID);
            bossBar.addPlayer(player.getPlayer());
            bossBars.put(player, bossBar);
        }
    }

    /**
     * removes the bossbar from the player
     */
    public void removePlayer(Player player) {
        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) bossBar.removePlayer(player.getPlayer());
    }

    /**
     * displays the bossbar for the player
     */
    public void display(Player player, CTxT hud) {
        // if the player doesnt have a bossbar, add one in
        if (!bossBars.containsKey(player)) addPlayer(player);

        // get the bossbar
        BossBar bossBar = bossBars.get(player);
        // set the bossbar text
        bossBar.setTitle(hud.b().toLegacyText());
        // set the color
        bossBar.setColor(BarColor.valueOf(((String) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__color)).toUpperCase()));

        // bossbar
        if (Destination.dest.get(player).hasXYZ() && (boolean) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance)) {
            int dist = Destination.dest.getDist(player);
            double progress = getProgress(dist,(int) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance_max));
            bossBar.setStyle(BarStyle.SEGMENTED_10);
            if ((int) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance_max) == 0) {
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
