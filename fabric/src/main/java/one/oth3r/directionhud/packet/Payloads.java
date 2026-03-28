package one.oth3r.directionhud.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.resources.Identifier;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Payloads {

    public record Initialization(String value) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<Initialization> ID = new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(DirectionHUD.MOD_ID,Assets.packets.INITIALIZATION.getIdentifier()));

        public static final StreamCodec<RegistryFriendlyByteBuf, Initialization> CODEC = ByteBufCodecs.STRING_UTF8.map(Initialization::new, Initialization::value).cast();

        @Override
        public Type<Initialization> type() {
            return ID;
        }
    }

    public record HUD(String value) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<HUD> ID = new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(DirectionHUD.MOD_ID,Assets.packets.HUD.getIdentifier()));

        public static final StreamCodec<RegistryFriendlyByteBuf, HUD> CODEC = ByteBufCodecs.STRING_UTF8.map(HUD::new, HUD::value).cast();

        @Override
        public Type<HUD> type() {
            return ID;
        }
    }

    public record PlayerData(String value) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<PlayerData> ID = new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(DirectionHUD.MOD_ID,Assets.packets.SPIGOT_PLAYER_DATA.getIdentifier()));

        public static final StreamCodec<RegistryFriendlyByteBuf, PlayerData> CODEC = ByteBufCodecs.STRING_UTF8.map(PlayerData::new, PlayerData::value).cast();

        @Override
        public Type<PlayerData> type() {
            return ID;
        }
    }

    // SPIGOT

    public record SpigotHUD(String value) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SpigotHUD> ID = new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(DirectionHUD.MOD_ID,Assets.packets.SPIGOT_HUD.getIdentifier()));

        public static final StreamCodec<FriendlyByteBuf, SpigotHUD> CODEC = StreamCodec.ofMember(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new SpigotHUD(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Type<SpigotHUD> type() {
            return ID;
        }
    }

    public record SpigotPlayerData(String value) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<SpigotPlayerData> ID = new CustomPacketPayload.Type<>(
                Identifier.fromNamespaceAndPath(DirectionHUD.MOD_ID,Assets.packets.PLAYER_DATA.getIdentifier()));

        public static final StreamCodec<FriendlyByteBuf, SpigotPlayerData> CODEC = StreamCodec.ofMember(
                (value, buf) -> buf.writeBytes(ByteBuffer.wrap(value.value.getBytes(StandardCharsets.UTF_8))),
                buf -> new SpigotPlayerData(new String(buf.readByteArray(), StandardCharsets.UTF_8)));

        @Override
        public Type<SpigotPlayerData> type() {
            return ID;
        }
    }
}
