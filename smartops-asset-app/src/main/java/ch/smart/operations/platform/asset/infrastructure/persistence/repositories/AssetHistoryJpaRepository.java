package ch.smart.operations.platform.asset.infrastructure.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


import ch.smart.operations.platform.asset.infrastructure.persistence.entities.AssetHistoryJpaEntity;

public interface AssetHistoryJpaRepository extends JpaRepository<AssetHistoryJpaEntity, UUID> {
    List<AssetHistoryJpaEntity> findByAssetIdOrderByEventTimestampDesc(UUID assetId);
    
}
