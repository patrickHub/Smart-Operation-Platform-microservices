# API Error Model

This document defines the standard error response model for the Smart Operations Platform.

The goal is to ensure that all modules return errors in a consistent, predictable, and debuggable format.

## Objectives

The platform error model should:

- provide stable machine-readable error codes
- remain readable for API consumers
- support troubleshooting in distributed flows
- include correlation information for observability
- separate business errors from technical failures

## Standard error response

All services should return errors in the following structure:

```json
{
  "timestamp": "2026-04-08T12:35:00Z",
  "status": 404,
  "error": "Not Found",
  "code": "ASSET_NOT_FOUND",
  "message": "Asset with id 9b8730ef-2364-4ef0-b7fd-6ed1fa0ec50d was not found",
  "path": "/api/v1/work-orders",
  "correlationId": "corr-123456",
  "details": []
}
```

## Fields

### `timestamp`
The UTC timestamp when the error was generated.

### `status`
The HTTP status code.

Examples:
- `400`
- `401`
- `403`
- `404`
- `409`
- `422`
- `500`

### `error`
The standard HTTP reason phrase or a short status label.

Examples:
- `Bad Request`
- `Unauthorized`
- `Forbidden`
- `Not Found`
- `Conflict`
- `Internal Server Error`

### `code`
A stable application-level error code.

This field is critical for clients and tests because messages may evolve, but error codes should remain stable.

Examples:
- `VALIDATION_ERROR`
- `CUSTOMER_NOT_FOUND`
- `INVALID_WORK_ORDER_STATE`

### `message`
A human-readable explanation.

This should be clear and useful but should not leak internal stack traces or sensitive implementation details.

### `path`
The request path that produced the error.

### `correlationId`
A trace identifier propagated across services, logs, and events.

This is essential for troubleshooting in distributed and event-driven workflows.

### `details`
Optional list containing field-level validation errors or additional structured details.

Example:

```json
[
  {
    "field": "email",
    "message": "must be a well-formed email address"
  },
  {
    "field": "password",
    "message": "size must be between 12 and 255"
  }
]
```

---

## Validation error format

Validation failures should return HTTP `400 Bad Request` with structured detail entries.

Example:

```json
{
  "timestamp": "2026-04-08T12:36:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/v1/users",
  "correlationId": "corr-123456",
  "details": [
    {
      "field": "email",
      "message": "must be a well-formed email address"
    },
    {
      "field": "password",
      "message": "size must be between 12 and 255"
    }
  ]
}
```

---

## Recommended HTTP status usage

### `400 Bad Request`
Use for syntactically valid requests with invalid input structure or validation failure.

Examples:
- malformed DTO values
- missing required fields
- invalid enum value

### `401 Unauthorized`
Use when authentication is required and the caller is not authenticated.

Examples:
- missing bearer token
- invalid token
- expired token

### `403 Forbidden`
Use when the caller is authenticated but lacks permission.

Examples:
- technician trying to create users
- support agent trying to cancel an invoice

### `404 Not Found`
Use when the referenced resource does not exist.

Examples:
- user not found
- customer not found
- asset not found
- work order not found

### `409 Conflict`
Use when the request conflicts with current state.

Examples:
- duplicate serial number
- invoice already exists for work order
- duplicate notification deduplication key

### `422 Unprocessable Entity`
Use for business rule violations where the request is syntactically valid but semantically rejected.

Examples:
- invalid work order state transition
- assigning technician to cancelled work order
- generating invoice for non-completed work order

### `500 Internal Server Error`
Use for unexpected server-side failures.

Do not expose internal implementation details in the message.

### `503 Service Unavailable`
Use when a required dependency is unavailable.

Examples:
- downstream internal lookup service unavailable
- database temporarily unreachable
- Kafka publishing infrastructure unavailable in a synchronous path

---

## Error categories

It is useful to think about errors in categories.

### 1. Validation errors
Examples:
- missing field
- invalid format
- invalid input range

Representative codes:
- `VALIDATION_ERROR`
- `INVALID_REQUEST`

### 2. Authorization and authentication errors
Examples:
- invalid login
- access denied
- missing token

Representative codes:
- `AUTHENTICATION_FAILED`
- `ACCESS_DENIED`
- `TOKEN_EXPIRED`

### 3. Resource lookup errors
Examples:
- missing customer
- missing site
- missing asset

Representative codes:
- `USER_NOT_FOUND`
- `CUSTOMER_NOT_FOUND`
- `SITE_NOT_FOUND`
- `ASSET_NOT_FOUND`
- `WORK_ORDER_NOT_FOUND`
- `INVOICE_NOT_FOUND`
- `NOTIFICATION_NOT_FOUND`

