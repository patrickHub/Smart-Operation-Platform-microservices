package ch.smart.operations.platform.billing.application.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceDto(
        UUID id,
        String invoiceNumber,
        UUID workOrderId,
        UUID customerId,
        UUID pricingPolicyId,
        String status,
        String currency,
        BigDecimal subtotalAmount,
        BigDecimal taxAmount,
        BigDecimal totalAmount,
        OffsetDateTime generatedAt,
        LocalDate dueDate,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy,
        List<InvoiceLineDto> lines
) {
}