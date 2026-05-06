package ch.smart.operations.platform.billing.infrastructure.persistence.repositories;

import ch.smart.operations.platform.billing.domain.enums.BillingOutboxEventStatus;
import ch.smart.operations.platform.billing.infrastructure.persistence.entities.BillingOutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BillingOutboxEventJpaRepository extends JpaRepository<BillingOutboxEventJpaEntity, UUID> {
    List<BillingOutboxEventJpaEntity> findTop50ByStatusAndRetryCountLessThanOrderByOccurredAtAsc(
            BillingOutboxEventStatus status,
            int retryCount
    );
}