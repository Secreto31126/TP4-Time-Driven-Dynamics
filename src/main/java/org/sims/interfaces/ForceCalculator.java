package org.sims.interfaces;

import java.util.*;
import java.util.function.*;

import org.sims.models.*;

public interface ForceCalculator extends Function<Collection<Particle>, Map<Particle, Vector3>> {
}
