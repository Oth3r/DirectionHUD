package one.oth3r.directionhud.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
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

        public static final PacketCodec<PacketByteBuf, Initialization> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new Initialization(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Id<Initialization> getId() {
            return ID;
        }
    }

    public record HUD(String value) implements CustomPayload {
        public static final CustomPayload.Id<HUD> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.HUD.getIdentifier()));

        public static final PacketCodec<PacketByteBuf, HUD> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new HUD(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Id<HUD> getId() {
            return ID;
        }
    }

    public record PlayerData(String value) implements CustomPayload {
        public static final CustomPayload.Id<PlayerData> ID = new CustomPayload.Id<>(
                new Identifier(DirectionHUD.MOD_ID,Assets.packets.PLAYER_DATA.getIdentifier()));

        public static final PacketCodec<PacketByteBuf, PlayerData> CODEC = PacketCodec.of(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new PlayerData(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Id<PlayerData> getId() {
            return ID;
        }
    }
}
