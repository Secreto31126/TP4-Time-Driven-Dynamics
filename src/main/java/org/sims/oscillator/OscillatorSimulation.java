package org.sims.oscillator;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.sims.integrals.Verlet;
import org.sims.interfaces.Integrator;
import org.sims.interfaces.Simulation;
import org.sims.models.Forces;
import org.sims.models.Particle;
import org.sims.models.Vector3;

public record OscillatorSimulation(long steps, Particle particle, Integrator integrator)
        implements Simulation<OscillatorStep> {
    /**
     * Build a simulation
     *
     * @param integrator the integrator to use
     * @param steps      the number of steps to simulate
     * @param dt         the time step
     * @param amplitude  the amplitude of the oscillator
     * @param omega      the angular frequency of the oscillator TODO: Huh?
     * @return the built simulation
     */
    public static OscillatorSimulation build(final Integrators integrator, final long steps, final double dt,
            final double amplitude, final double omega) {
        final var p = new Particle(new Vector3(1, 1, 1), Vector3.ZERO, 1, dt);
        return new OscillatorSimulation(steps, p, integrator.get());
    }

    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write("%d\n".formatted(steps));
    }

    public enum Integrators {
        VERLET(new Verlet(0.01, List.of(1.0, 2.0), OscillatorSimulation::oscillate));

        private final Integrator integrator;

        Integrators(final Integrator integrator) {
            this.integrator = integrator;
        }

        public Integrator get() {
            return integrator;
        }
    }

    private static Map<Particle, Vector3> oscillate(final Collection<Particle> particles, final List<Double> params) {
        // Get the oscillating particle (there's only one)
        final var particle = particles.stream().findAny().orElseThrow();
        final var amplitude = params.get(0);
        final var omega = params.get(1);

        return Map.of(particle, Forces.oscillator(particle, amplitude, omega));
    }
}
