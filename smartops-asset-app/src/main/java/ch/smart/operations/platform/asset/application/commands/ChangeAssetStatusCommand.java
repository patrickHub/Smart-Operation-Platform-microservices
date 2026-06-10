package ch.smart.operations.platform.asset.application.commands;

import java.util.UUID;

import ch.smart.operations.platform.asset.domain.enums.AssetStatus;

public record ChangeAssetStatusCommand(
    UUID assetId,
    AssetStatus status,
    String updatedBy
) {
    
}
