package ch.smart.operations.platform.workorder.application.commands;

import java.util.UUID;

public record AddWorkOrderTaskCommand(
        UUID workOrderId,
        String title,
        String description,
        Integer taskOrder,
        String createdBy
) {
}