package one.oth3r.directionhud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.WorldSavePath;
import one.oth3r.directionhud.commands.DHUDCommand;
import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.Data;
import one.oth3r.directionhud.packet.Payloads;
import one.oth3r.directionhud.utils.BossBarManager;
import one.oth3r.directionhud.utils.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DirectionHUD implements ModInitializer {
	public static final String PRIMARY = "#2993ff";
	public static final String SECONDARY = "#ffee35";
	public static BossBarManager bossBarManager = new BossBarManager();
	public static String DATA_DIR = "";
	public static String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toFile()+"/directionhud/";
	public static final Logger LOGGER = LogManager.getLogger("DirectionHUD");
	public static ArrayList<Player> clientPlayers = new ArrayList<>();
	public static final String MOD_ID = "directionhud";
	public static final Version VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();
	public static boolean isClient = false;
	public static final boolean isMod = true;
	public static boolean singleplayer = false;

	public static PlayerManager playerManager;
	public static MinecraftServer server;
	public static CommandManager commandManager;

	@Override
	public void onInitialize() {
		Data.loadFiles(true);
		// SERVER START/STOP
		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			DirectionHUD.playerManager = s.getPlayerManager();
			DirectionHUD.server = s;
			DirectionHUD.commandManager = s.getCommandManager();
			if (isClient) DATA_DIR = DirectionHUD.server.getSavePath(WorldSavePath.ROOT).normalize()+"/directionhud/";
			else DATA_DIR = FabricLoader.getInstance().getConfigDir().toFile()+"/directionhud/";
			Events.serverStart();
		});
		//STOP
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> Events.serverEnd());

		// PLAYER CONNECTIONS
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> Events.playerJoin(new Player(handler.player)));
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> Events.playerLeave(new Player(handler.player)));

		// PACKET REGISTRATION
		PayloadTypeRegistry.playS2C().register(Payloads.HUD.ID, Payloads.HUD.CODEC);
		PayloadTypeRegistry.playS2C().register(Payloads.SpigotHUD.ID, Payloads.SpigotHUD.CODEC);

		PayloadTypeRegistry.playS2C().register(Payloads.PlayerData.ID, Payloads.PlayerData.CODEC);
		PayloadTypeRegistry.playS2C().register(Payloads.SpigotPlayerData.ID, Payloads.SpigotPlayerData.CODEC);

		PayloadTypeRegistry.playC2S().register(Payloads.Initialization.ID, Payloads.Initialization.CODEC);

		// PACKET HANDLING
		ServerPlayNetworking.registerGlobalReceiver(Payloads.Initialization.ID,((payload, context) -> server.execute(() -> {
			Player player = new Player(context.player());
			DirectionHUD.LOGGER.info("Received initialization packet from "+player.getName());
			DirectionHUD.clientPlayers.add(player);
			player.sendPDataPackets();
		})));

		// COMMAND REGISTRATION
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DHUDCommand.register(dispatcher);
			HUDCommand.register(dispatcher);
			DestinationCommand.register(dispatcher);
		});

		// LOOP
		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> minecraftServer.execute(LoopManager::tick));
	}
	public static void clear() {
		playerManager = null;
		server = null;
		commandManager = null;
		clientPlayers.clear();
	}
}
