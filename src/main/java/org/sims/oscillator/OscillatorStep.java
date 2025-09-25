package org.sims.oscillator;

import java.io.*;

import org.sims.interfaces.Step;

public record OscillatorStep(long i) implements Step {
    @Override
    public void saveTo(Writer writer) throws IOException {
        writer.write(Long.toString(i));
        writer.write('\n');
    }
}
