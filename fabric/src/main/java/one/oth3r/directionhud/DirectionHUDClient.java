package one.oth3r.directionhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.utils.Player;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectionHUDClient implements ClientModInitializer {
    public static boolean singleplayer = false;
    public static boolean onSupportedServer = false;
    private static KeyBinding keyBinding;
    public static Text override = Text.of("");
    public static int overrideCd = 0;
    @Override
    public void onInitializeClient() {
        DirectionHUD.isClient = true;
        //CLIENT ONLY
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.directionhud.keybind.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.directionhud.all"
        ));
        // client tick loop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            //todo move to a client helper class
            assert client.player != null;
            // send a toggle hud command when key is pressed
            while (keyBinding.wasPressed()) {
                if (onSupportedServer) {
                    client.player.networkHandler.sendCommand("hud toggle");
                }
            }
            // only loop if on a directionHUD server
            if (onSupportedServer) {
                // tick down the override actionbar (every tick) if there is one
                if (overrideCd > 0) overrideCd -= 1;
                if (!client.isInSingleplayer()) {
                    //update the rainbow if not in single-player as it doesn't move
                    LoopManager.rainbowF += 10;
                    if (LoopManager.rainbowF >= 360) LoopManager.rainbowF = 0;
                }
            }
        });
        //PACKETS
        PayloadTypeRegistry.playS2C().register(Payloads.Settings.ID, Payloads.Settings.CODEC);
        // receiving setting packets from the server
        ClientPlayNetworking.registerGlobalReceiver(Payloads.Settings.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                Type hashMapToken = new TypeToken<HashMap<String, Object>>() {}.getType();
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                // if not single player store the payload in local playerdata (otherwise it doesn't need to be saved)
                if (!client.isInSingleplayer()) PlayerData.playerMap.put(Player.of(),gson.fromJson(payload.value(), hashMapToken));
                onSupportedServer = true;
            });
        });
        PayloadTypeRegistry.playS2C().register(Payloads.HUD.ID, Payloads.HUD.CODEC);
        // receiving HUD packets from the server
        ClientPlayNetworking.registerGlobalReceiver(Payloads.HUD.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                Type hashMapToken = new TypeToken<HashMap<HUD.Module, ArrayList<String>>>() {}.getType();
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                // if there is no actionbar override, build and send the HUD
                if (overrideCd <= 0) {
                    context.player().sendMessage(HUD.build.compile(getClientPlayer(client), gson.fromJson(payload.value(), hashMapToken)).b(), true);
                }
            });
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isInSingleplayer()) singleplayer = true;
            // send an initialization packet whenever joining a server
            client.execute(() -> {
                ClientPlayNetworking.send(new Payloads.Initialization("Hello from DirectionHUD client!"));
            });
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            singleplayer = false;
            onSupportedServer = false;
            PlayerData.playerMap.clear();
        });
    }
    public static Player getClientPlayer(MinecraftClient client) {
        //if in single-player, use the server to get a ServerPlayerEntity
        // else, just use a null as there is only one player using the code
        Player player = Player.of();
        if (client.isInSingleplayer() && client.player != null)
            player = Player.of(client.player.getUuidAsString());
        return player;
    }
}
