package ch.smart.operations.platform.workorder.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkOrderAssignmentDto(
        UUID id,
        UUID workOrderId,
        UUID technicianUserId,
        UUID assignedByUserId,
        OffsetDateTime assignedAt,
        String assignmentStatus,
        String reason
) {
}