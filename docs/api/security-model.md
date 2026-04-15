# API Security Model

This document defines the security approach for the Smart Operations Platform.

The goal is to provide a clear, practical, and implementation-ready model for authentication, authorization, and service protection.

## Security objectives

The platform security model should:

- authenticate all protected API calls
- authorize actions based on business roles
- isolate sensitive operations
- support future extraction to microservices
- remain simple enough for a first implementation

## Security approach overview

### Authentication
Public APIs use token-based authentication.

Recommended first implementation:
- JWT bearer tokens issued by the identity module

Long-term evolution:
- external identity provider such as Keycloak can replace internal authentication if desired

### Authorization
Authorization is role-based.

Roles are assigned to users and checked by the application layer and Spring Security.

### Internal service access
Internal service-to-service calls should be authenticated as well.

For the first implementation inside a modular monolith:
- internal calls can be protected logically through application boundaries

For future microservices:
- use internal JWTs, service accounts, mTLS, or trusted private network plus gateway enforcement

---

## Core security concepts

### User
A person using the platform.

### Role
A named business capability grouping.

Examples:
- `ADMIN`
- `SUPPORT_AGENT`
- `DISPATCHER`
- `TECHNICIAN`
- `BILLING_MANAGER`

### Permission
A finer-grained access capability.

Examples:
- `customer:create`
- `asset:update`
- `workorder:assign`
- `invoice:cancel`

For the first version, role-based checks are enough.
Permissions can be added later if you want more granular control.

---

## Authentication model

### Login flow
1. user submits username and password
2. identity module validates credentials
3. system issues signed JWT
4. client sends token in `Authorization: Bearer <token>` header
5. API validates token and extracts user identity and roles

### Token contents
The token should include at least:
- user ID
- username
- role list
- token expiration
- token issuer

Example claims:
```json
{
  "sub": "2f5d9d89-ef01-4d1f-8d68-9f2a1a3d8e11",
  "preferred_username": "jdupont",
  "roles": ["TECHNICIAN"],
  "iss": "smartops-identity",
  "exp": 1770000000
}
```

### Recommended token lifetime
Initial implementation:
- access token valid for 1 hour

Later enhancements:
- refresh tokens
- revocation support
- session tracking

---

## Public vs protected endpoints

### Public endpoints
Only a small number of endpoints should be public.

Examples:
- `POST /api/v1/auth/login`
- possibly health endpoints depending on environment policy

### Protected endpoints
Everything else should require authentication.

Examples:
- customer APIs
- asset APIs
- work order APIs
- billing APIs
- notification APIs
- audit APIs

---

## Role model

### `ADMIN`
Broad platform administration privileges.

Typical capabilities:
- create users
- deactivate users
- assign roles
- view audit data
- retry notifications
- manage sensitive platform resources

### `SUPPORT_AGENT`
Operational support role.

Typical capabilities:
- create customers
- create customer sites
- register assets
- create work orders
- update customer and asset details

### `DISPATCHER`
Coordination role.

Typical capabilities:
- assign technicians
- reprioritize work
- reschedule work orders
- view work order operations

### `TECHNICIAN`
Execution role.

Typical capabilities:
- view assigned work orders
- accept assignments
- start interventions
- complete interventions
- add intervention notes

### `BILLING_MANAGER`
Financial operations role.

Typical capabilities:
- view invoices
- mark invoices as sent
- cancel invoices
- manage billing states

---

## Initial endpoint-to-role mapping

### Identity
- create user -> `ADMIN`
- deactivate user -> `ADMIN`
- assign roles -> `ADMIN`
- get current user profile -> authenticated user

### Customer
- create customer -> `SUPPORT_AGENT`, `ADMIN`
- update customer -> `SUPPORT_AGENT`, `ADMIN`
- create site -> `SUPPORT_AGENT`, `ADMIN`
- search customers -> `SUPPORT_AGENT`, `ADMIN`, `DISPATCHER`, `BILLING_MANAGER`

### Asset
- register asset -> `SUPPORT_AGENT`, `ADMIN`
- update asset -> `SUPPORT_AGENT`, `ADMIN`
- change asset status -> `SUPPORT_AGENT`, `ADMIN`
- search assets -> `SUPPORT_AGENT`, `ADMIN`, `DISPATCHER`, `TECHNICIAN`, `BILLING_MANAGER`

