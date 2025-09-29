package org.sims.interfaces;

import java.util.*;

/**
 * @param <E> the type of entities the integrator works with
 */
public interface Integrator<E> extends Named {
    /**
     * Advance the simulation by one time step
     *
     * @param particles the particles to move
     * @return the moved particles
     */
    List<E> step(final Collection<E> particles);

    /**
     * A constructor for integrators
     *
     * @param <E> the type of entities the integrator works with
     */
    public interface Constructor<E> {
        /**
         * Create an integrator instance
         *
         * @param dt    the time step
         * @param force the force calculator
         * @return the integrator instance
         */
        Integrator<E> get(double dt, Force<E> force);

        /**
         * Initialize the particles' memories
         *
         * @param particles the particles to initialize
         * @return the initialized particles
         */
        List<E> set(final Collection<E> particles);
    }
}
