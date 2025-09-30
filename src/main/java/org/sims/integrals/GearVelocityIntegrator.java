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
        setGearCoefficients(List.of(3/16.0, 251/360.0, 1.0, 11/18.0, 1/6.0, 1/60.0));
    }

    @Override
    public String name() {
        return "GearVelocity";
    }

    @Override
    public List step(Collection entities) {
        return List.of();
    }
}
