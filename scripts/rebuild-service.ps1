param(
    [Parameter(Mandatory = $true)]
    [ValidateSet(
        "gateway-service",
        "identity-service",
        "customer-service",
        "asset-service",
        "workorder-service",
        "billing-service",
        "notification-service"
    )]
    [string]$Service,

    [switch]$SkipBackendBuild
)

$ErrorActionPreference = "Stop"

Write-Host "Rebuilding SmartOps service: $Service" -ForegroundColor Cyan

if (-not $SkipBackendBuild) {
    Write-Host "Building backend JARs..." -ForegroundColor Cyan
    mvn clean package -DskipTests
}

Write-Host "Stopping service: $Service" -ForegroundColor Yellow
docker compose stop $Service

Write-Host "Removing service container: $Service" -ForegroundColor Yellow
docker compose rm -f $Service

Write-Host "Building Docker image for: $Service" -ForegroundColor Cyan
docker compose build --no-cache $Service

Write-Host "Starting service: $Service" -ForegroundColor Cyan
docker compose up -d $Service

Write-Host "Service rebuilt successfully: $Service" -ForegroundColor Green
docker compose ps $Service