package org.sims;

import org.sims.interfaces.Step;
import org.sims.oscillator.*;

import me.tongfei.progressbar.ProgressBar;

/**
 * If you are boring and don't want to use jdk25
 *
 * So much boilerplate 🤢
 */
public class MainOscillator {
    private static final long SAVE_INTERVAL = 10L;
    private static final ProgressBar pb = new ProgressBar("Oscillating", -1);

    public static void main(final String[] args) throws Exception {
        final var simulation = OscillatorSimulation.build(1000);

        try (pb; final var engine = new OscillatorEngine(simulation)) {
            pb.maxHint(simulation.steps());
            new Orchestrator(simulation, engine).start(MainOscillator::onStep);
        }
    }

    private static Long onStep(final Step step) {
        pb.step();

        if (step.i() % SAVE_INTERVAL != 0) {
            return null;
        }

        return step.i() / SAVE_INTERVAL;
    }
}
