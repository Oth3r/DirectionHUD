package one.oth3r.directionhud;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.WorldSavePath;
import one.oth3r.directionhud.commands.DestinationCommand;
import one.oth3r.directionhud.commands.DirHUDCommand;
import one.oth3r.directionhud.commands.HUDCommand;
import one.oth3r.directionhud.common.Assets;
import one.oth3r.directionhud.common.Events;
import one.oth3r.directionhud.common.LoopManager;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.BossBarManager;
import one.oth3r.directionhud.utils.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class DirectionHUD {
	public static final String PRIMARY = "#2993ff";
	public static final String SECONDARY = "#ffee35";
	public static BossBarManager bossBarManager = new BossBarManager();
	public static String PLAYERDATA_DIR = "";
	public static final String CONFIG_DIR = FabricLoader.getInstance().getConfigDir().toFile()+"/";
	public static final Logger LOGGER = LogManager.getLogger("DirectionHUD");
	public static ArrayList<Player> clientPlayers = new ArrayList<>();
	public static final String MOD_ID = "directionhud";
	public static final Version VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion();
	public static boolean isClient;
	public static final boolean isMod = true;
	public static String playerData;
	public static String configDir;
	public static PlayerManager playerManager;
	public static MinecraftServer server;
	public static CommandManager commandManager;
	public static void initializeCommon() {
		configDir = FabricLoader.getInstance().getConfigDir().toFile()+"/";
		config.load();
		//START
		ServerLifecycleEvents.SERVER_STARTED.register(s -> {
			DirectionHUD.playerManager = s.getPlayerManager();
			DirectionHUD.server = s;
			DirectionHUD.commandManager = s.getCommandManager();
			if (isClient) PLAYERDATA_DIR = DirectionHUD.server.getSavePath(WorldSavePath.ROOT).normalize()+"/directionhud/playerdata/";
			else PLAYERDATA_DIR = FabricLoader.getInstance().getConfigDir().toFile()+"/directionhud/playerdata/";
			Events.serverStart();
		});
		//STOP
		ServerLifecycleEvents.SERVER_STOPPING.register(s -> {
			Events.serverEnd();
		});
		//PLAYER
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			Events.playerJoin(Player.of(handler.player));
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			Events.playerLeave(Player.of(handler.player));
		});
		//PACKETS
		ServerPlayNetworking.registerGlobalReceiver(PacketBuilder.getIdentifier(Assets.packets.INITIALIZATION),
				(server, player, handler, buf, responseSender) -> server.execute(() -> {
					DirectionHUD.LOGGER.info("Received initialization packet from "+player.getName().getString());
					Player dPlayer = Player.of(player);
					DirectionHUD.clientPlayers.add(dPlayer);
					dPlayer.sendPackets();
				}));
		//COMMANDS
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			DirHUDCommand.register(dispatcher);
			HUDCommand.register(dispatcher);
			DestinationCommand.register(dispatcher);
		});
		//LOOP
		ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> minecraftServer.execute(LoopManager::tick));
	}
}
