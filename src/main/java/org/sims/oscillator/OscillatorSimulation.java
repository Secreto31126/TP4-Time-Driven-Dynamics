package org.sims.oscillator;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public class OscillatorSimulation implements Simulation<OscillatorStep, Particle> {
    private final long steps;
    private final Particle entities;
    private final double k;
    private final double gamma;
    private final double mass;
    private final Integrator integrator;

    private OscillatorSimulation(final long steps, final double dt, final Particle entities,
            final double k, final double gamma, final double mass,
            final Integrator.Constructor constructor) {
        this.steps = steps;
        this.entities = entities;
        this.k = k;
        this.mass = mass;
        this.gamma = gamma;
        this.integrator = constructor.get(dt, this::oscillate);
    }

    /**
     * Build a simulation
     *
     * @param constructor the integrator to use
     * @param steps       the number of steps to simulate
     * @param dt          the time step
     * @param k           TODO: Idk
     * @param gamma       TODO: Idk
     * @param mass        the mass of the particle
     * @return the built simulation
     */
    public static OscillatorSimulation build(final long steps, final double dt,
            final double k, final double gamma, final double mass,
            final Integrator.Constructor constructor) {
        final var p = new Particle(new Vector3(1, 1, 1), Vector3.ZERO, 1, dt);

        return new OscillatorSimulation(steps, dt, p, k, gamma, mass, constructor);
    }

    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write("%d\n".formatted(steps));
    }

    private Map<Particle, Vector3> oscillate(final Collection<Particle> particles) {
        return particles.stream().collect(Collectors.toMap(Function.identity(), p -> {
            return Forces.oscillator(p, k, gamma, mass);
        }));
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
