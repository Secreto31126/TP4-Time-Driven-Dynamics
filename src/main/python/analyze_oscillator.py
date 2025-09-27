#!/usr/bin/env python3
"""
Compare analytical damped oscillator solution with simulation results.

The simulation implements: F = -amplitude * position - omega * velocity
This represents a damped harmonic oscillator with:
- Spring constant k = amplitude = 1.0
- Damping coefficient γ = omega = 100.0
- Mass m = 1.0 (assumed)
"""

import numpy as np
import matplotlib.pyplot as plt
import os
from pathlib import Path

def read_simulation_setup():
    """Read simulation parameters from setup.txt"""
    setup_file = Path("../../../sim/setup.txt")
    if not setup_file.exists():
        # Return default from MainOscillator.java if setup.txt not found
        print("Warning: sim/setup.txt not found, using default steps=1000")
        return 1000

    with open(setup_file, 'r') as f:
        steps = int(f.readline().strip())

    return steps

def read_simulation_data():
    """Read simulation step data from steps/*.txt files"""
    steps_dir = Path("../../../sim/steps")
    if not steps_dir.exists():
        raise FileNotFoundError("sim/steps directory not found")

    # Get all step files and sort them numerically
    step_files = list(steps_dir.glob("*.txt"))
    step_files.sort(key=lambda x: int(x.stem))

    times = []
    positions = []
    velocities = []

    # From MainOscillator.java: dt = 1e4, SAVE_INTERVAL = 10
    dt = 1e4
    save_interval = 10

    for i, step_file in enumerate(step_files):
        try:
            with open(step_file, 'r') as f:
                line = f.readline().strip()
                if line and not any(x in line.lower() for x in ['nan', 'inf']):
                    values = list(map(float, line.split()))
                    if len(values) >= 6:  # x y z vx vy vz
                        time = i * save_interval * dt  # Real time
                        times.append(time)
                        positions.append([values[0], values[1], values[2]])  # x, y, z
                        velocities.append([values[3], values[4], values[5]])  # vx, vy, vz
                else:
                    break  # Stop at first NaN/invalid value
        except (ValueError, FileNotFoundError):
            break

    return np.array(times), np.array(positions), np.array(velocities)

def analytical_solution(t, amplitude=1.0, omega=100.0, mass=1.0, x0=1.0, v0=0.0):
    """
    Analytical solution for damped harmonic oscillator.

    Equation: m*a = -k*x - γ*v
    Where k = amplitude, γ = omega

    Args:
        t: Time array
        amplitude: Spring constant (k)
        omega: Damping coefficient (γ)
        mass: Mass (m)
        x0: Initial position
        v0: Initial velocity

    Returns:
        position, velocity arrays
    """
    k = amplitude
    gamma = omega
    m = mass

    # Discriminant for determining oscillation type
    discriminant = (gamma / (2 * m))**2 - k / m

    if discriminant > 0:
        # Overdamped
        r1 = (-gamma + np.sqrt(gamma**2 - 4*m*k)) / (2*m)
        r2 = (-gamma - np.sqrt(gamma**2 - 4*m*k)) / (2*m)
        C1 = (v0 - r2*x0) / (r1 - r2)
        C2 = (r1*x0 - v0) / (r1 - r2)
        x = C1 * np.exp(r1 * t) + C2 * np.exp(r2 * t)
        v = C1 * r1 * np.exp(r1 * t) + C2 * r2 * np.exp(r2 * t)
    elif discriminant == 0:
        # Critically damped
        r = -gamma / (2*m)
        C1 = x0
        C2 = v0 - r*x0
        x = (C1 + C2*t) * np.exp(r * t)
        v = (C2 + (C1 + C2*t)*r) * np.exp(r * t)
    else:
        # Underdamped
        alpha = -gamma / (2*m)
        beta = np.sqrt(k/m - (gamma/(2*m))**2)
        A = x0
        B = (v0 - alpha*x0) / beta
        x = np.exp(alpha * t) * (A * np.cos(beta * t) + B * np.sin(beta * t))
        v = np.exp(alpha * t) * ((alpha*A + beta*B) * np.cos(beta * t) +
                                 (alpha*B - beta*A) * np.sin(beta * t))

    return x, v

