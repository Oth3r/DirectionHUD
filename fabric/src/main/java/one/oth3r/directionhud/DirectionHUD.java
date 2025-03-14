package one.oth3r.directionhud;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import one.oth3r.directionhud.utils.ModData;
import one.oth3r.directionhud.utils.ModEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectionHUD implements ModInitializer {
	public static final Logger LOGGER = LogManager.getLogger("DirectionHUD");
	public static final String MOD_ID = "directionhud";

	private static final ModData modData = new ModData(true,
			FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata().getVersion().toString(),
			"#2993ff", "#ffee35");

	public static ModData getData() {
		return modData;
	}

	@Override
	public void onInitialize() {
		ModEvents.registerCommon();
	}
}
