package ch.smart.operations.platform.billing.application.commands;

import java.time.LocalDate;
import java.util.UUID;

public record GenerateInvoiceFromWorkOrderCommand(
        UUID workOrderId,
        LocalDate dueDate,
        String createdBy
) {
}
