param(
    [switch]$Build,
    [switch]$Detached,
    [switch]$Deploy
)

$ErrorActionPreference = "Stop"

Write-Host "Starting SmartOps Platform..." -ForegroundColor Cyan

if ($Deploy) {
    Write-Host "Mode: deployment images from GHCR" -ForegroundColor Yellow

    if ($Build) {
        Write-Host "Build option ignored in deploy mode because images are pulled from registry." -ForegroundColor Yellow
    }

    docker compose `
        -f docker-compose.infra.yml `
        -f docker-compose.deploy.yml `
        up -d
}
else {
    Write-Host "Mode: local source build" -ForegroundColor Yellow

    if ($Build) {
        Write-Host "Building Docker images..." -ForegroundColor Cyan
        docker compose build
    }

    if ($Detached) {
        docker compose up -d
    }
    else {
        docker compose up
    }
}

Write-Host "SmartOps Platform start command completed." -ForegroundColor Green
Write-Host "Run '.\scripts\status.ps1' to check container status." -ForegroundColor Cyan