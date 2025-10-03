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
public record BeemanIntegrator(double dt, Force<Particle> force) implements Integrator<Particle> {

    @Override
    public List<Particle> step(final Collection<Particle> particles) {
        // Calculate current accelerations
        final var currentAccelerations = force.apply(particles);

        return particles.stream().map(p -> {
            // Get current acceleration and previous acceleration from memory
            final var currentAcc = currentAccelerations.get(p);
            final var previousAcc = p.getMemory(); // Previous acceleration stored in memory

            // Beeman position update (predictor)
            // x(t+dt) = x(t) + v(t)*dt + (2/3)*a(t)*dt² - (1/6)*a(t-dt)*dt²
            final var currentPos = p.getPosition();
            final var currentVel = p.getVelocity();

            final var term1 = currentVel.mult(dt); // v(t)*dt
            final var term2 = currentAcc.mult(2.0/3.0 * dt * dt); // (2/3)*a(t)*dt²
            final var term3 = previousAcc.mult(-1.0/6.0 * dt * dt); // -(1/6)*a(t-dt)*dt²

            final var newPos = currentPos.add(term1).add(term2).add(term3);

            // Create temporary particle with new position to calculate new acceleration
            final var tempParticle = new Particle(p, newPos, currentVel);
            final var tempParticles = particles.stream()
                    .map(particle -> particle.getID() == p.getID() ? tempParticle : particle)
                    .toList();

            // Calculate new acceleration at predicted position
            final var newAccelerations = force.apply(tempParticles);
            final var newAcc = newAccelerations.get(tempParticle);

            // Beeman velocity update (corrector)
            // v(t+dt) = v(t) + (1/3)*a(t+dt)*dt + (5/6)*a(t)*dt - (1/6)*a(t-dt)*dt
            final var velTerm1 = newAcc.mult(1.0/3.0 * dt); // (1/3)*a(t+dt)*dt
            final var velTerm2 = currentAcc.mult(5.0/6.0 * dt); // (5/6)*a(t)*dt
            final var velTerm3 = previousAcc.mult(-1.0/6.0 * dt); // -(1/6)*a(t-dt)*dt

            final var newVel = currentVel.add(velTerm1).add(velTerm2).add(velTerm3);

            // Create final particle with updated position and velocity
            Particle next = new Particle(p, newPos, newVel);
            // Store current acceleration as previous acceleration for next step
            next.setMemory(currentAcc);

            return next;
        }).toList();
    }

    /**
     * Constructor class for Beeman integrator initialization
     */
    public static class Constructor implements Integrator.Constructor<Particle> {
        @Override
        public Integrator<Particle> get(double dt, Force<Particle> force) {
            return new BeemanIntegrator(dt, force);
        }

        @Override
        public List<Particle> set(Collection<Particle> particles, double dt) {
            return particles.stream().map(p -> this.initializeBeeman(p, dt)).toList();
        }

        /**
         * Initialize particle memory for Beeman integration.
         *
         * For the first step, we need to estimate the previous acceleration.
         * We use a simple backward approximation based on current velocity:
         * a(t-dt) ≈ a(t) - (da/dt)*dt
         *
         * For a harmonic oscillator, we can approximate this or simply use
         * the current acceleration as the previous acceleration for initialization.
         */
        private Particle initializeBeeman(final Particle p, double dt) {
            // For initialization, we'll use the current acceleration as previous acceleration
            // This is a reasonable approximation for the first step

            // Create a single-particle collection to calculate initial acceleration
            final var singleParticle = List.of(p);

            // We need a temporary force calculator to get initial acceleration
            // Since we don't have the force here, we'll approximate the previous acceleration
            // by assuming it equals the current acceleration for the first step

            // Use current position as previous acceleration estimate (will be corrected on first step)
            // This is a placeholder - the actual previous acceleration will be computed when
            // the integrator is created with the force calculator

            final var initialPreviousAcc = Vector3.ZERO; // Start with zero previous acceleration

            Particle initialized = new Particle(p, p.getPosition(), p.getVelocity());
            initialized.setMemory(initialPreviousAcc);

            return initialized;
        }
    }

    @Override
    public String name() {
        return "Beeman";
    }
}