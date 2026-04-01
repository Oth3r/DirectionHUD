package one.oth3r.directionhud;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import one.oth3r.directionhud.utils.ModEvents;
import one.oth3r.directionhud.utils.DPlayer;

public class DirectionHUDClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModEvents.registerClient();
    }

    public static DPlayer getPlayerFromClient(Minecraft client) {
        return new DPlayer(client.player, true);
    }
}
