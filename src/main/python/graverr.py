import matplotlib.pyplot as plt

import numpy as np

import os

import resources

def main():
    values: dict[str, dict[int, dict[float, tuple[np.floating, np.floating]]]] = {}

    for integrator in os.listdir(resources.path("graverr")):
        for n in os.listdir(resources.path("graverr", integrator)):
            for dt in os.listdir(resources.path("graverr", integrator, n)):
                dt_path = resources.path("graverr", integrator, n, dt)

                energies = np.array([])
                for filename in os.listdir(dt_path):
                    tot = np.loadtxt(os.path.join(dt_path, filename))[50:] # skip initial transient
                    energies = np.append(energies, np.abs(tot - tot[0]) / abs(tot[0]))

                mean = np.mean(energies)
                std = np.std(energies)

                if integrator not in values:
                    values[integrator] = {}

                if int(n) not in values[integrator]:
                    values[integrator][int(n)] = {}

                values[integrator][int(n)][float(dt)] = (mean, std)

    return values

if __name__ == "__main__":
    values = main()

    for integrator, n_dict in values.items():
        for n, dt_dict in n_dict.items():
            sorted_pairs = sorted(dt_dict.items())
            dts, errs = zip(*sorted_pairs)
            means, stds = zip(*errs)

            plt.errorbar( # pyright: ignore[reportUnknownMemberType]
                dts,
                means,
                yerr=stds,
                fmt='o-',
                capsize=5,
                label=integrator
            )

    plt.xscale("log") # pyright: ignore[reportUnknownMemberType]
    plt.yscale("log") # pyright: ignore[reportUnknownMemberType]

    plt.xlabel(r"$\Delta$t (s)", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Error relativo promedio de energía total (J)", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.08)
    plt.show() # pyright: ignore[reportUnknownMemberType]
