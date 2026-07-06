package ch.smart.operations.platform.workorder.application.ports;

import java.util.List;
import java.util.UUID;

import ch.smart.operations.platform.workorder.domain.entities.WorkOrderAssignment;

public interface WorkOrderAssignmentRepository {
    WorkOrderAssignment save(WorkOrderAssignment assignment);
    List<WorkOrderAssignment> findByWorkOrderId(UUID workOrderId);
}
