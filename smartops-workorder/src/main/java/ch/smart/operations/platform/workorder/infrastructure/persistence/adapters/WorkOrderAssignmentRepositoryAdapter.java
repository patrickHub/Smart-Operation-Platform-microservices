package ch.smart.operations.platform.workorder.infrastructure.persistence.adapters;

import ch.smart.operations.platform.workorder.application.ports.WorkOrderAssignmentRepository;
import ch.smart.operations.platform.workorder.domain.entities.WorkOrderAssignment;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.WorkOrderAssignmentJpaEntity;
import ch.smart.operations.platform.workorder.infrastructure.persistence.repositories.WorkOrderAssignmentJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class WorkOrderAssignmentRepositoryAdapter implements WorkOrderAssignmentRepository {

    private final WorkOrderAssignmentJpaRepository repository;

    public WorkOrderAssignmentRepositoryAdapter(WorkOrderAssignmentJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkOrderAssignment save(WorkOrderAssignment assignment) {
        return toDomain(repository.save(toJpa(assignment)));
    }

    @Override
    public List<WorkOrderAssignment> findByWorkOrderId(UUID workOrderId) {
        return repository.findByWorkOrderId(workOrderId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private WorkOrderAssignment toDomain(WorkOrderAssignmentJpaEntity entity) {
        return new WorkOrderAssignment(
                entity.getId(),
                entity.getWorkOrderId(),
                entity.getTechnicianUserId(),
                entity.getAssignedByUserId(),
                entity.getAssignedAt(),
                entity.getAssignmentStatus(),
                entity.getReason()
        );
    }

    private WorkOrderAssignmentJpaEntity toJpa(WorkOrderAssignment assignment) {
        WorkOrderAssignmentJpaEntity entity = new WorkOrderAssignmentJpaEntity();

        entity.setId(assignment.getId());
        entity.setWorkOrderId(assignment.getWorkOrderId());
        entity.setTechnicianUserId(assignment.getTechnicianUserId());
        entity.setAssignedByUserId(assignment.getAssignedByUserId());
        entity.setAssignedAt(assignment.getAssignedAt());
        entity.setAssignmentStatus(assignment.getAssignmentStatus());
        entity.setReason(assignment.getReason());

        return entity;
    }
}