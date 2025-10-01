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
    private List<Double> computeDerivatives(List<Double> prevDerivatives){
        int k = prevDerivatives.size() - 1; // order = 5 → size = 6
        List<Double> newDerivatives = new ArrayList<>(Collections.nCopies(prevDerivatives.size(), 0.0));

        for (int i = 0; i <= k; i++) {
            double value = 0.0;
            for (int j = i; j <= k; j++) {
                value += prevDerivatives.get(j) * Math.pow(dt, j - i) / factorial(j - i);
            }
            newDerivatives.set(i, value);
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
        //m=1 -> F=a
        Map<Particle, Vector3> newAccelerations = newParticles;

       List<Particle> correctedParticles = new ArrayList<>();
        for(Map.Entry<Particle, Vector3> entry : newAccelerations.entrySet()){
            var particle = entry.getKey();
            var acceleration = entry.getValue();
            List<Double> predictedDerivatives = particle.getDerivatives();
            List<Double> derivatives = oldParticles.stream()
                    .filter(p -> p.getID() == particle.getID())
                    .findFirst()
                    .orElseThrow()
                    .getDerivatives();
            //deltaR2 = (a-a_predicted)^2/2
            double deltaR2 = Math.pow(derivatives.get(2) - predictedDerivatives.get(2),2)/2;

            //Correct each derivative
            List<Double> correctedDerivatives = correctDerivatives(predictedDerivatives, deltaR2);
            correctedParticles.add(new Particle(particle, correctedDerivatives));
        }
        return correctedParticles;
    }

    private double factorial(int n){
        if(n == 0) return 1;
        return n * factorial(n-1);
    }
    private List<Double> correctDerivatives(List<Double> predictedDerivatives, double deltaR2){
        List<Double> correctedDerivatives = new ArrayList<>(Collections.nCopies(predictedDerivatives.size(), 0.0));
        for(int i = 0; i < predictedDerivatives.size(); i++){
            double r_i = predictedDerivatives.get(i);
            double alpha_i = getGearCoefficients().get(i);
            double r_i_corrected = r_i + alpha_i * deltaR2 * factorial(i) /Math.pow(dt, i) ;
            correctedDerivatives.set(i, r_i_corrected);
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
