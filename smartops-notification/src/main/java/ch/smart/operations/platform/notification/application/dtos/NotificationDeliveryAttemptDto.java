package ch.smart.operations.platform.notification.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationDeliveryAttemptDto(
        UUID id,
        UUID notificationId,
        OffsetDateTime attemptedAt,
        String status,
        String providerResponse,
        String errorMessage
) {
}