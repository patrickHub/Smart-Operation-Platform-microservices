# Smart Operations Platform

## Project overview

The Smart Operations Platform is a cloud-native backend platform for managing service operations around customer assets and field interventions.

It is designed to demonstrate:

- clean modular architecture
- OOP and SOLID principles
- relational database design
- microservice-ready boundaries
- event-driven workflows with Kafka
- distributed system concepts such as idempotency and eventual consistency
- containerized deployment and AWS readiness

## Domain scope

The platform covers the following business capabilities:

- identity and access management
- customer and site management
- asset registration and lifecycle tracking
- work order creation and execution
- technician assignment
- notifications
- billing
- audit history

## Architectural direction

The implementation starts as a **modular monolith** with clearly separated modules:

- `smartops-identity`
- `smartops-customer`
- `smartops-asset`
- `smartops-workorder`
- `smartops-billing`
- `smartops-notification`
- `smartops-audit`

The long-term target is a microservice architecture where each bounded context can be extracted with minimal redesign.

## Documentation structure
This documentation set describes the architecture, data model, API surface, workflows, and deployment approach for the **Smart Operations Platform**.

### Architecture
- `architecture/context-diagram.md`  
  High-level system context showing external actors and external systems.

- `architecture/container-diagram.md`  
  Main technical containers and runtime building blocks.

- `architecture/component-diagram.md`  
  Internal component view of the platform and its main modules/services.

- `architecture/sequence-diagrams.md`  
  End-to-end sequence diagrams for core business workflows.

### Functional and technical documentation
- `api/README.md`  
  API design principles, conventions, error model, versioning, and security approach.

- `api/endpoints-catalog.md`  
  Catalog of planned endpoints by bounded context and service.

- `database/README.md`  
  Database strategy, schemas, ownership rules, and key tables.

- `workflows/README.md`  
  Business workflows and operational scenarios.

- `deployment/README.md`  
  Local development setup, containerization, CI/CD direction, and AWS target deployment model.

## Recommended reading order

For a new developer joining the project, the best reading order is:

1. `architecture/context-diagram.md`
2. `architecture/container-diagram.md`
3. `architecture/component-diagram.md`
4. `database/README.md`
5. `api/README.md`
6. `api/endpoints-catalog.md`
7. `workflows/README.md`
8. `deployment/README.md`

## Core architectural principles

1. **Business capability alignment**  
   Modules and future services are split by business capability, not by technical layer.

2. **Clear ownership**  
   Each module owns its business rules and persistence model.

3. **Microservice readiness**  
   Cross-module interaction is designed through explicit contracts and events.

4. **Event-driven integration**  
   Downstream actions such as billing, notification, and audit are driven by business events.

5. **Reliability first**  
   Critical events are published through an outbox-based approach to avoid message loss.

6. **Documentation as a first-class asset**  
   Design decisions, workflows, and contracts should be documented before and during implementation.

## Naming and terminology

Use these terms consistently across code and documentation:

- **Customer**: company receiving services
- **Site**: physical customer location
- **Asset**: equipment installed at a site
- **Work Order**: maintenance or repair request
- **Assignment**: allocation of a work order to a technician
- **Intervention**: execution of the work
- **Invoice**: billing record generated from completed work
- **Notification**: message triggered by a business event
- **Audit Event**: immutable historical business record


## Local DevOps Scripts

The backend provides PowerShell scripts under `scripts/` to simplify local development and demos.

### Build backend

```powershell
.\scripts\build-backend.ps1
.\scripts\build-backend.ps1 -SkipTests
```

### Start platform locally
```powershell
.\scripts\start-platform.ps1
.\scripts\start-platform.ps1 -Build
```

### Start platform from published GHCR images
```powershell
.\scripts\start-platform.ps1 -Deploy
```

### Stop platform
```powershell
.\scripts\stop-platform.ps1
.\scripts\stop-platform.ps1 -Volumes
```

