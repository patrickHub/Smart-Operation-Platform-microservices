# ADR(Architecture Decision Record)-001: Start with a Modular Monolith First

- **Status**: Accepted
- **Date**: 2026-04-05
- **Deciders**: Smart Operations Platform project
- **Technical Story**: Establish the initial implementation approach for a microservice-ready platform

## Context

The Smart Operations Platform is intended to demonstrate:

- strong domain modeling
- clean architecture
- database ownership by bounded context
- event-driven communication
- Kafka-based integration
- distributed system concerns
- AWS-ready deployment

A direct move to multiple microservices from day one would introduce substantial complexity very early:

- service bootstrapping overhead
- distributed debugging difficulty
- duplicated infrastructure setup
- cross-service contract churn while the domain is still evolving
- slower functional progress for the first end-to-end workflow

At the same time, the project goal is not to remain a simple monolith forever.  
The architecture must stay aligned with future service extraction.

## Decision

The platform will **start as a modular monolith** implemented in a single repository and a single runnable Spring Boot application.

The codebase will be structured into business-aligned modules:

- `smartops-identity`
- `smartops-customer`
- `smartops-asset`
- `smartops-workorder`
- `smartops-billing`
- `smartops-notification`
- `smartops-audit`

A separate runnable module such as `smartops-app` will compose these modules into one deployable application for the initial phase.

Module boundaries will be designed as if they were future services:
- clear ownership
- dedicated API/application/domain/infrastructure packages
- minimal coupling
- explicit contracts
- no leaking of internal persistence models across modules

## Rationale

This approach gives the project the best balance of speed and architectural quality.

### Benefits

#### Faster delivery of the first vertical slice
The project can reach an end-to-end working workflow earlier:
- create customer
- create site
- register asset
- create work order
- assign technician
- complete work order
- generate invoice
- create audit and notification records

#### Easier refactoring while the domain is still stabilizing
Service boundaries are often refined during implementation.  
A modular monolith makes boundary changes cheaper before the domain language is fully stable.

#### Lower operational complexity
Early development avoids the cost of:
- multiple deployables
- network-level failures between services
- service discovery concerns
- many local runtime processes

#### Better developer productivity
Single-process debugging and local execution are much easier when the platform is still forming.

#### Strong interview value
This decision is easy to justify:
> Start simple, validate boundaries, then distribute where it brings real value.

That reflects mature engineering judgment rather than premature complexity.

## Consequences

### Positive consequences
- quicker implementation
- easier local setup
- easier integration testing
- cleaner incremental design
- fewer moving parts initially

### Negative consequences
- the first runtime is not yet a true distributed system
- some infrastructure concerns are postponed
- internal module calls may feel easier than real service boundaries and must be disciplined carefully

## Mitigations

To avoid drifting into a tightly coupled monolith:

- keep modules separated by business capability
- use explicit contracts between modules
- do not share domain entities across modules
- avoid direct access to another module’s repositories or tables
- design event contracts early
- keep documentation aligned with future service extraction

## Alternatives considered

### Alternative 1: Start with full microservices immediately
Rejected for the initial phase.

Reasons:
- higher setup and debugging cost
- slower delivery of business value
- too much distributed complexity before domain stabilization

### Alternative 2: Build a traditional layered monolith without modular boundaries
Rejected.

Reasons:
- harder future extraction
- blurred business ownership
- weaker architectural storytelling
- higher long-term refactoring cost

## Implementation notes

The initial runtime will consist of:
- one Spring Boot application
- one PostgreSQL instance
- one Kafka runtime for local event flows
- clear Maven modules aligned with bounded contexts

The code should still be written so that future extraction remains realistic.

## Follow-up decisions

This ADR works together with:
- ADR-002: Schema per bounded context
- ADR-003: Kafka outbox pattern

## Review trigger

This decision should be revisited when:
- the first complete workflow is stable
- contracts between modules have matured
- deployment and scaling needs justify separate services
