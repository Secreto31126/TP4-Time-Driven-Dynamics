package org.sims.models;

public abstract class Forces {
    public static final double G = 1;
    public static final double H = 0.05;

    public static Vector3 gravity(final Particle p1, final Particle p2) {
        final var rij = p1.position().subtract(p2.position());
        final var r2 = rij.norm2();
        final var factor = - Math.pow(r2 + H * H, 1.5);
        return rij.div(factor);
    }
}
