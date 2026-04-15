# ADR-003: Use the Kafka Outbox Pattern for Reliable Event Publication

- **Status**: Accepted
- **Date**: 2026-04-05
- **Deciders**: Smart Operations Platform project
- **Technical Story**: Guarantee reliable event publication for critical business workflows

## Context

The Smart Operations Platform relies on event-driven integration for important downstream actions.

Examples:
- when a work order is created, notification and audit should react
- when a work order is assigned, notification and audit should react
- when a work order is completed, billing, notification, and audit should react
- when an invoice is generated, notification and audit should react

A naive implementation would update database state and publish a Kafka message directly in application code.

That approach is risky because it can fail in inconsistent ways:

- the database transaction succeeds but Kafka publish fails
- Kafka publish succeeds but the database transaction rolls back
- retries may create duplicates without clear control

Because these workflows are central to the value of the platform, silent event loss is unacceptable.

## Decision

The platform will use the **transactional outbox pattern** for critical domain event publication.

For event-producing bounded contexts such as `workorder` and `billing`:

1. the business transaction writes domain state changes
2. the same transaction writes an `outbox_events` record
3. a separate publisher process reads pending outbox rows
4. the publisher sends events to Kafka
5. the outbox row is marked as published or failed

## Rationale

This decision provides a practical and well-understood way to achieve reliable event publication without requiring distributed transactions between the database and Kafka.

### Benefits

#### Prevents silent message loss
If the business transaction commits, the event is preserved in the database even if Kafka is temporarily unavailable.

#### Preserves transactional consistency
State change and event intent are stored atomically.

#### Supports retry
Failed publications can be retried from persisted outbox rows.

#### Improves observability
Outbox rows give a clear operational record of:
- pending events
- published events
- failed events
- retry counts

#### Strong architectural credibility
The outbox pattern is a mature distributed systems technique and demonstrates serious backend design.

## Consequences

### Positive consequences
- reliable event publication
- cleaner recovery model
- explicit event publishing lifecycle
- easier production troubleshooting
- better support for downstream asynchronous workflows

### Negative consequences
- additional table and publisher logic
- slightly more implementation complexity
- eventual consistency instead of immediate downstream visibility

## Outbox design

Typical outbox columns:

- `id`
- `aggregate_type`
- `aggregate_id`
- `event_type`
- `event_payload`
- `event_key`
- `status`
- `occurred_at`
- `published_at`
- `retry_count`

Typical status values:
- `PENDING`
- `PUBLISHED`
- `FAILED`

## Event flow example

### Work order completion flow

1. technician completes work order
2. `workorder.work_orders` is updated to `COMPLETED`
3. `workorder.intervention_reports` is stored
4. `workorder.outbox_events` receives a `WORK_ORDER_COMPLETED` row in the same transaction
5. transaction commits
6. outbox publisher picks up the pending row
7. publisher sends `WORK_ORDER_COMPLETED` to Kafka
8. billing, notification, and audit consume the event
9. outbox row is marked `PUBLISHED`

## Consumer assumptions

Because Kafka and asynchronous systems can deliver duplicates, consumers must still be idempotent.

Examples:
- billing should not generate multiple invoices for the same work order
- notification should not generate duplicate notifications when a deduplication key is present
- audit should treat repeated `event_id` safely

The outbox pattern improves publication reliability, but it does not remove the need for idempotent consumers.

## Alternatives considered

### Alternative 1: Direct publish to Kafka inside the request flow
Rejected.

Reasons:
- risk of inconsistent state
- risk of silent event loss
- weak recovery model

### Alternative 2: Two-phase commit / distributed transaction
Rejected.

Reasons:
- too heavy for this project
- unnecessary operational complexity
- poor fit for practical cloud-native design

### Alternative 3: Best-effort publish with logs only
Rejected.

Reasons:
- insufficient reliability for critical business events
- manual recovery would be fragile and error-prone

## Implementation notes

Initial event-producing modules:
- `smartops-workorder`
- `smartops-billing`

Each will own its own outbox table inside its schema:
- `workorder.outbox_events`
- `billing.outbox_events`

A scheduled publisher or background component will:
- poll pending rows
- publish to Kafka
- update status
- increment retry count on failure

## Operational notes

Recommended observability:
- log event publication attempts with correlation ID
- monitor counts of pending and failed outbox rows
- alert on excessive outbox backlog
- expose internal metrics for publisher success/failure

## Follow-up decisions

This ADR is related to:
- ADR-001: Modular monolith first
- ADR-002: Schema per bounded context

## Review trigger

Revisit this decision when:
- event volume becomes high enough to require a more specialized outbox relay mechanism
- service extraction introduces independent runtime deployment for producers
- operational characteristics suggest CDC-based publishing as an alternative
