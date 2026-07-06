package ch.smart.operations.platform.workorder.infrastructure.persistence.repositories;

import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderStatus;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.WorkOrderJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkOrderJpaRepository extends JpaRepository<WorkOrderJpaEntity, UUID> {
    List<WorkOrderJpaEntity> findByStatus(WorkOrderStatus status);
    List<WorkOrderJpaEntity> findByPriority(WorkOrderPriority priority);
    List<WorkOrderJpaEntity> findByAssignedTechnicianUserId(UUID assignedTechnicianUserId);
    List<WorkOrderJpaEntity> findByCustomerId(UUID customerId);
}