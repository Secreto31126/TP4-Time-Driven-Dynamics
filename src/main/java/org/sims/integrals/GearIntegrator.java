package org.sims.integrals;

import java.util.*;

public abstract class GearIntegrator{

    private List<Double> gearCoefficients;

    public List<Double> getGearCoefficients(){
        return gearCoefficients;
    }

    public void setGearCoefficients(List<Double> gearCoefficients){
        this.gearCoefficients = gearCoefficients;
    }
}
