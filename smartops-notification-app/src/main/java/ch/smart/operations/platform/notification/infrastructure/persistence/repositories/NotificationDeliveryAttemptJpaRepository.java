package ch.smart.operations.platform.notification.infrastructure.persistence.repositories;

import ch.smart.operations.platform.notification.infrastructure.persistence.entities.NotificationDeliveryAttemptJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationDeliveryAttemptJpaRepository extends JpaRepository<NotificationDeliveryAttemptJpaEntity, UUID> {
    List<NotificationDeliveryAttemptJpaEntity> findByNotificationIdOrderByAttemptedAtDesc(UUID notificationId);
}