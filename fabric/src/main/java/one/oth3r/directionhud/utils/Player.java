package one.oth3r.directionhud.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.util.math.Vec3d;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.module.ModuleInstructions;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.packet.PacketSender;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.template.PlayerTemplate;
import org.joml.Vector3f;

import java.awt.*;
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
        PlayerManager playerManager = DirectionHUD.getData().getServer().getPlayerManager();
        if (identifier.contains("-")) serverPlayer = playerManager.getPlayer(UUID.fromString(identifier));
        else serverPlayer = playerManager.getPlayer(identifier);
        player = serverPlayer;
        client = false;
    }

    @Override
    public boolean isValid() {
        return serverPlayer != null;
    }

    public void performCommand(String cmd) {
        try {
            CommandDispatcher<ServerCommandSource> dispatcher = DirectionHUD.getData().getCommandManager().getDispatcher();
            ParseResults<ServerCommandSource> parse = dispatcher.parse(cmd, serverPlayer.getCommandSource());
            dispatcher.execute(parse);
        } catch (CommandSyntaxException e) {
            DirectionHUD.LOGGER.info("ERROR EXECUTING COMMAND - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }

    @Override
    public void sendMessage(CTxT message) {
        player.sendMessage(message.b(), false);
    }

    @Override
    public void sendActionBar(CTxT message) {
        player.sendMessage(message.b(),true);
    }

    @Override
    public void displayBossBar(CTxT message) {
        DirectionHUD.getData().getBossBarManager().display(this,message);
    }

    @Override
    public void removeBossBar() {
        DirectionHUD.getData().getBossBarManager().removePlayer(this);
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
        if (DirectionHUD.getData().getClientPlayers().contains(this) && !client) {
            new PacketSender(Assets.packets.PLAYER_DATA,Helper.getGson().toJson(getPData())).sendToPlayer(serverPlayer);
        }
    }

    @Override
    public void sendHUDPackets(ModuleInstructions instructions) {
        if (client) return;
        // send the instructions to build the hud to the client
        new PacketSender(Assets.packets.HUD,Helper.getGson().toJson(instructions)).sendToPlayer(serverPlayer);
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
    public long getWorldTime() {
        return player.getWorld().getTime();
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

    /**
     * gets the light level.
     *
     * @param lookTarget if enabled, it will get the light level of the next closest target after the player's target-look block
     * @return an int array, 2 in length, first entry for the skylight, second entry for the block light.
     */
    @Override
    public int[] getLightLevels(boolean lookTarget) {
        BlockPos pos = player.getBlockPos();
        // try to get the look target if possible
        if (lookTarget) {
            pos = Utl.getSideOfBlockPosPlayerIsLookingAt(serverPlayer, Utl.getPlayerReach(player));
            if (pos == null) return new int[]{-1,-1};
        }

        return new int[]{player.getWorld().getLightLevel(LightType.SKY,pos),player.getWorld().getLightLevel(LightType.BLOCK,pos)};
    }

    @Override
    public Vec getVec() {
        return new Vec(player.getX(),player.getY(),player.getZ());
    }

    @Override
    public Loc getLoc() {
        if (player == null) return new Loc();
        else return new Loc(this);
    }

    /// particles
    public DustParticleEffect getParticle(ParticleType particleType) {
        String color;
        float scale;

        switch (particleType) {
            case DEST -> {
                color = this.getPCache().getDEST().getDestSettings().getParticles().getDestColor();
                scale = 3;
            }
            case LINE -> {
                color = this.getPCache().getDEST().getDestSettings().getParticles().getLineColor();
                scale = 1;
            }
            case TRACKING -> {
                color = this.getPCache().getDEST().getDestSettings().getParticles().getTrackingColor();
                scale = 0.5F;
            }
            default -> {
                color = "#000000";
                scale = 1;
            }
        }

        return new DustParticleEffect(new Vector3f(Vec3d.unpackRgb(Color.decode(CUtl.color.format(color)).getRGB()).toVector3f()), scale);
    }

    @Override
    public void spawnParticle(ParticleType particleType, Vec position) {
        serverPlayer.getServerWorld().spawnParticles(serverPlayer,getParticle(particleType),
                true,position.getX(),position.getY(),position.getZ(),1,0,0,0,1);
    }
}
