package ch.smart.operations.platform.workorder.domain.entities;

import ch.smart.operations.platform.workorder.domain.enums.OutboxEventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class OutboxEvent {

    private final UUID id;
    private final String aggregateType;
    private final UUID aggregateId;
    private final String eventType;
    private final String eventPayload;
    private final String eventKey;
    private final OutboxEventStatus status;
    private final OffsetDateTime occurredAt;
    private final OffsetDateTime publishedAt;
    private final int retryCount;

    public OutboxEvent(
            UUID id,
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String eventPayload,
            String eventKey,
            OutboxEventStatus status,
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

    public static OutboxEvent pending(UUID aggregateId, String eventType, String eventPayload) {
        return new OutboxEvent(
                UUID.randomUUID(),
                "WORK_ORDER",
                aggregateId,
                eventType,
                eventPayload,
                aggregateId.toString(),
                OutboxEventStatus.PENDING,
                OffsetDateTime.now(),
                null,
                0
        );
    }

    public OutboxEvent published() {
        return new OutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                eventPayload,
                eventKey,
                OutboxEventStatus.PUBLISHED,
                occurredAt,
                OffsetDateTime.now(),
                retryCount
        );
    }

    public OutboxEvent failed() {
        return new OutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                eventPayload,
                eventKey,
                OutboxEventStatus.FAILED,
                occurredAt,
                publishedAt,
                retryCount + 1
        );
    }

    public OutboxEvent retryPending() {
        return new OutboxEvent(
                id,
                aggregateType,
                aggregateId,
                eventType,
                eventPayload,
                eventKey,
                OutboxEventStatus.PENDING,
                occurredAt,
                publishedAt,
                retryCount + 1
        );
    }

    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getEventPayload() { return eventPayload; }
    public String getEventKey() { return eventKey; }
    public OutboxEventStatus getStatus() { return status; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public OffsetDateTime getPublishedAt() { return publishedAt; }
    public int getRetryCount() { return retryCount; }
}