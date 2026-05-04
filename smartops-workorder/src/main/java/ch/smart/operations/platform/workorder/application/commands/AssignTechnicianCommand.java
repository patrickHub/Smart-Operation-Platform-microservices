package ch.smart.operations.platform.workorder.application.commands;

import java.util.UUID;

public record AssignTechnicianCommand(
        UUID workOrderId,
        UUID technicianUserId,
        UUID assignedByUserId,
        String updatedBy
) {
}