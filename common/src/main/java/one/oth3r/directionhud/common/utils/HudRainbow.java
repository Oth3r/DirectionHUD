package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.utils.Player;

public class HudRainbow extends Rainbow {
    // todo add linked rainbow support, when not linked switch between 2 rainbows :sob:
    private final Player player;

    public HudRainbow(Player player) {
        this.player = player;
    }

    public Rainbow select(int typ) {
        if (typ == 1) this.enabled = player.getPCache().getHud().getPrimary().getRainbow();
        else this.enabled = player.getPCache().getHud().getSecondary().getRainbow();
        return this;
    }
}
