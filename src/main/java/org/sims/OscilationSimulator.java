package org.sims;

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

class OscilationSimulator {
    public final double dt;
    public final long steps;
    public String integrationMethod;
    public final OscillatorForce force;
    public final long SAVE_INTERVAL;

    public OscilationSimulator(final double dt, final double steps, final String integrationMethod, Double springConstant, Double dampingCoefficient, Double mass, long SAVE_INTERVAL) {
        this.dt = dt;
        this.integrationMethod = integrationMethod;
        this.force = new OscillatorForce(springConstant, dampingCoefficient, mass);
        this.steps = (long) steps;
        this.SAVE_INTERVAL = SAVE_INTERVAL;
    }


    public void simulate() throws IOException {
        Integrator<Particle> integrator = null;
        List<Particle> particles = List.of(new Particle(new Vector3(1, 0, 0), Vector3.ZERO, 1.0));
        String integratorName = "";

        Resources.init();
        Resources.prepareDir("steps");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Object lock = new Object();
        switch (integrationMethod) {
            case "gearposition":
                integrator = new GearIntegrator(dt, force, GearType.POSITION);
                integratorName = "GearPosition";
                particles.forEach(particle -> particle.initializeGearSpringDerivatives(
                        particle.getPosition(), particle.getVelocity(), force.k(), force.mass(), force.gamma()));
                break;
            case "gearvelocity":
                integrator = new GearIntegrator(dt, force, GearType.VELOCITY);
                integratorName = "GearVelocity";
                particles.forEach(particle -> particle.initializeGearSpringDerivatives(
                        particle.getPosition(), particle.getVelocity(), force.k(), force.mass(), force.gamma()));
                break;
            case "verlet":
                integrator = new Verlet(dt, force);
                integratorName = "Verlet";
                break;
            default:
                System.out.println("Unknown integration method: " + integrationMethod);
                System.out.println("Available methods: gearposition, gearvelocity, verlet");
                System.exit(1);
        }

        File file = new File("sim/setup.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(String.format(Locale.US, "%d %.14f %.14f %.14f %.14f %s\n",
                steps, dt, force.k(), force.gamma(), force.mass(), integratorName));
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
