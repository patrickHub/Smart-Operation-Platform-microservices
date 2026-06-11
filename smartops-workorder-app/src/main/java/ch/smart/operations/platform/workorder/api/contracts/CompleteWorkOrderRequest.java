package ch.smart.operations.platform.workorder.api.contracts;

import ch.smart.operations.platform.workorder.domain.enums.InterventionResultStatus;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CompleteWorkOrderRequest(
        @NotNull(message = "technicianUserId is required.")
        UUID technicianUserId,

        OffsetDateTime startedAt,

        OffsetDateTime completedAt,

        @Min(value = 0, message = "laborDurationMinutes must be positive or zero.")
        Integer laborDurationMinutes,

        @NotBlank(message = "summary is required.")
        @Size(min = 5, max = 5000, message = "summary must be between 5 and 5000 characters.")
        String summary,

        @Size(max = 5000, message = "actionsPerformed must not exceed 5000 characters.")
        String actionsPerformed,

        @NotNull(message = "resultStatus is required.")
        InterventionResultStatus resultStatus,

        boolean followUpRequired,

        @Size(max = 5000, message = "followUpNotes must not exceed 5000 characters.")
        String followUpNotes,

        List<UsedPartRequest> usedParts,

        @NotBlank(message = "updatedBy is required.")
        @Size(max = 100, message = "updatedBy must not exceed 100 characters.")
        String updatedBy
) {
    public record UsedPartRequest(
            @NotBlank(message = "partNumber is required.")
            @Size(max = 100, message = "partNumber must not exceed 100 characters.")
            String partNumber,

            @NotBlank(message = "partName is required.")
            @Size(max = 255, message = "partName must not exceed 255 characters.")
            String partName,

            @NotNull(message = "quantity is required.")
            @DecimalMin(value = "0.01", message = "quantity must be greater than 0.")
            BigDecimal quantity,

            BigDecimal unitPrice,

            @Size(max = 10, message = "currency must not exceed 10 characters.")
            String currency
    ) {
    }
}