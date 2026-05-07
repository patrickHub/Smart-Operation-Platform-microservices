package ch.smart.operations.platform.notification.domain.entities;

import ch.smart.operations.platform.notification.domain.enums.NotificationChannel;
import ch.smart.operations.platform.notification.domain.enums.NotificationStatus;
import ch.smart.operations.platform.notification.domain.enums.RecipientType;
import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final UUID sourceEventId;
    private final String eventType;
    private final RecipientType recipientType;
    private final String recipientReference;
    private final NotificationChannel channel;
    private final NotificationStatus status;
    private final String subject;
    private final String body;
    private final String deduplicationKey;
    private final OffsetDateTime scheduledAt;
    private final OffsetDateTime sentAt;
    private final String failureReason;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public Notification(
            UUID id,
            UUID sourceEventId,
            String eventType,
            RecipientType recipientType,
            String recipientReference,
            NotificationChannel channel,
            NotificationStatus status,
            String subject,
            String body,
            String deduplicationKey,
            OffsetDateTime scheduledAt,
            OffsetDateTime sentAt,
            String failureReason,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.eventType = eventType;
        this.recipientType = recipientType;
        this.recipientReference = recipientReference;
        this.channel = channel;
        this.status = status;
        this.subject = subject;
        this.body = body;
        this.deduplicationKey = deduplicationKey;
        this.scheduledAt = scheduledAt;
        this.sentAt = sentAt;
        this.failureReason = failureReason;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static Notification pending(
            UUID sourceEventId,
            String eventType,
            RecipientType recipientType,
            String recipientReference,
            NotificationChannel channel,
            String subject,
            String body,
            String deduplicationKey
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Notification(
                UUID.randomUUID(),
                sourceEventId,
                eventType,
                recipientType,
                recipientReference,
                channel,
                NotificationStatus.PENDING,
                subject,
                body,
                deduplicationKey,
                null,
                null,
                null,
                now,
                now
        );
    }

    public Notification sent() {
        if (status == NotificationStatus.CANCELLED) {
            throw new BusinessRuleException("Cancelled notifications cannot be sent.");
        }

        OffsetDateTime now = OffsetDateTime.now();

        return new Notification(
                id,
                sourceEventId,
                eventType,
                recipientType,
                recipientReference,
                channel,
                NotificationStatus.SENT,
                subject,
                body,
                deduplicationKey,
                scheduledAt,
                now,
                null,
                createdAt,
                now
        );
    }

    public Notification failed(String reason) {
        OffsetDateTime now = OffsetDateTime.now();

        return new Notification(
                id,
                sourceEventId,
                eventType,
                recipientType,
                recipientReference,
                channel,
                NotificationStatus.FAILED,
                subject,
                body,
                deduplicationKey,
                scheduledAt,
                sentAt,
                reason,
                createdAt,
                now
        );
    }

    public Notification retry() {
        if (status != NotificationStatus.FAILED) {
            throw new BusinessRuleException("Only FAILED notifications can be retried.");
        }

        OffsetDateTime now = OffsetDateTime.now();

        return new Notification(
                id,
                sourceEventId,
                eventType,
                recipientType,
                recipientReference,
                channel,
                NotificationStatus.PENDING,
                subject,
                body,
                deduplicationKey,
                scheduledAt,
                null,
                null,
                createdAt,
                now
        );
    }

    public UUID getId() { return id; }
    public UUID getSourceEventId() { return sourceEventId; }
    public String getEventType() { return eventType; }
    public RecipientType getRecipientType() { return recipientType; }
    public String getRecipientReference() { return recipientReference; }
    public NotificationChannel getChannel() { return channel; }
    public NotificationStatus getStatus() { return status; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public String getDeduplicationKey() { return deduplicationKey; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public OffsetDateTime getSentAt() { return sentAt; }
    public String getFailureReason() { return failureReason; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}