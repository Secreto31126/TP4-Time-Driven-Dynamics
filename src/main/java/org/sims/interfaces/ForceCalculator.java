package org.sims.interfaces;

import java.util.*;
import java.util.function.*;

import org.sims.models.*;

public interface ForceCalculator<E> extends Function<Collection<E>, Map<E, Vector3>> {
}
