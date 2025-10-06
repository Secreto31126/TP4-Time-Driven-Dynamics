package org.sims.integrals;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sims.interfaces.Force;
import org.sims.interfaces.Integrator;
import org.sims.models.Particle;
import org.sims.models.Vector3;

public record Beeman(Double dt, Force<Particle> force, Map<Particle, Vector3> acc) implements Integrator<Particle> {
    @Override
    public List<Particle> step(final Collection<Particle> particles) {
        final var dt2 = dt * dt;

        final var moved = particles.stream().map(p -> {
            final var future_pos = p.getPosition()
                    .add(p.getVelocity().mult(dt))
                    .add(acc.get(p).mult((2.0 / 3.0) * dt2))
                    .subtract(p.getMemory().mult((1.0 / 6.0) * dt2));
            final Vector3 predicted_vel = p.getVelocity()
                .add(acc.get(p).mult((3.0 / 2.0) * dt))
                .subtract(p.getMemory().mult((1.0 / 2.0) * dt));

            return new Particle(p, future_pos, predicted_vel);
        }).toList();

        final var future_acc = force.apply(moved);

        return particles.stream().map(p -> {
            final var vel = p.getVelocity()
                    .add(future_acc.get(p).mult((1.0 / 3.0) * dt))
                    .add(acc.get(p).mult((5.0 / 6.0) * dt))
                    .subtract(p.getMemory().mult((1.0 / 6.0) * dt));

            final var future_pos = p.getPosition()
                    .add(p.getVelocity().mult(dt))
                    .add(acc.get(p).mult((2.0 / 3.0) * dt2))
                    .subtract(p.getMemory().mult((1.0 / 6.0) * dt2));

            p.setMemory(acc.get(p));
            acc.put(p, future_acc.get(p));

            return new Particle(p, future_pos, vel);
        }).toList();
    }

    public static class Constructor implements Integrator.Constructor<Particle> {
        @Override
        public Integrator<Particle> get(double dt, Force<Particle> force) {
            return new Beeman(dt, force, new HashMap<>());
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
