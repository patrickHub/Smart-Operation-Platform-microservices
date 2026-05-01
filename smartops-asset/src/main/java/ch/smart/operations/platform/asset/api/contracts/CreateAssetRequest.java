package ch.smart.operations.platform.asset.api.contracts;

import java.time.LocalDate;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.smart.operations.platform.asset.domain.enums.AssetCriticality;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAssetRequest(
    
    @JsonProperty("serialNumber")
    @NotBlank(message = "serialNumber 776 is required.")
    @Size(min = 2, max = 100, message = "serialNumber must be between 2 and 100 characters.")
    String serialNumber,

    @JsonProperty("assetTypeId")
    @NotNull(message = "assetTypeId is required.")
    UUID assetTypeId,

    @JsonProperty("siteId")
    @NotNull(message = "siteId is required.")
    UUID siteId,

    @JsonProperty("customerId")
    @NotNull(message = "customerId is required.")
    UUID customerId,

    @JsonProperty("name")
    @NotBlank(message = "name is required.")
    @Size(min = 2, max = 255, message = "name must be between 2 and 255 characters.")
    String name,

    @JsonProperty("description")
    @Size(max = 2000, message = "description must not exceed 2000 characters.")
    String description,

    @JsonProperty("installationDate")
    LocalDate installationDate,
    @JsonProperty("warrantyEndDate")
    LocalDate warrantyEndDate,

    @JsonProperty("criticality")
    AssetCriticality criticality,

    @JsonProperty("createdBy")
    @NotBlank(message = "createdBy is required.")
    @Size(max = 100, message = "createdBy must not exceed 100 characters.")
    String createdBy

) {
} 