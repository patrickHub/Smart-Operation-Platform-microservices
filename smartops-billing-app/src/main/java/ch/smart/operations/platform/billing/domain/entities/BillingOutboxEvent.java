package ch.smart.operations.platform.billing.domain.entities;

import ch.smart.operations.platform.billing.domain.enums.BillingOutboxEventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class BillingOutboxEvent {

    private final UUID id;
    private final String aggregateType;
    private final UUID aggregateId;
    private final String eventType;
    private final String eventPayload;
    private final String eventKey;
    private final BillingOutboxEventStatus status;
    private final OffsetDateTime occurredAt;
    private final OffsetDateTime publishedAt;
    private final int retryCount;

    public BillingOutboxEvent(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String eventPayload,
            String eventKey,
            BillingOutboxEventStatus status,
            OffsetDateTime occurredAt,
            OffsetDateTime publishedAt,
            int retryCount
    ) {
        this.id = id;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventPayload = eventPayload;
        this.eventKey = eventKey;
        this.status = status;
        this.occurredAt = occurredAt;
        this.publishedAt = publishedAt;
        this.retryCount = retryCount;
    }

    public static BillingOutboxEvent pending(UUID invoiceId, String eventType, String payload) {
        return new BillingOutboxEvent(
                UUID.randomUUID(),
                "INVOICE",
                invoiceId,
                eventType,
                payload,
                invoiceId.toString(),
                BillingOutboxEventStatus.PENDING,
                OffsetDateTime.now(),
                null,
                0
        );
    }

    public BillingOutboxEvent published() {
        return new BillingOutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                eventPayload,
                eventKey,
                BillingOutboxEventStatus.PUBLISHED,
                occurredAt,
                OffsetDateTime.now(),
                retryCount
        );
    }

    public BillingOutboxEvent failed() {
        return new BillingOutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                eventPayload,
                eventKey,
                BillingOutboxEventStatus.FAILED,
                occurredAt,
                publishedAt,
                retryCount + 1
        );
    }

    public BillingOutboxEvent retryPending() {
        return new BillingOutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                eventPayload,
                eventKey,
                BillingOutboxEventStatus.PENDING,
                occurredAt,
                publishedAt,
                retryCount + 1
        );
    }

    public UUID getId() {
        return id;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public String getEventKey() {
        return eventKey;
    }

    public BillingOutboxEventStatus getStatus() {
        return status;
    }

    public OffsetDateTime getOccurredAt() {
        return occurredAt;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public int getRetryCount() {
        return retryCount;
    }
}