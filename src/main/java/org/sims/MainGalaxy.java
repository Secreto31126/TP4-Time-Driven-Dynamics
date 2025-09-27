package org.sims;

import org.sims.galaxy.*;

import me.tongfei.progressbar.ProgressBar;

/**
 * If you are boring and don't want to use jdk25
 *
 * So much boilerplate 🤢
 */
public class MainGalaxy {
    private static final long SAVE_INTERVAL = 100L;
    private static final ProgressBar pb = new ProgressBar("Galaxing", -1);

    public static void main(final String[] args) throws Exception {
        final var simulation = GalaxySimulation.build(1000);
        final var onStep = new Orchestrator.SkipSteps(SAVE_INTERVAL, pb::step);

        try (pb; final var engine = new GalaxyEngine(simulation)) {
            pb.maxHint(simulation.steps());
            new Orchestrator(simulation, engine).start(onStep);
        }
    }
}
