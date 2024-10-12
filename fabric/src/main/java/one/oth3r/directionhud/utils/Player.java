package one.oth3r.directionhud.utils;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.utils.Vec;
import one.oth3r.directionhud.packet.PacketSender;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.template.PlayerTemplate;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.common.utils.Loc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class Player extends PlayerTemplate {
    private final PlayerEntity player;
    private final ServerPlayerEntity serverPlayer;
    private final boolean client;

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

    public Player() {
        player = null;
        serverPlayer = null;
        client = false;
    }

    /**
     * load a client player entity for client work
     */
    public Player(PlayerEntity playerEntity, boolean cpl) {
        player = playerEntity;
        serverPlayer = null;
        client = cpl;
    }

    public Player(ServerPlayerEntity serverPlayerEntity) {
        player = serverPlayerEntity;
        serverPlayer = serverPlayerEntity;
        client = false;
    }

    public Player(String identifier) {
        if (identifier.contains("-")) serverPlayer = DirectionHUD.server.getPlayerManager().getPlayer(UUID.fromString(identifier));
        else serverPlayer = DirectionHUD.server.getPlayerManager().getPlayer(identifier);
        player = serverPlayer;
        client = false;
    }

    @Override
    public boolean isValid() {
        return serverPlayer != null;
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

    @Override
    public void sendMessage(CTxT message) {
        player.sendMessage(message.b());
    }

    @Override
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
    public PData getPData() {
        return PlayerData.getPData(this);
    }

    @Override
    public CachedPData getPCache() {
        return PlayerData.getPCache(this);
    }

    @Override
    public void sendPDataPackets() {
        if (DirectionHUD.clientPlayers.contains(this) && !client) {
            new PacketSender(Assets.packets.PLAYER_DATA,Helper.getGson().toJson(getPData())).sendToPlayer(serverPlayer);
        }
    }

    @Override
    public void sendHUDPackets(HashMap<Hud.Module, ArrayList<String>> hudData) {
        if (client) return;
        // send the instructions to build the hud to the client
        new PacketSender(Assets.packets.HUD,Helper.getGson().toJson(hudData)).sendToPlayer(serverPlayer);
    }

    @Override
    public String getName() {
        return player.getName().getString();
    }

    public ServerPlayerEntity getPlayer() {
        return serverPlayer;
    }

    @Override
    public String getUUID() {
        return player.getUuidAsString();
    }

    @Override
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

    @Override
    public String getSpawnDimension() {
        if (client) return null;
        return Utl.dim.format(serverPlayer.getSpawnPointDimension());
    }

    @Override
    public float getYaw() {
        return player.getYaw();
    }

    @Override
    public float getPitch() {
        return player.getPitch();
    }

    @Override
    public Vec getVec() {
        // todo the old vec added 1 to the y, make sure nothing bad happens plz
        return new Vec(player.getX(),player.getY(),player.getZ());
    }
    @Override
    public Loc getLoc() {
        if (player == null) return new Loc();
        else return new Loc(this);
    }

    @Override
    public int getBlockX() {
        return player.getBlockX();
    }

    @Override
    public int getBlockY() {
        return player.getBlockY();
    }

    @Override
    public int getBlockZ() {
        return player.getBlockZ();
    }

    public void spawnParticle(String particleType, Vec3d vec) {
        serverPlayer.getServerWorld().spawnParticles(serverPlayer,Utl.particle.getParticle(particleType,this),
                true,vec.getX(),vec.getY(),vec.getZ(),1,0,0,0,1);
    }

    @Override
    public void spawnParticleLine(Vec target, String particleType) {
        Vec3d endVec = new Vec3d(target.getX(),target.getY(),target.getZ());
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
