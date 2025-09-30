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
 */
public class Particle implements Named{
    private static long SERIAL = 0L;
    private final long ID;
    private Vector3 position;
    private Vector3 velocity;
    private final double radius;
    private Vector3 memory;
    private List<Double> derivatives;

    /**
     * Create a new particle with a unique ID.
     *
     * @param position The initial position
     * @param velocity The initial velocity
     * @param radius   The radius of the particle
     * @param memory   The algorithm-specific data
     */
    private Particle(final Vector3 position, final Vector3 velocity, final double radius, final Vector3 memory) {
        this.ID = SERIAL++;
        this.position = position;
        this.velocity = velocity;
        this.radius = radius;
        this.memory = memory;
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
     */
    public Particle(final Particle p, final Vector3 pos, final Vector3 vel) {
        this.ID = p.ID;
        this.position = pos;
        this.velocity = vel;
        this.radius = p.radius;
        this.derivatives = initializeDerivatives(pos, velocity);

    }

    public Particle(Particle p, List<Double> derivatives) {
        this(p, p.getPosition(), p.getVelocity());
        this.derivatives = derivatives;
    }

    private List<Double> initializeDerivatives(Vector3 position, Vector3 velocity){
        List<Double> derivatives = new ArrayList<>();
        derivatives.add(position.x());
        derivatives.add(velocity.x());
        derivatives.add(0.0);
        derivatives.add(0.0);
        derivatives.add(0.0);
        derivatives.add(0.0);
        return derivatives;
    }
    public List<Double> getDerivatives() {
        return derivatives;
    }
    public void setDerivatives(List<Double> derivatives) {
        this.derivatives = derivatives;
    }

    public long getID() {
        return ID;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public double getRadius() {
        return radius;
    }

    public Vector3 getMemory() {
        return memory;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public void setMemory(Vector3 memory) {
        this.memory = memory;
    }

    public Particle getParticle(long ID){
        return this.ID == ID ? this : null;
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

    @Override
    public String name() {
        return "P";
    }

    @Override
    public String toString() {
        return "%s %s".formatted(position, velocity);
    }
}
