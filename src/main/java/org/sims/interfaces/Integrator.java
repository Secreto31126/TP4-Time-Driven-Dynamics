package org.sims.interfaces;

import java.util.*;

import org.sims.models.Particle;

public interface Integrator {
    void step(final Collection<Particle> particles);
}
