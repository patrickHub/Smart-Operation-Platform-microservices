package ch.smart.operations.platform.notification.application.ports;

import ch.smart.operations.platform.notification.domain.entities.NotificationDeliveryAttempt;

import java.util.List;
import java.util.UUID;

public interface NotificationDeliveryAttemptRepository {
    NotificationDeliveryAttempt save(NotificationDeliveryAttempt attempt);
    List<NotificationDeliveryAttempt> findByNotificationId(UUID notificationId);
}