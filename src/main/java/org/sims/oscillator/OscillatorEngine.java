package org.sims.oscillator;

import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record OscillatorEngine(Simulation<?, Particle<?>> simulation) implements Engine<OscillatorStep> {
    @Override
    public OscillatorStep initial() {
        return new OscillatorStep(0, simulation.entities().getFirst());
    }

    @Override
    public Iterator<OscillatorStep> iterator() {
        return new Iterator<>() {
            private long current = 0;
            private List<Particle<?>> p = simulation.entities();

            @Override
            public boolean hasNext() {
                return current < simulation.steps();
            }

            @Override
            public OscillatorStep next() {
                p = simulation.integrator().step(p);
                return new OscillatorStep(++current, p.getFirst());
            }
        };
    }

    @Override
    public void close() throws Exception {
    }
}
