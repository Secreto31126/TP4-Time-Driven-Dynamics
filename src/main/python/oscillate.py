import matplotlib.pyplot as plt

import numpy as np

import frames

M = 70.0
K = 1e4
GAMMA = 100.0

def solution(t: np.ndarray):
    w0 = np.sqrt(K / M)
    beta = GAMMA / (2 * M)
    wd = np.sqrt(w0 ** 2 - beta ** 2)
    x = np.exp(t * -beta) * np.cos(wd * t)
    return x

data: list[float] = []
for i in range(frames.count()):
    _, particles = frames.next(i)
    p = particles[0]
    data.append(p.position.x)

t_ref = np.linspace(0, 5, frames.count())
x_ana_curve = solution(t_ref)

plt.plot(t_ref, data) # pyright: ignore[reportUnknownMemberType]
plt.plot(t_ref, x_ana_curve) # pyright: ignore[reportUnknownMemberType]

plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

plt.xlabel("Tiempo (s)", fontsize=24) # pyright: ignore[reportUnknownMemberType]
plt.ylabel("Posición (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

plt.ylim(-1.1, 1.1) # pyright: ignore[reportUnknownMemberType]

plt.show() # pyright: ignore[reportUnknownMemberType]
