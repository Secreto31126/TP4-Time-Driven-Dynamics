package org.sims;

import java.util.*;

import org.sims.integrals.*;
import org.sims.interfaces.*;

public abstract class IntegratorPicker {
    private static final Map<String, Integrator.Constructor> INTEGRATORS = Map.of(
            // "beeman", Beeman.class,
            // "gear", GearPredictorCorrector.class,
            "verlet", Verlet::new);

    public static Integrator.Constructor pick(final String name) {
        return INTEGRATORS.getOrDefault(name.toLowerCase(), Verlet::new);
    }
}
