from typing import Callable

import time

import matplotlib.pyplot as plt
from matplotlib.animation import FuncAnimation

import numpy as np

from tqdm import tqdm

import frames
import resources
from streaming import SequentialStreamingExecutor as Executor

from classes.particle import Particle

abar = None
def main():
    global abar

    executor = Executor(frames.next, range(frames.count()))

    fig = plt.figure() # pyright: ignore[reportUnknownMemberType]
    ax = fig.add_subplot(111, projection='3d') # pyright: ignore[reportUnknownMemberType]

    s = 1.0
    ax.set_xlim(-s, s) # pyright: ignore[reportUnknownMemberType]
    ax.set_ylim(-s, s) # pyright: ignore[reportUnknownMemberType]
    ax.set_zlim(-s, s) # pyright: ignore[reportUnknownMemberType]

    ax.set_xlabel('X') # pyright: ignore[reportUnknownMemberType]
    ax.set_ylabel('Y') # pyright: ignore[reportUnknownMemberType]
    ax.set_zlabel('Z') # pyright: ignore[reportUnknownMemberType]

    x = np.array([])
    y = np.array([])
    z = np.array([])
    for p in frames.next(0)[1]:
        x = np.append(x, p.position.x)
        y = np.append(y, p.position.y)
        z = np.append(z, p.position.z)

    scat = ax.scatter(x, y, z, marker='o') # pyright: ignore[reportArgumentType, reportUnknownMemberType]

    def update(particles: list[Particle]):
        global abar

        if abar is not None and abar.n % abar.total == 0:
            abar.reset()

        x = np.array([])
        y = np.array([])
        z = np.array([])
        for particle in particles:
            x = np.append(x, particle.position.x)
            y = np.append(y, particle.position.y)
            z = np.append(z, particle.position.z)

        scat._offsets3d = (x, y, z) # pyright: ignore[reportAttributeAccessIssue]

        if abar is not None:
            abar.update()

        return scat,

    ani = FuncAnimation( # pyright: ignore[reportUnusedVariable]
        fig,
        update,
        frames=executor.stream(),
        save_count=frames.count(),
        interval=5,
        blit=False,
        repeat=True
    )

    if True:
        abar = tqdm(total=frames.count())
        plt.show() # pyright: ignore[reportUnknownMemberType]
        abar.close()

    if True:
        print("Saving animation...")

        filename = resources.path(f"{int(time.time())}.mp4")
        with tqdm(total=frames.count()) as sbar:
            callback: Callable[[int, int], bool | None] = lambda _i, _n: sbar.update()
            ani.save(filename, writer='ffmpeg', fps=60, dpi=300, progress_callback=callback)

        print(f"Animation saved at {filename}")

if __name__ == "__main__":
    main()
