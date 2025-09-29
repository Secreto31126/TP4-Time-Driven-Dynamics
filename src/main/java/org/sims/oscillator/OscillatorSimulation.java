package org.sims.oscillator;

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record OscillatorSimulation(
        long steps, double dt, List<Particle<?>> entities, double k, double gamma, double mass,
        Integrator<Particle<?>> integrator)
        implements Simulation<OscillatorStep, Particle<?>> {
    private OscillatorSimulation(final long steps, final double dt, final List<Particle<?>> entities,
            final double k, final double gamma, final double mass,
            final Integrator.Constructor<Particle<?>> constructor) {
        this(steps, dt, entities, k, gamma, mass, constructor.get(dt, new Force(k, gamma, mass)));
    }

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
            final Integrator.Constructor<Particle<?>> constructor) {
        final var entities = constructor.set(List.of(
                new Particle<>(
                        new Vector3(1, 0, 0),
                        Vector3.ZERO,
                        1)));

        return new OscillatorSimulation(steps, dt, entities, k, gamma, mass, constructor);
    }

    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write(String.format(Locale.US,
                "%d %.14f %.14f %.14f %.14f %s\n",
                steps, dt, k, gamma, mass, integrator.name()));
    }

    private record Force(double k, double gamma, double mass) implements ForceCalculator<Particle<?>> {
        @Override
        public Map<Particle<?>, Vector3> apply(final Collection<Particle<?>> particles) {
            return particles.stream().collect(Collectors.toMap(Function.identity(), p -> {
                return Forces.oscillator(p, k, gamma, mass);
            }));
        }
    }
}
