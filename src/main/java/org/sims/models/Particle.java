package org.sims.models;

import java.util.*;

import org.sims.interfaces.Named;

/**
 * A particle in 2D space with position, velocity, and radius.
 *
 * Each particle has a unique ID, auto-assigned on creation.
 */
public record Particle<M>(long ID, Vector3 position, Vector3 velocity, double radius, M memory) implements Named {
    private static long SERIAL = 0L;

    private Particle(final Vector3 position, final Vector3 velocity, final double radius, final M memory) {
        this(SERIAL++, position, velocity, radius, memory);
    }

    public Particle(final Vector3 position, final Vector3 velocity, final double radius) {
        this(position, velocity, radius, null);
    }

    public Particle(final Particle<M> p, final Vector3 pos, final Vector3 vel, final M memory) {
        this(p.ID, pos, vel, p.radius, memory);
    }

    /**
     * Compute the gravitational force exerted by
     * the universe of particles on the particle
     *
     * @param particles The universe of particles
     * @return The total gravitational force exerted on this particle
     */
    public Vector3 gravity(final Collection<Particle<M>> particles) {
        return particles.parallelStream()
                .filter(p -> p.ID != this.ID)
                .map(p -> Forces.gravity(this, p))
                .reduce(Vector3.ZERO, Vector3::add);
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
