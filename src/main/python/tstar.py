import matplotlib.pyplot as plt

import numpy as np

import os

import resources

def main():
    values: dict[str, dict[int, tuple[np.floating, np.floating, float]]] = {}

    for integral in os.listdir(resources.path("t-star")):
        for N in os.listdir(resources.path("t-star", integral)):
            thresholds = np.array([])

            for filename in os.listdir(resources.path("t-star", integral, N)):
                with open(resources.path("t-star", integral, N, filename), "r") as f:
                    tstar = float(f.readline().strip())
                    thresholds = np.append(thresholds, tstar)

            mean = np.mean(thresholds)
            std = np.std(thresholds)

            if integral not in values:
                values[integral] = {}

            values[integral][int(N)] = (mean, std, len(thresholds))

    return values

if __name__ == "__main__":
    values = main()

    for integral, n_dict in values.items():
        sorted_pairs = sorted(n_dict.items())
        Ns, errs = zip(*sorted_pairs)
        means, stds, runs = zip(*errs)

        plt.errorbar( # pyright: ignore[reportUnknownMemberType]
            Ns,
            means,
            yerr=stds,
            fmt='o-',
            capsize=5,
            label=integral
        )

        for n, m, e, run in zip(Ns, means, stds, runs):
            xy = (n - 40, m + e + 0.15) # Each dot text center (aprox), above the error bar with 0.25 margin
            plt.annotate(f'{run} iter.', xy, fontsize=16) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("N", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("<t*>", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    if len(values) > 1:
        plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.grid(which="both") # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.05)
    plt.show() # pyright: ignore[reportUnknownMemberType]
