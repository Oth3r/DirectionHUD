package one.oth3r.directionhud.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.md_5.bungee.api.ChatMessageType;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.PacketHelper;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.hud.module.ModuleInstructions;
import one.oth3r.directionhud.common.utils.*;
import one.oth3r.directionhud.common.template.PlayerTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.RayTraceResult;

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

    public Player(org.bukkit.entity.Player bukkitPlayer) {
        player = bukkitPlayer;
    }

    public Player(String identifier) {
        if (identifier.contains("-")) player = Bukkit.getPlayer(UUID.fromString(identifier));
        else player = Bukkit.getPlayer(identifier);
    }

    @Override
    public boolean isValid() {
        return player != null;
    }

    @Override
    public void performCommand(String cmd) {
        player.performCommand(cmd);
    }

    @Override
    public void sendMessage(CTxT message) {
        player.spigot().sendMessage(message.b());
    }

    @Override
    public void sendActionBar(CTxT message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message.b());
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
    public void sendPDataPackets() {
        // if player has DirectionHUD on client, send pData to client
        if (DirectionHUD.getData().getClientPlayers().contains(this)) {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            PacketHelper.sendPacket(this,Assets.packets.PLAYER_DATA,gson.toJson(this.getPData()));
        }
    }

    @Override
    public void sendHUDPackets(ModuleInstructions instructions) {
        PacketHelper.sendPacket(this, Assets.packets.HUD, Helper.getGson().toJson(instructions));
    }

    @Override
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

    @Override
    public String getUUID() {
        return player.getUniqueId().toString();
    }

    @Override
    public String getSpawnDimension() {
        if (player.getRespawnLocation() != null && player.getRespawnLocation().getWorld() != null) {
            return Utl.dim.format(player.getRespawnLocation().getWorld());
        }
        return Utl.dim.format(Bukkit.getWorlds().get(0));
    }

    @Override
    public String getDimension() {
        return Utl.dim.format(player.getWorld());
    }

    @Override
    public int getTimeOfDay() {
        return (int) player.getWorld().getTime() % 24000;
    }

    @Override
    public long getWorldTime() {
        // this is also not correct, but will fix later todo
        return player.getWorld().getTime();
    }

    @Override
    public boolean hasStorm() {
        return player.getWorld().hasStorm();
    }

    @Override
    public boolean hasThunderstorm() {
        return player.getWorld().isThundering();
    }

    /**
     * gets the light level.
     *
     * @param lookTarget if enabled, it will get the light level of the next closest target after the player's target-look block
     * @return an int array, 2 in length, first entry for the skylight, second entry for the block light. -1 is returned if not found
     */
    @Override
    public int[] getLightLevels(boolean lookTarget) {
        Location location = player.getLocation();

        // if getting the look target
        if (lookTarget) {
            // ray trace
            RayTraceResult result = player.rayTraceBlocks(5);
            // if possible
            if (result != null && result.getHitBlock() != null) {
                Block block = result.getHitBlock();
                // if not passible, the block that is the hit face
                if (!block.isPassable()) {
                    BlockFace face = result.getHitBlockFace();
                    // if null, null, but else get the location
                    if (face == null) location = null;
                    else {
                        location = block.getRelative(face).getLocation();
                    }
                }
                // else get the passible block location
                else location = block.getLocation();
            }
            // if not possible, null
            else location = null;

            // if null return bad data
            if (location == null) return new int[]{-1,-1};
        }

        // return the gathered data
        return new int[]{
                location.getBlock().getLightFromSky(),
                location.getBlock().getLightFromBlocks()
        };
    }

    @Override
    public float getYaw() {
        return player.getLocation().getYaw();
    }

    @Override
    public float getPitch() {
        return player.getLocation().getPitch();
    }

    @Override
    public Vec getVec() {
        return new Vec(player.getLocation().getX(),player.getLocation().getY(),player.getLocation().getZ());
    }

    @Override
    public Loc getLoc() {
        if (isValid()) return new Loc(new Player(player));
        else return new Loc();
    }

    public org.bukkit.entity.Player getPlayer() {
        return player;
    }

    @Override
    public void spawnParticle(ParticleType particleType, Vec position) {
        player.spawnParticle(Particle.DUST,position.getX(),position.getY(),position.getZ(),1,getParticle(particleType));
    }

    public Particle.DustOptions getParticle(ParticleType particleType) {
        String color;
        float size;

        switch (particleType) {
            case DEST -> {
                color = this.getPData().getDEST().getSetting().getParticles().getDestColor();
                size = 3;
            }
            case LINE -> {
                color = this.getPData().getDEST().getSetting().getParticles().getLineColor();
                size = 1;
            }
            case TRACKING -> {
                color = this.getPData().getDEST().getSetting().getParticles().getTrackingColor();
                size = 0.5f;
            }
            default -> {
                color = "#000000";
                size = 1;
            }
        }

        int[] rgb = CUtl.color.RGB(color);
        // return the particle options
        return new Particle.DustOptions(Color.fromRGB(rgb[0],rgb[1],rgb[2]), size);
    }
}
