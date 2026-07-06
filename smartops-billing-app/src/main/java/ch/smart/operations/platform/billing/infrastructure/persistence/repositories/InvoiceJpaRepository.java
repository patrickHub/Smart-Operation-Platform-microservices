package ch.smart.operations.platform.billing.infrastructure.persistence.repositories;

import ch.smart.operations.platform.billing.domain.enums.InvoiceStatus;
import ch.smart.operations.platform.billing.infrastructure.persistence.entities.InvoiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, UUID> {

    Optional<InvoiceJpaEntity> findByWorkOrderId(UUID workOrderId);

    boolean existsByWorkOrderId(UUID workOrderId);

    List<InvoiceJpaEntity> findByCustomerId(UUID customerId);

    List<InvoiceJpaEntity> findByStatus(InvoiceStatus status);
}