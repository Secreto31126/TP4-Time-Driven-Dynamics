package org.sims.interfaces;

import java.io.*;

/**
 * Simulations define the setup and parameters for a simulation run.
 */
public interface Simulation<S extends Step> {
    /**
     * The number of steps in the simulation
     *
     * @return The number of steps
     */
    long steps();

    /**
     * Save the simulation setup to a writer
     * 
     * @param writer the writer to save to
     * @throws IOException if an I/O error occurs
     */
    void saveTo(final Writer writer) throws IOException;
}
