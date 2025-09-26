package org.sims.oscillator;

import java.io.*;
import java.util.*;

import org.sims.interfaces.Integrator;
import org.sims.interfaces.Simulation;
import org.sims.models.Forces;
import org.sims.models.Particle;
import org.sims.models.Vector3;

public class OscillatorSimulation implements Simulation<OscillatorStep, Particle> {
    private final long steps;
    private final Particle entities;
    private final double amplitude;
    private final double omega;
    private final Integrator integrator;

    private OscillatorSimulation(final long steps, final double dt, final Particle entities, final double amplitude, final double omega,
            final Integrator.Constructor constructor) {
        this.steps = steps;
        this.entities = entities;
        this.amplitude = amplitude;
        this.omega = omega;
        this.integrator = constructor.get(dt, this::oscillate);
    }

    /**
     * Build a simulation
     *
     * @param constructor the integrator to use
     * @param steps      the number of steps to simulate
     * @param dt         the time step
     * @param amplitude  the amplitude of the oscillator
     * @param omega      the angular frequency of the oscillator TODO: Huh?
     * @return the built simulation
     */
    public static OscillatorSimulation build(final long steps, final double dt,
            final double amplitude, final double omega, final Integrator.Constructor constructor) {
        final var p = new Particle(new Vector3(1, 1, 1), Vector3.ZERO, 1, dt);

        return new OscillatorSimulation(steps, dt, p, amplitude, omega, constructor);
    }

    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write("%d\n".formatted(steps));
    }

    private Map<Particle, Vector3> oscillate(final Collection<Particle> particle) {
        final var p = particle.iterator().next();
        return Map.of(p, Forces.oscillator(p, amplitude, omega));
    }

    @Override
    public long steps() {
        return steps;
    }

    @Override
    public Particle entities() {
        return entities;
    }

    @Override
    public Integrator integrator() {
        return integrator;
    }
}
