package org.sims.models;

public record Vector(double x, double y) {
    public static final Vector NONE_NONE = new Vector(-1.0, -1.0);
    public static final Vector NONE_ZERO = new Vector(-1.0, 0.0);
    public static final Vector NONE_ONE = new Vector(-1.0, 1.0);

    public static final Vector ZERO_NONE = new Vector(0.0, -1.0);
    public static final Vector ZERO_ZERO = new Vector(0.0, 0.0);
    public static final Vector ZERO_ONE = new Vector(0.0, 1.0);

    public static final Vector ONE_NONE = new Vector(1.0, -1.0);
    public static final Vector ONE_ZERO = new Vector(1.0, 0.0);
    public static final Vector ONE_ONE = new Vector(1.0, 1.0);

    public static final Vector ZERO = ZERO_ZERO;

    public Vector neg() {
        return Vector.neg(this);
    }

    public Vector add(final Vector v) {
        return Vector.add(this, v);
    }

    public Vector subtract(final Vector v) {
        return Vector.subtract(this, v);
    }

    public Vector mult(final double scalar) {
        return Vector.mult(this, scalar);
    }

    public Vector div(final double scalar) {
        return Vector.div(this, scalar);
    }

    public double dot(final Vector v) {
        return Vector.dot(this, v);
    }

    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector hadamard(final Vector v) {
        return Vector.hadamard(this, v);
    }

    public double angle(final Vector v) {
        return Vector.angle(this, v);
    }

    public static Vector neg(final Vector v) {
        return new Vector(-v.x, -v.y);
    }

    public static Vector add(final Vector v1, final Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }

    public static Vector subtract(final Vector v1, final Vector v2) {
        return new Vector(v1.x - v2.x, v1.y - v2.y);
    }

    public static Vector mult(final Vector v, final double scalar) {
        return new Vector(v.x * scalar, v.y * scalar);
    }

    public static Vector div(final Vector v, final double scalar) {
        return new Vector(v.x / scalar, v.y / scalar);
    }

    public static double dot(final Vector v1, final Vector v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    public static double norm(final Vector v) {
        return Math.sqrt(v.x * v.x + v.y * v.y);
    }

    public static Vector hadamard(final Vector v1, final Vector v2) {
        return new Vector(v1.x * v2.x, v1.y * v2.y);
    }

    /**
     * Computes angle between 2 vectors
     * 
     * @param v1 first vector
     * @param v2 second vector
     * @return angle in radians
     */
    public static double angle(final Vector v1, final Vector v2) {
        double norm1 = norm(v1);
        double norm2 = norm(v2);
        return Math.acos(v1.dot(v2) / (norm1 * norm2));
    }

    @Override
    public String toString() {
        return "%.14f %.14f".formatted(x, y);
    }
}
