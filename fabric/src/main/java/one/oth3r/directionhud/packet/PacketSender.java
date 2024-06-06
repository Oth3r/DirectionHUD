package one.oth3r.directionhud.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Assets.packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketSender {

    private final PacketByteBuf data;
    private final packets type;

    public PacketSender(packets type, String data) {
        this.type = type;
        this.data = PacketByteBufs.create()
                .writeBytes(ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8)));
    }

    public void sendToPlayer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player,getIdentifier(type),data);
    }

    public void sendToServer() {
        ClientPlayNetworking.send(getIdentifier(type),data);
    }

    public static Identifier getIdentifier(Assets.packets packetType) {
        return new Identifier(DirectionHUD.MOD_ID, packetType.getIdentifier());
    }
}
