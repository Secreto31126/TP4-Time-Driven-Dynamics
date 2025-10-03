# Galaxy -> gala.py
# Sorry :]

import matplotlib.pyplot as plt

import numpy as np

from tqdm import tqdm

import ener
import frames
import resources
from streaming import SequentialStreamingExecutor as Executor

H = 0.05

def main():
    executor = Executor(frames.next, range(frames.count()))

    with open(resources.path("setup.txt"), "r") as f:
        line = f.readline().strip().split(' ')
        steps = int(line[0])

    kin = np.array([])
    pot = np.array([])
    for particles in tqdm(executor.stream(), total=frames.count()):
        kin = np.append(kin, ener.kinetic(particles))
        pot = np.append(pot, ener.potential(particles, H))

    return kin, pot, kin + pot, np.linspace(0, steps, frames.count())

if __name__ == "__main__":
    kin, pot, tot, steps = main()

    plt.plot(steps, kin, label="Cinética") # pyright: ignore[reportUnknownMemberType]
    plt.plot(steps, pot, label="Potencial") # pyright: ignore[reportUnknownMemberType]
    plt.plot(steps, tot, label="Energía Total") # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Pasos", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Energía (J)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.1)
    plt.show() # pyright: ignore[reportUnknownMemberType]
