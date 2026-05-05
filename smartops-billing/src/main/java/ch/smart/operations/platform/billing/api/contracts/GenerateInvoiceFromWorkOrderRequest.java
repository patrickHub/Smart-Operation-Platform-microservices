package ch.smart.operations.platform.billing.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GenerateInvoiceFromWorkOrderRequest(
        LocalDate dueDate,

        @NotBlank(message = "createdBy is required.")
        @Size(max = 100, message = "createdBy must not exceed 100 characters.")
        String createdBy
) {
}
