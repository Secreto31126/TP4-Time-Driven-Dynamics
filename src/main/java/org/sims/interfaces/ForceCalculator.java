package org.sims.interfaces;

import java.util.Collection;
import java.util.function.BiFunction;

import org.sims.models.*;

public interface ForceCalculator extends BiFunction<Particle, Collection<Particle>, Vector3> {
}
