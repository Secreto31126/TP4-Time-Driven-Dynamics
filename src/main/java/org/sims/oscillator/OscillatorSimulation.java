package org.sims.oscillator;

import java.io.*;
import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record OscillatorSimulation(long steps, double dt, List<Particle> entities, OscillatorForce force,
        Integrator<Particle> integrator)
        implements Simulation<Particle, OscillatorStep> {
    /**
     * Build a oscillation simulation
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
            final Integrator.Constructor<Particle> constructor) {
        final var force = new OscillatorForce(k, gamma, mass);
        final var integrator = constructor.get(dt, force);
        final var entities = constructor.set(OscillatorSimulation.initial(), dt);

        return new OscillatorSimulation(steps, dt, entities, force, integrator);
    }

    private static List<Particle> initial() {
        return List.of(new Particle(
                new Vector3(1, 0, 0),
                Vector3.ZERO,
                1));
    }

    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write(String.format(Locale.US,
                "%d %.14f %.14f %.14f %.14f %s\n",
                steps, dt, force.k(), force.gamma(), force.mass(), integrator.name()));
    }
}
