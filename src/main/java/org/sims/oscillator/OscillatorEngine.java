package org.sims.oscillator;

import java.util.*;

import org.sims.interfaces.*;

public record OscillatorEngine(Simulation<?> simulation) implements Engine<OscillatorStep> {
    @Override
    public OscillatorStep initial() {
        return new OscillatorStep(0);
    }

    @Override
    public Iterator<OscillatorStep> iterator() {
        return new Iterator<OscillatorStep>() {
            private long current = 0;

            @Override
            public boolean hasNext() {
                return current < simulation.steps();
            }

            @Override
            public OscillatorStep next() {
                return new OscillatorStep(++current);
            }
        };
    }

    @Override
    public void close() throws Exception {
    }
}
