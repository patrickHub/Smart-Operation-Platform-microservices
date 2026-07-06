package ch.smart.operations.platform.asset.infrastructure.persistence.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.smart.operations.platform.asset.domain.enums.AssetStatus;
import ch.smart.operations.platform.asset.infrastructure.persistence.entities.AssetJpaEntity;

public interface AssetJpaRepository extends JpaRepository<AssetJpaEntity, UUID> {

    boolean existsBySerialNumberIgnoreCase(String serialNumber);
    Optional<AssetJpaEntity> findBySerialNumberIgnoreCase(String serialNumber);
    List<AssetJpaEntity> findBySiteId(UUID siteId);
    List<AssetJpaEntity> findByCustomerId(UUID customerId);
    List<AssetJpaEntity> findByStatus(AssetStatus status);
    
}
