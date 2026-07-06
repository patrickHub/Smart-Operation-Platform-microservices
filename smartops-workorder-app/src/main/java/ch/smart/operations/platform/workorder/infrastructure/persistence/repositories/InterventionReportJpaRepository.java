package ch.smart.operations.platform.workorder.infrastructure.persistence.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.InterventionReportJpaEntity;

public interface InterventionReportJpaRepository extends JpaRepository<InterventionReportJpaEntity, UUID> {
    Optional<InterventionReportJpaEntity> findByWorkOrderId(UUID workOrderId);
    boolean existsByWorkOrderId(UUID workOrderId);
}