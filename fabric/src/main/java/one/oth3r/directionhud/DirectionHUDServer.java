package one.oth3r.directionhud;

import net.fabricmc.api.DedicatedServerModInitializer;
import src.main.java.one.oth3r.directionhud.common.files.LangReader;

public class DirectionHUDServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        DirectionHUD.isClient = false;
        LangReader.loadLanguageFile();
        DirectionHUD.initializeCommon();
    }
}
