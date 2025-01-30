package one.oth3r.directionhud.common.hud;

import one.oth3r.directionhud.common.utils.Rainbow;
import one.oth3r.directionhud.utils.Player;

public class HudRainbow extends Rainbow {
    // todo add linked rainbow support, when not linked switch between 2 rainbows :sob:
    private final Player player;

    public HudRainbow(Player player) {
        this.player = player;
    }

    public Rainbow select(HudColor color) {
        this.enabled = player.getPCache().getHud().getColor(color).getRainbow();
        return this;
    }
}
