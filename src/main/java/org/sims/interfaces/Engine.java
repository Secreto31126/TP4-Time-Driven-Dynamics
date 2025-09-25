package org.sims.interfaces;

/**
 * Engines are used to run simulations step by step.
 */
public interface Engine<S extends Step> extends Iterable<S>, AutoCloseable {
    /**
     * Get the initial step of the simulation
     *
     * @return the initial step
     */
    S initial();
}
