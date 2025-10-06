package org.sims.integrals;

import java.util.*;

import org.sims.interfaces.*;
import org.sims.models.*;

/**
 * Beeman predictor-corrector integrator for second-order ODEs.
 *
 * The Beeman algorithm improves accuracy over Velocity-Verlet by using both
 * current and previous accelerations to compute positions and velocities.
 *
 * Position update (predictor):
 * x(t+dt) = x(t) + v(t)*dt + (2/3)*a(t)*dt² - (1/6)*a(t-dt)*dt²
 *
 * Velocity update (corrector):
 * v(t+dt) = v(t) + (1/3)*a(t+dt)*dt + (5/6)*a(t)*dt - (1/6)*a(t-dt)*dt
 *
 * Requires storing previous acceleration for each particle.
 * Accuracy: O(dt³) for positions, O(dt²) for velocities.
 */
public record BeemanIntegrator(double dt, Force<Particle> force, Map<Particle, Vector3> acc)
        implements Integrator<Particle> {
    public BeemanIntegrator(double dt, Force<Particle> force, Collection<Particle> particles) {
        this(dt, force, force.apply(particles));

        final var lacosamocosa = particles.stream().map(p -> {
            final var previous_pos = p.getPosition()
                .subtract(p.getVelocity().mult(dt))
                .subtract(acc.get(p).mult((1.0 / 2.0) * dt * dt));
            final var previous_spd = p.getVelocity()
                .subtract(acc.get(p).mult(dt));
            return new Particle(p, previous_pos, previous_spd);
        }).toList();

        final var previous_acc = force.apply(lacosamocosa);
        particles.forEach(p -> p.setMemory(previous_acc.get(p)));
    }

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

            final var laparticulosa = new Particle(p, future_pos, vel);
            laparticulosa.setMemory(acc.get(p));

            acc.put(p, future_acc.get(p));

            return laparticulosa;
        }).toList();
    }

    @Override
    public String name() {
        return "Beeman";
    }
}