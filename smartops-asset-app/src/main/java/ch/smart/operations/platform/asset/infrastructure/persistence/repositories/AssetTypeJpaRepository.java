package ch.smart.operations.platform.asset.infrastructure.persistence.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.smart.operations.platform.asset.infrastructure.persistence.entities.AssetTypeJpaEntity;


public interface AssetTypeJpaRepository extends JpaRepository<AssetTypeJpaEntity, UUID> {}