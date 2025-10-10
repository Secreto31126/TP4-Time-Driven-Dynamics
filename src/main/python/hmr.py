import matplotlib.pyplot as plt

import numpy as np

from tqdm import tqdm

import os

import sys

import time

import ener
import frames
import resources
from streaming import SequentialStreamingExecutor as Executor

def main(cache_key: str | None = None):
    with open(resources.path("setup.txt"), "r") as f:
        line = f.readline().strip().split(' ')
        steps = int(line[0])
        dt = float(line[1])
        integral = line[-1]

    if cache_key:
        folder = resources.path('cache-hmr')
        hmr = np.load(resources.path(folder, f"{cache_key}.npy"))
    else:
        executor = Executor(frames.next, range(frames.count()))

        hmr = np.array([])
        for particles in tqdm(executor.stream(), total=frames.count()):
            hmr = np.append(hmr, ener.half_mass_radius(particles))

    tstar = None
    for i in range(1, len(hmr) - 2):
        if hmr[i - 1] <= 1 and hmr[i + 1] > 1:
            if i > 0:
                tstar = (i - 1) * 10
            break

    return hmr, tstar, np.linspace(0, steps * dt, frames.count()), integral, dt, len(frames.next(0)[1])

if __name__ == "__main__":
    cache_key = next(filter(lambda x: x.startswith("--cache="), sys.argv), None)
    if cache_key:
        cache_key = "=".join(cache_key.split('=')[1:])

    hmr, tstar, steps, integral, dt, N = main(cache_key)

    if "--cache" in sys.argv:
        folder = resources.path('cache-hmr')
        os.makedirs(folder, exist_ok=True)

        cache_key = str(int(time.time()))
        np.save(resources.path(folder, cache_key), hmr)

        print(f"Cached with key: {cache_key}")

    if tstar is not None:
        plt.axvline(tstar, linestyle='--', color='r', label='t*') # pyright: ignore[reportUnknownMemberType]

        if "--no-save" not in sys.argv:
            folder = resources.path('t-star', integral, str(N))
            os.makedirs(folder, exist_ok=True)
            with open(resources.path(folder, f'{int(time.time())}.txt'), 'w') as f:
                f.write(f"{tstar}\n")
    else:
        print("No se alcanzó un t*")

    if "--no-plot" not in sys.argv:
        plt.plot(steps, hmr, label=r"$r_{hm}$(t)") # pyright: ignore[reportUnknownMemberType]

        plt.ticklabel_format(useOffset=False, style='plain')

        plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
        plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

        plt.xlabel("Tiempo", fontsize=24) # pyright: ignore[reportUnknownMemberType]
        plt.ylabel(r"$r_{hm}$", fontsize=24) # pyright: ignore[reportUnknownMemberType]

        if tstar is not None:
            plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

        plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.06)
        plt.show() # pyright: ignore[reportUnknownMemberType]

    if "--no-save" not in sys.argv:
        i = input("Paso del estacionario (en blanco para no guardar): ")
        if i.strip() != "":
            hmr_est = int(int(i) / 10)
            folder = resources.path('hmr', integral, str(N))
            os.makedirs(folder, exist_ok=True)
            np.savetxt(resources.path(folder, f'{int(time.time())}.txt'), hmr[hmr_est:])
