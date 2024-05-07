package one.oth3r.directionhud.common.template;

import one.oth3r.directionhud.common.Destination;
import one.oth3r.directionhud.common.files.Data;
import one.oth3r.directionhud.utils.Player;

public abstract class FeatureChecker {
    protected Player player;

    public FeatureChecker(Player player) {
        this.player = player;
    }

    public boolean customPresets() {
        return Data.getConfig().getMaxColorPresets() > 0;
    }

    public boolean destination() {
        return true;
    }

    public boolean hud() {
        return Data.getConfig().getHud().getEditing();
    }

    public abstract boolean reload();

    public boolean global() {
        return Data.getConfig().getDestination().getGloabal();
    }

    public boolean globalEditing() {
        return global() && saving();
    };

    public boolean saving() {
        return Data.getConfig().getDestination().getSaving();
    }

    public boolean lastdeath() {
        return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__lastdeath) &&
                Data.getConfig().getDestination().getLastDeath().getSaving() && Data.getConfig().getDestination().getLastDeath().getMaxDeaths() > 0;
    }

    public boolean send() {
        return (boolean) player.getPData().getDEST().getSetting(Destination.Setting.features__send) &&
                Data.getConfig().getSocial().getEnabled();
    }

    public boolean track() {
        return player.getPCache().getDEST().getDestSettings().getFeatures().getTrack() &&
                Data.getConfig().getSocial().getEnabled();
    }
}
