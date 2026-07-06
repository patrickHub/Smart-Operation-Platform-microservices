package ch.smart.operations.platform.workorder.infrastructure.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.UsedPartJpaEntity;

public interface UsedPartJpaRepository extends JpaRepository<UsedPartJpaEntity, UUID> {
    List<UsedPartJpaEntity> findByInterventionReportId(UUID interventionReportId);
}
