package one.oth3r.directionhud.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Payloads {

    public record Initialization(String value) implements CustomPayload {
        public static final CustomPayload.Id<Initialization> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.INITIALIZATION.getIdentifier()));

        public static final PacketCodec<RegistryByteBuf, Initialization> CODEC = PacketCodecs.STRING.xmap(Initialization::new, Initialization::value).cast();

        @Override
        public Id<Initialization> getId() {
            return ID;
        }
    }

    public record HUD(String value) implements CustomPayload {
        public static final CustomPayload.Id<HUD> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.HUD.getIdentifier()));

        public static final PacketCodec<RegistryByteBuf, HUD> CODEC = PacketCodecs.STRING.xmap(HUD::new, HUD::value).cast();

        @Override
        public Id<HUD> getId() {
            return ID;
        }
    }

    public record PlayerData(String value) implements CustomPayload {
        public static final CustomPayload.Id<PlayerData> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.SPIGOT_PLAYER_DATA.getIdentifier()));

        public static final PacketCodec<RegistryByteBuf, PlayerData> CODEC = PacketCodecs.STRING.xmap(PlayerData::new, PlayerData::value).cast();

        @Override
        public Id<PlayerData> getId() {
            return ID;
        }
    }

    // SPIGOT

    public record SpigotHUD(String value) implements CustomPayload {
        public static final CustomPayload.Id<SpigotHUD> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.SPIGOT_HUD.getIdentifier()));

        public static final PacketCodec<PacketByteBuf, SpigotHUD> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new SpigotHUD(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Id<SpigotHUD> getId() {
            return ID;
        }
    }

    public record SpigotPlayerData(String value) implements CustomPayload {
        public static final CustomPayload.Id<SpigotPlayerData> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.PLAYER_DATA.getIdentifier()));

        public static final PacketCodec<PacketByteBuf, SpigotPlayerData> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new SpigotPlayerData(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Id<SpigotPlayerData> getId() {
            return ID;
        }
    }
}
