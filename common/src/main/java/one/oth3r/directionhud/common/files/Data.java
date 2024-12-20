package one.oth3r.directionhud.common.files;

import one.oth3r.directionhud.common.files.dimension.DimensionSettings;
import one.oth3r.directionhud.common.files.playerdata.PData;

public class Data {
    /**
     * directionHUD config file
     */
    private static Config config = new Config();

    public static Config getConfig() {
        return new Config(config);
    }

    public static void setConfig(Config newConfig) {
        config = new Config(newConfig);
    }

    private static ModuleText moduleText = new ModuleText();

    public static ModuleText getModuleText() {
        return moduleText;
    }

//    public static void setModuleText(ModuleText moduleText) {
//        Data.moduleText = moduleText;
//    }

    /**
     * global destinations
     */
    private static GlobalDest globalDestinations = new GlobalDest();

    public static GlobalDest getGlobal() {
        return globalDestinations;
    }

    public static void setGlobalDestinations(GlobalDest globalDestinations) {
        Data.globalDestinations = new GlobalDest(globalDestinations);
    }

    public static void loadFiles(boolean tryLegacy) {
        Config.load(tryLegacy);
        PData.loadDefaults();
        LangReader.loadLanguageFile();
        DimensionSettings.load();
        if (config.getDestination().getGlobal()) GlobalDest.load();
    }

    /**
     * clears the per-server data
     */
    public static void clearServerData() {
        globalDestinations = new GlobalDest();
    }
}
