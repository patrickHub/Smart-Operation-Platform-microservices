package ch.smart.operations.platform.workorder.application.ports;

import ch.smart.operations.platform.workorder.domain.entities.WorkOrder;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkOrderRepository {
    WorkOrder save(WorkOrder workOrder);
    Optional<WorkOrder> findById(UUID id);
    List<WorkOrder> findAll();
    List<WorkOrder> findByStatus(WorkOrderStatus status);
    List<WorkOrder> findByPriority(WorkOrderPriority priority);
    List<WorkOrder> findByAssignedTechnicianUserId(UUID technicianUserId);
    List<WorkOrder> findByCustomerId(UUID customerId);
}