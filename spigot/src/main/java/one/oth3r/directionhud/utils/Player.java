package one.oth3r.directionhud.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatMessageType;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.PacketHelper;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.utils.Loc;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Player {
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
    @Override
    public String toString() {
        return "DirectionHUD Player: "+player.getName();
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
    // Call after toggling/updating the hud.
    public void updateHUD() {
        // if toggled off
        if (!PlayerData.get.hud.state(this)) {
            //if actionbar send empty to clear else remove bossbar
            if (PlayerData.get.hud.setting.get(this,HUD.Settings.type).equals(config.HUDTypes.actionbar.toString()))
                this.sendActionBar(CTxT.of(""));
            else DirectionHUD.bossBarManager.removePlayer(this);
        }
        if (PlayerData.get.hud.setting.get(this,HUD.Settings.type).equals(config.HUDTypes.actionbar.toString()))
            DirectionHUD.bossBarManager.removePlayer(this);
        else this.sendActionBar(CTxT.of(""));
    }
    public void sendSettingPackets() {
        // if player has DirectionHUD on client, send a hashmap with data
        if (DirectionHUD.clientPlayers.contains(this)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            PacketHelper.sendPacket(this,Assets.packets.SETTINGS,gson.toJson(PlayerData.get.fromMap(this)));
        }
    }
    public void sendHUDPackets(HashMap<HUD.modules.Types, ArrayList<String>> hudData) {
        // send the instructions to build the hud to the client
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        PacketHelper.sendPacket(this, Assets.packets.HUD, gson.toJson(hudData));
    }
    public void displayHUD(CTxT message) {
        if (message.getString().equals("")) {
            //if the HUD is enabled but there is no output
            if (PlayerData.getOneTime(this,"hud.enabled_but_off") == null) {
                PlayerData.setOneTime(this,"hud.enabled_but_off","true");
                if ((config.HUDTypes.get((String) PlayerData.get.hud.setting.get(this, HUD.Settings.type)).equals(config.HUDTypes.actionbar))) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, CTxT.of("").b());
                } else {
                    DirectionHUD.bossBarManager.removePlayer(this);
                }
            }
            return;
        } else if (PlayerData.getOneTime(this,"hud.enabled_but_off") != null) {
            // if hud was in previous state and now isn't, remove the temp tag
            PlayerData.setOneTime(this,"hud.enabled_but_off",null);
        }
        if ((config.HUDTypes.get((String) PlayerData.get.hud.setting.get(this, HUD.Settings.type)).equals(config.HUDTypes.actionbar))) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message.b());
        } else {
            DirectionHUD.bossBarManager.display(this,message);
        }
    }
    public String getName() {
        return player.getName();
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
    public float getYaw() {
        return player.getLocation().getYaw();
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
