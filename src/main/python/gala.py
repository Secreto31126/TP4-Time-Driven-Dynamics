# Galaxy -> gala.py
# Sorry :]

import os

import sys

import time

import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter

import numpy as np

from tqdm import tqdm

import ener
import frames
import resources
from streaming import SequentialStreamingExecutor as Executor

H = 0.05

def main():
    executor = Executor(frames.next, range(frames.count()))
def truncate_at_most_2(n: float) -> str:
    TRUNC = int(n * 100) / 100
    return str(TRUNC).rstrip('0').rstrip('.')

def sci_notation(val: float, _):
    if val == 0:
        return "0"

    EXP = int(np.floor(np.log10(abs(val))))
    COEFF = val / (10**EXP)

    return f"${truncate_at_most_2(COEFF)}\\times 10^{{{EXP}}}$"

    with open(resources.path("setup.txt"), "r") as f:
        line = f.readline().strip().split(' ')
        steps = int(line[0])
        dt = float(line[1])
        integral = line[-1]

    kin = np.array([])
    pot = np.array([])
    for particles in tqdm(executor.stream(), total=frames.count()):
        kin = np.append(kin, ener.kinetic(particles))
        pot = np.append(pot, ener.potential(particles, H))

    return kin, pot, kin + pot, np.linspace(0, steps, frames.count()) * dt, dt, integral, len(frames.next(0)[1])

if __name__ == "__main__":
    kin, pot, tot, steps, dt, integral, n = main()

    if "--no-save" in sys.argv:
        folder = resources.path('graverr', integral, str(n), str(dt))
        os.makedirs(folder, exist_ok=True)
        np.savetxt(resources.path(folder, f'{int(time.time())}.txt'), tot)

    if "--no-plot" in sys.argv:
        exit(0)

    if "--all" in sys.argv:
        plt.plot(steps, kin, label="Cinética") # pyright: ignore[reportUnknownMemberType]
        plt.plot(steps, pot, label="Potencial") # pyright: ignore[reportUnknownMemberType]

    plt.plot(steps, tot, label="Energía Total") # pyright: ignore[reportUnknownMemberType]

    plt.ticklabel_format(useOffset=False, style='plain')

    plt.gca().yaxis.set_major_formatter(FuncFormatter(sci_notation)) # pyright: ignore[reportUnknownArgumentType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Tiempo", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Energía", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.1)
    plt.show() # pyright: ignore[reportUnknownMemberType]
