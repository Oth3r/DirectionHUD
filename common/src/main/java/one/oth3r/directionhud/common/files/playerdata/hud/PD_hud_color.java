package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;
import one.oth3r.directionhud.utils.Player;

public class PD_hud_color {
    public PD_hud_color(Player player, String color, Boolean bold, Boolean italics, Boolean rainbow) {
        this.color = color;
        this.bold = bold;
        this.italics = italics;
        this.rainbow = rainbow;
        this.player = player;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    private transient Player player;
    @SerializedName("color")
    private String color;
    @SerializedName("bold")
    private Boolean bold;
    @SerializedName("italics")
    private Boolean italics;
    @SerializedName("rainbow")
    private Boolean rainbow;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        player.getPData().save();
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
        player.getPData().save();
    }

    public Boolean getItalics() {
        return italics;
    }

    public void setItalics(Boolean italics) {
        this.italics = italics;
        player.getPData().save();
    }

    public Boolean getRainbow() {
        return rainbow;
    }

    public void setRainbow(Boolean rainbow) {
        this.rainbow = rainbow;
        player.getPData().save();
    }
}
