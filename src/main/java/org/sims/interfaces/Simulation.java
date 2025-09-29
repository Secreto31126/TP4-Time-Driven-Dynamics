package org.sims.interfaces;

import java.io.*;
import java.util.*;

/**
 * Simulations define the setup and parameters for a simulation run.
 *
 * @param <S> the type of steps the simulation produces
 * @param <E> the type of the entities in the simulation
 */
public interface Simulation<S extends Step, E> {
    /**
     * The number of steps in the simulation
     *
     * @return The number of steps
     */
    long steps();

    /**
     * The entities in the simulation
     *
     * @return The entities
     */
    List<E> entities();

    /**
     * The integrator used in the simulation
     *
     * @return The integrator
     */
    Integrator<E> integrator();

    /**
     * Save the simulation setup to a writer
     * 
     * @param writer the writer to save to
     * @throws IOException if an I/O error occurs
     */
    void saveTo(final Writer writer) throws IOException;
}
