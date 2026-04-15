# ADR-002: Use Schema per Bounded Context

- **Status**: Accepted
- **Date**: 2026-04-05
- **Deciders**: Smart Operations Platform project
- **Technical Story**: Define the database ownership model in a microservice-ready way

## Context

The Smart Operations Platform is organized around bounded contexts:

- Identity
- Customer
- Asset
- Work Order
- Billing
- Notification
- Audit

The project starts as a modular monolith, but it is intentionally designed for future microservice extraction.

A central database design question is how to organize persistence while the platform is still implemented as one application.

A single shared schema would make early implementation simpler, but it would blur ownership and make later extraction more difficult.

## Decision

The platform will use **one PostgreSQL database with one schema per bounded context** in the initial implementation phase.

The planned schemas are:

- `identity`
- `customer`
- `asset`
- `workorder`
- `billing`
- `notification`
- `audit`

Each schema owns its own tables and data lifecycle.

## Rationale

This decision creates a strong bridge between the modular monolith phase and the future microservice phase.

### Benefits

#### Clear ownership
Each bounded context owns its own data structures.

Examples:
- `customer.customers`
- `asset.assets`
- `workorder.work_orders`
- `billing.invoices`

#### Better service extraction later
When a module becomes a service, its schema can move with minimal redesign.

#### Strong architectural clarity
The persistence model reinforces the business boundaries already defined in the code and documentation.

#### Reduced accidental coupling
Developers are less likely to create cross-context joins or repository shortcuts when schema boundaries are explicit.

#### Better interview storytelling
This is easy to explain:
> Even in the modular monolith phase, data ownership was designed as if each bounded context were already a service.

## Consequences

### Positive consequences
- explicit data ownership
- better long-term maintainability
- easier future service extraction
- clearer migration responsibilities
- improved documentation alignment

### Negative consequences
- slightly more setup effort initially
- developers must be careful not to create cross-schema coupling that would be invalid in microservices
- some queries become more deliberate because ownership is explicit

## Rules derived from this decision

### Rule 1: Intra-schema foreign keys are allowed
Examples:
- `customer.customer_sites.customer_id -> customer.customers.id`
- `billing.invoice_lines.invoice_id -> billing.invoices.id`

### Rule 2: Cross-context references are logical references
Examples:
- `asset.assets.site_id`
- `workorder.work_orders.asset_id`
- `billing.invoices.work_order_id`

These identifiers represent references to data owned elsewhere, but they should not become tightly coupled relational dependencies in the long-term design.

### Rule 3: Cross-context validation belongs in application logic
If `workorder` needs to validate an `asset_id`, it should do so via a contract or service boundary, not by reading another module’s tables directly.

### Rule 4: Controlled denormalization is acceptable
Some fields may be duplicated intentionally for autonomy, searchability, or historical correctness.

Examples:
- `asset.assets.customer_id`
- `workorder.work_orders.customer_id`
- `workorder.work_orders.site_id`

## Alternatives considered

### Alternative 1: One shared schema for the whole platform
Rejected.

Reasons:
- weak data ownership
- blurred boundaries
- greater risk of cross-domain SQL coupling
- harder future extraction

### Alternative 2: Separate database per module from day one
Rejected for the initial phase.

Reasons:
- higher operational overhead
- more local setup complexity
- slower early development

The project does not need that level of separation before the domain is stable.

## Implementation notes

The initial database setup will use:
- one PostgreSQL instance
- one logical database
- multiple schemas
- Flyway migrations organized to reflect schema ownership

Examples of schema ownership:
- `identity`: users, roles, permissions
- `customer`: customers, customer_sites, customer_contacts
- `asset`: asset_types, assets, asset_history
- `workorder`: work_orders, assignments, intervention_reports, used_parts, outbox_events
- `billing`: invoices, invoice_lines, pricing_policies, outbox_events
- `notification`: notifications, templates, delivery_attempts
- `audit`: audit_events

## Follow-up decisions

This ADR complements:
- ADR-001: Modular monolith first
- ADR-003: Kafka outbox pattern

## Review trigger

Revisit this decision when:
- a bounded context is extracted into an independent runtime
- infrastructure requirements justify separate databases
- cross-context reporting needs suggest dedicated read models or projections
