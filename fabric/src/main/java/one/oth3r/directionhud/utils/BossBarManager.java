package one.oth3r.directionhud.utils;

import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.util.Identifier;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.PlayerData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BossBarManager {
    private final Map<Player, Identifier> bossBars = new HashMap<>();
    public void addPlayer(Player player) {
        bossBars.put(player,new Identifier(DirectionHUD.MOD_ID,player.getUUID()+"-bossbar"));
        DirectionHUD.server.getBossBarManager().add(bossBars.get(player),CTxT.of("").b()).addPlayer(player.getPlayer());
    }
    public void removePlayer(Player player) {
        Identifier identifier = bossBars.remove(player);
        if (identifier != null) {
            Objects.requireNonNull(DirectionHUD.server.getBossBarManager().get(identifier)).removePlayer(player.getPlayer());
            DirectionHUD.server.getBossBarManager().remove(DirectionHUD.server.getBossBarManager().get(identifier));
        }
    }
    public void display(Player player, CTxT hud) {
        if (!bossBars.containsKey(player)) addPlayer(player);
        BossBar bossBar = DirectionHUD.server.getBossBarManager().get(bossBars.get(player));
        assert bossBar != null;
        bossBar.setName(hud.b());
        bossBar.setColor(BossBar.Color.valueOf(((String) PlayerData.get.hud.setting.get(player, HUD.Settings.bossbar__color)).toUpperCase()));
        if (Destination.get(player).hasXYZ() && (boolean) PlayerData.get.hud.setting.get(player,HUD.Settings.bossbar__distance)) {
            int dist = Destination.getDist(player);
            double progress = getProgress(dist,(long) PlayerData.get.hud.setting.get(player,HUD.Settings.bossbar__distance_max));
            bossBar.setStyle(BossBar.Style.NOTCHED_10);
            if ((long) PlayerData.get.hud.setting.get(player,HUD.Settings.bossbar__distance_max) == 0) {
                progress = getProgress(dist,1000);
                StringBuilder s = new StringBuilder();
                for (int i = 1;i<5;i++) {
                    if (dist > Integer.parseInt(750+s.toString())) {
                        bossBar.setStyle(BossBar.Style.NOTCHED_20);
                        progress = getProgress(dist,Integer.parseInt(2000+s.toString()));
                    }
                    if (dist > Integer.parseInt(1750+s.toString())) {
                        bossBar.setStyle(BossBar.Style.NOTCHED_10);
                        progress = getProgress(dist,Integer.parseInt(10000+s.toString()));
                    }
                    s.append("0");
                }
            }
            bossBar.setPercent((float) progress);
        } else {
            bossBar.setPercent(1);
            bossBar.setStyle(BossBar.Style.PROGRESS);
        }
    }
    private double getProgress(int current, long max) {
        double progress = (double) current/max;
        if (current > max) progress = 1.0;
        progress = Math.max(Math.min(progress,1.0),0.0);
        progress = (progress-1)*-1;
        return progress;
    }
}
