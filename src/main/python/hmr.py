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

def main():
    executor = Executor(frames.next, range(frames.count()))

    with open(resources.path("setup.txt"), "r") as f:
        line = f.readline().strip().split(' ')
        steps = int(line[0])
        dt = float(line[1])
        integral = line[-1]

    hmr = np.array([])
    for particles in tqdm(executor.stream(), total=frames.count()):
        hmr = np.append(hmr, ener.half_mass_radius(particles))

    threshold = None
    for i, r in enumerate(hmr):
        if r <= 1:
            if i > 0:
                threshold = (i - 1) * 10
            break

    return hmr, threshold, np.linspace(0, steps, frames.count()), integral, dt, len(frames.next(0)[1])

if __name__ == "__main__":
    hmr, threshold, steps, integral, dt, N = main()

    if threshold is not None:
        plt.axvline(threshold, linestyle='--', color='r') # pyright: ignore[reportUnknownMemberType]

        folder = resources.path('t-star', integral, str(N))
        os.makedirs(folder, exist_ok=True)
        with open(resources.path(folder, f'{int(time.time())}.txt'), 'w') as f:
            f.write(f"{threshold * dt}\n")
    else:
        print("No se alcanzó un t* con r < 1.")

    if "--no-plot" in sys.argv:
        exit(0)

    plt.plot(steps, hmr) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Pasos", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel(r"r$_{hm}$ (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.08)
    plt.show() # pyright: ignore[reportUnknownMemberType]

    i = input("Paso del estacionario (en blanco para no guardar): ")
    if i.strip() != "":
        hmr_est = int(int(i) / 10)
        folder = resources.path('hmr', integral, str(N))
        os.makedirs(folder, exist_ok=True)
        np.savetxt(resources.path(folder, f'{int(time.time())}.txt'), hmr[hmr_est:])
