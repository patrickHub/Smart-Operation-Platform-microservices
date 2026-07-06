package ch.smart.operations.platform.billing.application.commands;

import java.util.UUID;

public record InvoiceActionCommand(
        UUID invoiceId,
        String updatedBy
) {
}
