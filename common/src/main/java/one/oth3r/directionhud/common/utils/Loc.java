package one.oth3r.directionhud.common.utils;

import com.google.gson.Gson;
import one.oth3r.directionhud.common.files.FileData;
import one.oth3r.directionhud.common.files.dimension.Dimension;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;
import one.oth3r.directionhud.common.utils.Helper.Command.Suggester;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Loc {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private String dimension = null;

    public Loc() {}

    /**
     * copies a Loc from an existing Loc
     * @param loc Loc to copy
     */
    public Loc(Loc loc) {
        copyFrom(loc);
    }

    public void copyFrom(Loc loc) {
        x = loc.getX();
        y = loc.getY();
        z = loc.getZ();
        dimension = loc.getDimension();
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
        // if not legacy
        if (xyz.charAt(0)=='{' && xyz.charAt(xyz.length()-1)=='}') {
            copyFrom(new Loc(xyz));
            return;
        }
        if (xyz.charAt(0)=='[' && xyz.charAt(xyz.length()-1)==']') {
            String[] list = xyz.substring(1, xyz.length() - 1).split(", ");
            if (list.length >= 3)  {
                this.x = Helper.Num.toInt(list[0]);
                if (list[1] != null && !list[1].equals("null")) this.y = Helper.Num.toInt(list[1]);
                this.z = Helper.Num.toInt(list[2]);
            }
            if (list.length == 4) this.dimension = Utl.dim.updateLegacy(list[3]);
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
    }

    /**
     * creates a Loc based on the player's current location and dimension
     */
    public Loc(Player player) {
        Vec vec = player.getVec();
        this.x = xzBounds(vec.getBlockX());
        this.y = yBounds(vec.getBlockY());
        this.z = xzBounds(vec.getBlockZ());
        this.dimension = player.getDimension();
    }

    private Integer yBounds(Integer s) {
        if (s == null) return null;
        if (s > FileData.getConfig().getLocation().getMaxY()) return FileData.getConfig().getLocation().getMaxY();
        return Math.max(s, FileData.getConfig().getLocation().getMaxY()*-1);
    }

    private Integer xzBounds(Integer s) {
        if (s == null) return null;
        if (s > FileData.getConfig().getLocation().getMaxXZ()) return FileData.getConfig().getLocation().getMaxXZ();
        return Math.max(s, FileData.getConfig().getLocation().getMaxXZ()*-1);
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

    public String getXYZ() {
        if (x == null || z == null) return null;
        if (y == null) return x+" "+z;
        return x+" "+y+" "+z;
    }

    public boolean hasY() {
        return y != null;
    }

    /**
     * checks if the Loc is valid or not
     */
    public boolean isValid() {
        return hasXYZ() && dimension != null;
    }

    public Vec getVec(Player player) {
        // make sure y isn't null, using the player y if needed
        Integer i = this.y;
        if (i == null) i = player.getVec().getBlockY();

        // if nothing else is null, return as vec
        if (this.x != null && this.z != null) {
            return new Vec(this.x+0.5,i+0.5,this.z+0.5);
        }

        // empty Loc, return empty vec
        return new Vec();
    }

    /**
     * create a Loc badge
     * @return badge
     */
    public CTxT getBadge() {
        CTxT msg = CTxT.of("");
        if (this.dimension != null) msg.append(Dimension.getBadge(getDimension())).append(" ");
        return msg.append(CTxT.of(getXYZ()).color('f'));
    }

    /**
     * turns the LOC into a string for a DirectionHUD command
     * x (y) z dimension
     */
    public String toCMD() {
        // if not valid return empty
        if (!isValid()) return "";
        StringBuilder sb = new StringBuilder();

        sb.append(x).append(" ");
        if (y != null) sb.append(y).append(" ");
        sb.append(z).append(" ");
        sb.append(Suggester.wrapQuotes(dimension));

        return sb.toString();
    }

    // OVERRIDES

    /**
     * displays a string version of the Loc, which is a gson
     */
    @Override
    public String toString() {
        return Helper.getGson().toJson(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loc loc = (Loc) o;
        return Objects.equals(x, loc.x) &&
                Objects.equals(y, loc.y) &&
                Objects.equals(z, loc.z) &&
                Objects.equals(dimension, loc.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, dimension);
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
}
