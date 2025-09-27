package org.sims;

import java.io.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.function.*;

import org.sims.interfaces.*;

public record Orchestrator(Simulation<?, ?> simulation, Engine<?> engine) {
    /**
     * Start the simulation
     *
     * @param onStep a callback called on each step, returning the idx to save
     *               the step to, or empty to skip saving.
     */
    public void start(final Function<Step, Optional<Long>> onStep) throws Exception {
        Resources.init();
        Resources.prepareDir("steps");

        try (final var writer = Resources.writer("setup.txt")) {
            simulation.saveTo(writer);
        }

        try (final var animator = Executors.newFixedThreadPool(3)) {
            animator.submit(new Animator(engine.initial(), 0L));
            engine.forEach(step -> onStep.apply(step).ifPresent(idx -> animator.submit(new Animator(step, idx))));
        }
    }

    /**
     * A task to save an animation step
     */
    private static record Animator(Step step, Long idx) implements Runnable {
        @Override
        public void run() {
            final var filename = "%d.txt".formatted(idx);

            try (final var writer = Resources.writer("steps", filename)) {
                step.saveTo(writer);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }
}
