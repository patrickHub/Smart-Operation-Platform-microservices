package ch.smart.operations.platform.billing.infrastructure.persistence.adapters;

import ch.smart.operations.platform.billing.application.ports.BillingOutboxEventRepository;
import ch.smart.operations.platform.billing.domain.entities.BillingOutboxEvent;
import ch.smart.operations.platform.billing.infrastructure.persistence.entities.BillingOutboxEventJpaEntity;
import ch.smart.operations.platform.billing.infrastructure.persistence.repositories.BillingOutboxEventJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public class BillingOutboxEventRepositoryAdapter implements BillingOutboxEventRepository {

    private final BillingOutboxEventJpaRepository repository;

    public BillingOutboxEventRepositoryAdapter(BillingOutboxEventJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public BillingOutboxEvent save(BillingOutboxEvent event) {
        return toDomain(repository.save(toJpa(event)));
    }

    private BillingOutboxEvent toDomain(BillingOutboxEventJpaEntity entity) {
        return new BillingOutboxEvent(
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

    private BillingOutboxEventJpaEntity toJpa(BillingOutboxEvent event) {
        BillingOutboxEventJpaEntity entity = new BillingOutboxEventJpaEntity();

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