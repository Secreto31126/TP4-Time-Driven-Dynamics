package org.sims;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
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
        final double springConstant = (double) params.get("spring_constant");
        final double dampingCoefficient = (double) params.get("damping_coefficient");
        final double mass = (double) params.get("mass");
        final double steps = (double) params.get("steps");

        new GalaxySimulator(dt, steps, integrator, springConstant, dampingCoefficient, mass, SAVE_INTERVAL).simulate();
    }

}
