package one.oth3r.directionhud;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import one.oth3r.directionhud.common.Assets;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class PacketBuilder {
    private final String message;
    private PacketByteBuf packetByteBuf = PacketByteBufs.create();
    public PacketBuilder(PacketByteBuf buf) {
        // Read any data sent in the packet
        message = Charset.defaultCharset().decode(ByteBuffer.wrap(buf.array())).toString().trim();
        packetByteBuf = buf;
    }
    public PacketBuilder(String message) {
        this.message = message;
        packetByteBuf.writeBytes(ByteBuffer.wrap(message.getBytes(Charset.defaultCharset())).array());
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
