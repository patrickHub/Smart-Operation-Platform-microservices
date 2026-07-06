package ch.smart.operations.platform.workorder.application.dtos;

import java.math.BigDecimal;

public record UsedPartForBillingDto(
        String partNumber,
        String partName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String currency
) {
}