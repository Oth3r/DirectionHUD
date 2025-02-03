package one.oth3r.directionhud.common.files;

import one.oth3r.directionhud.common.ModData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.common.files.playerdata.PlayerData;

public class FileData {
    /**
     * directionHUD config file
     */
    private static final Config config = new Config();

    public static Config getConfig() {
        return new Config(config);
    }

    public static void setConfig(Config newConfig) {
        config.copyFileData(newConfig);
    }

    /**
     * module display data
     */
    private static final ModuleText moduleText = new ModuleText();

    public static ModuleText getModuleText() {
        return moduleText;
    }

    /**
     * global destinations
     */
    private static final GlobalDest globalDestinations = new GlobalDest();

    public static GlobalDest getGlobal() {
        return new GlobalDest(globalDestinations);
    }

    public static void setGlobalDestinations(GlobalDest globalDestinations, boolean save) {
        FileData.globalDestinations.copyFileData(globalDestinations);
        if (save) globalDestinations.save();
    }

    /**
     * loads the global destinations to object, as {@link #getGlobal()} returns a copy of the object
     */
    public static void loadGlobalDestinations() {
        globalDestinations.load();
    }

    public static void loadFiles() {
        config.load();
        PlayerData.loadDefaults();
        LangReader.loadLanguageFile();
        Dimension.loadDimensionSettings();
        moduleText.load();
        // the server has to be started to edit per world data
        if (ModData.isServerStarted() && config.getDestination().getGlobal()) loadGlobalDestinations();
    }

    /**
     * clears the per-server data
     */
    public static void clearServerData() {
        globalDestinations.copyFileData(new GlobalDest());
    }
}
