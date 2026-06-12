package ch.smart.operations.platform.notification.infrastructure.persistence.entities;

import ch.smart.operations.platform.notification.domain.enums.DeliveryAttemptStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_delivery_attempts", schema = "notification")
public class NotificationDeliveryAttemptJpaEntity {

    @Id
    private UUID id;

    @Column(name = "notification_id", nullable = false)
    private UUID notificationId;

    @Column(name = "attempted_at", nullable = false)
    private OffsetDateTime attemptedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DeliveryAttemptStatus status;

    @Column(name = "provider_response", columnDefinition = "TEXT")
    private String providerResponse;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;


    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getNotificationId() {
        return this.notificationId;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public OffsetDateTime getAttemptedAt() {
        return this.attemptedAt;
    }

    public void setAttemptedAt(OffsetDateTime attemptedAt) {
        this.attemptedAt = attemptedAt;
    }

    public DeliveryAttemptStatus getStatus() {
        return this.status;
    }

    public void setStatus(DeliveryAttemptStatus status) {
        this.status = status;
    }

    public String getProviderResponse() {
        return this.providerResponse;
    }

    public void setProviderResponse(String providerResponse) {
        this.providerResponse = providerResponse;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
