# Smart Operations Platform Documentation

This documentation set describes the architecture, data model, API surface, workflows, and deployment approach for the **Smart Operations Platform**.

## Documentation structure

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

## Status of the project

This repository currently contains:

- multi-module Maven structure
- documentation-first design
- microservice-ready module boundaries

The next major step after documentation is implementation of the first vertical slice:

1. customer creation
2. site creation
3. asset registration
4. work order creation
5. technician assignment
6. work order completion
7. invoice generation
8. audit and notification event handling


## 👤 Author

Patrick Djomo
Senior Software Engineer | Full-Stack Developer
