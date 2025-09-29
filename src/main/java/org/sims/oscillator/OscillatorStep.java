package org.sims.oscillator;

import java.io.*;

import org.sims.interfaces.Step;
import org.sims.models.*;

public record OscillatorStep(long i, Particle<?> particle) implements Step {
    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write(particle.toString());
        writer.write('\n');
    }
}
