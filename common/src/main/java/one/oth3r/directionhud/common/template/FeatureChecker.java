package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.utils.Player;

public abstract class FeatureChecker {
    protected Player player;

    public FeatureChecker(Player player) {
        this.player = player;
    }

    public boolean customPresets() {
        return FileData.getConfig().getMaxColorPresets() > 0;
    }

    public boolean destination() {
        return true;
    }

    public boolean hud() {
        return FileData.getConfig().getHud().getEditing();
    }

    public abstract boolean reload();

    public boolean global() {
        return FileData.getConfig().getDestination().getGlobal();
    }

    public boolean globalEditing() {
        return global() && saving();
    };

    public boolean saving() {
        return FileData.getConfig().getDestination().getSaving();
    }

    public boolean lastdeath() {
        return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__lastdeath) &&
                FileData.getConfig().getDestination().getLastDeath().getSaving() && FileData.getConfig().getDestination().getLastDeath().getMaxDeaths() > 0;
    }

    public boolean send() {
        return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__send) &&
                FileData.getConfig().getSocial().getEnabled();
    }

    public boolean track() {
        return player.getPCache().getDEST().getDestSettings().getFeatures().getTrack() &&
                FileData.getConfig().getSocial().getEnabled();
    }
}
