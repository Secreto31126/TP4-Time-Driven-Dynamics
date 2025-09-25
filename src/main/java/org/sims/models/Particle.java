package org.sims.models;

import org.sims.interfaces.Named;

/**
 * A particle in 2D space with position, velocity, and radius.
 *
 * Each particle has a unique ID, auto-assigned on creation.
 */
public record Particle(long ID, Vector position, Vector velocity, double radius) implements Named {
    private static long SERIAL = 0L;

    public Particle(final Vector position, final Vector velocity, final double radius) {
        this(SERIAL++, position, velocity, radius);
    }

    /**
     * Move the particle a delta time
     *
     * @param dt time step
     * @return new particle data
     */
    public Particle move(final double dt) {
        final var p = position.add(velocity.mult(dt));
        return new Particle(ID, p, velocity, radius);
    }

    @Override
    public String name() {
        return "P";
    }

    @Override
    public String toString() {
        return "%s %s".formatted(position, velocity);
    }
}
