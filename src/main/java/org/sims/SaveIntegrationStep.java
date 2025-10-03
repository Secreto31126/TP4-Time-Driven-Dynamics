package org.sims;

import org.sims.models.Particle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

class SaveIntegrationStep implements Runnable {
    private final Object lock;
    private final List<Particle> particles;
    File file;

    public SaveIntegrationStep(int fileIndex, Object lock, List<Particle> particles) throws IOException {
        this.lock = lock;
        this.particles = particles;
        file = new File("sim/steps/" + fileIndex + ".txt");
    }

    @Override
    public void run() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Particle particle : particles) {
                writer.write(String.format("%s%n", particle));
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
