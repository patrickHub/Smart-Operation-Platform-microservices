package ch.smart.operations.platform.workorder.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkOrderDto(
        UUID id,
        String workOrderNumber,
        UUID assetId,
        UUID customerId,
        UUID siteId,
        UUID createdByUserId,
        UUID assignedTechnicianUserId,
        String type,
        String priority,
        String status,
        String title,
        String description,
        OffsetDateTime requestedDate,
        OffsetDateTime scheduledStart,
        OffsetDateTime scheduledEnd,
        OffsetDateTime completedAt,
        String cancellationReason,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy
) {
}