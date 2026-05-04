package ch.smart.operations.platform.workorder.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddWorkOrderTaskRequest(
        @NotBlank(message = "title is required.")
        @Size(max = 255, message = "title must not exceed 255 characters.")
        String title,

        @Size(max = 2000, message = "description must not exceed 2000 characters.")
        String description,

        @NotNull(message = "taskOrder is required.")
        Integer taskOrder,

        @NotBlank(message = "createdBy is required.")
        @Size(max = 100, message = "createdBy must not exceed 100 characters.")
        String createdBy
) {
}