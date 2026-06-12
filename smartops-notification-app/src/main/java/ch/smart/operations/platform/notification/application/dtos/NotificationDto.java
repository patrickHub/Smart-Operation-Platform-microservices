package ch.smart.operations.platform.notification.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDto(
        UUID id,
        UUID sourceEventId,
        String eventType,
        String recipientType,
        String recipientReference,
        String channel,
        String status,
        String subject,
        String body,
        String deduplicationKey,
        OffsetDateTime scheduledAt,
        OffsetDateTime sentAt,
        String failureReason,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}