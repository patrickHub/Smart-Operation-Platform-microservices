package ch.smart.operations.platform.billing.infrastructure.persistence.adapters;

import ch.smart.operations.platform.billing.application.ports.InvoiceRepository;
import ch.smart.operations.platform.billing.domain.entities.Invoice;
import ch.smart.operations.platform.billing.domain.enums.InvoiceStatus;
import ch.smart.operations.platform.billing.infrastructure.persistence.entities.InvoiceJpaEntity;
import ch.smart.operations.platform.billing.infrastructure.persistence.repositories.InvoiceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InvoiceRepositoryAdapter implements InvoiceRepository {

    private final InvoiceJpaRepository repository;

    public InvoiceRepositoryAdapter(InvoiceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Invoice save(Invoice invoice) {
        return toDomain(repository.save(toJpa(invoice)));
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Invoice> findByWorkOrderId(UUID workOrderId) {
        return repository.findByWorkOrderId(workOrderId).map(this::toDomain);
    }

    @Override
    public boolean existsByWorkOrderId(UUID workOrderId) {
        return repository.existsByWorkOrderId(workOrderId);
    }

    @Override
    public List<Invoice> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Invoice> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Invoice> findByWorkOrderIdAsList(UUID workOrderId) {
        return repository.findByWorkOrderId(workOrderId)
                .map(this::toDomain)
                .stream()
                .toList();
    }

    @Override
    public List<Invoice> findByStatus(InvoiceStatus status) {
        return repository.findByStatus(status).stream().map(this::toDomain).toList();
    }

    private Invoice toDomain(InvoiceJpaEntity entity) {
        return new Invoice(
                entity.getId(),
                entity.getInvoiceNumber(),
                entity.getWorkOrderId(),
                entity.getCustomerId(),
                entity.getPricingPolicyId(),
                entity.getStatus(),
                entity.getCurrency(),
                entity.getSubtotalAmount(),
                entity.getTaxAmount(),
                entity.getTotalAmount(),
                entity.getGeneratedAt(),
                entity.getDueDate(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }

    private InvoiceJpaEntity toJpa(Invoice invoice) {
        InvoiceJpaEntity entity = new InvoiceJpaEntity();

        entity.setId(invoice.getId());
        entity.setInvoiceNumber(invoice.getInvoiceNumber());
        entity.setWorkOrderId(invoice.getWorkOrderId());
        entity.setCustomerId(invoice.getCustomerId());
        entity.setPricingPolicyId(invoice.getPricingPolicyId());
        entity.setStatus(invoice.getStatus());
        entity.setCurrency(invoice.getCurrency());
        entity.setSubtotalAmount(invoice.getSubtotalAmount());
        entity.setTaxAmount(invoice.getTaxAmount());
        entity.setTotalAmount(invoice.getTotalAmount());
        entity.setGeneratedAt(invoice.getGeneratedAt());
        entity.setDueDate(invoice.getDueDate());
        entity.setVersion(invoice.getVersion());
        entity.setCreatedAt(invoice.getCreatedAt());
        entity.setCreatedBy(invoice.getCreatedBy());
        entity.setUpdatedAt(invoice.getUpdatedAt());
        entity.setUpdatedBy(invoice.getUpdatedBy());

        return entity;
    }
}