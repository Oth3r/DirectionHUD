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
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.template.PlayerTemplate;
import one.oth3r.directionhud.common.utils.Loc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Player extends PlayerTemplate {
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
            DirectionHUD.LOGGER.info("ERROR EXECUTING COMMAND - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }
    public void sendMessage(CTxT message) {
        player.sendMessage(message.b());
    }
    public void sendActionBar(CTxT message) {
        player.sendMessage(message.b(),true);
    }

    @Override
    public void displayBossBar(CTxT message) {
        DirectionHUD.bossBarManager.display(this,message);
    }
    @Override
    public void removeBossBar() {
        DirectionHUD.bossBarManager.removePlayer(this);
    }
    @Override
    public void sendPDataPackets() {
        if (DirectionHUD.clientPlayers.contains(this)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            PacketBuilder packet = new PacketBuilder(gson.toJson(getPData()));
            packet.sendToPlayer(Assets.packets.PLAYER_DATA,player);
        }
    }
    public void sendHUDPackets(HashMap<Hud.Module, ArrayList<String>> hudData) {
        // send the instructions to build the hud to the client
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        PacketBuilder packet = new PacketBuilder(gson.toJson(hudData));
        packet.sendToPlayer(Assets.packets.HUD,player);
    }
    public String getName() {
        return player.getName().getString();
    }
    public PData getPData() {
        return PlayerData.getPData(this);
    }
    public ServerPlayerEntity getPlayer() {
        return player;
    }
    public String getUUID() {
        return player.getUuidAsString();
    }
    public String getDimension() {
        return Utl.dim.format(player.getWorld().getRegistryKey());
    }

    @Override
    public int getTimeOfDay() {
        return (int) player.getWorld().getTimeOfDay() % 24000;
    }

    @Override
    public boolean hasStorm() {
        return player.getWorld().isRaining();
    }

    @Override
    public boolean hasThunderstorm() {
        return player.getWorld().isThundering();
    }

    public String getSpawnDimension() {
        return Utl.dim.format(player.getSpawnPointDimension());
    }
    public float getYaw() {
        return player.getYaw();
    }
    public float getPitch() {
        return player.getPitch();
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
