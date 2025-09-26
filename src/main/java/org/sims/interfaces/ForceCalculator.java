package org.sims.interfaces;

import java.util.*;
import java.util.function.BiFunction;

import org.sims.models.*;

public interface ForceCalculator extends BiFunction<Collection<Particle>, List<Double>, Map<Particle, Vector3>> {
}
