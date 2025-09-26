package org.sims.models;

public abstract class Forces {
    public static final double G = 1;
    public static final double H = 0.05;

    /**
     * Newtonian gravity with softening
     *
     * @apiNote Assumes G = 1, m1 = m2 = 1
     *
     * @param p1 The first particle
     * @param p2 The second particle
     * @return The force exerted by p2 on p1
     */
    public static Vector3 gravity(final Particle p1, final Particle p2) {
        final var rij = p1.position().subtract(p2.position());
        final var r2 = rij.norm2();
        final var factor = -Math.pow(r2 + H * H, 1.5);
        return rij.div(factor);
    }

    /**
     * Harmonic oscillation force
     *
     * @apiNote Assumes m = 1, r0 = Vector3.ZERO
     *
     * @param p         The particle
     * @param amplitude The oscillating amplitude
     * @param omega     The angular frequency
     * @return
     */
    public static Vector3 oscillator(final Particle p, final double amplitude, final double omega) {
        return p.position().mult(-amplitude).add(p.velocity().mult(-omega));
    }
}
