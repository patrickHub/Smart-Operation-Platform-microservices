package ch.smart.operations.platform.billing.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InvoiceActionRequest(
        @NotBlank(message = "updatedBy is required.")
        @Size(max = 100, message = "updatedBy must not exceed 100 characters.")
        String updatedBy
) {
}