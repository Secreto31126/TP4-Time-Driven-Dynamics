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
    public List<Vector3> initializeGearGravityDerivatives(Vector3 position, Vector3 velocity){
        //mu = G*m*m
        double mu = 1;

        Vector3 r0 = position;
        Vector3 r1 = velocity;

        double r2sq = r0.dot(r0);              // |r|^2
        double rmag  = Math.sqrt(r2sq);        // |r|
        double invR  = 1.0 / rmag;
        double invR3 = invR * invR * invR;     // 1/|r|^3
        double invR5 = invR3 * invR * invR;    // 1/|r|^5

        // a = -μ r / |r|^3
        Vector3 r2 = r0.mult(-mu * invR3);

        // j = -μ ( v / |r|^3 - 3 (r·v) r / |r|^5 )
        double rv = r0.dot(r1);
        Vector3 r3 = r1.mult(-mu * invR3)
                .add(r0.mult(3.0 * mu * rv * invR5));

        // If you want, start r4 and r5 at zero; Gear will refine after the first step.
        Vector3 r4 = Vector3.ZERO;
        Vector3 r5 = Vector3.ZERO;

        return List.of(r0, r1, r2, r3, r4, r5);

    }

    /**
     * Creates N particles around a nucleous to simulate a galaxy.
     * @param N The number of particles to create
     * @param nucleous The position of the nucleous
     * @param galaxyRadius The maximum distance from the nucleous
     * @return A list of particles
     */
    public static List<Particle> spawnGalaxy(final long N, final Vector3 nucleous, final double galaxyRadius, final double particleRadius, final double velocityMagnitude){
        List<Particle> particles = new ArrayList<>();
        for(int i=0; i<N; i++){
            double randPositionX = (Math.random() * 2 - 1) * galaxyRadius;
            double randPositionY = (Math.random() * 2 - 1) * galaxyRadius;
            double randPositionZ = (Math.random() * 2 - 1) * galaxyRadius;
            Vector3 position = new Vector3(randPositionX, randPositionY, randPositionZ).add(nucleous);

            //random velocity with fixed magnitude
            double randVelocityX = (Math.random() * 2 - 1);
            double randVelocityY = (Math.random() * 2 - 1);
            double randVelocityZ = (Math.random() * 2 - 1);
            Vector3 velocity = new Vector3(randVelocityX, randVelocityY, randVelocityZ);
            double velocityNorm = velocity.norm();

            // v = v/|v| * fixedMagnitude
            Vector3 normalizedVelocity = velocity.div(velocityNorm).mult(velocityMagnitude);
            Particle p = new Particle(position, normalizedVelocity, particleRadius);

            particles.add(p);
        }

        return particles;
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
