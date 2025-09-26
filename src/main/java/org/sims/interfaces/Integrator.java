package org.sims.interfaces;

import java.util.*;

import org.sims.models.Particle;

public interface Integrator {
    /**
     * Advance the simulation by one time step
     *
     * @param particles the particles to move
     * @return the moved particles
     */
    List<Particle> step(final Collection<Particle> particles);

    public interface Constructor {
        Integrator get(double dt, ForceCalculator force);
    }
}
