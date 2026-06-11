package ch.smart.operations.platform.workorder.infrastructure.persistence.entities;

import ch.smart.operations.platform.workorder.domain.enums.OutboxEventStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events", schema = "workorder")
public class OutboxEventJpaEntity {

    @Id
    private UUID id;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "event_payload", nullable = false, columnDefinition = "TEXT")
    private String eventPayload;

    @Column(name = "event_key", nullable = false)
    private String eventKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OutboxEventStatus status;

    @Column(name = "occurred_at", nullable = false)
    private OffsetDateTime occurredAt;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;


    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAggregateType() {
        return this.aggregateType;
    }

    public void setAggregateType(String aggregateType) {
        this.aggregateType = aggregateType;
    }

    public UUID getAggregateId() {
        return this.aggregateId;
    }

    public void setAggregateId(UUID aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventPayload() {
        return this.eventPayload;
    }

    public void setEventPayload(String eventPayload) {
        this.eventPayload = eventPayload;
    }

    public String getEventKey() {
        return this.eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public OutboxEventStatus getStatus() {
        return this.status;
    }

    public void setStatus(OutboxEventStatus status) {
        this.status = status;
    }

    public OffsetDateTime getOccurredAt() {
        return this.occurredAt;
    }

    public void setOccurredAt(OffsetDateTime occurredAt) {
        this.occurredAt = occurredAt;
    }

    public OffsetDateTime getPublishedAt() {
        return this.publishedAt;
    }

    public void setPublishedAt(OffsetDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public int getRetryCount() {
        return this.retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
    
}