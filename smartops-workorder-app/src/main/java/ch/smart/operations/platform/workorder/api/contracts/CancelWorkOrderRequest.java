package ch.smart.operations.platform.workorder.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelWorkOrderRequest(
        @NotBlank(message = "reason is required.")
        @Size(max = 2000, message = "reason must not exceed 2000 characters.")
        String reason,

        @NotBlank(message = "updatedBy is required.")
        @Size(max = 100, message = "updatedBy must not exceed 100 characters.")
        String updatedBy
) {
}