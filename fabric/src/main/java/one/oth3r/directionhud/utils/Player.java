package one.oth3r.directionhud.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.PacketBuilder;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.utils.Loc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Player {
    private ServerPlayerEntity player;
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
        return "DirectionHUD Player: "+this.getName();
    }
    public Player() {}
    public static Player of() {
        // creates a player object with a null inside for client use
        Player instance = new Player();
        instance.player = null;
        return instance;
    }
    public static Player of(@NotNull ServerPlayerEntity player) {
        Player instance = new Player();
        instance.player = player;
        return instance;
    }
    @Nullable
    public static Player of(String identifier) {
        Player instance = new Player();
        if (identifier.contains("-")) instance.player = DirectionHUD.server.getPlayerManager().getPlayer(UUID.fromString(identifier));
        else instance.player = DirectionHUD.server.getPlayerManager().getPlayer(identifier);
        if (instance.player == null) return null;
        return instance;
    }
    public void performCommand(String cmd) {
        try {
            ParseResults<ServerCommandSource> parse =
                    DirectionHUD.commandManager.getDispatcher().parse(cmd, player.getCommandSource());
            DirectionHUD.commandManager.getDispatcher().execute(parse);
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(CTxT message) {
        player.sendMessage(message.b());
    }
    public void sendActionBar(CTxT message) {
        player.sendMessage(message.b(),true);
    }
    // Call after toggling the hud.
    public void updateHUD() {
        // if toggled off
        if (!PlayerData.get.hud.state(this)) {
            //if actionbar send empty to clear else remove bossbar
            if (PlayerData.get.hud.setting.get(this,HUD.Setting.type).equals(HUD.Setting.DisplayType.actionbar.toString()))
                this.sendActionBar(CTxT.of(""));
            else DirectionHUD.bossBarManager.removePlayer(this);
        }
        if (PlayerData.get.hud.setting.get(this, HUD.Setting.type).equals(HUD.Setting.DisplayType.actionbar.toString()))
            DirectionHUD.bossBarManager.removePlayer(this);
        else this.sendActionBar(CTxT.of(""));
    }
    public void sendSettingPackets() {
        // if player has DirectionHUD on client, send a hashmap with data
        if (DirectionHUD.clientPlayers.contains(this)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            PacketBuilder packet = new PacketBuilder(gson.toJson(PlayerData.get.fromMap(this)));
            packet.sendToPlayer(Assets.packets.SETTINGS,player);
        }
    }
    public void sendHUDPackets(HashMap<HUD.Module, ArrayList<String>> hudData) {
        // send the instructions to build the hud to the client
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        PacketBuilder packet = new PacketBuilder(gson.toJson(hudData));
        packet.sendToPlayer(Assets.packets.HUD,player);
    }
    public void displayHUD(CTxT message) {
        if (message.toString().equals("")) {
            //if the HUD is enabled but there is no output
            if (PlayerData.getOneTime(this,"hud.enabled_but_off") == null) {
                PlayerData.setOneTime(this,"hud.enabled_but_off","true");
                if ((HUD.Setting.DisplayType.get((String) PlayerData.get.hud.setting.get(this, HUD.Setting.type)).equals(HUD.Setting.DisplayType.actionbar))) {
                    player.sendMessage(CTxT.of("").b(),true);
                } else {
                    DirectionHUD.bossBarManager.removePlayer(this);
                }
            }
            return;
        } else if (PlayerData.getOneTime(this,"hud.enabled_but_off") != null) {
            // if hud was in previous state and now isn't, remove the temp tag
            PlayerData.setOneTime(this,"hud.enabled_but_off",null);
        }
        if ((HUD.Setting.DisplayType.get((String) PlayerData.get.hud.setting.get(this, HUD.Setting.type)).equals(HUD.Setting.DisplayType.actionbar))) {
            player.sendMessage(message.b(),true);
        } else {
            DirectionHUD.bossBarManager.display(this,message);
        }
    }
    public String getName() {
        return player.getName().getString();
    }
    public ServerPlayerEntity getPlayer() {
        return player;
    }
    public String getUUID() {
        return player.getUuidAsString();
    }
    public String getDimension() {
        return Utl.dim.format(player.getWorld().getRegistryKey().getValue());
    }
    public String getSpawnDimension() {
        return Utl.dim.format(player.getSpawnPointDimension().getValue());
    }
    public float getYaw() {
        return player.getYaw();
    }
    public ArrayList<Double> getVec() {
        ArrayList<Double> vec = new ArrayList<>();
        vec.add(player.getX());
        vec.add(player.getY()+1);
        vec.add(player.getZ());
        return vec;
    }
    public Loc getLoc() {
        if (player == null) return new Loc();
        else return new Loc(this);
    }
    public int getBlockX() {
        return player.getBlockX();
    }
    public int getBlockY() {
        return player.getBlockY();
    }
    public int getBlockZ() {
        return player.getBlockZ();
    }
    public void spawnParticle(String particleType, Vec3d vec) {
        player.getServerWorld().spawnParticles(player,Utl.particle.getParticle(particleType,this),
                true,vec.getX(),vec.getY(),vec.getZ(),1,0,0,0,1);
    }
    public void spawnParticleLine(ArrayList<Double> end, String particleType) {
        Vec3d endVec = Utl.vec.convertTo(end);
        Vec3d pVec = player.getPos().add(0, 1, 0);
        if (player.getVehicle() != null) pVec.add(0,-0.2,0);
        double distance = pVec.distanceTo(endVec);
        Vec3d particlePos = pVec.subtract(0, 0.2, 0);
        double spacing = 1;
        Vec3d segment = endVec.subtract(pVec).normalize().multiply(spacing);
        double distCovered = 0;
        for (; distCovered <= distance; particlePos = particlePos.add(segment)) {
            distCovered += spacing;
            if (pVec.distanceTo(endVec) < 2) continue;
            if (distCovered >= 50) break;
            this.spawnParticle(particleType,particlePos);
        }
    }
}
