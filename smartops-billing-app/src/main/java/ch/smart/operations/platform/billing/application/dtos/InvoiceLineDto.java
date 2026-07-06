package ch.smart.operations.platform.billing.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceLineDto(
        UUID id,
        UUID invoiceId,
        int lineNumber,
        String lineType,
        String description,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {
}
