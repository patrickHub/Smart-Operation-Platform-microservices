param(
    [switch]$Volumes,
    [switch]$Deploy
)

$ErrorActionPreference = "Stop"

Write-Host "Stopping SmartOps Platform..." -ForegroundColor Cyan

if ($Deploy) {
    if ($Volumes) {
        docker compose `
            -f docker-compose.infra.yml `
            -f docker-compose.deploy.yml `
            down --volumes --remove-orphans
    }
    else {
        docker compose `
            -f docker-compose.infra.yml `
            -f docker-compose.deploy.yml `
            down --remove-orphans
    }
}
else {
    if ($Volumes) {
        docker compose down --volumes --remove-orphans
    }
    else {
        docker compose down --remove-orphans
    }
}

Write-Host "SmartOps Platform stopped." -ForegroundColor Green