package one.oth3r.directionhud;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import one.oth3r.directionhud.common.Assets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class PacketBuilder {
    private final String message;
    private final PacketByteBuf packetByteBuf = PacketByteBufs.create();
    public PacketBuilder(ByteBuf buf) {
        // Read any data sent in the packet
        packetByteBuf.writeBytes(buf);
        message = packetByteBuf.toString(StandardCharsets.UTF_8);
    }
    public PacketBuilder(String message) {
        this.message = message;
        packetByteBuf.writeBytes(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
    }
    public static Identifier getIdentifier(Assets.packets packetType) {
        return new Identifier(DirectionHUD.MOD_ID, packetType.getIdentifier());
    }
    public void sendToPlayer(Assets.packets packetType, ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, getIdentifier(packetType), packetByteBuf);
    }
    public void sendToServer(Identifier identifier) {
        ClientPlayNetworking.send(identifier, packetByteBuf);
    }
    public String getMessage() {
        return this.message;
    }
}
