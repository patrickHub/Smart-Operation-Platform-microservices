# Workflow Documentation

This directory documents the main business workflows of the SmartOps Platform.

## Purpose

The workflows describe how business actions move through the system, which modules participate, and which synchronous or asynchronous interactions occur.

## Core workflow set

### 1. Customer onboarding
Steps:
1. support agent creates a customer
2. support agent creates one or more customer sites
3. support agent optionally adds contacts

Main modules:
- customer
- audit

### 2. Asset registration
Steps:
1. support agent selects a customer site
2. support agent registers an asset
3. asset is linked to site and customer references
4. asset-registration event may be emitted for downstream consumers

Main modules:
- asset
- customer
- audit

### 3. Work-order creation
Steps:
1. support agent submits a work-order request
2. workorder validates referenced asset
3. workorder persists the new work order
4. workorder emits `WORK_ORDER_CREATED`
5. notification and audit consume the event

Main modules:
- workorder
- asset
- notification
- audit

### 4. Technician assignment
Steps:
1. dispatcher selects a technician
2. workorder validates technician eligibility
3. workorder records assignment
4. workorder emits `WORK_ORDER_ASSIGNED`
5. notification and audit consume the event

Main modules:
- workorder
- identity
- notification
- audit

### 5. Intervention execution
Steps:
1. technician accepts the assignment
2. technician starts the intervention
3. technician records actions and parts
4. technician completes the work order
5. workorder emits `WORK_ORDER_COMPLETED`

Main modules:
- workorder
- billing
- notification
- audit

### 6. Invoice generation
Steps:
1. billing consumes `WORK_ORDER_COMPLETED`
2. billing checks invoice idempotency
3. billing creates invoice lines and totals
4. billing emits `INVOICE_GENERATED`
5. notification and audit consume the event

Main modules:
- billing
- notification
- audit

## Workflow principles

### Synchronous interactions
Used when immediate validation or immediate response is required.

Examples:
- validating that an asset exists
- validating that a technician exists and is active

### Asynchronous interactions
Used for downstream reactions and loose coupling.

Examples:
- notifications
- billing
- audit trail

## Failure-handling principles

### Business validation failure
Return a synchronous API error and do not emit business events.

### Downstream consumer failure
Do not roll back the original business transaction. Retry in the consuming side.

### Event publication failure
Use the outbox pattern so the original transaction remains committed and the event can be published later.

## Next documentation to add here

As implementation progresses, this folder should be extended with:
- exception scenarios
- cancellation workflows
- rescheduling workflows
- dead-letter and retry flows
- operational runbooks for common incidents
