# API Documentation

This directory documents the public and internal APIs of the Smart Operations Platform.

## Goals

The API layer is designed to:
- expose business capabilities through clear REST endpoints
- separate external contracts from internal persistence models
- support future service extraction with stable contracts
- provide predictable error handling and pagination patterns

## Public API areas

### Identity
- user management
- authentication
- role-based access

### Customer
- customer management
- site management
- customer contact management

### Asset
- asset registration
- asset lookup
- asset status updates

### Workorder
- work-order creation
- technician assignment
- status transitions
- intervention completion

### Billing
- invoice lookup
- invoice status management

### Notification
- notification lookup
- retry of failed deliveries

### Audit
- audit-event lookup
- filtering by aggregate and time

## Public endpoint conventions

- Base path format: `/api/v1/...`
- JSON request and response payloads
- Resource-oriented naming
- Consistent error model
- JWT-based authentication for protected endpoints

## Internal endpoint conventions

Internal service-to-service lookup contracts use:
- `/internal/v1/...`

Examples:
- `/internal/v1/users/{id}/summary`
- `/internal/v1/sites/{id}/summary`
- `/internal/v1/assets/{id}/summary`

These contracts return lightweight summary views instead of full domain models.

## Standard response principles

Responses should:
- include stable IDs
- expose business identifiers where useful
- include timestamps for creation and updates
- avoid leaking database entity details

## Standard error contract

All APIs should converge on a consistent error structure.

```json
{
  "timestamp": "2026-04-08T12:35:00Z",
  "status": 404,
  "error": "Not Found",
  "code": "ASSET_NOT_FOUND",
  "message": "Asset with id 9b8730ef-2364-4ef0-b7fd-6ed1fa0ec50d was not found",
  "path": "/api/v1/work-orders",
  "correlationId": "corr-123456"
}
```

## Versioning strategy

The first API version uses URI versioning:
- `v1`

Future changes should prefer:
- additive evolution
- backward-compatible response expansion
- event-version increments when payload shape changes

## Security model

Typical role access:
- **Admin**: user and role management
- **Support Agent**: customer, site, asset, work-order creation
- **Dispatcher**: work-order assignment and scheduling
- **Technician**: accept, start, and complete assigned work orders
- **Billing Manager**: invoice viewing and billing-state updates

## Next documentation to add here

As implementation progresses, this folder should be extended with:
- endpoint catalog per module
- request and response examples
- OpenAPI export
- authentication flow examples
- error-code registry
