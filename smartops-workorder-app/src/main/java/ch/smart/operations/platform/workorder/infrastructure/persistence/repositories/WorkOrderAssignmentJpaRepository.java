package ch.smart.operations.platform.workorder.infrastructure.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.WorkOrderAssignmentJpaEntity;

public interface WorkOrderAssignmentJpaRepository extends JpaRepository<WorkOrderAssignmentJpaEntity, UUID> {

    List<WorkOrderAssignmentJpaEntity> findByWorkOrderId(UUID workOrderId);
    
}
