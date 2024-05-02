package one.oth3r.directionhud.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class Loc {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private String dimension = null;
    private String name = null;
    private String color = null;
    public Loc() {}

    /**
     * copies a Loc from an existing Loc
     * @param loc Loc to copy
     */
    public Loc(Loc loc) {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        dimension = loc.getDimension();
        name = loc.getName();
        color = loc.getColor();
    }

    /**
     * creates Loc with x, y, z, dimension, name, & color
     */
    public Loc(Integer x, Integer y, Integer z, String dimension, String name, String color) {
        this.x = xzBounds(x);
        this.y = yBounds(y);
        this.z = xzBounds(z);
        if (Dimension.checkValid(dimension)) this.dimension = dimension;
        this.name = name;
        this.color = color;
    }

    /**
     * creates Loc with x, y, z, & dimension
     */
    public Loc(Integer x, Integer y, Integer z, String dimension) {
        this.x = xzBounds(x);
        this.y = yBounds(y);
        this.z = xzBounds(z);
        if (Dimension.checkValid(dimension)) this.dimension = dimension;
    }

    /**
     * creates Loc with x, y, & z
     */
    public Loc(Integer x, Integer y, Integer z) {
        this.x = xzBounds(x);
        this.y = yBounds(y);
        this.z = xzBounds(z);
    }

    /**
     * creates a Loc with x, z, and dimension
     */
    public Loc(Integer x, Integer z, String dimension) {
        this.x = xzBounds(x);
        this.z = xzBounds(z);
        if (Dimension.checkValid(dimension)) this.dimension = dimension;
    }

    /**
     * creates a Loc with only x and z
     */
    public Loc(Integer x, Integer z) {
        this.x = xzBounds(x);
        this.z = xzBounds(z);
    }

    /**
     * creates a Loc from legacy Loc string
     * @param xyz
     */
    public Loc(boolean legacy, String xyz) {
        if (xyz == null || xyz.equals("null")) return;
        if (xyz.charAt(0)=='[' && xyz.charAt(xyz.length()-1)==']') {
            String[] list = xyz.substring(1, xyz.length() - 1).split(", ");
            if (list.length >= 3)  {
                this.x = Helper.Num.toInt(list[0]);
                if (list[1] != null && !list[1].equals("null")) this.y = Helper.Num.toInt(list[1]);
                this.z = Helper.Num.toInt(list[2]);
            }
            if (list.length == 4) this.dimension = list[3];
            return;
        }
        ArrayList<String> sp = new ArrayList<>(Arrays.asList(xyz.split(" ")));
        if (sp.isEmpty()) return;
        if (sp.size() == 1) {
            this.x = 0;
            this.z = 0;
            return;
        }
        if (!Helper.Num.isInt(sp.get(0))) sp.set(0, "0");
        if (!Helper.Num.isInt(sp.get(1))) sp.set(1, "0");
        if (sp.size() == 3 && !Helper.Num.isNum(sp.get(2))) sp.set(2,"0");
        this.x = xzBounds(Helper.Num.toInt(sp.get(0)));
        if (sp.size() == 2) {
            this.z = xzBounds(Helper.Num.toInt(sp.get(1)));
            return;
        }
        this.y = yBounds(Helper.Num.toInt(sp.get(1)));
        this.z = xzBounds(Helper.Num.toInt(sp.get(2)));
    }

    /**
     * makes a Loc based on Loc.toString()
     * @param loc the string produced by Loc.toString()
     */
    public Loc(String loc) {
        if (loc.equals("null") || !loc.contains("{")) return;
        Gson gson = new Gson();
        Loc data = gson.fromJson(loc, Loc.class);
        this.x = data.x;
        this.y = data.y;
        this.z = data.z;
        this.dimension = data.dimension;
        this.name = data.name;
        this.color = data.color;
    }

    /**
     * creates a Loc based on the player's current location and dimension
     */
    public Loc(Player player) {
        this.x = xzBounds(player.getBlockX());
        this.y = yBounds(player.getBlockY());
        this.z = xzBounds(player.getBlockZ());
        this.dimension = player.getDimension();
    }

    /**
     * creates a Loc based on the player's location and a custom name
     * @param name the custom name
     */
    public Loc(Player player, String name) {
        this.x = xzBounds(player.getBlockX());
        this.y = yBounds(player.getBlockY());
        this.z = xzBounds(player.getBlockZ());
        this.dimension = player.getDimension();
        this.name = name;
    }

    private Integer yBounds(Integer s) {
        if (s == null) return null;
        if (s > config.MAXy) return config.MAXy;
        return Math.max(s, config.MAXy*-1);
    }

    private Integer xzBounds(Integer s) {
        if (s == null) return null;
        if (s > config.MAXxz) return config.MAXxz;
        return Math.max(s, config.MAXxz*-1);
    }

    public void convertTo(String toDimension) {
        String fromDimension = this.getDimension();
        if (fromDimension.equalsIgnoreCase(toDimension)) return;
        if (!Dimension.checkValid(toDimension)) return;
        Double ratio = Dimension.getRatio(fromDimension, toDimension);
        this.setDimension(toDimension);
        this.setX((int) (this.getX()*ratio));
        this.setZ((int) (this.getZ()*ratio));
    }

    public boolean hasXYZ() {
        return this.getXYZ() != null;
    }

    public boolean hasDestRequirements() {
        return hasXYZ() && this.dimension != null && this.name != null && this.color != null;
    }

    public String getXYZ() {
        if (x == null || z == null) return null;
        if (y == null) return x+" "+z;
        return x+" "+y+" "+z;
    }

    /**
     * displays a string version of the Loc, which is a HashMap
     */
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    // todo create a common Vec class please this sucks
    public ArrayList<Double> getVec(Player player) {
        ArrayList<Double> vector = new ArrayList<>();
        Integer i = this.y;
        if (i == null) i = player.getBlockY();
        if (this.x != null && this.z != null) {
            vector.add((double)this.x+0.5);
            vector.add((double)i+0.5);
            vector.add((double)this.z+0.5);
        }
        return vector;
    }

    /**
     * create a Loc badge
     * @return badge
     */
    public CTxT getBadge() {
        CTxT msg = CTxT.of("");
        // if there's a dimension, add a dimension badge to the start of the message
        if (this.dimension != null) msg.append(Dimension.getBadge(getDimension())).append(" ");
        // if there's a name, make the badge the name, e.g. [O] name
        if (this.name != null) msg.append(CTxT.of(this.name).color(this.color==null?"#ffffff":this.color)
                .hEvent(CTxT.of(getXYZ())));
        // no name, just have the coordinates
        else msg.append(CTxT.of(getXYZ()));
        return msg;
    }

    /**
     * creates a Loc badge with the coordinates even with the name filled
     * @return badge
     */
    public CTxT getNamelessBadge() {
        CTxT msg = CTxT.of("");
        // if there's a dimension, add a dimension badge to the start of the message
        if (this.dimension != null) msg.append(Dimension.getBadge(getDimension())).append(" ");
        // if there's a name, make it the hover
        if (this.name != null) msg.append(CTxT.of(getXYZ())
                .hEvent(CTxT.of(this.name).color(this.color==null?"#ffffff":this.color)));
        // no name, just have the coordinates
        else msg.append(CTxT.of(getXYZ()));
        return msg;
    }

    // ----- GETTERS AND SETTERS -----

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        if (Dimension.checkValid(dimension)) this.dimension = dimension;
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
        this.color = CUtl.color.format(color);
    }
}
