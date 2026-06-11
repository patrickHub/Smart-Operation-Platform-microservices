package ch.smart.operations.platform.workorder.application.ports;

import ch.smart.operations.platform.workorder.domain.entities.WorkOrderTask;

import java.util.List;
import java.util.UUID;

public interface WorkOrderTaskRepository {
    WorkOrderTask save(WorkOrderTask task);
    List<WorkOrderTask> findByWorkOrderId(UUID workOrderId);
}