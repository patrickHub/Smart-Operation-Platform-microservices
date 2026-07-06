package ch.smart.operations.platform.asset.api.contracts;

import ch.smart.operations.platform.asset.domain.enums.AssetStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ChangeAssetStatusRequest(
    @NotNull(message = "status is required.")
    AssetStatus status,

    @NotBlank(message = "updatedBy is required.")
    @Size(max = 100, message = "updatedBy must not exceed 100 characters.")
    String updatedBy
) {
    
}
