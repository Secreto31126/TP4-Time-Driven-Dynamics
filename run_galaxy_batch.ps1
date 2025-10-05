# Batch Galaxy Simulation Runner (PowerShell)
# Loops through JSON configuration files and runs Java + Python analysis

param(
    [string]$ConfigDir = "configs"
)

# Configuration
$ParamsPath = "src\main\resources\params.json"
$VenvActivate = "src\main\python\Scripts\Activate.ps1"
$PythonScript = "src\main\python\gala.py"

# Check if config directory exists
if (-not (Test-Path $ConfigDir)) {
    Write-Host "Error: Configuration directory '$ConfigDir' not found" -ForegroundColor Red
    Write-Host "Usage: .\run_galaxy_batch.ps1 [config_directory]"
    exit 1
}

# Get JSON files
$jsonFiles = Get-ChildItem -Path $ConfigDir -Filter "*.json"
$totalFiles = $jsonFiles.Count

if ($totalFiles -eq 0) {
    Write-Host "Error: No JSON files found in '$ConfigDir'" -ForegroundColor Red
    exit 1
}

Write-Host "Found $totalFiles configuration(s) in '$ConfigDir'" -ForegroundColor Green
Write-Host ""

# Main loop
$configNum = 0
foreach ($configFile in $jsonFiles) {
    $configNum++
    $configName = $configFile.Name

    Write-Host "========================================" -ForegroundColor Yellow
    Write-Host "[$configNum/$totalFiles] Processing: $configName" -ForegroundColor Yellow
    Write-Host "========================================" -ForegroundColor Yellow

    # Step 1: Copy configuration
    Write-Host "[1/3] Copying configuration to $ParamsPath..." -ForegroundColor Green
    Copy-Item -Path $configFile.FullName -Destination $ParamsPath -Force

    # Step 2: Run Java simulation
    Write-Host "[2/3] Running Java Galaxy simulation..." -ForegroundColor Green
    $javaProcess = Start-Process -FilePath "mvn" -ArgumentList "-q", "exec:java", "-Dexec.mainClass=org.sims.MainGalaxy" -Wait -PassThru -NoNewWindow

    if ($javaProcess.ExitCode -eq 0) {
        Write-Host "Java simulation completed successfully" -ForegroundColor Green
    } else {
        Write-Host "Java simulation failed for $configName" -ForegroundColor Red
        continue
    }

    # Step 3: Run Python analysis
    Write-Host "[3/3] Running Python analysis (gala.py)..." -ForegroundColor Green

    # Activate virtual environment and run Python script
    if (Test-Path $VenvActivate) {
        # Activate venv and run Python
        & $VenvActivate
        $pythonProcess = Start-Process -FilePath "python" -ArgumentList "$PythonScript", "--no-plot" -Wait -PassThru -NoNewWindow

        if ($pythonProcess.ExitCode -eq 0) {
            Write-Host "Python analysis completed successfully" -ForegroundColor Green
        } else {
            Write-Host "Python analysis failed for $configName" -ForegroundColor Red
        }

        # Deactivate venv
        deactivate
    } else {
        Write-Host "Virtual environment activation script not found at $VenvActivate" -ForegroundColor Red
        Write-Host "Attempting to run Python directly..." -ForegroundColor Yellow
        $pythonProcess = Start-Process -FilePath "python" -ArgumentList "$PythonScript", "--no-plot" -Wait -PassThru -NoNewWindow

        if ($pythonProcess.ExitCode -ne 0) {
            Write-Host "Python execution failed" -ForegroundColor Red
        }
    }

    Write-Host "Completed $configName" -ForegroundColor Green
    Write-Host ""
}

Write-Host "========================================" -ForegroundColor Green
Write-Host "All configurations processed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
