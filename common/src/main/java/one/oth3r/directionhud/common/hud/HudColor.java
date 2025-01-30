package one.oth3r.directionhud.common.hud;

import one.oth3r.directionhud.common.files.playerdata.PDHud;
import one.oth3r.directionhud.utils.Player;

import java.util.Arrays;

public enum HudColor {
    PRIMARY("primary", 1),
    SECONDARY("secondary", 2);

    private final String name;
    private final int id;

    HudColor(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    /**
     * gets a {@link PDHud.Color} from the player's playerdata cache
     */
    public PDHud.Color getSettings(Player player) {
        return player.getPCache().getHud().getColor(this);
    }

    /**
     * gets a {@link HudColor} from its ID, without throwing an exception <br>
     * if the id cannot be found, null will be returned
     */
    public static HudColor fromId(int id) {
        return Arrays.stream(HudColor.values()).filter(color -> color.getId() == id).findFirst().orElse(null);
    }

    /**
     * gets a {@link HudColor} from its name, without throwing an exception <br>
     * id the name cannot be found, null will be returned
     */
    public static HudColor fromName(String name) {
        return Arrays.stream(HudColor.values()).filter(color -> color.getName().equals(name)).findFirst().orElse(null);
    }

}
