package one.oth3r.directionhud;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import one.oth3r.directionhud.utils.ModEvents;
import one.oth3r.directionhud.utils.Player;

public class DirectionHUDClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModEvents.registerClient();
    }

    public static Player getPlayerFromClient(MinecraftClient client) {
        return new Player(client.player, true);
    }
}
