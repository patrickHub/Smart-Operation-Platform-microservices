package ch.smart.operations.platform.billing.application.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WorkOrderBillingSummaryDto(
        UUID id,
        String workOrderNumber,
        UUID customerId,
        UUID assetId,
        UUID siteId,
        String type,
        String status,
        OffsetDateTime completedAt,
        Integer laborDurationMinutes,
        String resultStatus,
        List<UsedPartBillingDto> usedParts
) {
}