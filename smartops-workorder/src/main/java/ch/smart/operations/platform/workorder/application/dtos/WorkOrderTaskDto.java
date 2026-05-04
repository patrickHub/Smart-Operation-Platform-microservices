package ch.smart.operations.platform.workorder.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record WorkOrderTaskDto(
        UUID id,
        UUID workOrderId,
        String title,
        String description,
        Integer taskOrder,
        String status,
        OffsetDateTime createdAt,
        String createdBy,
        OffsetDateTime updatedAt,
        String updatedBy
) {
}