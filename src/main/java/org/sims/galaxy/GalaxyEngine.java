package org.sims.galaxy;

import java.util.*;

import org.sims.interfaces.Engine;

public record GalaxyEngine(GalaxySimulation simulation) implements Engine<GalaxyStep> {
    @Override
    public GalaxyStep initial() {
        return new GalaxyStep(0);
    }

    @Override
    public Iterator<GalaxyStep> iterator() {
        return new Iterator<>() {
            private long current = 0;

            @Override
            public boolean hasNext() {
                return current < simulation.steps();
            }

            @Override
            public GalaxyStep next() {
                return new GalaxyStep(++current);
            }
        };
    }

    @Override
    public void close() throws Exception {
    }
}
