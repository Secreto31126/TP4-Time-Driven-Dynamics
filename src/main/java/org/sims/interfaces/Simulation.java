package org.sims.interfaces;

import java.io.*;

/**
 * Simulations define the setup and parameters for a simulation run.
 */
public interface Simulation<S extends Step, E> {
    /**
     * The number of steps in the simulation
     *
     * @return The number of steps
     */
    long steps();

    /**
     * The entities involved in the simulation
     *
     * @return A collection of entities
     */
    E entities();

    /**
     * The integrator used in the simulation
     *
     * @return The integrator
     */
    Integrator integrator();

    /**
     * Save the simulation setup to a writer
     * 
     * @param writer the writer to save to
     * @throws IOException if an I/O error occurs
     */
    void saveTo(final Writer writer) throws IOException;
}
