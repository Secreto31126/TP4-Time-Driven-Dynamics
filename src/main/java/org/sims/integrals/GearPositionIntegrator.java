package org.sims.integrals;

import org.sims.interfaces.Force;
import org.sims.interfaces.ForceCalculator;
import org.sims.interfaces.Integrator;
import org.sims.interfaces.Memory;
import org.sims.models.Particle;
import org.sims.models.Vector3;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Gear predictor-corrector for Forces that depend ONLY on position.
 */
public class GearPositionIntegrator extends GearIntegrator implements Integrator<Particle<List<Double>>> {

    private double dt;
    private Force forceCalculator;
    public GearPositionIntegrator(double dt, Force forceCalculator){
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
    public List<Particle<List<Double>>> step(Collection<Particle<List<Double>>> entities) {
        for(Particle p : entities){
            //1. Predict
            List<Double> prevDerivatives = p.memory();
            List<Double> predictedDerivatives = computeDerivatives(prevDerivatives);

            //2. Calculate forces
            //TODO calculate forces and get acceleration

            //3. Correct
            //TODO correct predicted derivatives with new acceleration
        }
        return null;
    }

    @Override
    public String name() {
        return "Gear Position Integrator";
    }
}
