# Script to set Maven and Java environment variables

# Define paths
$mavenHome = "D:\Development\swdtool\apache-maven-3.9.11"
$javaHome = "C:\Users\RyanKKLam\.jdks\temurin-24"

# Set user-level environment variables
[System.Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, "User")
[System.Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, "User")

# Get current PATH
$currentPath = [System.Environment]::GetEnvironmentVariable("PATH", "User")

# Add Maven and Java to PATH
$mavenBinPath = "$mavenHome\bin"
$javaBinPath = "$javaHome\bin"

# Check if paths already exist in PATH
if (-not $currentPath.Contains($mavenBinPath)) {
    $newPath = "$currentPath;$mavenBinPath"
    [System.Environment]::SetEnvironmentVariable("PATH", $newPath, "User")
    Write-Host "Maven added to PATH environment variable"
} else {
    Write-Host "Maven already in PATH environment variable"
}

if (-not $currentPath.Contains($javaBinPath)) {
    $newPath = "$currentPath;$javaBinPath"
    [System.Environment]::SetEnvironmentVariable("PATH", $newPath, "User")
    Write-Host "Java added to PATH environment variable"
} else {
    Write-Host "Java already in PATH environment variable"
}

Write-Host ""
Write-Host "Environment variables set successfully!"
Write-Host "Note: New environment variables require closing and reopening all command prompt windows to take effect."
