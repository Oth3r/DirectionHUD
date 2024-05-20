package one.oth3r.directionhud.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import one.oth3r.directionhud.common.Assets.packets;

public class PacketSender {

    private final String data;
    private final packets type;

    public PacketSender(packets type, String data) {
        this.data = data;
        this.type = type;
    }

    public void sendToPlayer(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player,getPayload());
    }

    public void sendToServer() {
        ClientPlayNetworking.send(getPayload());
    }

    private CustomPayload getPayload() {
        return switch (type) {
            case INITIALIZATION -> new Payloads.Initialization(data);
            case HUD -> new Payloads.HUD(data);
            default -> new Payloads.PlayerData(data);
        };
    }
}
