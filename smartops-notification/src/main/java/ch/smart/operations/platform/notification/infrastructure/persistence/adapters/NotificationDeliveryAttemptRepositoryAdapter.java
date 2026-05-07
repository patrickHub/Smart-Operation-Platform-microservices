package ch.smart.operations.platform.notification.infrastructure.persistence.adapters;

import ch.smart.operations.platform.notification.application.ports.NotificationDeliveryAttemptRepository;
import ch.smart.operations.platform.notification.domain.entities.NotificationDeliveryAttempt;
import ch.smart.operations.platform.notification.infrastructure.persistence.entities.NotificationDeliveryAttemptJpaEntity;
import ch.smart.operations.platform.notification.infrastructure.persistence.repositories.NotificationDeliveryAttemptJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class NotificationDeliveryAttemptRepositoryAdapter implements NotificationDeliveryAttemptRepository {

    private final NotificationDeliveryAttemptJpaRepository repository;

    public NotificationDeliveryAttemptRepositoryAdapter(NotificationDeliveryAttemptJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public NotificationDeliveryAttempt save(NotificationDeliveryAttempt attempt) {
        return toDomain(repository.save(toJpa(attempt)));
    }

    @Override
    public List<NotificationDeliveryAttempt> findByNotificationId(UUID notificationId) {
        return repository.findByNotificationIdOrderByAttemptedAtDesc(notificationId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private NotificationDeliveryAttempt toDomain(NotificationDeliveryAttemptJpaEntity entity) {
        return new NotificationDeliveryAttempt(
                entity.getId(),
                entity.getNotificationId(),
                entity.getAttemptedAt(),
                entity.getStatus(),
                entity.getProviderResponse(),
                entity.getErrorMessage()
        );
    }

    private NotificationDeliveryAttemptJpaEntity toJpa(NotificationDeliveryAttempt attempt) {
        NotificationDeliveryAttemptJpaEntity entity = new NotificationDeliveryAttemptJpaEntity();
        entity.setId(attempt.getId());
        entity.setNotificationId(attempt.getNotificationId());
        entity.setAttemptedAt(attempt.getAttemptedAt());
        entity.setStatus(attempt.getStatus());
        entity.setProviderResponse(attempt.getProviderResponse());
        entity.setErrorMessage(attempt.getErrorMessage());
        return entity;
    }
}