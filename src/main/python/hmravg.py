import matplotlib.pyplot as plt
from matplotlib.ticker import FuncFormatter

import numpy as np

import os

import resources

def truncate_at_most_2(n: float) -> str:
    TRUNC = int(n * 100) / 100
    return str(TRUNC).rstrip('0').rstrip('.')

def sci_notation(val: float, _):
    if val == 0:
        return "0"

    EXP = int(np.floor(np.log10(abs(val))))
    COEFF = val / (10**EXP)

    return f"${truncate_at_most_2(COEFF)}\\times 10^{{{EXP}}}$"

def main():
    values: dict[str, dict[int, tuple[np.floating, np.floating]]] = {}

    for integral in os.listdir(resources.path("hmr")):
        for N in os.listdir(resources.path("hmr", integral)):
            slopes = np.array([])

            for filename in os.listdir(resources.path("hmr", integral, N)):
                data = np.loadtxt(resources.path("hmr", integral, N, filename))
                slope, _ = np.polyfit(np.linspace(0, len(data), len(data)), data, 1)

                slopes = np.append(slopes, slope)

            slope_mean = np.mean(slopes)
            slope_std = np.std(slopes)

            if integral not in values:
                values[integral] = {}

            values[integral][int(N)] = (slope_mean, slope_std)

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

    plt.xlabel("N", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel(r"<r$_{hm}$>", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.gca().yaxis.set_major_formatter(FuncFormatter(sci_notation)) # pyright: ignore[reportUnknownArgumentType]

    if len(values) > 1:
        plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.grid(which="both") # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.13)
    plt.show() # pyright: ignore[reportUnknownMemberType]
