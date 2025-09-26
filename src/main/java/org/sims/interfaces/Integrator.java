package org.sims.interfaces;

import java.util.*;

import org.sims.models.Particle;

public interface Integrator {
    List<Particle> step(final Collection<Particle> particles);
}
