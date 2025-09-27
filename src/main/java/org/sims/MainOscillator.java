package org.sims;

import org.sims.oscillator.*;

import me.tongfei.progressbar.ProgressBar;

/**
 * If you are boring and don't want to use jdk25
 *
 * So much boilerplate 🤢
 */
public class MainOscillator {
    private static final long SAVE_INTERVAL = 10L;

    public static void main(final String[] args) throws Exception {
        final var seconds = 5.0;
        final var dt = 1e-4;

        final var integrator = IntegratorPicker.pick(args[0]);
        final var simulation = OscillatorSimulation.build((long) (seconds / dt), dt, 1e4, 100, 70.0, integrator);

        final var pb = new ProgressBar("Oscillating", simulation.steps());
        final var onStep = new Orchestrator.SkipSteps(SAVE_INTERVAL, pb::step);

        try (pb; final var engine = new OscillatorEngine(simulation)) {
            new Orchestrator(simulation, engine).start(onStep);
        }
    }
}
