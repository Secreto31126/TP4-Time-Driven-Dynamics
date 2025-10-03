package org.sims.integrals;

import java.util.Collection;
import java.util.List;

import org.sims.interfaces.Force;
import org.sims.interfaces.Integrator;
import org.sims.models.Particle;

public record Beeman(Double dt, Force<Particle> force) implements Integrator<Particle> {
    @Override
    public List<Particle> step(final Collection<Particle> particles) {
        final var acc = force.apply(particles);

        return particles.stream().map(p -> {
            final var a = p.getPosition().mult(2);
            final var b = p.getMemory().neg();
            final var c = acc.get(p).mult(dt * dt);

            final var pos = a.add(b).add(c);
            final var vel = pos.subtract(p.getMemory()).div(2 * dt);

            Particle next = new Particle(p, pos, vel);
            next.setMemory(p.getPosition());
            return next;
        }).toList();
    }

    public static class Constructor implements Integrator.Constructor<Particle> {
        @Override
        public Integrator<Particle> get(double dt, Force<Particle> force) {
            return new Beeman(dt, force);
        }

        @Override
        public List<Particle> set(Collection<Particle> particles, double dt) {
            return particles.stream().map(p -> this.before(p, dt)).toList();
        }

        private Particle before(final Particle p, double dt) {
            final var before = p.getPosition().subtract(p.getVelocity().mult(dt));
            return new Particle(p, before, p.getVelocity());
        }
    }

    @Override
    public String name() {
        return "Verlet";
    }
}
