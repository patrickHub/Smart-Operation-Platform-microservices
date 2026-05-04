package ch.smart.operations.platform.workorder.infrastructure.persistence.adapters;

import ch.smart.operations.platform.workorder.application.ports.WorkOrderTaskRepository;
import ch.smart.operations.platform.workorder.domain.entities.WorkOrderTask;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.WorkOrderTaskJpaEntity;
import ch.smart.operations.platform.workorder.infrastructure.persistence.repositories.WorkOrderTaskJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class WorkOrderTaskRepositoryAdapter implements WorkOrderTaskRepository {

    private final WorkOrderTaskJpaRepository repository;

    public WorkOrderTaskRepositoryAdapter(WorkOrderTaskJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkOrderTask save(WorkOrderTask task) {
        return toDomain(repository.save(toJpa(task)));
    }

    @Override
    public List<WorkOrderTask> findByWorkOrderId(UUID workOrderId) {
        return repository.findByWorkOrderIdOrderByTaskOrderAsc(workOrderId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private WorkOrderTask toDomain(WorkOrderTaskJpaEntity entity) {
        return new WorkOrderTask(
                entity.getId(),
                entity.getWorkOrderId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getTaskOrder(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }

    private WorkOrderTaskJpaEntity toJpa(WorkOrderTask task) {
        WorkOrderTaskJpaEntity entity = new WorkOrderTaskJpaEntity();
        entity.setId(task.getId());
        entity.setWorkOrderId(task.getWorkOrderId());
        entity.setTitle(task.getTitle());
        entity.setDescription(task.getDescription());
        entity.setTaskOrder(task.getTaskOrder());
        entity.setStatus(task.getStatus());
        entity.setCreatedAt(task.getCreatedAt());
        entity.setCreatedBy(task.getCreatedBy());
        entity.setUpdatedAt(task.getUpdatedAt());
        entity.setUpdatedBy(task.getUpdatedBy());
        return entity;
    }
}