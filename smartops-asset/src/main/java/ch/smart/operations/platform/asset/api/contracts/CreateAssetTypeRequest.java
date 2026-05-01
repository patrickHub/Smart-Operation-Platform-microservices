package ch.smart.operations.platform.asset.api.contracts;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateAssetTypeRequest(
    @NotBlank(message = "code is required")
    @Size(min = 2, max = 150, message = "code must be between 2 and 150 characters")
    String code,

    @NotBlank(message = "name is required")
    @Size(min = 2, max = 150, message = "name must be between 2 and 150 characters")
    String name,

    @Size(max = 150, message = "manufacture must not exceed 150 characters")
    String manufacturer,

    @Size(max = 150, message = "model must not exceed 150 characters")
    String model,

    @Size(max = 2000, message = "description must not exceed 2000 characters")
    String description

) {
    
}
