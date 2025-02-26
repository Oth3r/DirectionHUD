package one.oth3r.directionhud;

import net.fabricmc.api.ClientModInitializer;
import one.oth3r.directionhud.utils.ModEvents;

public class DirectionHUDClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ModEvents.registerClient();
    }
}
