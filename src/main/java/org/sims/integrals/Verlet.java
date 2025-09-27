package org.sims.integrals;

import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record Verlet(double dt, ForceCalculator force) implements Integrator {
    @Override
    public List<Particle> step(final Collection<Particle> particles) {
        final var list = List.copyOf(particles);
        final var acc = force.apply(list);

        return particles.stream().map(p -> {
            final var a = p.position().mult(2);
            final var b = p.position(1).neg();
            final var c = acc.get(p).mult(dt * dt);

            final var pos = a.add(b).add(c);
            final var vel = pos.subtract(p.position(1)).div(2 * dt);

            return new Particle(p, pos, vel);
        }).toList();
    }
}
