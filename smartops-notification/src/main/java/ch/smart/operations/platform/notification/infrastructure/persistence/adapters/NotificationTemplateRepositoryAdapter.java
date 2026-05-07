package ch.smart.operations.platform.notification.infrastructure.persistence.adapters;

import ch.smart.operations.platform.notification.application.ports.NotificationTemplateRepository;
import ch.smart.operations.platform.notification.domain.entities.NotificationTemplate;
import ch.smart.operations.platform.notification.domain.enums.NotificationChannel;
import ch.smart.operations.platform.notification.infrastructure.persistence.entities.NotificationTemplateJpaEntity;
import ch.smart.operations.platform.notification.infrastructure.persistence.repositories.NotificationTemplateJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class NotificationTemplateRepositoryAdapter implements NotificationTemplateRepository {

    private final NotificationTemplateJpaRepository repository;

    public NotificationTemplateRepositoryAdapter(NotificationTemplateJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<NotificationTemplate> findActiveByEventTypeAndChannel(String eventType, NotificationChannel channel) {
        return repository.findFirstByEventTypeAndChannelAndActiveTrue(eventType, channel)
                .map(this::toDomain);
    }

    private NotificationTemplate toDomain(NotificationTemplateJpaEntity entity) {
        return new NotificationTemplate(
                entity.getId(),
                entity.getCode(),
                entity.getEventType(),
                entity.getChannel(),
                entity.getSubjectTemplate(),
                entity.getBodyTemplate(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}