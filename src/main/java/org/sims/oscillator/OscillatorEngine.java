package org.sims.oscillator;

import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record OscillatorEngine(Simulation<?, Particle> simulation) implements Engine<OscillatorStep> {
    @Override
    public OscillatorStep initial() {
        return new OscillatorStep(0, simulation.entities());
    }

    @Override
    public Iterator<OscillatorStep> iterator() {
        return new Iterator<OscillatorStep>() {
            private long current = 0;
            private Particle p = simulation.entities();

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
