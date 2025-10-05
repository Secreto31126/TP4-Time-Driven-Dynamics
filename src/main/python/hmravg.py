import matplotlib.pyplot as plt

import numpy as np

import os

import resources

def main():
    values: dict[str, dict[int, tuple[np.floating, np.floating]]] = {}

    for integral in os.listdir(resources.path("hmr")):
        for N in os.listdir(resources.path("hmr", integral)):
            hmr = np.array([])

            for filename in os.listdir(resources.path("hmr", integral, N)):
                with open(resources.path("hmr", integral, N, filename), "r") as f:
                    hmr = np.append(hmr, float(f.readline().strip()))

            mean = np.mean(hmr)
            std = np.std(hmr)

            if integral not in values:
                values[integral] = {}

            values[integral][int(N)] = (mean, std)

    return values

if __name__ == "__main__":
    values = main()

    for integral, n_dict in values.items():
        sorted_pairs = sorted(n_dict.items())
        Ns, errs = zip(*sorted_pairs)
        means, stds = zip(*errs)

        plt.errorbar( # pyright: ignore[reportUnknownMemberType]
            Ns,
            means,
            yerr=stds,
            fmt='o-',
            capsize=5,
            label=integral
        )

        fit = np.polyfit(Ns, np.log(means), 1, w=1/np.array(stds))
        plt.plot(Ns, fit, "r--", label=f"{integral} pendiente") # pyright: ignore[reportUnknownMemberType]

    plt.yscale("log") # pyright: ignore[reportUnknownMemberType]

    plt.xlabel("N", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel(r"<r$_{hm}$> (m)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    if len(values) > 1:
        plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.grid(which="both") # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.08)
    plt.show() # pyright: ignore[reportUnknownMemberType]
