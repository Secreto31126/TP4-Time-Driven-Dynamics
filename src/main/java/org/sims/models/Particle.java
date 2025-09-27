package org.sims.models;

import java.util.*;

import org.sims.interfaces.Named;

/**
 * A particle in 2D space with position, velocity, and radius.
 *
 * @apiNote positions are NOT immutable,
 * use position() to get the current immutable position.
 *
 * Each particle has a unique ID, auto-assigned on creation.
 */
public record Particle(long ID, LinkedList<Vector3> positions, Vector3 position, Vector3 velocity, double radius, List<Double> derivatives) implements Named {
    private static final long MEMORY = 5;

    private static long SERIAL = 0L;


    /**
     * Constructor for predictor-corrector methods.
     * Provides a way to modify derivatives in each step
     * @return New Particle with all parameters the same and modified derivatives
     */
    public Particle(final Particle p, final Vector3 pos, final Vector3 vel, List<Double> derivatives){
        this(p.ID, p.positions, pos, vel, p.radius, derivatives);
    }

    public Particle(final Vector3 position, final Vector3 velocity, final double radius, final double dt) {
        this(SERIAL++, positions(position, velocity, dt), position, velocity, radius, List.of());
    }

    public Particle(final Particle p, final Vector3 pos, final Vector3 vel) {
        this(p.ID, p.positions, pos, vel, p.radius, List.of());
        positions.addFirst(pos);
        positions.removeLast();
    }



    /**
     * Compute the gravitational force exerted by
     * the universe of particles on the particle
     *
     * @param particles The universe of particles
     * @return The total gravitational force exerted on this particle
     */
    public Vector3 gravity(final Collection<Particle> particles) {
        return particles.parallelStream()
                .filter(p -> p.ID != this.ID)
                .map(p -> Forces.gravity(this, p))
                .reduce(Vector3.ZERO, Vector3::add);
    }

    /**
     * Returns the position of the particle n steps back in history.
     *
     * @apiNote To get the current position, use the immutable position value.
     * This method is NOT thread-safe.
     *
     * @param i The number of steps back in history (0 for current position).
     * @return The particle position at the specified history step.
     */
    public Vector3 position(int i) {
        return positions.get(i);
    }

    public List<Double> getDerivatives(){
        return derivatives;
    }

    @Override
    public String name() {
        return "P";
    }

    @Override
    public String toString() {
        return "%s %s".formatted(position, velocity);
    }

    /**
     * Generates a list of previous positions based on the position and velocity.
     *
     * @param position The initial position.
     * @param velocity The initial velocity.
     * @param dt       The time step vector.
     * @return A linked list of MEMORY previous positions.
     */
    private static LinkedList<Vector3> positions(final Vector3 position, final Vector3 velocity, final double dt) {
        final var positions = new LinkedList<Vector3>(List.of(position));

        for (int i = 1; i < MEMORY; i++) {
            positions.push(position.subtract(velocity.mult(dt * i)));
        }

        return positions;
    }
}
