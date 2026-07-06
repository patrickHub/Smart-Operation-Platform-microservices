package ch.smart.operations.platform.workorder.infrastructure.persistence.repositories;

import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.WorkOrderTaskJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkOrderTaskJpaRepository extends JpaRepository<WorkOrderTaskJpaEntity, UUID> {
    List<WorkOrderTaskJpaEntity> findByWorkOrderIdOrderByTaskOrderAsc(UUID workOrderId);
}