package org.sims.models;

public record Vector3(double x, double y, double z) {
    public static final Vector3 ZERO_ZERO_ZERO = new Vector3(0.0, 0.0, 0.0);
    public static final Vector3 ZERO = ZERO_ZERO_ZERO;

    public Vector3 neg() {
        return new Vector3(-x, -y, -z);
    }

    public Vector3 add(final Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 subtract(final Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public Vector3 mult(final double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    public Vector3 div(final double scalar) {
        return new Vector3(x / scalar, y / scalar, z / scalar);
    }

    public double dot(final Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public double norm() {
        return Math.sqrt(this.norm2());
    }

    public double norm2() {
        return x * x + y * y + z * z;
    }

    public Vector3 hadamard(final Vector3 v) {
        return new Vector3(x * v.x, y * v.y, z * v.z);
    }

    @Override
    public String toString() {
        return "%.14f %.14f %.14f".formatted(x, y, z);
    }
}
