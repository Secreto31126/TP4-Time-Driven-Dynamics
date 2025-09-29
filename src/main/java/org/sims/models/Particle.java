package org.sims.models;

import java.util.*;

import org.sims.interfaces.*;

/**
 * A particle in 2D space with position, velocity, and radius.
 *
 * Each particle has a unique ID, auto-assigned on creation.
 *
 * Particles can optionally store a memory of type M, which
 * can save algorithm-specific data between iterations.
 *
 * @param <M> The type of memory the particle will save.
 */
public record Particle<M>(long ID, Vector3 position, Vector3 velocity, double radius, M memory) implements Named, Memory<M> {
    private static long SERIAL = 0L;

    /**
     * Create a new particle with a unique ID.
     *
     * @param position The initial position
     * @param velocity The initial velocity
     * @param radius   The radius of the particle
     * @param memory   The algorithm-specific data
     */
    private Particle(final Vector3 position, final Vector3 velocity, final double radius, final M memory) {
        this(SERIAL++, position, velocity, radius, memory);
    }

    /**
     * Create a new particle with a unique ID and no memory.
     *
     * @param position The initial position
     * @param velocity The initial velocity
     * @param radius   The radius of the particle
     */
    public Particle(final Vector3 position, final Vector3 velocity, final double radius) {
        this(position, velocity, radius, null);
    }

    /**
     * Copy constructor with new position, velocity and memory.
     *
     * @apiNote The new particle will preserve
     * the ID and radius of the original.
     *
     * @param p      The particle to copy
     * @param pos    The new position
     * @param vel    The new velocity
     * @param memory The new memory
     */
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
