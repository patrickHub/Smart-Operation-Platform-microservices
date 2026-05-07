package ch.smart.operations.platform.notification.infrastructure.persistence.repositories;

import ch.smart.operations.platform.notification.domain.enums.NotificationChannel;
import ch.smart.operations.platform.notification.infrastructure.persistence.entities.NotificationTemplateJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface NotificationTemplateJpaRepository extends JpaRepository<NotificationTemplateJpaEntity, UUID> {
    Optional<NotificationTemplateJpaEntity> findFirstByEventTypeAndChannelAndActiveTrue(
            String eventType,
            NotificationChannel channel
    );
}