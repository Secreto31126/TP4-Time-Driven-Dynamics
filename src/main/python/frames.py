from functools import cache
import os

import pandas as pd

from classes.particle import Particle
import resources

def next(f: int):
    """
    Reads the input file for a given frame.
    """
    file_path = resources.path('steps', f"{f}.txt")
    df = pd.read_csv(file_path, header=None, delimiter=' ').astype("float") # type: ignore[reportUnknownMemberType]
    return f, [Particle(*d) for _, d in df.iterrows()]

@cache
def count():
    """
    Returns the number of animations steps.
    """
    return len(os.listdir(resources.path('steps')))
