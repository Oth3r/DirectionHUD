package one.oth3r.directionhud.common.utils;

import one.oth3r.directionhud.common.files.config;
import one.oth3r.directionhud.utils.CTxT;
import one.oth3r.directionhud.utils.Player;
import one.oth3r.directionhud.utils.Utl;

import java.util.ArrayList;
import java.util.Arrays;

import static one.oth3r.directionhud.utils.Utl.dim.conversionRatios;
import static one.oth3r.directionhud.utils.Utl.isInt;

public class Loc {
    private Integer x = null;
    private Integer y = null;
    private Integer z = null;
    private String dimension = null;
    public Loc() {}
    public Loc(Integer x, Integer y, Integer z, String dimension) {
        this.x = xzBounds(x);
        this.y = yBounds(y);
        this.z = xzBounds(z);
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    public Loc(Integer x, Integer y, Integer z) {
        this.x = xzBounds(x);
        this.y = yBounds(y);
        this.z = xzBounds(z);
    }
    public Loc(Integer x, Integer z, String dimension) {
        this.x = xzBounds(x);
        this.z = xzBounds(z);
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    public Loc(Integer x, Integer z) {
        this.x = xzBounds(x);
        this.z = xzBounds(z);
    }
    public Loc(String xyz) {
        parseXYZ(xyz);
    }
    public Loc(String xyz, String dimension) {
        parseXYZ(xyz);
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    private static Integer yBounds(Integer s) {
        if (s == null) return null;
        if (s > config.MAXy) return config.MAXy;
        return Math.max(s, config.MAXy*-1);
    }
    private static Integer xzBounds(Integer s) {
        if (s == null) return null;
        if (s > config.MAXxz) return config.MAXxz;
        return Math.max(s, config.MAXxz*-1);
    }
    private void parseXYZ(String xyz) {
        if (xyz == null || xyz.equals("null")) return;
        if (xyz.charAt(0)=='[' && xyz.charAt(xyz.length()-1)==']') {
            String[] list = xyz.substring(1, xyz.length() - 1).split(", ");
            if (list.length >= 3)  {
                this.x = Integer.parseInt(list[0]);
                if (list[1] != null && !list[1].equals("null")) this.y = Integer.parseInt(list[1]);
                this.z = Integer.parseInt(list[2]);
            }
            if (list.length == 4) this.dimension = list[3];
            return;
        }
        ArrayList<String> sp = new ArrayList<>(Arrays.asList(xyz.split(" ")));
        if (sp.size() == 1) {
            this.x = 0;
            this.z = 0;
            return;
        }
        if (!isInt(sp.get(0))) sp.set(0, "0");
        if (!isInt(sp.get(1))) sp.set(1, "0");
        if (sp.size() == 3 && !isInt(sp.get(2))) sp.set(2,"0");
        this.x = xzBounds(Integer.parseInt(sp.get(0)));
        if (sp.size() == 2) {
            this.z = xzBounds(Integer.parseInt(sp.get(1)));
            return;
        }
        this.y = yBounds(Integer.parseInt(sp.get(1)));
        this.z = xzBounds(Integer.parseInt(sp.get(2)));
    }
    public Loc(Player player) {
        this.x = xzBounds(player.getBlockX());
        this.y = yBounds(player.getBlockY());
        this.z = xzBounds(player.getBlockZ());
        this.dimension = player.getDimension();
    }
    public Loc(Player player, String dimension) {
        this.x = xzBounds(player.getBlockX());
        this.y = yBounds(player.getBlockY());
        this.z = xzBounds(player.getBlockZ());
        if (Utl.dim.checkValid(dimension)) this.dimension = dimension;
    }
    public void convertTo(String toDimension) {
        String fromDimension = this.getDIM();
        if (fromDimension.equalsIgnoreCase(toDimension)) return;
        if (!Utl.dim.checkValid(toDimension)) return;
        Utl.Pair<String, String> dimensionPair = new Utl.Pair<>(fromDimension, toDimension);
        Double ratio;
        if (conversionRatios.containsKey(dimensionPair)) ratio = conversionRatios.get(dimensionPair);
        else {
            dimensionPair = new Utl.Pair<>(toDimension,fromDimension);
            if (conversionRatios.containsKey(dimensionPair)) ratio = 1/conversionRatios.get(dimensionPair);
            else return;
        }
        this.setDIM(toDimension);
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
    public String getLocC() {
        if (x == null || z == null) return "null";
        if (this.dimension == null) return Arrays.toString(new String[]{this.x+"",this.y+"",this.z+""});
        return Arrays.toString(new String[]{this.x+"",this.y+"",this.z+"",this.dimension});
    }
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
    public CTxT getBadge() {
        CTxT msg = CTxT.of("");
        if (this.dimension != null) msg.append(Utl.dim.getLetterButton(getDIM())).append(" ");
        return msg.append(CTxT.of(getXYZ()).color('f'));
    }
    public CTxT getBadge(String name,String color) {
        CTxT msg = CTxT.of("");
        if (this.dimension != null) msg.append(Utl.dim.getLetterButton(getDIM())).append(" ");
        return msg.append(CTxT.of(name).color(color).hEvent(CTxT.of(getXYZ())));
    }

    public Integer getX() {
        return x;
    }
    public void setX(Integer x) {
        this.x = x;
    }
    public boolean yExists() {
        return this.y != null;
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
    public String getDIM() {
        return dimension;
    }
    public void setDIM(String setDIM) {
        if (Utl.dim.checkValid(setDIM)) this.dimension = setDIM;
    }
    public String toString() {
        return this.x+" "+this.y+" "+this.z+" "+this.dimension;
    }
}
