package ch.smart.operations.platform.billing.application.dtos;

import java.math.BigDecimal;

public record UsedPartBillingDto(
        String partNumber,
        String partName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String currency
) {
}