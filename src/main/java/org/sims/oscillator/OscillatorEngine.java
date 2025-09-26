package org.sims.oscillator;

import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record OscillatorEngine(OscillatorSimulation simulation) implements Engine<OscillatorStep> {
    @Override
    public OscillatorStep initial() {
        return new OscillatorStep(0, simulation.particle());
    }

    @Override
    public Iterator<OscillatorStep> iterator() {
        return new Iterator<OscillatorStep>() {
            private long current = 0;
            private Particle p = simulation.particle();

            @Override
            public boolean hasNext() {
                return current < simulation.steps();
            }

            @Override
            public OscillatorStep next() {
                p = simulation.integrator().step(List.of(p)).getFirst();
                return new OscillatorStep(++current, p);
            }
        };
    }

    @Override
    public void close() throws Exception {
    }
}
