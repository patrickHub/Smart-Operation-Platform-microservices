package ch.smart.operations.platform.workorder.application.commands;

import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateWorkOrderCommand(
        UUID assetId,
        UUID createdByUserId,
        WorkOrderType type,
        WorkOrderPriority priority,
        String title,
        String description,
        OffsetDateTime requestedDate,
        OffsetDateTime scheduledStart,
        OffsetDateTime scheduledEnd,
        String createdBy
) {
}