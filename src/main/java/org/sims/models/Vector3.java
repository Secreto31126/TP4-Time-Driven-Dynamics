package org.sims.models;

public record Vector3(double x, double y, double z) {
    public static final Vector3 ZERO_ZERO_ZERO = new Vector3(0.0, 0.0, 0.0);
    public static final Vector3 ZERO = ZERO_ZERO_ZERO;

    public Vector3 neg() {
        return Vector3.neg(this);
    }

    public Vector3 add(final Vector3 v) {
        return Vector3.add(this, v);
    }

    public Vector3 subtract(final Vector3 v) {
        return Vector3.subtract(this, v);
    }

    public Vector3 mult(final double scalar) {
        return Vector3.mult(this, scalar);
    }

    public Vector3 div(final double scalar) {
        return Vector3.div(this, scalar);
    }

    public double dot(final Vector3 v) {
        return Vector3.dot(this, v);
    }

    public double norm() {
        return Vector3.norm(this);
    }

    public double norm2() {
        return Vector3.norm2(this);
    }

    public Vector3 hadamard(final Vector3 v) {
        return Vector3.hadamard(this, v);
    }

    public static Vector3 neg(final Vector3 v) {
        return new Vector3(-v.x, -v.y, -v.z);
    }

    public static Vector3 add(final Vector3 v1, final Vector3 v2) {
        return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
    }

    public static Vector3 subtract(final Vector3 v1, final Vector3 v2) {
        return new Vector3(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
    }

    public static Vector3 mult(final Vector3 v, final double scalar) {
        return new Vector3(v.x * scalar, v.y * scalar, v.z * scalar);
    }

    public static Vector3 div(final Vector3 v, final double scalar) {
        return new Vector3(v.x / scalar, v.y / scalar, v.z / scalar);
    }

    public static double dot(final Vector3 v1, final Vector3 v2) {
        return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
    }

    public static double norm(final Vector3 v) {
        return Math.sqrt(Vector3.norm2(v));
    }

    public static double norm2(final Vector3 v) {
        return v.x * v.x + v.y * v.y + v.z * v.z;
    }

    public static Vector3 hadamard(final Vector3 v1, final Vector3 v2) {
        return new Vector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
    }

    @Override
    public String toString() {
        return "%.14f %.14f %.14f".formatted(x, y, z);
    }
}
