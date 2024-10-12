package one.oth3r.directionhud.common.utils;

public class Vec {
    private double x;
    private double y;
    private double z;

    public Vec() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Vec(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec(Vec vec) {
        x = vec.x;
        y = vec.y;
        z = vec.z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double distanceTo(Vec vec) {
        double xD = vec.x - this.x;
        double yD = vec.y - this.y;
        double zD = vec.z - this.z;
        return Math.sqrt((xD * xD) + (yD * yD) + (zD * zD));
    }

    @Override
    public String toString() {
        return "Vec{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
