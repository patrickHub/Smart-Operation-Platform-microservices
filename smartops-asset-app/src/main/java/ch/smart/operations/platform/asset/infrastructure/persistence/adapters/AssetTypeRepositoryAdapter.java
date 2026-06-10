package ch.smart.operations.platform.asset.infrastructure.persistence.adapters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.smart.operations.platform.asset.application.ports.AssetTypeRepository;
import ch.smart.operations.platform.asset.domain.entities.AssetType;
import ch.smart.operations.platform.asset.infrastructure.persistence.entities.AssetTypeJpaEntity;
import ch.smart.operations.platform.asset.infrastructure.persistence.repositories.AssetTypeJpaRepository;

@Repository
public class AssetTypeRepositoryAdapter implements AssetTypeRepository {

    private final AssetTypeJpaRepository assetTypeJpaRepository;

        public AssetTypeRepositoryAdapter(AssetTypeJpaRepository assetTypeJpaRepository) {
            this.assetTypeJpaRepository = assetTypeJpaRepository;
        }

    @Override
    public AssetType save(AssetType assetType) {
        AssetTypeJpaEntity entity = toJpa(assetType);   
        AssetTypeJpaEntity saved = assetTypeJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<AssetType> findAll() {
        return assetTypeJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

        @Override
    public Optional<AssetType> findById(UUID id) {
        return assetTypeJpaRepository.findById(id)
                .map(this::toDomain); 
    }

    private AssetType toDomain(AssetTypeJpaEntity entity) {
        return new AssetType(
           entity.getId(),
           entity.getCode(), 
           entity.getName(), 
           entity.getManufacturer(), 
           entity.getModel(),
           entity.getDescription(), 
           entity.getCreatedAt(), 
           entity.getUpdatedAt());
    }

    private AssetTypeJpaEntity toJpa(AssetType assetType) {
        AssetTypeJpaEntity entity = new AssetTypeJpaEntity();
        entity.setId(assetType.getId());
        entity.setCode(assetType.getCode());
        entity.setName(assetType.getName());
        entity.setManufacturer(assetType.getManufacturer());
        entity.setModel(assetType.getModel());
        entity.setDescription(assetType.getDescription());
        entity.setCreatedAt(assetType.getCreatedAt());
        entity.setUpdatedAt(assetType.getUpdatedAt());
        return entity;
    }
    
}
