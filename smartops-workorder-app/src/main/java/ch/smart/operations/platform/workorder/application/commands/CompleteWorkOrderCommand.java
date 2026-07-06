package ch.smart.operations.platform.workorder.application.commands;

import ch.smart.operations.platform.workorder.domain.enums.InterventionResultStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record CompleteWorkOrderCommand(
        UUID workOrderId,
        UUID technicianUserId,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        Integer laborDurationMinutes,
        String summary,
        String actionsPerformed,
        InterventionResultStatus resultStatus,
        boolean followUpRequired,
        String followUpNotes,
        List<UsedPartCommand> usedParts,
        String updatedBy
) {
    public record UsedPartCommand(
            String partNumber,
            String partName,
            BigDecimal quantity,
            BigDecimal unitPrice,
            String currency
    ) {
    }
}