package ch.smart.operations.platform.billing.application.ports;

import ch.smart.operations.platform.billing.domain.entities.Invoice;
import ch.smart.operations.platform.billing.domain.enums.InvoiceStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository {
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(UUID id);
    Optional<Invoice> findByWorkOrderId(UUID workOrderId);
    boolean existsByWorkOrderId(UUID workOrderId);
    List<Invoice> findAll();
    List<Invoice> findByCustomerId(UUID customerId);
    List<Invoice> findByWorkOrderIdAsList(UUID workOrderId);
    List<Invoice> findByStatus(InvoiceStatus status);
}