package one.oth3r.directionhud.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import one.oth3r.directionhud.common.Assets.packets;

public class PacketSender {

    private final String data;
    private final packets type;

    public PacketSender(packets type, String data) {
        this.data = data;
        this.type = type;
    }

    public void sendToPlayer(ServerPlayer player) {
        ServerPlayNetworking.send(player,getPayload());
    }

    public void sendToServer() {
        ClientPlayNetworking.send(getPayload());
    }

    private CustomPacketPayload getPayload() {
        return switch (type) {
            case INITIALIZATION -> new Payloads.Initialization(data);
            case HUD -> new Payloads.HUD(data);
            default -> new Payloads.PlayerData(data);
        };
    }
}
