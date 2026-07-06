package ch.smart.operations.platform.notification.infrastructure.persistence.repositories;

import ch.smart.operations.platform.notification.domain.enums.NotificationStatus;
import ch.smart.operations.platform.notification.infrastructure.persistence.entities.NotificationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationJpaRepository extends JpaRepository<NotificationJpaEntity, UUID> {
    boolean existsByDeduplicationKey(String deduplicationKey);
    List<NotificationJpaEntity> findByRecipientReference(String recipientReference);
    List<NotificationJpaEntity> findByStatus(NotificationStatus status);
    List<NotificationJpaEntity> findByEventType(String eventType);
}