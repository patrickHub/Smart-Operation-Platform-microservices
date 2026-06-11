package ch.smart.operations.platform.workorder.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record TechnicianActionRequest(
        @NotNull(message = "technicianUserId is required.")
        UUID technicianUserId,

        @NotBlank(message = "updatedBy is required.")
        @Size(max = 100, message = "updatedBy must not exceed 100 characters.")
        String updatedBy
) {
}