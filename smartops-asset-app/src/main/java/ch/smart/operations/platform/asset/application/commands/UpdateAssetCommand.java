package ch.smart.operations.platform.asset.application.commands;

import java.time.LocalDate;
import java.util.UUID;

import ch.smart.operations.platform.asset.domain.enums.AssetCriticality;

public record UpdateAssetCommand(
    UUID assetId,
    String name,
    String description,
    LocalDate installationDate,
    LocalDate warrantyEndDate,
    AssetCriticality criticality,
    String updatedBy 
) {
    
}
