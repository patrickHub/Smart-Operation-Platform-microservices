package ch.smart.operations.platform.asset.application.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.smart.operations.platform.asset.domain.entities.Asset;
import ch.smart.operations.platform.asset.domain.enums.AssetStatus;

public interface AssetRepository {

    Asset save(Asset asset);
    Optional<Asset> findById(UUID id);
    boolean existsBySerialNumber(String serialNumber);
    List <Asset> findAll();
    List<Asset> findBySerialNumber(String serialNumber);
    List<Asset> findBySiteId(UUID siteId);
    List<Asset> findByCustomerId(UUID customerId);
    List<Asset> findByStatus(AssetStatus status);
    
}
