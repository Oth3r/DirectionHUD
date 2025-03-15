package one.oth3r.directionhud.common.utils;

public class DirectionHudData {
    private final String VERSION;
    private final String PRIMARY;
    private final String SECONDARY;
    private final boolean isMod;
    private String configDirectory;
    private String dataDirectory;

    private boolean isClient;
    private boolean isServerStarted;

    public DirectionHudData(boolean isMod, String version, String primary, String secondary) {
        this.isMod = isMod;
        this.VERSION = version;
        this.PRIMARY = primary;
        this.SECONDARY = secondary;
        this.isClient = false;
        this.isServerStarted = false;
        this.dataDirectory = "";
        this.configDirectory = "";
    }

    /**
     * clears the directionhud data (that should be cleared when a server ends)
     */
    public void clear() {
        isServerStarted = false;
        // only clear the data directory, because the config dir should always be the same
        dataDirectory = "";
    }

    public boolean isMod() {
        return isMod;
    }

    public String getVersion() {
        return VERSION;
    }

    public String getPrimary() {
        return PRIMARY;
    }

    public String getSecondary() {
        return SECONDARY;
    }

    public boolean isClient() {
        return isClient;
    }

    public void setClient(boolean client) {
        isClient = client;
    }

    public boolean isServerStarted() {
        return isServerStarted;
    }

    public void setServerStarted(boolean serverStarted) {
        isServerStarted = serverStarted;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public String getConfigDirectory() {
        return configDirectory;
    }

    public void setConfigDirectory(String configDirectory) {
        this.configDirectory = configDirectory;
    }
}
