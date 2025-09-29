package org.sims.integrals;

import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

public record Verlet(double dt, Force<Particle<Vector3>> force) implements Integrator<Particle<Vector3>> {
    @Override
    public List<Particle<Vector3>> step(final Collection<Particle<Vector3>> particles) {
        final var acc = force.apply(particles);

        return particles.stream().map(p -> {
            final var a = p.position().mult(2);
            final var b = p.memory().neg();
            final var c = acc.get(p).mult(dt * dt);

            final var pos = a.add(b).add(c);
            final var vel = pos.subtract(p.memory()).div(2 * dt);

            return new Particle<Vector3>(p, pos, vel, p.position());
        }).toList();
    }

    public static class Constructor implements Integrator.Constructor<Particle<Vector3>> {
        @Override
        public Integrator<Particle<Vector3>> get(double dt, Force<Particle<Vector3>> force) {
            return new Verlet(dt, force);
        }

        @Override
        public List<Particle<Vector3>> set(Collection<Particle<Vector3>> particles) {
            return particles.stream().map(p -> new Particle<>(p, p.position(), p.velocity(), p.position())).toList();
        }
    }

    @Override
    public String name() {
        return "Verlet";
    }
}
