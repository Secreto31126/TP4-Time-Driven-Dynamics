package org.sims.galaxy;

import java.io.*;
import java.util.List;

import org.sims.interfaces.*;
import org.sims.models.*;

public record GalaxySimulation(long steps, List<Particle<?>> entities, Integrator<Particle<?>> integrator)
        implements Simulation<Particle<?>, GalaxyStep> {
    /**
     * Build a simulation
     *
     * @return the built simulation
     */
    public static GalaxySimulation build(final long steps) {
        return new GalaxySimulation(steps, List.of(), null);
    }

    @Override
    public void saveTo(final Writer writer) throws IOException {
        writer.write("%d\n".formatted(steps));
    }
}
