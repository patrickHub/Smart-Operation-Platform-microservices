# Database Documentation

This directory documents the SmartOps Platform data model and persistence strategy.

## Goals

The database design is intended to:
- align with bounded contexts
- support a modular monolith today
- remain compatible with later microservice extraction
- preserve service data ownership
- support reliable event publication

## Current persistence strategy

### Phase 1
- one PostgreSQL instance
- one database
- one schema per bounded context

Schemas:
- `identity`
- `customer`
- `asset`
- `workorder`
- `billing`
- `notification`
- `audit`

### Future target
- schema-per-service or database-per-service
- no direct cross-service table access
- same ownership boundaries retained

## Main schema ownership

### identity
Owns:
- users
- roles
- permissions
- user_roles
- role_permissions

### customer
Owns:
- customers
- customer_sites
- customer_contacts

### asset
Owns:
- asset_types
- assets
- asset_history

### workorder
Owns:
- work_orders
- work_order_tasks
- work_order_assignments
- intervention_reports
- used_parts
- outbox_events

### billing
Owns:
- pricing_policies
- invoices
- invoice_lines
- outbox_events

### notification
Owns:
- notification_templates
- notifications
- notification_delivery_attempts

### audit
Owns:
- audit_events

## Data ownership rules

### Rule 1
Each bounded context owns its own tables.

### Rule 2
Cross-context references are stored as IDs, not direct foreign keys across service boundaries.

### Rule 3
Only intra-schema relationships should use hard foreign keys.

### Rule 4
Controlled denormalization is acceptable where it improves autonomy and query efficiency.

## Key technical conventions

- primary keys use UUID
- timestamps use timezone-aware values
- important aggregates include optimistic locking via `version`
- business identifiers such as `work_order_number` and `invoice_number` remain explicit
- schema changes are managed with Flyway

## Outbox pattern

The `workorder` and `billing` schemas include `outbox_events` tables.

Purpose:
- persist business state and domain events in one transaction
- avoid silent event loss
- support asynchronous publication to Kafka

## Indexing guidance

Important indexes should exist for:
- business identifiers
- status columns
- foreign-reference IDs
- created timestamps
- outbox processing status

## Migration strategy

Initial migration approach:
- centralized Flyway migrations in `smartops-app`

Later evolution:
- move migrations into extracted services
- each service owns its own migration history

## Next documentation to add here

As implementation progresses, this folder should be extended with:
- ER diagrams
- DDL files
- migration plan
- indexing strategy per table
- retention and archival policy
