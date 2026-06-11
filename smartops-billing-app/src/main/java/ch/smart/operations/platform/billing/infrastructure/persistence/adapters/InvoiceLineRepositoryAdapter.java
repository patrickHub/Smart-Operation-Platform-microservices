package ch.smart.operations.platform.billing.infrastructure.persistence.adapters;

import ch.smart.operations.platform.billing.application.ports.InvoiceLineRepository;
import ch.smart.operations.platform.billing.domain.entities.InvoiceLine;
import ch.smart.operations.platform.billing.infrastructure.persistence.entities.InvoiceLineJpaEntity;
import ch.smart.operations.platform.billing.infrastructure.persistence.repositories.InvoiceLineJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class InvoiceLineRepositoryAdapter implements InvoiceLineRepository {

    private final InvoiceLineJpaRepository repository;

    public InvoiceLineRepositoryAdapter(InvoiceLineJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public InvoiceLine save(InvoiceLine line) {
        return toDomain(repository.save(toJpa(line)));
    }

    @Override
    public List<InvoiceLine> findByInvoiceId(UUID invoiceId) {
        return repository.findByInvoiceIdOrderByLineNumberAsc(invoiceId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private InvoiceLine toDomain(InvoiceLineJpaEntity entity) {
        return new InvoiceLine(
                entity.getId(),
                entity.getInvoiceId(),
                entity.getLineNumber(),
                entity.getLineType(),
                entity.getDescription(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getLineTotal()
        );
    }

    private InvoiceLineJpaEntity toJpa(InvoiceLine line) {
        InvoiceLineJpaEntity entity = new InvoiceLineJpaEntity();

        entity.setId(line.getId());
        entity.setInvoiceId(line.getInvoiceId());
        entity.setLineNumber(line.getLineNumber());
        entity.setLineType(line.getLineType());
        entity.setDescription(line.getDescription());
        entity.setQuantity(line.getQuantity());
        entity.setUnitPrice(line.getUnitPrice());
        entity.setLineTotal(line.getLineTotal());

        return entity;
    }
}