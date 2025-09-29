package org.sims.interfaces;

import java.util.*;
import java.util.function.*;

import org.sims.models.*;

/**
 * A Force calculates the pull over every entity
 * in a collection. For each entity, it returns
 * a Vector3 representing the force acting on it.
 *
 * A force implementation must of course know the
 * type of entities it works with, which will
 * generally include information about position,
 * mass, velocity, charge, etc.
 *
 * @param <E> The type of entities the force works with.
 */
public interface Force<E> extends Function<Collection<E>, Map<E, Vector3>> {
}
