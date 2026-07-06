package ch.smart.operations.platform.workorder.application.commands;

import java.util.UUID;

public record TechnicianActionCommand(
        UUID workOrderId,
        UUID technicianUserId,
        String updatedBy
) {
}
