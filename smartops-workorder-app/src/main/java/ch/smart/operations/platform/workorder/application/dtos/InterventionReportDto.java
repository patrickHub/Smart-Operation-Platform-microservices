package ch.smart.operations.platform.workorder.application.dtos;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InterventionReportDto(
        UUID id,
        UUID workOrderId,
        UUID technicianUserId,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        Integer laborDurationMinutes,
        String summary,
        String actionsPerformed,
        String resultStatus,
        boolean followUpRequired,
        String followUpNotes,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy,
        List<UsedPartDto> usedParts
) {
}