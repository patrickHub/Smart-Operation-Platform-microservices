package ch.smart.operations.platform.notification.infrastructure.persistence.adapters;

import ch.smart.operations.platform.notification.application.ports.NotificationRepository;
import ch.smart.operations.platform.notification.domain.entities.Notification;
import ch.smart.operations.platform.notification.domain.enums.NotificationStatus;
import ch.smart.operations.platform.notification.infrastructure.persistence.entities.NotificationJpaEntity;
import ch.smart.operations.platform.notification.infrastructure.persistence.repositories.NotificationJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository repository;

    public NotificationRepositoryAdapter(NotificationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Notification save(Notification notification) {
        return toDomain(repository.save(toJpa(notification)));
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByDeduplicationKey(String deduplicationKey) {
        if (deduplicationKey == null || deduplicationKey.isBlank()) {
            return false;
        }
        return repository.existsByDeduplicationKey(deduplicationKey);
    }

    @Override
    public List<Notification> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Notification> findByRecipientReference(String recipientReference) {
        return repository.findByRecipientReference(recipientReference).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Notification> findByStatus(NotificationStatus status) {
        return repository.findByStatus(status).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Notification> findByEventType(String eventType) {
        return repository.findByEventType(eventType).stream().map(this::toDomain).toList();
    }

    private Notification toDomain(NotificationJpaEntity entity) {
        return new Notification(
                entity.getId(),
                entity.getSourceEventId(),
                entity.getEventType(),
                entity.getRecipientType(),
                entity.getRecipientReference(),
                entity.getChannel(),
                entity.getStatus(),
                entity.getSubject(),
                entity.getBody(),
                entity.getDeduplicationKey(),
                entity.getScheduledAt(),
                entity.getSentAt(),
                entity.getFailureReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private NotificationJpaEntity toJpa(Notification notification) {
        NotificationJpaEntity entity = new NotificationJpaEntity();
        entity.setId(notification.getId());
        entity.setSourceEventId(notification.getSourceEventId());
        entity.setEventType(notification.getEventType());
        entity.setRecipientType(notification.getRecipientType());
        entity.setRecipientReference(notification.getRecipientReference());
        entity.setChannel(notification.getChannel());
        entity.setStatus(notification.getStatus());
        entity.setSubject(notification.getSubject());
        entity.setBody(notification.getBody());
        entity.setDeduplicationKey(notification.getDeduplicationKey());
        entity.setScheduledAt(notification.getScheduledAt());
        entity.setSentAt(notification.getSentAt());
        entity.setFailureReason(notification.getFailureReason());
        entity.setCreatedAt(notification.getCreatedAt());
        entity.setUpdatedAt(notification.getUpdatedAt());
        return entity;
    }
}
