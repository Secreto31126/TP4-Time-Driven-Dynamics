#!/bin/bash

# Batch Galaxy Simulation Runner
# Loops through JSON configuration files and runs Java + Python analysis

set -e  # Exit on error

# Configuration
CONFIG_DIR="${1:-configs}"
PARAMS_PATH="src/main/resources/params.json"
VENV_ACTIVATE="src/main/python/Scripts/activate"
PYTHON_SCRIPT="src/main/python/gala.py"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if config directory exists
if [ ! -d "$CONFIG_DIR" ]; then
    echo -e "${RED}Error: Configuration directory '$CONFIG_DIR' not found${NC}"
    echo "Usage: $0 [config_directory]"
    exit 1
fi

# Count JSON files
json_files=("$CONFIG_DIR"/*.json)
total_files=${#json_files[@]}

if [ $total_files -eq 0 ]; then
    echo -e "${RED}Error: No JSON files found in '$CONFIG_DIR'${NC}"
    exit 1
fi

echo -e "${GREEN}Found $total_files configuration(s) in '$CONFIG_DIR'${NC}"
echo ""

# Main loop
config_num=0
for config_file in "${json_files[@]}"; do
    config_num=$((config_num + 1))
    config_name=$(basename "$config_file")

    echo -e "${YELLOW}========================================${NC}"
    echo -e "${YELLOW}[$config_num/$total_files] Processing: $config_name${NC}"
    echo -e "${YELLOW}========================================${NC}"

    # Step 1: Copy configuration
    echo -e "${GREEN}[1/3] Copying configuration to $PARAMS_PATH...${NC}"
    cp "$config_file" "$PARAMS_PATH"

    # Step 2: Run Java simulation
    echo -e "${GREEN}[2/3] Running Java Galaxy simulation...${NC}"
    if mvn -q exec:java -Dexec.mainClass="org.sims.MainGalaxy"; then
        echo -e "${GREEN}Java simulation completed successfully${NC}"
    else
        echo -e "${RED}Java simulation failed for $config_name${NC}"
        continue
    fi

    # Step 3: Run Python analysis
    echo -e "${GREEN}[3/3] Running Python analysis (gala.py)...${NC}"

    # Activate virtual environment and run Python script
    if [ -f "$VENV_ACTIVATE" ]; then
        # Source the activation script and run Python in the same shell
        (
            source "$VENV_ACTIVATE"
            python "$PYTHON_SCRIPT" --no-plot
        )

        if [ $? -eq 0 ]; then
            echo -e "${GREEN}Python analysis completed successfully${NC}"
        else
            echo -e "${RED}Python analysis failed for $config_name${NC}"
        fi
    else
        echo -e "${RED}Virtual environment activation script not found at $VENV_ACTIVATE${NC}"
        echo -e "${YELLOW}Attempting to run Python directly...${NC}"
        python "$PYTHON_SCRIPT" --no-plot || echo -e "${RED}Python execution failed${NC}"
    fi

    echo -e "${GREEN}Completed $config_name${NC}"
    echo ""
done

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}All configurations processed!${NC}"
echo -e "${GREEN}========================================${NC}"
