# Sequence Diagrams

This document captures the first important business workflows of the SmartOps Platform.

## 1. Create customer, site, asset, and work order

```mermaid
sequenceDiagram
    actor Support as Support Agent
    participant Customer as Customer Module
    participant Asset as Asset Module
    participant WorkOrder as Workorder Module
    participant DB as PostgreSQL
    participant Kafka as Kafka
    participant Notification as Notification Module
    participant Audit as Audit Module

    Support->>Customer: Create customer
    Customer->>DB: Insert customer
    Customer-->>Support: Customer created

    Support->>Customer: Create site
    Customer->>DB: Insert site
    Customer-->>Support: Site created

    Support->>Asset: Register asset
    Asset->>DB: Insert asset
    Asset-->>Support: Asset created

    Support->>WorkOrder: Create work order
    WorkOrder->>Asset: Validate asset reference
    Asset-->>WorkOrder: Asset summary
    WorkOrder->>DB: Insert work order + outbox event
    WorkOrder->>Kafka: Publish WORK_ORDER_CREATED
    Kafka-->>Notification: Deliver WORK_ORDER_CREATED
    Kafka-->>Audit: Deliver WORK_ORDER_CREATED
    Notification->>DB: Store notification
    Audit->>DB: Store audit event
    WorkOrder-->>Support: Work order created
```

## 2. Assign technician to work order

```mermaid
sequenceDiagram
    actor Dispatcher
    participant WorkOrder as Workorder Module
    participant Identity as Identity Module
    participant DB as PostgreSQL
    participant Kafka as Kafka
    participant Notification as Notification Module
    participant Audit as Audit Module

    Dispatcher->>WorkOrder: Assign technician
    WorkOrder->>Identity: Validate technician
    Identity-->>WorkOrder: User summary
    WorkOrder->>DB: Update work order + insert assignment + outbox
    WorkOrder->>Kafka: Publish WORK_ORDER_ASSIGNED
    Kafka-->>Notification: Deliver WORK_ORDER_ASSIGNED
    Kafka-->>Audit: Deliver WORK_ORDER_ASSIGNED
    Notification->>DB: Store assignment notification
    Audit->>DB: Store audit event
    WorkOrder-->>Dispatcher: Assignment confirmed
```

## 3. Complete work order and generate invoice

```mermaid
sequenceDiagram
    actor Technician
    participant WorkOrder as Workorder Module
    participant DB as PostgreSQL
    participant Kafka as Kafka
    participant Billing as Billing Module
    participant Notification as Notification Module
    participant Audit as Audit Module

    Technician->>WorkOrder: Complete work order
    WorkOrder->>DB: Save intervention report + used parts + outbox
    WorkOrder->>Kafka: Publish WORK_ORDER_COMPLETED
    Kafka-->>Billing: Deliver WORK_ORDER_COMPLETED
    Kafka-->>Notification: Deliver WORK_ORDER_COMPLETED
    Kafka-->>Audit: Deliver WORK_ORDER_COMPLETED

    Billing->>DB: Create invoice + outbox
    Billing->>Kafka: Publish INVOICE_GENERATED

    Kafka-->>Notification: Deliver INVOICE_GENERATED
    Kafka-->>Audit: Deliver INVOICE_GENERATED

    Notification->>DB: Store completion/invoice notifications
    Audit->>DB: Store work-order and invoice audit events
    WorkOrder-->>Technician: Work order completed
```

## 4. Outbox publishing pattern

```mermaid
sequenceDiagram
    participant UseCase as Application Use Case
    participant Domain as Domain Aggregate
    participant DB as PostgreSQL
    participant Publisher as Outbox Publisher
    participant Kafka as Kafka

    UseCase->>Domain: Execute business command
    Domain-->>UseCase: Domain event
    UseCase->>DB: Persist aggregate state
    UseCase->>DB: Persist outbox record
    Note over DB: Same database transaction
    Publisher->>DB: Poll pending outbox records
    Publisher->>Kafka: Publish event
    Publisher->>DB: Mark outbox record as published
```

## Notes

### Why sequence diagrams matter
These diagrams make it easier to explain:
- where business rules run
- when data is committed
- when events are emitted
- how downstream modules react

### Recommended first implementation focus
Start with the workflows above before documenting additional edge cases such as cancellation, retries, and failure handling.
