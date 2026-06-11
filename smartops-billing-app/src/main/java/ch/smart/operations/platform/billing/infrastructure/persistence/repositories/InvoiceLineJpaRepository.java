package ch.smart.operations.platform.billing.infrastructure.persistence.repositories;

import ch.smart.operations.platform.billing.infrastructure.persistence.entities.InvoiceLineJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvoiceLineJpaRepository extends JpaRepository<InvoiceLineJpaEntity, UUID> {

    List<InvoiceLineJpaEntity> findByInvoiceIdOrderByLineNumberAsc(UUID invoiceId);
}