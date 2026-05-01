package ch.smart.operations.platform.asset.infrastructure.persistence.adapters;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.smart.operations.platform.asset.application.ports.AssetHistoryRepository;
import ch.smart.operations.platform.asset.domain.entities.AssetHistory;
import ch.smart.operations.platform.asset.infrastructure.persistence.entities.AssetHistoryJpaEntity;
import ch.smart.operations.platform.asset.infrastructure.persistence.repositories.AssetHistoryJpaRepository;


@Repository
public class AssetHistoryRepositoryAdapter implements AssetHistoryRepository {
    private final AssetHistoryJpaRepository assetHistoryJpaRepository;

    public AssetHistoryRepositoryAdapter(AssetHistoryJpaRepository assetHistoryJpaRepository) {
        this.assetHistoryJpaRepository = assetHistoryJpaRepository;
    }

    @Override
    public AssetHistory save(AssetHistory assetHistory) {
        AssetHistoryJpaEntity saved = assetHistoryJpaRepository.save(toJpa(assetHistory));
        return toDomain(saved);
    }

    @Override
    public List<AssetHistory> findByAssetId(UUID assetId) {
        return assetHistoryJpaRepository.findByAssetIdOrderByEventTimestampDesc(assetId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AssetHistory toDomain(AssetHistoryJpaEntity entity) {
        return new AssetHistory(
                entity.getId(),
                entity.getAssetId(),
                entity.getEventType(),
                entity.getEventTimestamp(),
                entity.getActorId(),
                entity.getSummary(),
                entity.getPayloadJson(),
                entity.getCreatedAt()
        );
    }

    private AssetHistoryJpaEntity toJpa(AssetHistory assetHistory) {
        AssetHistoryJpaEntity entity = new AssetHistoryJpaEntity();
        entity.setId(assetHistory.getId());
        entity.setAssetId(assetHistory.getAssetId());
        entity.setEventType(assetHistory.getEventType());
        entity.setEventTimestamp(assetHistory.getEventTimestamp());
        entity.setActorId(assetHistory.getActorId());
        entity.setSummary(assetHistory.getSummary());
        entity.setPayloadJson(assetHistory.getPayloadJson());
        entity.setCreatedAt(assetHistory.getCreatedAt());
        return entity;
    }

}
