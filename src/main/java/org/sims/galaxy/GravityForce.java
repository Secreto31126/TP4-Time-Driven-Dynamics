package org.sims.galaxy;

import org.sims.interfaces.Force;
import org.sims.models.Forces;
import org.sims.models.Particle;
import org.sims.models.Vector3;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GravityForce implements Force<Particle> {

    //TODO se puede optimizar pasando 1 vez por dupla de particulas?
    @Override
    public Map<Particle, Vector3> apply(Collection<Particle> particles) {

        Map<Particle, Vector3> accMap = new HashMap<>();

        // O(N^2) simple version; you can switch to i<j pairing later
        for (Particle p : particles) {
            for (Particle other : particles) {
                if (p == other) continue;
                Vector3 a = Forces.gravity(p, other); // should return *acceleration* on p due to other
                accMap.merge(p, a, Vector3::add);     // <-- accumulate properly
            }
        }
        return accMap;
    }
}
