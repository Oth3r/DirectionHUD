package one.oth3r.directionhud.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatMessageType;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.PacketHelper;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.utils.Loc;
import one.oth3r.directionhud.common.template.PlayerTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Player extends PlayerTemplate {
    private org.bukkit.entity.Player player;
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        return Objects.equals(player, other.player);
    }
    @Override
    public int hashCode() {
        return Objects.hash(player);
    }
    public Player() {}
    public static Player of(@Nonnull org.bukkit.entity.Player player) {
        Player instance = new Player();
        instance.player = player;
        return instance;
    }
    @Nullable
    public static Player of(String identifier) {
        Player instance = new Player();
        if (identifier.contains("-")) instance.player = Bukkit.getPlayer(UUID.fromString(identifier));
        else instance.player = Bukkit.getPlayer(identifier);
        if (instance.player == null) return null;
        return instance;
    }
    public void performCommand(String cmd) {
        player.performCommand(cmd);
    }
    public void sendMessage(CTxT message) {
        player.spigot().sendMessage(message.b());
    }
    public void sendActionBar(CTxT message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message.b());
    }

    @Override
    public void displayBossBar(CTxT message) {
        DirectionHUD.bossBarManager.display(this,message);
    }

    @Override
    public void removeBossBar() {
        DirectionHUD.bossBarManager.removePlayer(this);
    }
    public void sendPDataPackets() {
        // if player has DirectionHUD on client, send pData to client
        if (DirectionHUD.clientPlayers.contains(this)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            PacketHelper.sendPacket(this,Assets.packets.PLAYER_DATA,gson.toJson(this.getPData()));
        }
    }
    public void sendHUDPackets(HashMap<Hud.Module, ArrayList<String>> hudData) {
        // send the instructions to build the hud to the client
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        PacketHelper.sendPacket(this, Assets.packets.HUD, gson.toJson(hudData));
    }
    public String getName() {
        return player.getName();
    }

    @Override
    public PData getPData() {
        return PlayerData.getPData(this);
    }

    @Override
    public CachedPData getPCache() {
        return PlayerData.getPCache(this);
    }

    public org.bukkit.entity.Player getPlayer() {
        return player;
    }
    public String getUUID() {
        return player.getUniqueId().toString();
    }
    public String getSpawnDimension() {
        return Utl.dim.format(Bukkit.getWorlds().get(0).getName());
    }
    public String getDimension() {
        return Utl.dim.format(player.getWorld().getName());
    }

    @Override
    public int getTimeOfDay() {
        return (int) player.getWorld().getTime() % 24000;
    }

    @Override
    public boolean hasStorm() {
        return player.getWorld().hasStorm();
    }

    @Override
    public boolean hasThunderstorm() {
        return player.getWorld().isThundering();
    }

    public float getYaw() {
        return player.getLocation().getYaw();
    }
    public float getPitch() {
        return player.getLocation().getPitch();
    }
    public ArrayList<Double> getVec() {
        ArrayList<Double> vec = new ArrayList<>();
        vec.add(player.getLocation().toVector().getX());
        vec.add(player.getLocation().toVector().getY()+1);
        vec.add(player.getLocation().toVector().getZ());
        return vec;
    }
    public Loc getLoc() {
        if (player == null) return new Loc();
        else return new Loc(Player.of(player));
    }
    public int getBlockX() {
        return player.getLocation().getBlockX();
    }
    public int getBlockY() {
        return player.getLocation().getBlockY();
    }
    public int getBlockZ() {
        return player.getLocation().getBlockZ();
    }
    public void spawnParticleLine(ArrayList<Double> end, String particleType) {
        Vector endVec = Utl.vec.convertTo(end);
        Vector pVec = player.getLocation().toVector().add(new Vector(0, 1, 0));
        if (player.getVehicle() != null) pVec.add(new Vector(0,-0.2,0));
        double distance = pVec.distance(endVec);
        Vector particlePos = pVec.subtract(new Vector(0, 0.2, 0));
        double spacing = 1;
        Vector segment = endVec.subtract(pVec).normalize().multiply(spacing);
        double distCovered = 0;
        for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
            distCovered += spacing;
            if (pVec.distance(endVec) < 2) continue;
            if (distCovered >= 50) break;
            player.spawnParticle(Particle.REDSTONE,particlePos.getX(),particlePos.getY(),particlePos.getZ(),1,Utl.particle.getParticle(particleType, Player.of(player)));
        }
    }
}
