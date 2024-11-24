package one.oth3r.directionhud.common.utils;

public class Vec {
    private final double x;
    private final double y;
    private final double z;

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

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Vec add(Vec addVec) {
        return add(addVec.x,addVec.y,addVec.z);
    }

    public Vec add(double x, double y, double z) {
        return new Vec(this.x + x, this.y + y, this.z + z);
    }

    public Vec subtract(Vec subVec) {
        return add(-subVec.x,-subVec.y,-subVec.z);
    }

    public Vec subtract(double x, double y, double z) {
        return new Vec(this.x - x, this.y - y, this.z - z);
    }

    public Vec multiply(double x, double y, double z) {
        return new Vec(this.x * x, this.y * y, this.z * z);
    }

    public Vec divide(double x, double y, double z) {
        return new Vec(this.x / x, this.y / y, this.z / z);
    }

    public Vec normalize() {
        // calculate the magnitude (length) of the vector
        double magnitude = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);

        // If the magnitude is tiny, return zero
        if (magnitude < 1e-5) return new Vec();

        // divide each axis by magnitude to normalize
        return new Vec(this.x / magnitude, this.y / magnitude, this.z / magnitude);
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
