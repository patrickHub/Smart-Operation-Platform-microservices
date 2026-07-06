package ch.smart.operations.platform.asset.api.contracts;

import java.time.LocalDate;


import ch.smart.operations.platform.asset.domain.enums.AssetCriticality;
import ch.smart.operations.platform.asset.domain.enums.AssetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAssetRequest(
    @NotBlank(message = "name is required.")
    @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters.")
    String name,

    @Size(max = 2000, message = "description must not exceed 2000 characters.")
    String description,

    LocalDate installationDate,
    LocalDate warrantyEndDate,

    AssetStatus status,
    AssetCriticality criticality,

    @NotBlank(message = "updatedBy is required.")
    @Size(max = 100, message = "updatedBy must not exceed 100 characters.")
    String updatedBy
) {
}