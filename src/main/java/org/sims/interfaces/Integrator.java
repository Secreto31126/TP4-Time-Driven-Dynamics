package org.sims.interfaces;

import java.util.*;

import org.sims.models.Particle;

public interface Integrator {
    List<Particle> step(final Collection<Particle> particles);

    public interface Constructor {
        Integrator get(double dt, ForceCalculator force);
    }
}
