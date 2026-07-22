param(
    [switch]$Deploy
)

$ErrorActionPreference = "Stop"

Write-Host "SmartOps container status:" -ForegroundColor Cyan

if ($Deploy) {
    docker compose `
        -f docker-compose.infra.yml `
        -f docker-compose.deploy.yml `
        ps
}
else {
    docker compose ps
}