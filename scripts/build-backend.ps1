param(
    [switch]$SkipTests
)

$ErrorActionPreference = "Stop"

Write-Host "Building SmartOps backend..." -ForegroundColor Cyan

if ($SkipTests) {
    mvn clean install -DskipTests
}
else {
    mvn clean verify
}

Write-Host "Backend build completed successfully." -ForegroundColor Green