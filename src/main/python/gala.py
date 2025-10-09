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

def truncate_at_most_2(n: float) -> str:
    TRUNC = int(n * 100) / 100
    return str(TRUNC).rstrip('0').rstrip('.')

def sci_notation(val: float, _):
    if val == 0:
        return "0"

    EXP = int(np.floor(np.log10(abs(val))))
    COEFF = val / (10**EXP)

    return f"${truncate_at_most_2(COEFF)}\\times 10^{{{EXP}}}$"

def main(cache_key: str | None = None):
    with open(resources.path("setup.txt"), "r") as f:
        line = f.readline().strip().split(' ')
        steps = int(line[0])
        dt = float(line[1])
        integral = line[-1]

    if cache_key:
        folder = resources.path('cache-galapy')
        kin = np.load(resources.path(folder, f"{cache_key}-kin.npy"))
        pot = np.load(resources.path(folder, f"{cache_key}-pot.npy"))
    else:
        executor = Executor(frames.next, range(frames.count()))

        kin = np.array([])
        pot = np.array([])

        for particles in tqdm(executor.stream(), total=frames.count()):
            kin = np.append(kin, ener.kinetic(particles))
            pot = np.append(pot, ener.potential(particles, H))

    return kin, pot, kin + pot, np.linspace(0, steps * dt, frames.count()), dt, integral, len(frames.next(0)[1])

if __name__ == "__main__":
    cache_key = next(filter(lambda x: x.startswith("--cache="), sys.argv), None)
    if cache_key:
        cache_key = "=".join(cache_key.split('=')[1:])

    kin, pot, tot, steps, dt, integral, n = main(cache_key)

    if "--cache" in sys.argv:
        folder = resources.path('cache-galapy')
        os.makedirs(folder, exist_ok=True)

        cache_key = str(int(time.time()))
        np.save(resources.path(folder, f"{cache_key}-kin"), kin)
        np.save(resources.path(folder, f"{cache_key}-pot"), pot)

        print(f"Cached with key: {cache_key}")

    if "--no-save" in sys.argv:
        folder = resources.path('graverr', integral, str(n), str(dt))
        os.makedirs(folder, exist_ok=True)
        np.savetxt(resources.path(folder, f'{int(time.time())}.txt'), tot)

    if "--no-plot" in sys.argv:
        exit(0)

    if "--all" in sys.argv:
        plt.plot(steps, kin, label=r"$E^k(t)$") # pyright: ignore[reportUnknownMemberType]
        plt.plot(steps, pot, label=r"$E^{pot}(t)$") # pyright: ignore[reportUnknownMemberType]

    plt.plot(steps, tot, label=r"$E(t)$") # pyright: ignore[reportUnknownMemberType]

    plt.ticklabel_format(useOffset=False, style='plain')

    plt.gca().yaxis.set_major_formatter(FuncFormatter(sci_notation)) # pyright: ignore[reportUnknownArgumentType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Tiempo", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Energía", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.1)
    plt.show() # pyright: ignore[reportUnknownMemberType]
