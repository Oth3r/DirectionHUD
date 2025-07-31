package one.oth3r.directionhud.utils;

import net.minecraft.entity.boss.BossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.utils.Helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class BossBarManager {
    private final Map<Player, Identifier> bossBars = new HashMap<>();
    public void addPlayer(Player player) {
        bossBars.put(player, Identifier.of(DirectionHUD.MOD_ID,player.getUUID()+"-bossbar"));
        DirectionHUD.getData().getServer().getBossBarManager().add(bossBars.get(player),new CTxT().b()).addPlayer(player.getPlayer());
    }
    public void removePlayer(Player player) {
        Identifier identifier = bossBars.remove(player);
        if (identifier != null) {
            MinecraftServer server = DirectionHUD.getData().getServer();
            Objects.requireNonNull(server.getBossBarManager().get(identifier)).removePlayer(player.getPlayer());
            server.getBossBarManager().remove(server.getBossBarManager().get(identifier));
        }
    }
    public void display(Player player, CTxT hud) {
        if (!bossBars.containsKey(player)) addPlayer(player);
        BossBar bossBar = DirectionHUD.getData().getServer().getBossBarManager().get(bossBars.get(player));
        assert bossBar != null;
        bossBar.setName(hud.b());
        bossBar.setColor(Helper.Enums.get(player.getPCache().getHud().getSetting(Hud.Setting.bossbar__color).toString(),BossBar.Color.class));
        if (Destination.dest.get(player).hasXYZ() && (boolean) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance)) {
            int dist = Destination.dest.getDist(player);
            double progress = getProgress(dist,(int) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance_max));
            bossBar.setStyle(BossBar.Style.NOTCHED_10);
            if ((int) player.getPCache().getHud().getSetting(Hud.Setting.bossbar__distance_max) == 0) {
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
    private double getProgress(int current, double max) {
        double progress = (double) current/max;
        if (current > max) progress = 1.0;
        progress = Math.max(Math.min(progress,1.0),0.0);
        progress = (progress-1)*-1;
        return progress;
    }
    public void clear() {
        bossBars.clear();
    }
}
