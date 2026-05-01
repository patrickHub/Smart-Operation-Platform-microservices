package ch.smart.operations.platform.asset.application.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.smart.operations.platform.asset.domain.entities.AssetType;

public interface AssetTypeRepository {

    AssetType save(AssetType assetType);
    List<AssetType> findAll();
    Optional<AssetType> findById(UUID id);

}
