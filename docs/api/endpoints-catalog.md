# Smart Operations Platform API Endpoints Catalog

This document is the endpoint inventory for the Smart Operations Platform.

It is intentionally written as a planning and documentation artifact before full implementation.

## Conventions

### Base path
All public APIs use versioned paths:

```text
/api/v1
```

Internal service-to-service APIs use:

```text
/internal/v1
```

### Response style
- JSON request/response bodies
- stable UUID identifiers
- clear domain-oriented DTOs
- consistent error contract

### Security
All public endpoints are protected unless stated otherwise.

Authorization is role-based and enforced by the identity and application layers.

### Main roles
- `ADMIN`
- `SUPPORT_AGENT`
- `DISPATCHER`
- `TECHNICIAN`
- `BILLING_MANAGER`

---

## 1. Identity Service

Module:
```text
smartops-identity
```

Responsibilities:
- user management
- authentication
- role assignment
- user lookup for internal consumers

### Public endpoints

#### Create user
```http
POST /api/v1/users
```
Allowed roles:
- `ADMIN`

#### Get user by ID
```http
GET /api/v1/users/{userId}
```
Allowed roles:
- `ADMIN`

#### Search users
```http
GET /api/v1/users
```
Supported query examples:
- `?role=TECHNICIAN`
- `?status=ACTIVE`

Allowed roles:
- `ADMIN`

#### Deactivate user
```http
PATCH /api/v1/users/{userId}/deactivate
```
Allowed roles:
- `ADMIN`

#### Assign roles to user
```http
POST /api/v1/users/{userId}/roles
```
Allowed roles:
- `ADMIN`

#### Login
```http
POST /api/v1/auth/login
```
Allowed roles:
- public

#### Get current authenticated profile
```http
GET /api/v1/auth/me
```
Allowed roles:
- authenticated users

### Internal endpoints

#### User summary lookup
```http
GET /internal/v1/users/{userId}/summary
```
Used by:
- workorder module
- notification module
- audit module

---

## 2. Customer Service

Module:
```text
smartops-customer
```

Responsibilities:
- customer management
- site management
- customer contact management

### Public endpoints

#### Create customer
```http
POST /api/v1/customers
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Get customer
```http
GET /api/v1/customers/{customerId}
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `BILLING_MANAGER`

#### Search customers
```http
GET /api/v1/customers
```
Supported query examples:
- `?query=acme`
- `?status=ACTIVE`

Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `BILLING_MANAGER`

#### Update customer
```http
PUT /api/v1/customers/{customerId}
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Create customer site
```http
POST /api/v1/customers/{customerId}/sites
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Get customer site
```http
GET /api/v1/sites/{siteId}
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `BILLING_MANAGER`

#### Search sites
```http
GET /api/v1/sites
```
Supported query examples:
- `?customerId={customerId}`
- `?city=Geneva`

Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `BILLING_MANAGER`

#### Create customer contact
```http
POST /api/v1/customers/{customerId}/contacts
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

### Internal endpoints

#### Site summary lookup
```http
GET /internal/v1/sites/{siteId}/summary
```
Used by:
- asset module

#### Customer summary lookup
```http
GET /internal/v1/customers/{customerId}/summary
```
Used by:
- asset module
- billing module

---

## 3. Asset Service

Module:
```text
smartops-asset
```

Responsibilities:
- asset registration
- asset search
- asset lifecycle status
- asset summary for downstream services

### Public endpoints

#### Register asset
```http
POST /api/v1/assets
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Get asset
```http
GET /api/v1/assets/{assetId}
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `TECHNICIAN`
- `BILLING_MANAGER`

#### Search assets
```http
GET /api/v1/assets
```
Supported query examples:
- `?serialNumber=SN-HAM-000123`
- `?siteId={siteId}`
- `?customerId={customerId}`
- `?status=ACTIVE`

Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `TECHNICIAN`
- `BILLING_MANAGER`

