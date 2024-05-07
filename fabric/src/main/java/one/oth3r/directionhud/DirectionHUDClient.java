package one.oth3r.directionhud;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Hud;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.utils.Player;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectionHUDClient implements ClientModInitializer {
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

        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.getIdentifier(Assets.packets.PLAYER_DATA), (client, handler, buf, responseSender) -> {
            // receiving setting packets from the server, copy to not throw an error
            PacketBuilder packet = new PacketBuilder(buf.copy());
            assert client.player != null;
            client.execute(() -> {
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                // if the client isn't in single player, load the file stuff from the packet
                if (!client.isInSingleplayer()) {
                    Player player = new Player(client.player,true);
                    PData pData = gson.fromJson(packet.getMessage(), PData.class);

                    PlayerData.setPlayerData(player,pData);
                    PlayerData.setPlayerCache(player,new CachedPData(pData));
                }
                onSupportedServer = true;
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(PacketBuilder.getIdentifier(Assets.packets.HUD), (client, handler, buf, responseSender) -> {
            // receiving HUD packets from the server
            PacketBuilder packet = new PacketBuilder(buf.copy());
            if (client.player == null) return;
            client.execute(() -> {
                Type hashMapToken = new TypeToken<HashMap<Hud.Module, ArrayList<String>>>() {}.getType();
                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                // if there is no actionbar override, build and send the HUD
                if (overrideCd <= 0) {
                    client.player.sendMessage(Hud.build.compile(new Player(client.player,true), gson.fromJson(packet.getMessage(), hashMapToken)).b(), true);
                }
            });
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isInSingleplayer()) DirectionHUD.singleplayer = true;
            // send an initialization packet whenever joining a server
            client.execute(() -> {
                PacketBuilder sPacket = new PacketBuilder("Hello from DirectionHUD client!");
                sPacket.sendToServer(PacketBuilder.getIdentifier(Assets.packets.INITIALIZATION));
            });
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            DirectionHUD.singleplayer = false;
            onSupportedServer = false;
            if (client.player == null) return;

            Player player = new Player(client.player,true);
            PlayerData.removePlayerData(player);
            PlayerData.removePlayerCache(player);
        });
    }
}
