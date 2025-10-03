package org.sims;

import org.sims.galaxy.GravityForce;
import org.sims.integrals.BeemanIntegrator;
import org.sims.integrals.GearIntegrator;
import org.sims.integrals.GearType;
import org.sims.integrals.Verlet;
import org.sims.interfaces.Integrator;
import org.sims.models.Particle;
import org.sims.models.Vector3;
import org.sims.oscillator.OscillatorForce;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GalaxySimulator {
    public final double dt;
    public final long steps;
    public String integrationMethod;
    public final GravityForce force;
    public final long SAVE_INTERVAL;
    public final long N;
    public final double mass;
    public final double galaxyRadius;
    public final double particleRadius;
    public final double velocityMagnitude;
    public List<Particle> particles;

    public GalaxySimulator(final double dt, final double steps, final String integrationMethod, long SAVE_INTERVAL,
                           long N, double mass, double galaxyRadius, double particleRadius, double velocityMagnitude, List<Particle> particles) {
        this.mass = mass;
        this.N = N;
        this.dt = dt;
        this.integrationMethod = integrationMethod;
        this.galaxyRadius = galaxyRadius;
        this.particleRadius = particleRadius;
        this.velocityMagnitude = velocityMagnitude;
        this.particles = particles;
        this.force = new GravityForce();
        this.steps = (long) steps;
        this.SAVE_INTERVAL = SAVE_INTERVAL;
    }


    public void simulate() throws IOException {
        Integrator<Particle> integrator = null;
        String integratorName = "";

        Resources.init();
        Resources.prepareDir("steps");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Object lock = new Object();
        switch (integrationMethod) {
            case "gearposition":
                integrator = new GearIntegrator(dt, force, GearType.POSITION);
                integratorName = "GearGravityPosition";
                particles.forEach(particle -> particle.setDerivatives(particle.initializeGearGravityDerivatives(particle.getPosition(), particle.getVelocity())));
                break;
            case "gearvelocity":
                integrator = new GearIntegrator(dt, force, GearType.VELOCITY);
                integratorName = "GearGravityVelocity";
                particles.forEach(particle -> particle.setDerivatives(particle.initializeGearGravityDerivatives(particle.getPosition(), particle.getVelocity())));
                break;
            case "verlet":
                integrator = new Verlet(dt, force);
                integratorName = "VerletGravity";
                break;
            case "beeman":
                integrator = new BeemanIntegrator(dt, force);
                integratorName = "BeemanGravity";
                break;
            default:
                System.out.println("Unknown integration method: " + integrationMethod);
                System.out.println("Available methods: gearposition, gearvelocity, verlet");
                System.exit(1);
        }

        File file = new File("sim/setup.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(String.format(Locale.US, "%d %.14f %s\n",
                steps, dt, integratorName));
        writer.close();


        //============ Simulation loop ============
        for (int i = 0; i < steps; i++) {
            //1. Compute next step
            particles = integrator.step(particles); //TODO remove warning

            //2. Save to file every SAVE_INTERVAL steps
            if (i % SAVE_INTERVAL == 0) {
                System.out.println("Saving step " + i + "/" + steps);
                executor.submit(new SaveIntegrationStep((int) (i / SAVE_INTERVAL), lock, particles));
            }
        }

        executor.shutdown(); // stop accepting new tasks
        try {
            if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
                System.err.println("Some tasks did not finish in time!");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }
}
