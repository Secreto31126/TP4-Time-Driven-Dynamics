import matplotlib.pyplot as plt

import numpy as np

import frames
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

def main(seconds: float):
    executor = Executor(frames.next, range(frames.count()))

    t = np.linspace(0, seconds, frames.count())

    data: list[float] = []
    for particles in executor.stream():
        p = particles[0]
        data.append(p.position.x)

    return data, solution(t), t

if __name__ == "__main__":
    sim, sol, t = main(5)

    plt.plot(t, sim) # pyright: ignore[reportUnknownMemberType]
    plt.plot(t, sol) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("Tiempo (s)", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Posición (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.ylim(-1.1, 1.1) # pyright: ignore[reportUnknownMemberType]

    plt.show() # pyright: ignore[reportUnknownMemberType]
