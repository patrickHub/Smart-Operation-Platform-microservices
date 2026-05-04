package ch.smart.operations.platform.workorder.application.commands;

import java.util.UUID;

public record CancelWorkOrderCommand(
        UUID workOrderId,
        String reason,
        String updatedBy
) {
}