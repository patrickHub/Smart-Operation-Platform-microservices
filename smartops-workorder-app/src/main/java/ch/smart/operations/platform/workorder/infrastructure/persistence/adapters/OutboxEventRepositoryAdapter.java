package ch.smart.operations.platform.workorder.infrastructure.persistence.adapters;

import ch.smart.operations.platform.workorder.application.ports.OutboxEventRepository;
import ch.smart.operations.platform.workorder.domain.entities.OutboxEvent;
import ch.smart.operations.platform.workorder.domain.enums.OutboxEventStatus;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.OutboxEventJpaEntity;
import ch.smart.operations.platform.workorder.infrastructure.persistence.repositories.OutboxEventJpaRepository;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class OutboxEventRepositoryAdapter implements OutboxEventRepository {

    private final OutboxEventJpaRepository repository;

    public OutboxEventRepositoryAdapter(OutboxEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return toDomain(repository.save(toJpa(event)));
    }

     @Override
    public List<OutboxEvent> findPendingEvents(int maxRetryCount) {
        return repository
                .findTop50ByStatusAndRetryCountLessThanOrderByOccurredAtAsc(
                        OutboxEventStatus.PENDING,
                        maxRetryCount
                )
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private OutboxEvent toDomain(OutboxEventJpaEntity entity) {
        return new OutboxEvent(
                entity.getId(),
                entity.getAggregateType(),
                entity.getAggregateId(),
                entity.getEventType(),
                entity.getEventPayload(),
                entity.getEventKey(),
                entity.getStatus(),
                entity.getOccurredAt(),
                entity.getPublishedAt(),
                entity.getRetryCount()
        );
    }

    private OutboxEventJpaEntity toJpa(OutboxEvent event) {
        OutboxEventJpaEntity entity = new OutboxEventJpaEntity();
        entity.setId(event.getId());
        entity.setAggregateType(event.getAggregateType());
        entity.setAggregateId(event.getAggregateId());
        entity.setEventType(event.getEventType());
        entity.setEventPayload(event.getEventPayload());
        entity.setEventKey(event.getEventKey());
        entity.setStatus(event.getStatus());
        entity.setOccurredAt(event.getOccurredAt());
        entity.setPublishedAt(event.getPublishedAt());
        entity.setRetryCount(event.getRetryCount());
        return entity;
    }
}