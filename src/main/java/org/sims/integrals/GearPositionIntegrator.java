package org.sims.integrals;

import org.sims.interfaces.ForceCalculator;
import org.sims.interfaces.Integrator;
import org.sims.models.Particle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Gear predictor-corrector for Forces that depend ONLY on position.
 */
public class GearPositionIntegrator extends GearIntegrator implements Integrator {

    private double dt;
    private ForceCalculator forceCalculator;
    public GearPositionIntegrator(double dt, ForceCalculator forceCalculator){
        this.dt = dt;
        this.forceCalculator = forceCalculator;

        //coefficients for a force only position-dependant
        setGearCoefficients(Map.of(
                2, List.of(0.0, 1.0, 0.0),
                3, List.of(1/6.0, 5.0/6.0, 1.0),
                4, List.of(19/20.0, 3.0/4.0, 1.0, 1/2.0, 1/12.0),
                5, List.of(3/20.0, 251/360.0, 1.0, 11/18.0, 1/6.0, 1/60.0)
        ));
    }

    @Override
    public List<Particle> step(Collection<Particle> particles) {

        for(Particle p : particles){
            //1. Predict
            List<Double> prevDerivatives = p.getDerivatives();
            List<Double> predictedDerivatives = computeDerivatives(prevDerivatives);

            //2. Calculate forces
            //TODO calculate forces and get acceleration

            //3. Correct
            //TODO correct predicted derivatives with new acceleration
        }
        return null;
    }

    /**
     * Computes the variation of the particles' derivatives
     *
     * @return List of derivatives up to order 5
     *
     */
    private List<Double> computeDerivatives(List<Double> prevDerivatives){
        return prevDerivatives; //TODO do
    }

    @Override
    public String name() {
        return "GearPosition";
    }
}
