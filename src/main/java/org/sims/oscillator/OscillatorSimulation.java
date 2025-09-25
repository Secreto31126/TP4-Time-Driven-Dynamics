package org.sims.oscillator;

import java.io.*;

import org.sims.interfaces.Simulation;

public record OscillatorSimulation(long steps) implements Simulation<OscillatorStep> {
    /**
     * Build a simulation
     *
     * @return the built simulation
     */
    public static OscillatorSimulation build(final long steps) {
        return new OscillatorSimulation(steps);
    }

    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write("%d\n".formatted(steps));
    }
}
