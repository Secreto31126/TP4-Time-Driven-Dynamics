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

        Map<Particle, Vector3> forcesMap = new HashMap<>();
        for (Particle p : particles) {
            for(final Particle other : particles){
                if(!p.equals(other)){
                    Vector3 force = Forces.gravity(p, other);

                    // Sum the forces acting on particle p to previous contributions
                    forcesMap.putIfAbsent(p, forcesMap.getOrDefault(p, Vector3.ZERO).add(force));
                }
            }
        }
        return forcesMap;
    }
}
