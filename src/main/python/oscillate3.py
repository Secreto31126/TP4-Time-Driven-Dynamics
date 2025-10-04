from typing import Callable

import matplotlib.pyplot as plt

import numpy as np

from tqdm import tqdm

import frames
from classes.particle import Particle
from streaming import SequentialStreamingExecutor as Executor

M = 70.0
K = 1e4
GAMMA = 100.0

def solution(t: np.ndarray):
    w0 = np.sqrt(K / M)
    beta = GAMMA / (2 * M)
    wd = np.sqrt(w0 ** 2 - beta ** 2)
    x = np.exp(t * -beta) * np.cos(wd * t)
    return x

def verlet_read(f: int):
    return frames.next(f, 'verlet-steps')

def beeman_read(f: int):
    return frames.next(f, 'beeman-steps')

def gear5_read(f: int):
    return frames.next(f, 'gear5-steps')

def main(seconds: float):
    def custom_read(f: Callable[[int], tuple[int, list[Particle]]], dir: str):
        executor = Executor(f, range(frames.count(dir)))
        arr = np.array([])
        for particles in tqdm(executor.stream(), total=frames.count(dir)):
            arr = np.append(arr, particles[0].position.x)
        return arr

    verlet = custom_read(verlet_read, 'verlet-steps')
    beeman = custom_read(beeman_read, 'beeman-steps')
    gear5 = custom_read(gear5_read, 'gear5-steps')

    t = np.linspace(0, seconds, frames.count('verlet-steps'))
    sol = solution(t)

    return verlet, beeman, gear5, sol, t

if __name__ == "__main__":
    verlet, beeman, gear, sol, t = main(5)

    plt.plot(t, sol, ls='--', lw=3, label="Solución analítica") # pyright: ignore[reportUnknownMemberType]
    plt.plot(t, verlet, label="Verlet") # pyright: ignore[reportUnknownMemberType]
    plt.plot(t, beeman, label="Beeman") # pyright: ignore[reportUnknownMemberType]
    plt.plot(t, gear, label="Gear 5") # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Tiempo (s)", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Posición (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.ylim(-1.1, 1.1) # pyright: ignore[reportUnknownMemberType]

    plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.1)
    plt.show() # pyright: ignore[reportUnknownMemberType]
