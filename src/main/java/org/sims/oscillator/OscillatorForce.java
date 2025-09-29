package org.sims.oscillator;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.sims.interfaces.*;
import org.sims.models.*;

record OscillatorForce(double k, double gamma, double mass) implements Force<Particle<?>> {
    @Override
    public Map<Particle<?>, Vector3> apply(final Collection<Particle<?>> particles) {
        return particles.stream().collect(Collectors.toMap(Function.identity(), p -> {
            return Forces.oscillator(p, k, gamma, mass);
        }));
    }
}
