package ch.smart.operations.platform.workorder.application.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record UsedPartDto(
        UUID id,
        UUID interventionReportId,
        String partNumber,
        String partName,
        BigDecimal quantity,
        BigDecimal unitPrice,
        String currency
) {
}