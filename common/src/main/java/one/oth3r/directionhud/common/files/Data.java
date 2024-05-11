package one.oth3r.directionhud.common.files;

import one.oth3r.directionhud.common.files.dimension.Dimension;
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

    public static void loadFiles(boolean tryLegacy) {
        Config.load(tryLegacy);
        PData.loadDefaults();
        LangReader.loadLanguageFile();
        DimensionSettings.load();
        if (config.getOnline()) GlobalDest.load();
    }
}
