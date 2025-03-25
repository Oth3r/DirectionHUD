package one.oth3r.directionhud.utils;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.utils.Helper;

import java.util.HashMap;
import java.util.Map;

public class BossBarManager {
    private final Map<Player, BossBar> bossBars = new HashMap<>();

    /**
     * creates a bossbar for the player
     */
    public void addPlayer(Player player) {
        if (bossBars.get(player) == null) {
            BossBar bossBar = BossBar.bossBar(Component.text("DirectionHUD"),1, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
            player.getPlayer().showBossBar(bossBar);
            bossBars.put(player, bossBar);
        }
    }

    /**
     * removes the bossbar from the player
     */
    public void removePlayer(Player player) {
        BossBar bossBar = bossBars.remove(player);
        if (bossBar != null) player.getPlayer().hideBossBar(bossBar);
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
        bossBar.name(hud.b());
        // set the color
        bossBar.color(Helper.Enums.get(player.getPCache().getHud().getSetting(Hud.Setting.bossbar__color), BossBar.Color.class));

        // bossbar
        if (Destination.dest.get(player).hasXYZ() && (boolean) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance)) {
            int dist = Destination.dest.getDist(player);
            float progress = getProgress(dist,(int) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance_max));
            bossBar.overlay(BossBar.Overlay.NOTCHED_10);
            if ((int) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance_max) == 0) {
                progress = getProgress(dist,1000);
                StringBuilder s = new StringBuilder();
                for (int i = 1;i<5;i++) {
                    if (dist > Integer.parseInt(750+s.toString())) {
                        bossBar.overlay(BossBar.Overlay.NOTCHED_20);
                        progress = getProgress(dist,Integer.parseInt(2000+s.toString()));
                    }
                    if (dist > Integer.parseInt(1750+s.toString())) {
                        bossBar.overlay(BossBar.Overlay.NOTCHED_10);
                        progress = getProgress(dist,Integer.parseInt(10000+s.toString()));
                    }
                    s.append("0");
                }
            }
            bossBar.progress(progress);
        } else {
            bossBar.progress(1);
            bossBar.overlay(BossBar.Overlay.PROGRESS);
        }
    }
    private float getProgress(int current, double max) {
        double progress = (double) current/max;
        if (current > max) progress = 1.0;
        progress = Math.max(Math.min(progress,1.0),0.0);
        progress = (progress-1)*-1;
        return (float) progress;
    }
}