def plot_comparison():
    """Create comparison plot of analytical vs simulated oscillator"""

    # Read simulation data
    try:
        total_steps = read_simulation_setup()
        sim_times, sim_positions, sim_velocities = read_simulation_data()
        print(f"Total simulation steps: {total_steps}")
        print(f"Valid data points: {len(sim_times)}")

        if len(sim_times) == 0:
            print("No valid simulation data found!")
            return

    except Exception as e:
        print(f"Error reading simulation data: {e}")
        return

    # Simulation parameters (from MainOscillator.java)
    amplitude = 1.0  # Spring constant
    omega = 100.0    # Damping coefficient
    mass = 1.0       # Assumed mass

    # Initial conditions (from simulation)
    x0, y0, z0 = 1.0, 1.0, 1.0  # Initial position from OscillatorSimulation.java:40
    v0x, v0y, v0z = 0.0, 0.0, 0.0  # Initial velocity

    # Create time array for analytical solution
    if len(sim_times) > 0:
        t_max = max(sim_times[-1], 0.1)  # Ensure we have some time range
    else:
        t_max = 0.1

    t_analytical = np.linspace(0, t_max, 1000)

    # Calculate analytical solutions for each dimension
    x_analytical, vx_analytical = analytical_solution(t_analytical, amplitude, omega, mass, x0, v0x)
    y_analytical, vy_analytical = analytical_solution(t_analytical, amplitude, omega, mass, y0, v0y)
    z_analytical, vz_analytical = analytical_solution(t_analytical, amplitude, omega, mass, z0, v0z)

    # Create plots
    fig, axes = plt.subplots(2, 3, figsize=(15, 10))
    fig.suptitle('Damped Harmonic Oscillator: Analytical vs Simulation', fontsize=16)

    # Position plots
    dimensions = ['X', 'Y', 'Z']
    colors = ['red', 'green', 'blue']

    for i, (dim, color) in enumerate(zip(dimensions, colors)):
        # Position plot
        ax_pos = axes[0, i]
        ax_pos.plot(t_analytical, [x_analytical, y_analytical, z_analytical][i],
                   label=f'Analytical {dim}', color=color, linewidth=2)

        if len(sim_times) > 0:
            ax_pos.scatter(sim_times, sim_positions[:, i],
                          label=f'Simulation {dim}', color=color, alpha=0.7, s=20)

        ax_pos.set_xlabel('Time')
        ax_pos.set_ylabel(f'Position {dim}')
        ax_pos.set_title(f'Position {dim} vs Time')
        ax_pos.legend()
        ax_pos.grid(True, alpha=0.3)

        # Velocity plot
        ax_vel = axes[1, i]
        ax_vel.plot(t_analytical, [vx_analytical, vy_analytical, vz_analytical][i],
                   label=f'Analytical v{dim.lower()}', color=color, linewidth=2)

        if len(sim_times) > 0:
            ax_vel.scatter(sim_times, sim_velocities[:, i],
                          label=f'Simulation v{dim.lower()}', color=color, alpha=0.7, s=20)

        ax_vel.set_xlabel('Time')
        ax_vel.set_ylabel(f'Velocity {dim}')
        ax_vel.set_title(f'Velocity {dim} vs Time')
        ax_vel.legend()
        ax_vel.grid(True, alpha=0.3)

    plt.tight_layout()

    # Add information text
    info_text = f"""
    Simulation Parameters:
    • Spring constant (k = amplitude): {amplitude}
    • Damping coefficient (γ = omega): {omega}
    • Mass: {mass}
    • Time step (dt): 1e4 = {1e4}
    • Save interval: 10 steps

    Note: The large time step causes numerical instability,
    leading to divergence and NaN values in the simulation.
    """

    fig.text(0.02, 0.02, info_text, fontsize=9, verticalalignment='bottom',
             bbox=dict(boxstyle='round', facecolor='lightgray', alpha=0.8))

    # Save the plot
    plt.savefig('oscillator_comparison.png', dpi=300, bbox_inches='tight')
    plt.show()

    # Print analysis
    print("\n" + "="*60)
    print("ANALYSIS RESULTS")
    print("="*60)
    print(f"Spring constant (k): {amplitude}")
    print(f"Damping coefficient (γ): {omega}")
    print(f"Damping ratio (ζ): {omega / (2 * np.sqrt(amplitude * mass)):.3f}")

    discriminant = (omega / (2 * mass))**2 - amplitude / mass
    if discriminant > 0:
        print("System type: OVERDAMPED (no oscillations)")
    elif discriminant == 0:
        print("System type: CRITICALLY DAMPED")
    else:
        print("System type: UNDERDAMPED (oscillations)")

    print(f"Time step (dt): {1e4} - TOO LARGE for numerical stability")
    print(f"Valid simulation points: {len(sim_times)} out of {total_steps//10} expected")

    if len(sim_times) > 0:
        print(f"Simulation diverged after time: {sim_times[-1]:.2e}")

    print("\nRECOMMENDATION: Use a much smaller time step (dt < 0.01) for stability")

if __name__ == "__main__":
    plot_comparison()