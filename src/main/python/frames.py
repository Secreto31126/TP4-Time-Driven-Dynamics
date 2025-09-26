from functools import cache
import os

from classes.particle import Particle
import resources

def next(f: int):
    """
    Reads the input file for a given frame.
    """
    file_path = resources.path('steps', f"{f}.txt")
    with open(file_path, 'r') as file:
        return f, [Particle(*map(float, line.strip().split())) for line in file]

@cache
def count():
    """
    Returns the number of animations steps.
    """
    return len(os.listdir(resources.path('steps')))
