package ch.smart.operations.platform.workorder.application.ports;

import ch.smart.operations.platform.workorder.application.dtos.AssetSummaryDto;

import java.util.Optional;
import java.util.UUID;

public interface AssetReferencePort {
    Optional<AssetSummaryDto> findAssetSummary(UUID assetId);
}