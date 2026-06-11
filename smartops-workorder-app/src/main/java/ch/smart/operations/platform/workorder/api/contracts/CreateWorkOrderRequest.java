package ch.smart.operations.platform.workorder.api.contracts;

import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CreateWorkOrderRequest(

        @NotNull(message = "assetId is required.")
        UUID assetId,

        @NotNull(message = "createdByUserId is required.")
        UUID createdByUserId,

        @NotNull(message = "type is required.")
        WorkOrderType type,

        @NotNull(message = "priority is required.")
        WorkOrderPriority priority,

        @NotBlank(message = "title is required.")
        @Size(min = 2, max = 255, message = "title must be between 2 and 255 characters.")
        String title,

        @NotBlank(message = "description is required.")
        @Size(min = 5, max = 5000, message = "description must be between 5 and 5000 characters.")
        String description,

        OffsetDateTime requestedDate,
        OffsetDateTime scheduledStart,
        OffsetDateTime scheduledEnd,

        @NotBlank(message = "createdBy is required.")
        @Size(max = 100, message = "createdBy must not exceed 100 characters.")
        String createdBy
) {
}