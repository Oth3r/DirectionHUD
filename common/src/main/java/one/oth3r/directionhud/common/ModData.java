package one.oth3r.directionhud.common;

public class ModData {
    private static boolean serverStarted = false;

    public static boolean isServerStarted() {
        return serverStarted;
    }

    public static void setServerStarted(boolean serverStarted) {
        ModData.serverStarted = serverStarted;
    }
}
