package ch.smart.operations.platform.workorder.infrastructure.persistence.repositories;

import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.OutboxEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
}