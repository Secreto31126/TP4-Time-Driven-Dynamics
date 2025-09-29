package org.sims.integrals;

import java.util.*;

public abstract class GearIntegrator{

    private Map<Integer, List<Double>> gearCoefficients;

    public Map<Integer, List<Double>> getGearCoefficients(){
        return gearCoefficients;
    }

    public void setGearCoefficients(Map<Integer,List<Double>> gearCoefficients){
        this.gearCoefficients = gearCoefficients;
    }
}
