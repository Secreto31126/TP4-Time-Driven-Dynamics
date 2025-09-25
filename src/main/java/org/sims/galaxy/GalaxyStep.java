package org.sims.galaxy;

import java.io.*;

import org.sims.interfaces.Step;

/**
 * A step in the simulation
 *
 * @param i the step index
 */
public record GalaxyStep(long i) implements Step {
    @Override
    public void saveTo(final Writer writer) throws IOException {
        writer.write(Long.toString(i));
        writer.write('\n');
    }
}
