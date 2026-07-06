package ch.smart.operations.platform.notification.domain.entities;

import ch.smart.operations.platform.notification.domain.enums.DeliveryAttemptStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationDeliveryAttempt {

    private final UUID id;
    private final UUID notificationId;
    private final OffsetDateTime attemptedAt;
    private final DeliveryAttemptStatus status;
    private final String providerResponse;
    private final String errorMessage;

    public NotificationDeliveryAttempt(
            UUID id,
            UUID notificationId,
            OffsetDateTime attemptedAt,
            DeliveryAttemptStatus status,
            String providerResponse,
            String errorMessage
    ) {
        this.id = id;
        this.notificationId = notificationId;
        this.attemptedAt = attemptedAt;
        this.status = status;
        this.providerResponse = providerResponse;
        this.errorMessage = errorMessage;
    }

    public static NotificationDeliveryAttempt success(UUID notificationId, String providerResponse) {
        return new NotificationDeliveryAttempt(
                UUID.randomUUID(),
                notificationId,
                OffsetDateTime.now(),
                DeliveryAttemptStatus.SUCCESS,
                providerResponse,
                null
        );
    }

    public static NotificationDeliveryAttempt failed(UUID notificationId, String errorMessage) {
        return new NotificationDeliveryAttempt(
                UUID.randomUUID(),
                notificationId,
                OffsetDateTime.now(),
                DeliveryAttemptStatus.FAILED,
                null,
                errorMessage
        );
    }

    public UUID getId() { return id; }
    public UUID getNotificationId() { return notificationId; }
    public OffsetDateTime getAttemptedAt() { return attemptedAt; }
    public DeliveryAttemptStatus getStatus() { return status; }
    public String getProviderResponse() { return providerResponse; }
    public String getErrorMessage() { return errorMessage; }
}