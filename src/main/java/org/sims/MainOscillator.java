package org.sims;

import com.google.gson.Gson;
import org.sims.integrals.GearPositionIntegrator;
import org.sims.integrals.Verlet;
import org.sims.interfaces.Force;
import org.sims.interfaces.Integrator;
import org.sims.models.Particle;
import org.sims.models.Vector3;
import org.sims.oscillator.*;

import me.tongfei.progressbar.ProgressBar;

import java.io.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainOscillator {

    public static void main(final String[] args) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        FileReader reader = new FileReader("src/main/resources/params.json");
        Map<String, Object> params = gson.fromJson(reader, type);

        final double dt = (double) params.get("dt");
        final String integrator = params.get("integrator").toString();
        final double springConstant = (double) params.get("spring_constant");
        final double dampingCoefficient = (double) params.get("damping_coefficient");
        final double mass = (double) params.get("mass");
        final double steps = (double) params.get("steps");

        new Simulator(dt, steps, integrator, springConstant, dampingCoefficient, mass).simulate();
    }

}

class Simulator{
    public final double dt;
    public final long steps;
    public String integrationMethod;
    public final OscillatorForce force;
    public static final long SAVE_INTERVAL = 10L;

    public Simulator(final double dt, final double steps, final String integrationMethod, Double springConstant, Double dampingCoefficient, Double mass) {
        this.dt = dt;
        this.integrationMethod = integrationMethod;
        this.force = new OscillatorForce(springConstant, dampingCoefficient, mass);
        this.steps = (long) steps;
    }


    public void simulate() throws IOException {
        Integrator<Particle> integrator = null;
        List<Particle> particles = List.of(new Particle(new Vector3(1,0,0), Vector3.ZERO, 1.0));
        String integratorName = "";

        Resources.init();
        Resources.prepareDir("steps");

        ExecutorService executor = Executors.newFixedThreadPool(4);
        Object lock = new Object();
        switch(integrationMethod){
            case "gearposition":
                integrator = new GearPositionIntegrator(dt, force);
                integratorName = "Gear Position";
                break;
            case "gearvelocity":
                //TODO
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
        writer.write(String.format(Locale.US,"%d %.14f %.14f %.14f %.14f %s\n",
                steps, dt, force.k(), force.gamma(), force.mass(), integratorName));
        writer.close();

        for(int i=0; i < steps; i++){
            //1. Compute next step
            particles = integrator.step(particles); //TODO remove warning

            //2. Save to file every SAVE_INTERVAL steps
            if(i%SAVE_INTERVAL == 0){
                System.out.println("Saving step " + i + "/" + steps);
                executor.submit(new SaveIntegrationStep((int) (i/SAVE_INTERVAL), lock, particles));
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

class SaveIntegrationStep implements Runnable {
    private final Object lock;
    private final List<Particle> particles;
    File file;

    public SaveIntegrationStep(int fileIndex, Object lock, List<Particle> particles) throws IOException {
        this.lock = lock;
        this.particles = particles;
        file = new File("sim/steps/"+ fileIndex+".txt");
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Particle particle : particles) {
                writer.write(String.format("%s%n", particle));
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

