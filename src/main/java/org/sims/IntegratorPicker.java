package org.sims;

import org.sims.integrals.*;
import org.sims.interfaces.*;
import org.sims.interfaces.Integrator.Constructor;
import org.sims.models.*;

public enum IntegratorPicker {
    VERLET(new Verlet.Constructor());

    private final Integrator.Constructor<? extends Particle<?>> integrator;

    IntegratorPicker(Integrator.Constructor<? extends Particle<?>> integrator) {
        this.integrator = integrator;
    }

    @SuppressWarnings("unchecked") // I got sick of Java's generics :]
    public static Integrator.Constructor<Particle<?>> pick(final String name) {
        return (Constructor<Particle<?>>) IntegratorPicker.valueOf(name.toUpperCase()).integrator;
    }
}
