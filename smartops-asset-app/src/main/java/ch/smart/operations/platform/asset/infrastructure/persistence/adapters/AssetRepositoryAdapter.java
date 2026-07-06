package ch.smart.operations.platform.asset.infrastructure.persistence.adapters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.smart.operations.platform.asset.application.ports.AssetRepository;
import ch.smart.operations.platform.asset.domain.entities.Asset;
import ch.smart.operations.platform.asset.domain.enums.AssetStatus;
import ch.smart.operations.platform.asset.infrastructure.persistence.entities.AssetJpaEntity;
import ch.smart.operations.platform.asset.infrastructure.persistence.repositories.AssetJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Repository
public class AssetRepositoryAdapter implements AssetRepository {
    private final AssetJpaRepository assetJpaRepository;
    private final Logger logger = LoggerFactory.getLogger(AssetRepositoryAdapter.class);

    public AssetRepositoryAdapter(AssetJpaRepository assetJpaRepository) {
        this.assetJpaRepository = assetJpaRepository;
    }

    @Override
    public Asset save(Asset asset) {
        logger.info("Saving asset: {}", asset);
        AssetJpaEntity saved = assetJpaRepository.save(toJpa(asset));
        logger.info("Asset saved with ID: {}", saved.getId());
        return toDomain(saved);
    }

    @Override
    public Optional<Asset> findById(UUID id) {
        return assetJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsBySerialNumber(String serialNumber) {
        return assetJpaRepository.existsBySerialNumberIgnoreCase(serialNumber);
    }

    @Override
    public List<Asset> findAll() {
        return assetJpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Asset> findBySerialNumber(String serialNumber) {
        return assetJpaRepository.findBySerialNumberIgnoreCase(serialNumber)
                .map(this::toDomain)
                .stream()
                .toList();
    }

    @Override
    public List<Asset> findBySiteId(UUID siteId) {
        return assetJpaRepository.findBySiteId(siteId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Asset> findByCustomerId(UUID customerId) {
        return assetJpaRepository.findByCustomerId(customerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Asset> findByStatus(AssetStatus status) {
        return assetJpaRepository.findByStatus(status).stream().map(this::toDomain).toList();
    }

    private Asset toDomain(AssetJpaEntity entity) {
        return new Asset(
                entity.getId(),
                entity.getAssetNumber(),
                entity.getSerialNumber(),
                entity.getAssetTypeId(),
                entity.getSiteId(),
                entity.getCustomerId(),
                entity.getName(),
                entity.getDescription(),
                entity.getInstallationDate(),
                entity.getWarrantyEndDate(),
                entity.getStatus(),
                entity.getCriticality(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }

    private AssetJpaEntity toJpa(Asset asset) {
        AssetJpaEntity entity = new AssetJpaEntity();
        entity.setId(asset.getId());
        entity.setAssetNumber(asset.getAssetNumber());
        entity.setSerialNumber(asset.getSerialNumber());
        entity.setAssetTypeId(asset.getAssetTypeId());
        entity.setSiteId(asset.getSiteId());
        entity.setCustomerId(asset.getCustomerId());
        entity.setName(asset.getName());
        entity.setDescription(asset.getDescription());
        entity.setInstallationDate(asset.getInstallationDate());
        entity.setWarrantyEndDate(asset.getWarrantyEndDate());
        entity.setStatus(asset.getStatus());
        entity.setVersion(asset.getVersion());
        entity.setCriticality(asset.getCriticality());
        entity.setCreatedAt(asset.getCreatedAt());
        entity.setCreatedBy(asset.getCreatedBy());
        entity.setUpdatedAt(asset.getUpdatedAt());
        entity.setUpdatedBy(asset.getUpdatedBy());
        return entity;
    }
}