### Rebuild one service
```powershell
.\scripts\rebuild-service.ps1 customer-service
.\scripts\rebuild-service.ps1 gateway-service -SkipBackendBuild
```

### View logs
```powershell
.\scripts\logs.ps1 gateway-service
.\scripts\logs.ps1 customer-service -Follow
```

### Check status
```powershell
.\scripts\status.ps1
```

## Test everything

Run:
```powershell
.\scripts\build-backend.ps1 -SkipTests
```

Then:
```powershell
.\scripts\start-platform.ps1 -Build
```

Check:
```powershell
.\scripts\status.ps1
```

Logs:
```powershell
.\scripts\logs.ps1 gateway-service
```

Stop:
```powershell
.\scripts\stop-platform.ps1
```

## Status of the project

The SmartOps backend has progressed from a documentation-first prototype into a working microservices-based backend platform.

This repository currently contains:

- multi-module Maven structure using Java 21 and Spring Boot
- microservice-ready module boundaries
- separated backend services for Identity, Customer, Asset, WorkOrder, Billing, Notification and Gateway
- PostgreSQL persistence with schema-per-service organization
- Flyway database migrations for all services
- Kafka-based event handling for work order, billing and notification flows
- API Gateway as a single backend entry point
- JWT authentication and role-based authorization
- service-to-service security preparation with internal tokens
- Resilience4j circuit breakers and time limiters for downstream service calls
- centralized Docker Compose environment configuration using `.env` and `.env.example`
- split Docker Compose structure for infrastructure, local services and deployment images
- Docker health checks for PostgreSQL, Kafka and all Spring Boot services
- GitHub Actions backend CI pipeline
- Docker Compose smoke test in CI
- GitHub Container Registry image publishing for `main` and `develop`
- deployment-ready Docker Compose configuration using published GHCR images
- PowerShell task runner scripts for local Windows development

The first complete backend vertical slice has been implemented and tested:

1. customer creation
2. site creation
3. asset registration
4. work order creation
5. technician assignment
6. work order acceptance and start
7. work order completion
8. invoice generation
9. notification event handling
10. secured API access through the gateway
11. Docker Compose platform startup
12. CI smoke testing through gateway health and login endpoint

Current backend services:

| Service | Module | Port | Responsibility |
|---|---|---:|---|
| Gateway | `smartops-gateway-app` | `8080` | Single API entry point, routing, CORS, JWT validation, role-based access |
| Identity | `smartops-identity-app` | `8086` | Authentication, JWT generation, user profile and role management |
| Customer | `smartops-customer-app` | `8081` | Customers, customer sites and customer contacts |
| Asset | `smartops-asset-app` | `8082` | Asset registration, asset status and asset history |
| WorkOrder | `smartops-workorder-app` | `8083` | Work order lifecycle, tasks, assignments and intervention reports |
| Billing | `smartops-billing-app` | `8084` | Invoice generation, invoice lifecycle and pricing policies |
| Notification | `smartops-notification-app` | `8085` | Notification creation, retry handling and delivery attempts |

DevOps status:

| Area | Status |
|---|---|
| Local Docker Compose platform | Implemented |
| Split Compose files | Implemented |
| Docker health checks | Implemented |
| Spring profiles | Implemented |
| GitHub Actions Maven build | Implemented |
| Docker Compose validation in CI | Implemented |
| Docker image build in CI | Implemented |
| Docker Compose smoke test in CI | Implemented |
| GHCR image publishing | Implemented for `main` and `develop` |
| Deployment Compose using registry images | Implemented |
| PowerShell task runner scripts | Implemented |

The next backend steps are focused on production-readiness and observability:

1. improve structured logging and correlation IDs across all services
2. add centralized observability with Prometheus and Grafana
3. add distributed tracing with OpenTelemetry
4. add more integration tests for business workflows
5. add release tagging and semantic Docker image versioning
6. prepare cloud deployment documentation


## 👤 Author

Patrick Djomo \n
Senior Software Engineer | Full-Stack Developer
