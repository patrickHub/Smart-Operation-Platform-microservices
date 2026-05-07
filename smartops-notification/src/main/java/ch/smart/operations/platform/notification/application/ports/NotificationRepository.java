package ch.smart.operations.platform.notification.application.ports;

import ch.smart.operations.platform.notification.domain.entities.Notification;
import ch.smart.operations.platform.notification.domain.enums.NotificationStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {
    Notification save(Notification notification);
    Optional<Notification> findById(UUID id);
    boolean existsByDeduplicationKey(String deduplicationKey);
    List<Notification> findAll();
    List<Notification> findByRecipientReference(String recipientReference);
    List<Notification> findByStatus(NotificationStatus status);
    List<Notification> findByEventType(String eventType);
}