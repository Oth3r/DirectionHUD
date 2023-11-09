package one.oth3r.directionhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.HUD;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.PlayerData;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.Player;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectionHUDClient implements ClientModInitializer {
    public static boolean singleplayer = false;
    public static boolean onSupportedServer = false;
    public static HashMap<String, Object> packetData = new HashMap<>();
    public static HashMap<HUD.Module, ArrayList<String>> hudData;
    private static KeyBinding keyBinding;
    private static int HUDRefresh;
    public static Text override = Text.of("");
    public static int overrideCd = 0;
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
            // loop if on a directionHUD server
            if (onSupportedServer) {
                Player player = getClientPlayer(client);
                // tick down the override actionbar (every tick) if there is one
                if (overrideCd > 0) overrideCd -= 1;
                // hud loop
                if (!client.isInSingleplayer()) {
                    //update the rainbow if not in singleplayer as it doesn't
                    LoopManager.rainbowF += 10;
                    if (LoopManager.rainbowF >= 360) LoopManager.rainbowF = 0;
                }
                if (hudData != null && (boolean)PlayerData.get.hud.setting.get(player, HUD.Setting.state) &&
                        HUD.Setting.DisplayType.get((String) PlayerData.get.hud.setting.get(player, HUD.Setting.type)).equals(HUD.Setting.DisplayType.actionbar)) {
                    // make sure there is HUD data before looping, and that the hud type is an actionbar
                    HUDRefresh++;
                    if (HUDRefresh >= config.HUDLoop) {
                        HUDRefresh = 0;
                        // refreshes based on the client's config setting (maybe fix later)
                        // if there is no actionbar override, build and send the HUD
                        if (overrideCd <= 0) client.player.sendMessage(HUD.build(player,hudData).b(),true);
                    }
                }
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.getIdentifier(Assets.packets.SETTINGS), (client, handler, buf, responseSender) -> {
            // receiving data packets from the server
            PacketBuilder packet = new PacketBuilder(buf);
            assert client.player != null;
            client.execute(() -> {
                Type hashMapToken = new TypeToken<HashMap<String, Object>>() {}.getType();
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                packetData = gson.fromJson(packet.getMessage(), hashMapToken);
                DirectionHUD.LOGGER.info(packet.getMessage());
                if (!client.isInSingleplayer()) PlayerData.playerMap.put(Player.of(),packetData);
                onSupportedServer = true;
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.getIdentifier(Assets.packets.HUD), (client, handler, buf, responseSender) -> {
            // receiving data packets from the server
            PacketBuilder packet = new PacketBuilder(buf);
            assert client.player != null;
            client.execute(() -> {
                Type hashMapToken = new TypeToken<HashMap<HUD.Module, ArrayList<String>>>() {}.getType();
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                hudData = gson.fromJson(packet.getMessage(), hashMapToken);
            });
        });
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isInSingleplayer()) singleplayer = true;
            // send an initialization packet whenever joining a server
            client.execute(() -> {
                PacketBuilder sPacket = new PacketBuilder("Hello from DirectionHUD client!");
                sPacket.sendToServer(PacketBuilder.getIdentifier(Assets.packets.INITIALIZATION));
            });
        });
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            singleplayer = false;
            onSupportedServer = false;
            packetData = new HashMap<>();
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
