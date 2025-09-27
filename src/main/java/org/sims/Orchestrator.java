package org.sims;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;

import org.sims.interfaces.*;

public record Orchestrator(Simulation<?, ?> simulation, Engine<?> engine) {
    /**
     * Start the simulation
     *
     * @param onStep The OnStep event handler.
     */
    public void start(final OnStep onStep) throws Exception {
        Resources.init();
        Resources.prepareDir("steps");

        try (final var writer = Resources.writer("setup.txt")) {
            simulation.saveTo(writer);
        }

        try (final var animator = Executors.newFixedThreadPool(3)) {
            save(animator, engine.initial(), 0L);
            engine.forEach(step -> onStep.apply(step).ifPresent(idx -> save(animator, step, idx)));
        }
    }

    /**
     * A function called on each step.
     *
     * Returning empty optionals allow the caller to skip saving steps.
     * Similar to a filter, but it requires the idx to save to.
     */
    public interface OnStep extends Function<Step, Optional<Long>> {
    }

    /**
     * Save a step with idx using an executor service
     */
    private static void save(final ExecutorService ex, final Step step, final Long idx) {
        ex.submit(new Animator(step, idx));
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
