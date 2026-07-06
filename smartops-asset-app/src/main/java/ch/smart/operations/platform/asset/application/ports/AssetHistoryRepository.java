package ch.smart.operations.platform.asset.application.ports;

import java.util.List;
import java.util.UUID;

import ch.smart.operations.platform.asset.domain.entities.AssetHistory;

public interface AssetHistoryRepository {

    AssetHistory save(AssetHistory assetHistory);
    List<AssetHistory> findByAssetId(UUID assetId);
    
} 