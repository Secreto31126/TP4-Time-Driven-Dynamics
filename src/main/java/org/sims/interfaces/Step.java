package org.sims.interfaces;

import java.io.*;

/**
 * A step in a simulation.
 *
 * Stores the state of the simulation in the step i.
 */
public interface Step {
    /**
     * The step index
     *
     * @return the step index
     */
    long i();

    /**
     * Save the step to a writer
     *
     * @param writer the writer to save to
     * @throws IOException if an I/O error occurs
     */
    void saveTo(final Writer writer) throws IOException;
}
