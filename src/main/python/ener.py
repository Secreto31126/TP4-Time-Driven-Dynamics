# Energy -> ener.py
# Sorry :]

from functools import reduce
import math

from classes.particle import Particle

def kinetic(particles: list[Particle]):
    """"
    Kinetic energy of a list of particles.

    Assumes mass == 1 for each particle.
    """
    return reduce(lambda acc, p: acc + p.velocity.norm() / 2, particles, 0.0)

def potential(particles: list[Particle], h: float):
    """
    Potential energy of a list of particles.

    Assumes G == 1 and mass == 1 for each particle.
    """
    H2 = h * h

    e = 0.0
    for i, p in enumerate(particles):
        for o in particles[i+1:]:
            r = p.position - o.position
            e += - 1 / math.sqrt(r.norm() + H2)

    return e
