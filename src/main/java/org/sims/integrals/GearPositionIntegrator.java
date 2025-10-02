package org.sims.integrals;

import org.sims.interfaces.Force;
import org.sims.interfaces.Integrator;
import org.sims.interfaces.Memory;
import org.sims.models.Particle;
import org.sims.models.Vector3;

import java.util.*;

/**
 * Gear predictor-corrector for Forces that depend ONLY on position.
 */
public class GearPositionIntegrator extends GearIntegrator implements Integrator<Particle> {

    private final double dt;
    private final Force<Particle> forceCalculator;
    public GearPositionIntegrator(double dt, Force<Particle> forceCalculator){
        this.dt = dt;
        this.forceCalculator = forceCalculator;

        //coefficients for a force only position-dependant
        setGearCoefficients(List.of(3/20.0, 251/360.0, 1.0, 11/18.0, 1/6.0, 1/60.0));
    }

    /**
     * Computes the variation of the particles' derivatives
     *
     * @return List of derivatives up to order 5
     *
     */
    private List<Vector3> computeDerivatives(List<Vector3> prevDerivatives) {
        int k = prevDerivatives.size() - 1;
        List<Vector3> newDerivatives = new ArrayList<>(Collections.nCopies(prevDerivatives.size(), Vector3.ZERO));

        for (int i = 0; i <= k; i++) {
            Vector3 sum = Vector3.ZERO;
            // For the i-th derivative, sum contributions from all higher-order derivatives j
            for (int j = i; j <= k; j++) {
                double factor = Math.pow(dt, j - i) / factorial(j - i);
                sum = sum.add(prevDerivatives.get(j).mult(factor));
            }
            newDerivatives.set(i, sum);
        }
        return newDerivatives;
    }

    @Override
    public List<Particle> step(Collection<Particle> entities) {

        //1. Predict Derivatives
        List<Particle> predictedParticles =  entities.stream().map(
                particle-> new Particle(particle, computeDerivatives(particle.getDerivatives())))
                .toList();

        //2. Calculate forces
        Map<Particle, Vector3> uncorrectedForces = forceCalculator.apply(predictedParticles);

        //3. Correct
        return correctParticles( uncorrectedForces, entities);
    }


    private List<Particle> correctParticles(Map<Particle, Vector3> newParticles, Collection<Particle> oldParticles){
        //1.Acceleration
        //a = F/m
        Map<Particle, Vector3> newAccelerations = new HashMap<>();
        for (var entry : newParticles.entrySet()) {
            Vector3 acceleration = entry.getValue();
            newAccelerations.put(entry.getKey(), acceleration);
        }

        List<Particle> correctedParticles = new ArrayList<>();

        // 2. Loop over particles and correct
        for (Map.Entry<Particle, Vector3> entry : newAccelerations.entrySet()) {
            var particle = entry.getKey();
            var acceleration = entry.getValue();

            List<Vector3> predictedDerivatives = particle.getDerivatives();
            List<Vector3> oldDerivatives = oldParticles.stream()
                    .filter(p -> p.getID() == particle.getID())
                    .findFirst()
                    .orElseThrow()
                    .getDerivatives();

            // Δr² = a_new - a_predicted   (vector!)
            Vector3 deltaR2 = acceleration.subtract(predictedDerivatives.get(2)).mult(dt * dt / 2.0);

            // Correct all derivatives
            List<Vector3> correctedDerivatives = correctDerivatives(predictedDerivatives, deltaR2);

            correctedParticles.add(new Particle(particle, correctedDerivatives));
        }

        return correctedParticles;

    }

    private int factorial(int n) {
        return (n <= 1) ? 1 : n * factorial(n - 1);
    }

    private List<Vector3> correctDerivatives(List<Vector3> predictedDerivatives, Vector3 deltaR2) {
        List<Vector3> correctedDerivatives = new ArrayList<>(predictedDerivatives.size());

        for (int i = 0; i < predictedDerivatives.size(); i++) {
            double alpha_i = getGearCoefficients().get(i);
            double scale = alpha_i * factorial(i) / Math.pow(dt, i);

            // Vector correction
            Vector3 r_i_corrected = predictedDerivatives.get(i).add(deltaR2.mult(scale));
            correctedDerivatives.add(r_i_corrected);
        }

        return correctedDerivatives;
    }
    @Override
    public String name() {
        return "GearPositionIntegrator";
    }

    public static class Constructor implements Integrator.Constructor<Particle> {
        @Override
        public Integrator<Particle> get(double dt, Force<Particle> force) {
            return new GearPositionIntegrator(dt, force);
        }

        @Override
        public List<Particle> set(Collection<Particle> particles, double dt) {
            return particles.stream().map(p -> this.before(p, dt)).toList();
        }

        private Particle before(final Particle p, double dt) {
            final var before = p.getPosition().subtract(p.getVelocity().mult(dt));
            //TODO getPosition ????
            return new Particle(p, before, p.getVelocity());
        }
    }
}
