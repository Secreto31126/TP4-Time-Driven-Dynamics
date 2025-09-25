package org.sims.galaxy;

import java.io.*;

import org.sims.interfaces.Simulation;

public record GalaxySimulation(long steps) implements Simulation<GalaxyStep> {
    /**
     * Build a simulation
     *
     * @return the built simulation
     */
    public static GalaxySimulation build(final long steps) {
        return new GalaxySimulation(steps);
    }

    @Override
    public void saveTo(final Writer writer) throws IOException {
        writer.write("%d\n".formatted(steps));
    }
}
