package one.oth3r.directionhud.common.files.dimension;

import com.google.gson.annotations.SerializedName;

public class DimensionEntry {

    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("color")
    private String color;

    public DimensionEntry() {}

    public DimensionEntry(String id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
