package org.sims;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.*;

import org.sims.interfaces.*;

public record Orchestrator(Simulation<?, ?> simulation, Engine<?> engine) {
    /**
     * Start the simulation.
     *
     * OnStep is called on each step and can be used
     * to filter which steps to save.
     *
     * @apiNote Saves the setup and steps in the "steps" directory.
     * @apiNote The step 0 is always saved.
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
     * A simple OnStep implementation that skips saving every n steps
     * and notifies a callback on each execution.
     */
    public record SkipSteps(long n, Runnable callback) implements OnStep {
        @Override
        public Optional<Long> apply(final Step step) {
            callback.run();
            return step.i() % n == 0 ? Optional.of(step.i() / n) : Optional.empty();
        }
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