#### Update asset
```http
PUT /api/v1/assets/{assetId}
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Change asset status
```http
PATCH /api/v1/assets/{assetId}/status
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Get asset history
```http
GET /api/v1/assets/{assetId}/history
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `TECHNICIAN`

### Internal endpoints

#### Asset summary lookup
```http
GET /internal/v1/assets/{assetId}/summary
```
Used by:
- workorder module

---

## 4. Work Order Service

Module:
```text
smartops-workorder
```

Responsibilities:
- work order creation
- assignment
- intervention lifecycle
- completion reporting
- event publication

### Public endpoints

#### Create work order
```http
POST /api/v1/work-orders
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`

#### Get work order
```http
GET /api/v1/work-orders/{workOrderId}
```
Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `TECHNICIAN`
- `BILLING_MANAGER`

#### Search work orders
```http
GET /api/v1/work-orders
```
Supported query examples:
- `?status=CREATED`
- `?priority=HIGH`
- `?assignedTechnicianUserId={userId}`
- `?customerId={customerId}`

Allowed roles:
- `SUPPORT_AGENT`
- `ADMIN`
- `DISPATCHER`
- `TECHNICIAN`
- `BILLING_MANAGER`

#### Assign technician
```http
POST /api/v1/work-orders/{workOrderId}/assignments
```
Allowed roles:
- `DISPATCHER`
- `ADMIN`

#### Accept assignment
```http
POST /api/v1/work-orders/{workOrderId}/accept
```
Allowed roles:
- `TECHNICIAN`

#### Start intervention
```http
POST /api/v1/work-orders/{workOrderId}/start
```
Allowed roles:
- `TECHNICIAN`

#### Complete work order
```http
POST /api/v1/work-orders/{workOrderId}/complete
```
Allowed roles:
- `TECHNICIAN`

#### Cancel work order
```http
POST /api/v1/work-orders/{workOrderId}/cancel
```
Allowed roles:
- `SUPPORT_AGENT`
- `DISPATCHER`
- `ADMIN`

#### Add work order task
```http
POST /api/v1/work-orders/{workOrderId}/tasks
```
Allowed roles:
- `SUPPORT_AGENT`
- `DISPATCHER`
- `ADMIN`

#### List work order assignments
```http
GET /api/v1/work-orders/{workOrderId}/assignments
```
Allowed roles:
- `SUPPORT_AGENT`
- `DISPATCHER`
- `ADMIN`
- `TECHNICIAN`

#### Get intervention report
```http
GET /api/v1/work-orders/{workOrderId}/report
```
Allowed roles:
- `SUPPORT_AGENT`
- `DISPATCHER`
- `ADMIN`
- `TECHNICIAN`
- `BILLING_MANAGER`

### Internal endpoints

#### Work order summary lookup
```http
GET /internal/v1/work-orders/{workOrderId}/summary
```
Used by:
- billing module
- notification module
- audit module

---

## 5. Billing Service

Module:
```text
smartops-billing
```

Responsibilities:
- invoice generation
- invoice retrieval
- billing state management

### Public endpoints

#### Get invoice
```http
GET /api/v1/invoices/{invoiceId}
```
Allowed roles:
- `BILLING_MANAGER`
- `ADMIN`

#### Search invoices
```http
GET /api/v1/invoices
```
Supported query examples:
- `?customerId={customerId}`
- `?workOrderId={workOrderId}`
- `?status=GENERATED`

Allowed roles:
- `BILLING_MANAGER`
- `ADMIN`

#### Mark invoice as sent
```http
POST /api/v1/invoices/{invoiceId}/mark-sent
```
Allowed roles:
- `BILLING_MANAGER`
- `ADMIN`

#### Cancel invoice
```http
POST /api/v1/invoices/{invoiceId}/cancel
```
Allowed roles:
- `BILLING_MANAGER`
- `ADMIN`

### Internal endpoints

#### Generate invoice from work order
```http
POST /internal/v1/invoices/generate-from-work-order/{workOrderId}
```
Used by:
- internal recovery/admin workflows only

---

## 6. Notification Service

Module:
```text
smartops-notification
```

Responsibilities:
- notification persistence
- notification dispatch
- retry tracking
- event-based recipient messaging

### Public endpoints

#### Get notification
```http
GET /api/v1/notifications/{notificationId}
```
Allowed roles:
- `ADMIN`

#### Search notifications
```http
GET /api/v1/notifications
```
Supported query examples:
- `?recipientReference={userId}`
- `?status=FAILED`
- `?eventType=WORK_ORDER_ASSIGNED`

Allowed roles:
- `ADMIN`

#### Retry failed notification
```http
POST /api/v1/notifications/{notificationId}/retry
```
Allowed roles:
- `ADMIN`

#### List delivery attempts
```http
GET /api/v1/notifications/{notificationId}/delivery-attempts
```
Allowed roles:
- `ADMIN`

---

## 7. Audit Service

Module:
```text
smartops-audit
```

Responsibilities:
- immutable audit history
- event-based traceability
- business event search

### Public endpoints

#### Get audit event
```http
GET /api/v1/audit-events/{eventId}
```
Allowed roles:
- `ADMIN`

#### Search audit events
```http
GET /api/v1/audit-events
```
Supported query examples:
- `?aggregateType=WORK_ORDER`
- `?aggregateId={workOrderId}`
- `?eventType=WORK_ORDER_COMPLETED`
- `?correlationId={correlationId}`

Allowed roles:
- `ADMIN`

---

## 8. Planned event-driven contracts

These are not REST endpoints, but they are first-class platform contracts.

### Work order events
- `WORK_ORDER_CREATED`
- `WORK_ORDER_ASSIGNED`
- `WORK_ORDER_ACCEPTED`
- `WORK_ORDER_STARTED`
- `WORK_ORDER_COMPLETED`
- `WORK_ORDER_CANCELLED`

### Asset events
- `ASSET_REGISTERED`
- `ASSET_STATUS_CHANGED`

### Billing events
- `INVOICE_GENERATED`
- `INVOICE_CANCELLED`

### Notification events
- `NOTIFICATION_SENT`
- `NOTIFICATION_FAILED`

### Identity events
- `USER_CREATED`
- `USER_DEACTIVATED`

---

## 9. Recommended implementation priority

### Phase 1
Implement these first:
- `POST /api/v1/customers`
- `POST /api/v1/customers/{customerId}/sites`
- `POST /api/v1/assets`
- `POST /api/v1/work-orders`
- `GET /api/v1/work-orders/{workOrderId}`

### Phase 2
Then add:
- `POST /api/v1/work-orders/{workOrderId}/assignments`
- `POST /api/v1/work-orders/{workOrderId}/accept`
- `POST /api/v1/work-orders/{workOrderId}/start`
- `POST /api/v1/work-orders/{workOrderId}/complete`

### Phase 3
Then add downstream visibility:
- `GET /api/v1/invoices/{invoiceId}`
- `GET /api/v1/notifications`
- `GET /api/v1/audit-events`

---

## 10. Notes for future refinement

This catalog is the initial contract inventory.

As implementation progresses, this document should be refined with:
- request DTO schemas
- response DTO schemas
- validation rules
- example payloads
- error codes
- pagination format
- sorting/filtering conventions
- OpenAPI references
