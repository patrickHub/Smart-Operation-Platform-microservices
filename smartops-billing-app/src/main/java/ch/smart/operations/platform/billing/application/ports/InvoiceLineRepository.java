package ch.smart.operations.platform.billing.application.ports;

import ch.smart.operations.platform.billing.domain.entities.InvoiceLine;

import java.util.List;
import java.util.UUID;

public interface InvoiceLineRepository {
    InvoiceLine save(InvoiceLine line);
    List<InvoiceLine> findByInvoiceId(UUID invoiceId);
}