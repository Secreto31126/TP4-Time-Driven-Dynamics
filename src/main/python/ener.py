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
    return reduce(lambda acc, p: acc + p.velocity.norm2() / 2, particles, 0.0)

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
            e += - 1 / math.sqrt(r.norm2() + H2)

    return e

def center_of_mass(particles: list[Particle]):
    """
    Center of mass of a list of particles.

    Assumes mass == 1 for each particle.
    """
    n = float(len(particles))
    pos = reduce(lambda acc, p: acc + p.position, particles, particles[0].position * 0)
    return pos / n

def half_mass_radius(particles: list[Particle]):
    """
    Half-mass radius of a list of particles.

    Assumes mass == 1 for each particle.
    """
    com = center_of_mass(particles)

    distances = sorted([(p.position - com).norm() for p in particles])

    half_index = len(distances) // 2
    if len(distances) % 2 == 0:
        return (distances[half_index - 1] + distances[half_index]) / 2
    else:
        return distances[half_index]
