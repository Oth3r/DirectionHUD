package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.DirectionHUD;
import one.oth3r.otterlib.file.ResourceReader;

public class DirectionHudData {
    protected final String PRIMARY;
    protected final String SECONDARY;
    protected String version;
    protected final boolean isMod;
    protected String configDirectory;
    protected String dataDirectory;
    protected ResourceReader defaultLanguageLocation;

    protected boolean isClient;
    protected boolean isServerStarted;

    public DirectionHudData(boolean isMod, String primary, String secondary) {
        this.isMod = isMod;
        this.version = "v1.x.x.x";
        this.PRIMARY = primary;
        this.SECONDARY = secondary;
        this.isClient = false;
        this.isServerStarted = false;
        this.dataDirectory = "";
        this.configDirectory = "";
        this.defaultLanguageLocation = new ResourceReader("assets/directionhud/lang/", DirectionHUD.class.getClassLoader());
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
        return version;
    }

    public void setVersion(String VERSION) {
        this.version = VERSION;
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

    public ResourceReader getDefaultLanguageLocation() {
        return defaultLanguageLocation;
    }
}
