package org.sims;

import com.google.gson.Gson;

import java.io.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MainOscillator {
    private static final long SAVE_INTERVAL = 10L;

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

        new OscilationSimulator(dt, steps, integrator, springConstant, dampingCoefficient, mass, SAVE_INTERVAL).simulate();
    }

}

