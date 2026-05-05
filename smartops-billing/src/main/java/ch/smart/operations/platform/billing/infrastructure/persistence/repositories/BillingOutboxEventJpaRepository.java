package ch.smart.operations.platform.billing.infrastructure.persistence.repositories;

import ch.smart.operations.platform.billing.infrastructure.persistence.entities.BillingOutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BillingOutboxEventJpaRepository extends JpaRepository<BillingOutboxEventJpaEntity, UUID> {
}