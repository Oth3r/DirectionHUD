package one.oth3r.directionhud.utils;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.players.PlayerList;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.hud.module.ModuleInstructions;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.packet.PacketSender;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.template.PlayerTemplate;

import java.awt.*;
import java.util.Objects;
import java.util.UUID;

public class DPlayer extends PlayerTemplate {
    private final net.minecraft.world.entity.player.Player player;
    private final ServerPlayer serverPlayer;
    private final boolean client;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DPlayer other = (DPlayer) obj;
        return Objects.equals(player, other.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    public DPlayer() {
        player = null;
        serverPlayer = null;
        client = false;
    }

    /**
     * load a client player entity for client work
     */
    public DPlayer(net.minecraft.world.entity.player.Player playerEntity, boolean cpl) {
        player = playerEntity;
        serverPlayer = null;
        client = cpl;
    }

    public DPlayer(ServerPlayer serverPlayerEntity) {
        player = serverPlayerEntity;
        serverPlayer = serverPlayerEntity;
        client = false;
    }

    public DPlayer(String identifier) {
        PlayerList playerManager = DirectionHUD.getData().getServer().getPlayerList();
        if (identifier.contains("-")) serverPlayer = playerManager.getPlayer(UUID.fromString(identifier));
        else serverPlayer = playerManager.getPlayerByName(identifier);
        player = serverPlayer;
        client = false;
    }

    @Override
    public boolean isValid() {
        return serverPlayer != null;
    }

    public void performCommand(String cmd) {
        try {
            CommandDispatcher<CommandSourceStack> dispatcher = DirectionHUD.getData().getCommandManager().getDispatcher();
            ParseResults<CommandSourceStack> parse = dispatcher.parse(cmd, serverPlayer.createCommandSourceStack());
            dispatcher.execute(parse);
        } catch (CommandSyntaxException e) {
            DirectionHUD.LOGGER.info("ERROR EXECUTING COMMAND - PLEASE REPORT WITH THE ERROR LOG");
            DirectionHUD.LOGGER.info(e.getMessage());
        }
    }

    @Override
    public void sendMessage(CTxT message) {
        player.sendSystemMessage(message.b());
    }

    @Override
    public void sendActionBar(CTxT message) {
        player.sendOverlayMessage(message.b());
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

    public ServerPlayer getPlayer() {
        return serverPlayer;
    }

    @Override
    public String getUUID() {
        return player.getStringUUID();
    }

    @Override
    public String getDimension() {
        return Utl.dim.format(player.level().dimension());
    }

    @Override
    public int getTimeOfDay() {
        return (int) player.level().getOverworldClockTime() % 24000;
    }

    @Override
    public long getWorldTime() {
        return player.level().getGameTime();
    }

    @Override
    public boolean hasStorm() {
        return player.level().isRaining();
    }

    @Override
    public boolean hasThunderstorm() {
        return player.level().isThundering();
    }

    @Override
    public String getSpawnDimension() {
        if (client) return null;
        ServerPlayer.RespawnConfig respawn = serverPlayer.getRespawnConfig();
        if (respawn == null) return Utl.dim.format(Level.OVERWORLD);
        return Utl.dim.format(respawn.respawnData().dimension());
    }

    @Override
    public float getYaw() {
        return player.getYRot();
    }

    @Override
    public float getPitch() {
        return player.getXRot();
    }

    /**
     * gets the light level.
     *
     * @param lookTarget if enabled, it will get the light level of the next closest target after the player's target-look block
     * @return an int array, 2 in length, first entry for the skylight, second entry for the block light.
     */
    @Override
    public int[] getLightLevels(boolean lookTarget) {
        BlockPos pos = player.blockPosition();
        // try to get the look target if possible
        if (lookTarget) {
            pos = Utl.getSideOfBlockPosPlayerIsLookingAt(serverPlayer, Utl.getPlayerReach(player));
            if (pos == null) return new int[]{-1,-1};
        }

        return new int[]{player.level().getBrightness(LightLayer.SKY,pos),player.level().getBrightness(LightLayer.BLOCK,pos)};
    }

    /**
     * gets a Vec to the players body level, not their feet
     */
    @Override
    public Vec getVec() {
        double adjustment = 1;
        if (player.getVehicle() != null) adjustment += .45;
        else if (player.isVisuallyCrawling() || player.isSwimming()) adjustment -= .65;
        else if (player.isFallFlying()) adjustment -= 1;
        return new Vec(player.getX(),player.getY()+adjustment,player.getZ());
    }

    @Override
    public Loc getLoc() {
        if (player == null) return new Loc();
        else return new Loc(this);
    }

    /// particles
    public DustParticleOptions getParticle(ParticleType particleType) {
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

        return new DustParticleOptions(Color.decode(CUtl.color.format(color)).getRGB(), scale);
    }

    @Override
    public void spawnParticle(ParticleType particleType, Vec position) {
        serverPlayer.level().sendParticles(serverPlayer,getParticle(particleType),
                true,true,position.getX(),position.getY(),position.getZ(),1,0,0,0,1);
    }
}
