package one.oth3r.directionhud.common.files.playerdata.hud;

import com.google.gson.annotations.SerializedName;

public class pd_hud_color {
    @SerializedName("color")
    private String color;
    @SerializedName("bold")
    private Boolean bold;
    @SerializedName("italics")
    private Boolean italics;
    @SerializedName("rainbow")
    private Boolean rainbow;

    public pd_hud_color(String color, Boolean bold, Boolean italics, Boolean rainbow) {
        this.color = color;
        this.bold = bold;
        this.italics = italics;
        this.rainbow = rainbow;
    }
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getBold() {
        return bold;
    }

    public void setBold(Boolean bold) {
        this.bold = bold;
    }

    public Boolean getItalics() {
        return italics;
    }

    public void setItalics(Boolean italics) {
        this.italics = italics;
    }

    public Boolean getRainbow() {
        return rainbow;
    }

    public void setRainbow(Boolean rainbow) {
        this.rainbow = rainbow;
    }
}
