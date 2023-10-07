package one.oth3r.directionhud;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import one.oth3r.directionhud.common.Assets;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.HashMap;

public class DirectionHUDClient implements ClientModInitializer {
    public static boolean onSupportedServer = false;
    public static HashMap<String, Object> packetData = new HashMap<>();
    private static KeyBinding keyBinding;
    @Override
    public void onInitializeClient() {
        DirectionHUD.isClient = true;
        DirectionHUD.initializeCommon();
        //CLIENT ONLY
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.directionhud.keybind.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.directionhud.all"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                assert client.player != null;
                if (client.isInSingleplayer() || onSupportedServer) {
                    client.player.networkHandler.sendCommand("hud toggle");
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.getIdentifier(Assets.packets.SETTINGS), (client, handler, buf, responseSender) -> {
            // receiving data packets from the server
            PacketBuilder packet = new PacketBuilder(buf);
            assert client.player != null;
            Type arrayListMap = new TypeToken<HashMap<String, Object>>() {}.getType();
            client.execute(() -> packetData = new Gson().fromJson(packet.getMessage(), arrayListMap));
            onSupportedServer = true;
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            // send an initialization packet whenever joining a server
            client.execute(() -> {
                PacketBuilder sPacket = new PacketBuilder("Hello from DirectionHUD client!");
                sPacket.sendToServer(PacketBuilder.getIdentifier(Assets.packets.INITIALIZATION));
            });
        });
    }
}
