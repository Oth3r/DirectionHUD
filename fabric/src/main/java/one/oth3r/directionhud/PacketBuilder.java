package one.oth3r.directionhud;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class PacketBuilder implements Packet<PacketListener> {
    public static final Identifier INITIALIZATION_PACKET = new Identifier(DirectionHUD.MOD_ID, "initialization_packet");
    public static final Identifier HUD_STATE = new Identifier(DirectionHUD.MOD_ID, "hud_state_packet");
    private final String message;
    public PacketBuilder(PacketByteBuf buf) {
        // Read any data sent in the packet
        message = buf.readString(32767);
    }
    public PacketBuilder(String message) {
        this.message = message;
    }
    @Override
    public void write(PacketByteBuf buf) {
        buf.writeString(message);
    }
    @Override
    public void apply(PacketListener listener) {
    }

    public void sendToPlayer(Identifier identifier, ServerPlayerEntity player) {
        PacketBuilder packet = new PacketBuilder(message);
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        ServerPlayNetworking.send(player, identifier, buf);
    }
    public void sendToServer(Identifier identifier) {
        PacketBuilder packet = new PacketBuilder(message);
        PacketByteBuf buf = PacketByteBufs.create();
        packet.write(buf);
        ClientPlayNetworking.send(identifier, buf);
    }
    public String getMessage() {
        return this.message;
    }
}
