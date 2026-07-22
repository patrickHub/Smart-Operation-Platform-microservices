param(
    [ValidateSet(
        "postgres",
        "kafka",
        "kafka-ui",
        "gateway-service",
        "identity-service",
        "customer-service",
        "asset-service",
        "workorder-service",
        "billing-service",
        "notification-service",
        "all"
    )]
    [string]$Service = "all",

    [switch]$Follow
)

$ErrorActionPreference = "Stop"

if ($Service -eq "all") {
    if ($Follow) {
        docker compose logs -f
    }
    else {
        docker compose logs
    }
}
else {
    if ($Follow) {
        docker compose logs -f $Service
    }
    else {
        docker compose logs $Service
    }
}