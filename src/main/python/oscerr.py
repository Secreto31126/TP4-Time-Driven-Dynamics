import os

import matplotlib.pyplot as plt

import resources

def main():
    values: dict[str, tuple[list[float], list[float]]] = {}

    errors = resources.path("osc-error")
    for integral in os.listdir(errors):
        integral_path = resources.path("osc-error", integral)

        for filename in os.listdir(integral_path):
            dt_path = os.path.join(integral_path, filename)

            dt = filename[:-4]
            with open(dt_path, "r") as f:
                err = float(f.readline().strip())

            if integral not in values:
                values[integral] = ([], [])
            values[integral][0].append(float(dt))
            values[integral][1].append(err)

    return values

if __name__ == "__main__":
    values = main()

    for integral, (dts, errs) in values.items():
        sorted_pairs = sorted(zip(dts, errs))
        dts, errs = zip(*sorted_pairs)

        plt.loglog(dts, errs, marker='o', label=integral) # pyright: ignore[reportUnknownMemberType]

    plt.xticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.yticks(fontsize=20) # pyright: ignore[reportUnknownMemberType]

    plt.xlabel(r"$\Delta$t (s)", fontsize=24) # pyright: ignore[reportUnknownMemberType]
    plt.ylabel("Error Cuadrático Medio", fontsize=24) # pyright: ignore[reportUnknownMemberType]

    plt.legend(fontsize=20) # pyright: ignore[reportUnknownMemberType]
    plt.grid(which="both") # pyright: ignore[reportUnknownMemberType]

    plt.subplots_adjust(top=0.99, right=0.99, bottom=0.1, left=0.08)
    plt.show() # pyright: ignore[reportUnknownMemberType]
