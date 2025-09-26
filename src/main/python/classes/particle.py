from dataclasses import dataclass

from classes.vector import Vector

@dataclass(frozen=True, init=False)
class Particle:
    position: Vector
    velocity: Vector
    radius: float

    def __init__(self, x: float, y: float, z: float, vx: float, vy: float, vz: float):
        object.__setattr__(self, 'position', Vector(x, y, z))
        object.__setattr__(self, 'velocity', Vector(vx, vy, vz))
        object.__setattr__(self, 'radius', 1)
