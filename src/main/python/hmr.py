import os
import matplotlib.pyplot as plt

import numpy as np

from tqdm import tqdm

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

    return hmr, threshold, np.linspace(0, steps, frames.count()), dt, integral

if __name__ == "__main__":
    hmr, threshold, steps, dt, integral = main()

    plt.plot(steps, hmr) # pyright: ignore[reportUnknownMemberType]

    if threshold is not None:
        plt.axvline(threshold, linestyle='--', color='r') # pyright: ignore[reportUnknownMemberType]

        folder = resources.path('t-star', integral)
        os.makedirs(folder, exist_ok=True)
        with open(resources.path(folder, f'{dt}.txt'), 'w') as f:
            f.write(f"{threshold}\n")
    else:
        print("No se alcanzó un t* con r < 1.")

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Pasos", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Radio de masa media (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.06)
    plt.show() # pyright: ignore[reportUnknownMemberType]

    n = input("Paso del estacionario (en blanco para no guardar): ")
    if n.strip() != "":
        hmr_est = int(n) / 10
        folder = resources.path('hmr', integral)
        os.makedirs(folder, exist_ok=True)
        np.savetxt(resources.path(folder, f'{dt}.txt'), hmr[hmr_est:])
