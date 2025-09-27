package org.sims.integrals;

import org.sims.interfaces.Integrator;
import org.sims.models.Particle;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Gear predictor-corrector for Forces that depend ONLY on position.
 */
public class GearVelocityIntegrator extends GearIntegrator implements Integrator {


    //coefficients for a force position and velocity-dependant
    public GearVelocityIntegrator(){
        setGearCoefficients(Map.of(
                2, List.of(0.0, 1.0, 0.0),
                3, List.of(1/6.0, 5.0/6.0, 1.0),
                4, List.of(19/90.0, 3.0/4.0, 1.0, 1/2.0, 1/12.0),
                5, List.of(3/16.0, 251/360.0, 1.0, 11/18.0, 1/6.0, 1/60.0)
        ));
    }

    @Override
    public List<Particle> step(Collection<Particle> particles) {

        return null;
    }

    @Override
    public String name() {
        return "GearVelocity";
    }
}
