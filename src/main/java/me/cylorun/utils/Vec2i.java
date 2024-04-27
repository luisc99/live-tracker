package me.cylorun.utils;


public class Vec2i {
    public static final Vec2i ZERO = new Vec2i(0, 0);
    private final int x;
    private final int z;

    public Vec2i(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }


    public int getZ() {
        return this.z;
    }

    public int distanceTo(Vec2i vec) {
        return (int) Math.sqrt(Math.pow(this.getX() - vec.getX(), 2) + Math.pow(this.getZ() - vec.getZ(), 0));
    }

    @Override
    public String toString() {
        return "Pos{x=" + this.x + ", z=" + this.z + '}';
    }

}