### 4. Business rule errors
Examples:
- invalid transition
- duplicate invoice
- asset not eligible

Representative codes:
- `INVALID_WORK_ORDER_STATE`
- `TECHNICIAN_NOT_ELIGIBLE`
- `INVOICE_ALREADY_EXISTS`
- `ASSET_NOT_ELIGIBLE`
- `CUSTOMER_INACTIVE`

### 5. Technical and infrastructure errors
Examples:
- database unavailable
- dependency timeout
- event publication failure in non-async path

Representative codes:
- `INTERNAL_ERROR`
- `DEPENDENCY_UNAVAILABLE`
- `DATABASE_ERROR`
- `TIMEOUT_ERROR`

---

## Initial platform error code catalog

### Identity
- `USER_NOT_FOUND`
- `ROLE_NOT_FOUND`
- `USERNAME_ALREADY_EXISTS`
- `EMAIL_ALREADY_EXISTS`
- `AUTHENTICATION_FAILED`
- `USER_INACTIVE`
- `ACCESS_DENIED`

### Customer
- `CUSTOMER_NOT_FOUND`
- `CUSTOMER_ALREADY_EXISTS`
- `SITE_NOT_FOUND`
- `SITE_ALREADY_EXISTS`
- `CUSTOMER_INACTIVE`

### Asset
- `ASSET_NOT_FOUND`
- `ASSET_ALREADY_EXISTS`
- `SERIAL_NUMBER_ALREADY_EXISTS`
- `ASSET_TYPE_NOT_FOUND`
- `ASSET_NOT_ELIGIBLE`
- `ASSET_INACTIVE`

### Work order
- `WORK_ORDER_NOT_FOUND`
- `WORK_ORDER_ALREADY_EXISTS`
- `INVALID_WORK_ORDER_STATE`
- `TECHNICIAN_NOT_FOUND`
- `TECHNICIAN_NOT_ELIGIBLE`
- `ASSIGNMENT_NOT_FOUND`
- `INTERVENTION_REPORT_NOT_FOUND`

### Billing
- `INVOICE_NOT_FOUND`
- `INVOICE_ALREADY_EXISTS`
- `INVALID_INVOICE_STATE`
- `PRICING_POLICY_NOT_FOUND`
- `WORK_ORDER_NOT_BILLABLE`

### Notification
- `NOTIFICATION_NOT_FOUND`
- `NOTIFICATION_ALREADY_EXISTS`
- `NOTIFICATION_DELIVERY_FAILED`
- `INVALID_NOTIFICATION_STATE`

### Audit
- `AUDIT_EVENT_NOT_FOUND`

### Generic
- `VALIDATION_ERROR`
- `INVALID_REQUEST`
- `INTERNAL_ERROR`
- `DEPENDENCY_UNAVAILABLE`
- `TIMEOUT_ERROR`

---

## Mapping examples

### Example 1: asset not found
HTTP status:
```text
404 Not Found
```

Response code:
```text
ASSET_NOT_FOUND
```

### Example 2: invalid work order transition
HTTP status:
```text
422 Unprocessable Entity
```

Response code:
```text
INVALID_WORK_ORDER_STATE
```

### Example 3: duplicate serial number
HTTP status:
```text
409 Conflict
```

Response code:
```text
SERIAL_NUMBER_ALREADY_EXISTS
```

### Example 4: access denied
HTTP status:
```text
403 Forbidden
```

Response code:
```text
ACCESS_DENIED
```

---

## Exception handling guidelines

Each module should centralize HTTP error mapping using a global exception handler.

Recommended approach:
- domain exceptions for business rule failures
- application exceptions for orchestration failures
- infrastructure exceptions for technical issues
- one global handler per application runtime

Suggested mapping style:
- `EntityNotFoundException` -> `404`
- `BusinessRuleViolationException` -> `422`
- `DuplicateResourceException` -> `409`
- `AccessDeniedException` -> `403`
- `MethodArgumentNotValidException` -> `400`
- unknown exception -> `500`

---

## Logging rules for errors

### Business errors
- log at `WARN` when expected during normal operation
- include correlation ID
- do not log full stack trace unless needed

### Technical errors
- log at `ERROR`
- include correlation ID
- include stack trace
- include dependency context when helpful

### Security errors
- avoid leaking sensitive data
- never log raw passwords or tokens

---

## API consumer guidance

Clients should rely primarily on:
- HTTP status
- application error code

Clients should not rely only on the `message` field because wording may evolve.

---

## Future evolution

This model can later be extended with:
- localized user-facing messages
- error documentation links
- subcodes for finer classification
- standardized RFC 9457 Problem Details alignment if desired

For the current version, the custom structured error model is sufficient and practical.
