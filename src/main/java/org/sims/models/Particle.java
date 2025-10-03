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
    private List<Vector3> derivatives;
    private static final double DEFAULT_SPRING_CONSTANT = 10.0;
    private static final double DEFAULT_PARTICLE_MASS = 1.0;
    private static final double DEFAULT_DAMPENING_CONSTANT = 1.0;

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
        this(position, velocity, radius, position);
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
    }

    public Particle(Particle p, List<Vector3> derivatives) {
        //replaciong position and velocity by derivatives 0 and 1 -> same thing but updated
        this(p, derivatives.get(0), derivatives.get(1));
        this.derivatives = derivatives;
    }

    public void initializeGearSpringDerivatives(Vector3 position, Vector3 velocity, double springConstant, double particleMass, double dampeningConstant){
        double k = springConstant;
        double m = particleMass;
        double gamma = dampeningConstant;
        Vector3 r0 = position;
        Vector3 r1 = velocity;
        Vector3 r2 = r0.mult(-k/m).add(r1.mult(-gamma/m));
        Vector3 r3 = r1.mult(-k/m).add(r2.mult(-gamma/m));
        Vector3 r4 = r2.mult(-k/m).add(r3.mult(-gamma/m));
        Vector3 r5 = r3.mult(-k/m).add(r4.mult(-gamma/m));

        this.derivatives = List.of(r0, r1, r2, r3, r4, r5);
    }
    public static List<Vector3> initializeGearGravityDerivatives(Vector3 position, Vector3 velocity, double springConstant, double particleMass, double dampeningConstant){
        double k = springConstant;
        double m = particleMass;
        double gamma = dampeningConstant;
        Vector3 r0 = position;
        Vector3 r1 = velocity;
        Vector3 r2 = r0.mult(-k/m).add(r1.mult(-gamma/m));
        Vector3 r3 = r1.mult(-k/m).add(r2.mult(-gamma/m));
        Vector3 r4 = r2.mult(-k/m).add(r3.mult(-gamma/m));
        Vector3 r5 = r3.mult(-k/m).add(r4.mult(-gamma/m));

        return List.of(r0, r1, r2, r3, r4, r5);

    }
    public List<Vector3> getDerivatives() {
        return derivatives;
    }
    public void setDerivatives(List<Vector3> derivatives) {
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
    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Particle o))
            return false;

        return ID == o.ID;
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(ID);
    }
}
