package one.oth3r.directionhud.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.WorldSavePath;
import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.directionhud.DirectionHUDClient;
import one.oth3r.directionhud.commands.ModCommands;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.playerdata.CachedPData;
import one.oth3r.directionhud.common.files.playerdata.PData;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;
import one.oth3r.directionhud.common.hud.Hud;
import one.oth3r.directionhud.common.hud.module.ModuleInstructions;
import one.oth3r.directionhud.common.utils.Helper;
import one.oth3r.directionhud.packet.PacketSender;
import one.oth3r.directionhud.packet.Payloads;
import org.lwjgl.glfw.GLFW;

public class ModEvents {
    private static class Keybindings {
        private static KeyBinding keyBinding;

        private static void register() {
            keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    "key.directionhud.keybind.toggle",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_H,
                    "category.directionhud.all"
            ));

        }

        private static void loopLogic(MinecraftClient client) {
            while (keyBinding.wasPressed()) {
                if (DirectionHUD.getData().isOnSupportedServer()) {
                    assert client.player != null;
                    client.player.networkHandler.sendCommand("hud toggle");
                }
            }
        }
    }

    private static class Packet {
        private static void common() {
            // register the data
            // PACKET REGISTRATION
            PayloadTypeRegistry.playS2C().register(Payloads.HUD.ID, Payloads.HUD.CODEC);
            PayloadTypeRegistry.playS2C().register(Payloads.SpigotHUD.ID, Payloads.SpigotHUD.CODEC);

            PayloadTypeRegistry.playS2C().register(Payloads.PlayerData.ID, Payloads.PlayerData.CODEC);
            PayloadTypeRegistry.playS2C().register(Payloads.SpigotPlayerData.ID, Payloads.SpigotPlayerData.CODEC);

            PayloadTypeRegistry.playC2S().register(Payloads.Initialization.ID, Payloads.Initialization.CODEC);

            // PACKET HANDLING
            ServerPlayNetworking.registerGlobalReceiver(Payloads.Initialization.ID, ((payload, context) ->
                    DirectionHUD.getData().getServer().execute(() -> {
                Player player = new Player(context.player());
                DirectionHUD.LOGGER.info("Received initialization packet from "+player.getName());
                DirectionHUD.getData().getClientPlayers().add(player);
                player.sendPDataPackets();
            })));
        }

        private static void client() {
            // receiving setting packets from the server
            ClientPlayNetworking.registerGlobalReceiver(Payloads.PlayerData.ID, (payload, context) -> {
                playerDataPacketLogic(context.client(),payload.value());
            });
            // spigot
            ClientPlayNetworking.registerGlobalReceiver(Payloads.SpigotPlayerData.ID, (payload, context) -> {
                playerDataPacketLogic(context.client(),payload.value());
            });

            // receiving HUD packets from the server
            ClientPlayNetworking.registerGlobalReceiver(Payloads.HUD.ID, (payload, context) -> {
                hudPacketLogic(context.client(),payload.value());
            });
            // spigot
            ClientPlayNetworking.registerGlobalReceiver(Payloads.SpigotHUD.ID, (payload, context) -> {
                hudPacketLogic(context.client(),payload.value());
            });
        }

        public static void playerDataPacketLogic(MinecraftClient client, String packet) {
            client.execute(() -> {
                // if not single player store the payload in local playerdata (otherwise it doesn't need to be saved)
                if (!client.isInSingleplayer()) {
                    Player player = DirectionHUDClient.getPlayerFromClient(client);
                    PData pData = Helper.getGson().fromJson(packet, PData.class);
                    pData.setPlayer(player);

                    PlayerData.setPlayerData(player,pData);
                    PlayerData.setPlayerCache(player,new CachedPData(pData));
                }
                DirectionHUD.getData().setOnSupportedServer(true);
            });
        }

        public static void hudPacketLogic(MinecraftClient client, String packet) {
            client.execute(() -> {
                // if there is no actionbar override, build and send the HUD
                if (DirectionHUD.getData().getActionBarOverride().canDisplay()) {
                    Player player = DirectionHUDClient.getPlayerFromClient(client);
                    player.sendActionBar(Hud.build.compile(player, Helper.getGson().fromJson(packet, ModuleInstructions.class)));
                }
            });
        }
    }

    private static void clientConnections() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.isInSingleplayer()) DirectionHUD.getData().setSingleplayer(true);
            // send an initialization packet whenever joining a server
            client.execute(() -> new PacketSender(Assets.packets.INITIALIZATION,"Hello from the DirectionHUD client!").sendToServer());
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            DirectionHUD.getData().setSingleplayer(false);
            DirectionHUD.getData().setOnSupportedServer(false);
            if (client.player == null) return;

            // clear client player data and cache
            Player player = DirectionHUDClient.getPlayerFromClient(client);
            PlayerData.removePlayerData(player);
            PlayerData.removePlayerCache(player);
        });
    }

    private static void playerConnections() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Events.playerJoin(new Player(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> Events.playerLeave(new Player(handler.player)));
    }

    private static void serverLifecycle() {
        // START
        ServerLifecycleEvents.SERVER_STARTED.register(s -> {
            ModData modData = DirectionHUD.getData();
            modData.setPlayerManager(s.getPlayerManager());
            modData.setServer(s);
            modData.setCommandManager(s.getCommandManager());
            if (modData.isClient()) modData.setDataDirectory(DirectionHUD.getData().getServer().getSavePath(WorldSavePath.ROOT).normalize()+"/directionhud/");
            else modData.setDataDirectory(FabricLoader.getInstance().getConfigDir().toFile()+"/directionhud/");
            Events.serverStart();
        });
        // STOP
        ServerLifecycleEvents.SERVER_STOPPING.register(s -> {
            Events.serverEnd();
        });

        // LOOP SETUP
        ServerTickEvents.END_SERVER_TICK.register(s -> s.execute(LoopManager::tick));

        // COMMAND REGISTRATION
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ModCommands.Register.directionhud(dispatcher);
            ModCommands.Register.destination(dispatcher);
            ModCommands.Register.hud(dispatcher);
        });
    }

    private static void clientLifecycle() {
        // client tick loop
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ModData modData = DirectionHUD.getData();
            assert client.player != null;
            // tick keybind logic
            Keybindings.loopLogic(client);

            // only loop if on a directionHUD server
            if (modData.isOnSupportedServer()) {
                // tick down the override actionbar (every tick) if there is one
                modData.getActionBarOverride().tick();
                if (!modData.isSingleplayer()) {
                    //update the rainbow if not in single-player as it doesn't move
                    LoopManager.tickRainbow();
                }
            }
        });
    }

    public static void registerCommon() {
        Events.init();
        ModData modData = DirectionHUD.getData();
        // directory
        modData.setConfigDirectory(FabricLoader.getInstance().getConfigDir().toFile()+"/directionhud/");
        FileData.loadFiles();

        playerConnections();
        serverLifecycle();
        Packet.common();
    }

    public static void registerClient() {
        DirectionHUD.getData().setClient(true);
        clientConnections();
        clientLifecycle();
        Packet.client();
        Keybindings.register();
    }
}