### Work order
- create work order -> `SUPPORT_AGENT`, `ADMIN`
- assign technician -> `DISPATCHER`, `ADMIN`
- accept assignment -> `TECHNICIAN`
- start intervention -> `TECHNICIAN`
- complete work order -> `TECHNICIAN`
- cancel work order -> `SUPPORT_AGENT`, `DISPATCHER`, `ADMIN`

### Billing
- get invoice -> `BILLING_MANAGER`, `ADMIN`
- search invoices -> `BILLING_MANAGER`, `ADMIN`
- mark invoice as sent -> `BILLING_MANAGER`, `ADMIN`
- cancel invoice -> `BILLING_MANAGER`, `ADMIN`

### Notification
- query notifications -> `ADMIN`
- retry notification -> `ADMIN`

### Audit
- query audit events -> `ADMIN`

---

## Ownership-based authorization rules

Role checks alone are not always enough.

Some actions should also enforce ownership or contextual business rules.

### Technician-specific rules
A technician should only be allowed to:
- view work orders assigned to them
- accept their own assignment
- start their own assigned work
- complete their own assigned work

Even if two users have role `TECHNICIAN`, one technician must not complete another technician's assignment.

### Billing rules
Billing manager can view invoice data, but invoice creation should still be triggered by business events and platform rules.

### Support rules
Support agents may create work orders, but not assign technicians unless they also have dispatcher privileges.

---

## Internal API security

Internal endpoints under `/internal/v1` are not public business APIs.

They should still be protected.

### First implementation
Inside a modular monolith, internal contracts can be represented by:
- package/module boundaries
- internal service interfaces
- controller exposure only when truly necessary

### Future microservice implementation
Internal endpoints should be protected using one of:
- service account JWTs
- gateway-enforced policies
- private network restrictions
- mTLS between services

---

## Security headers and HTTP practices

The platform should support the following practices:

- `Authorization: Bearer <token>`
- TLS in non-local environments
- no sensitive data in query strings
- no credentials in logs
- standard secure HTTP response headers via framework configuration

Recommended operational headers later:
- HSTS
- X-Content-Type-Options
- X-Frame-Options
- Cache-Control for sensitive responses

---

## Password handling

If internal authentication is implemented:

- store only strong password hashes
- never store plain text passwords
- use a strong password hashing algorithm such as BCrypt
- validate password strength at creation time

Recommended first-password rules:
- minimum 12 characters
- require mixed character types
- reject obviously weak passwords

---

## Audit and security events

Sensitive actions should produce audit entries.

Examples:
- user created
- role changed
- user deactivated
- login failed
- work order cancelled
- invoice cancelled
- notification retry triggered

Security-relevant auditability is important for troubleshooting and governance.

---

## Failure responses

When security checks fail:

### Authentication failure
HTTP status:
```text
401 Unauthorized
```

Recommended code:
```text
AUTHENTICATION_FAILED
```

### Expired or invalid token
HTTP status:
```text
401 Unauthorized
```

Recommended code:
```text
TOKEN_EXPIRED
```
or
```text
INVALID_TOKEN
```

### Authorization failure
HTTP status:
```text
403 Forbidden
```

Recommended code:
```text
ACCESS_DENIED
```

Avoid exposing excessive detail such as:
- whether a username exists
- internal policy implementation details
- sensitive identity provider state

---

## Recommended Spring Security direction

For implementation, a strong first version is:

- stateless Spring Security configuration
- JWT authentication filter
- role-based method or route authorization
- custom authentication entry point
- custom access denied handler
- current-user utility abstraction

Suggested enforcement styles:
- route-based protection for coarse security
- method-level security for business-sensitive operations

Example styles:
- `hasRole('ADMIN')`
- `hasAnyRole('SUPPORT_AGENT', 'ADMIN')`

---

## Local development model

For local development you can keep things practical.

### Option 1
Temporarily allow simplified login flow with locally issued JWTs.

### Option 2
Seed a few default users in development:
- admin
- support agent
- dispatcher
- technician
- billing manager

This is very useful for API testing and demos.

---

## Future enhancements

Possible security improvements later:
- refresh tokens
- token revocation
- MFA for admin actions
- external identity provider integration
- fine-grained permissions
- service-to-service mTLS
- audit dashboards for security-sensitive actions

For the current stage, JWT plus role-based authorization is the right balance.
