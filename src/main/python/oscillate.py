import matplotlib.pyplot as plt

import numpy as np

import sys

import os

from tqdm import tqdm

import frames
import resources
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

def ecm(sim: np.ndarray, sol: np.ndarray):
    return np.mean((sim - sol) ** 2)

def main(seconds: float):
    executor = Executor(frames.next, range(frames.count()))

    t = np.linspace(0, seconds, frames.count())

    with open(resources.path("setup.txt"), "r") as f:
        line = f.readline().strip().split(' ')
        dt = float(line[1])
        integral = line[-1]

    sim = np.array([])
    for particles in tqdm(executor.stream(), total=frames.count()):
        sim = np.append(sim, particles[0].position.x)

    sol = solution(t)
    err = ecm(sim[1:], sol[1:])

    return sim, sol, err, t, dt, integral

if __name__ == "__main__":
    sim, sol, err, t, dt, integral = main(5)

    folder = resources.path('osc-error', integral)
    os.makedirs(folder, exist_ok=True)
    with open(resources.path(folder, f'{dt}.txt'), 'w') as f:
        f.write(f"{err}\n")

    if "--no-plot" in sys.argv:
        exit(0)

    plt.plot(t, sol, ls='--', lw=3) # pyright: ignore[reportUnknownMemberType]
    plt.plot(t, sim) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Tiempo (s)", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Posición (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.ylim(-1.1, 1.1) # pyright: ignore[reportUnknownMemberType]

    plt.legend(["Solución analítica", "Simulación"], fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.1)
    plt.show() # pyright: ignore[reportUnknownMemberType]
