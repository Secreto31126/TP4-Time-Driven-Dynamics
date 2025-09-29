package org.sims.interfaces;

import java.util.*;

/**
 * Integrators are the mathematical implementations of the
 * time evolution algorithms, such as Verlet, or Beeman.
 *
 * As the responsible of moving the entities, it must know
 * the entities type and the memory they need to save,
 * hence the existance of a nested Constructor interface.
 *
 * Constructor requires the user to provide a static class
 * that creates the integrator instance and initializes
 * the entities' memories as deemed necesary by the
 * algorithm.
 *
 * @param <E> the type of entities the integrator works with.
 */
public interface Integrator<E extends Memory<?>> extends Named {
    /**
     * Advance the simulation by one time step
     *
     * @param entities the entities to move
     * @return the moved entities
     */
    List<E> step(final Collection<E> entities);

    /**
     * A constructor for integrators
     *
     * @param <E> the type of entities the integrator works with
     */
    public interface Constructor<E extends Memory<?>> {
        /**
         * Create an integrator instance
         *
         * @param dt    the time step
         * @param force the force calculator
         * @return the integrator instance
         */
        Integrator<E> get(double dt, Force<E> force);

        /**
         * Initialize the entities' memories
         *
         * @param entities the entities to initialize
         * @return the initialized entities
         */
        List<E> set(final Collection<E> entities);
    }
}
