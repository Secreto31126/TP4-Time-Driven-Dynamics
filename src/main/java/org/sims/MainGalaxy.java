package org.sims;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.sims.models.Particle;
import org.sims.models.Vector3;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * If you are boring and don't want to use jdk25
 *
 * So much boilerplate 🤢
 */
//public class MainGalaxy {
//    private static final long SAVE_INTERVAL = 100L;
//
//    public static void main(final String[] args) throws Exception {
//        final var simulation = GalaxySimulation.build(1000);
//
//        final var pb = new ProgressBar("Galaxing", simulation.steps());
//        final var onStep = new Orchestrator.SkipSteps(SAVE_INTERVAL, pb::step);
//
//        try (pb; final var engine = new GalaxyEngine(simulation)) {
//            new Orchestrator(simulation, engine).start(onStep);
//        }
//    }
//}



public class MainGalaxy {
    private static final long SAVE_INTERVAL = 100L;
    public static void main(final String[] args) throws Exception {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        FileReader reader = new FileReader("src/main/resources/params.json");
        Map<String, Object> params = gson.fromJson(reader, type);

        final double dt = (double) params.get("dt");
        final String integrator = params.get("integrator").toString();
        final double steps = (double) params.get("steps");
        final long N1 = ((Double) params.get("N1")).longValue();
        final long N2 = ((Double) params.get("N2")).longValue();
        final double galaxyRadius = (double) params.get("galaxy_radius");
        final double particleRadius = (double) params.get("particle_radius");
        final double velocityMagnitude = (double) params.get("velocity_magnitude");
        final double mass = (double) params.get("mass");
        List<Particle> particles = new ArrayList<>();
        final boolean single_galaxy = (boolean) params.get("single_galaxy");
        if (!single_galaxy) {
            List<Particle> galaxy1 = Particle.spawnGalaxy(N1, new Vector3(-2*galaxyRadius, 0, 0), galaxyRadius, particleRadius, velocityMagnitude, true, 0.1);
            List<Particle> galaxy2 = Particle.spawnGalaxy(N2, new Vector3(2*galaxyRadius, 0, 0), galaxyRadius, particleRadius, -velocityMagnitude, true, -0.1);
            galaxy1.addAll(galaxy2);
            particles.addAll(galaxy1);
        }
        else{
            particles = Particle.spawnGalaxy(N1, Vector3.ZERO, galaxyRadius, particleRadius, velocityMagnitude);
        }

        new GalaxySimulator(dt, steps, integrator, SAVE_INTERVAL, N1, mass, galaxyRadius, particleRadius, velocityMagnitude, particles).simulate();
    }

}
