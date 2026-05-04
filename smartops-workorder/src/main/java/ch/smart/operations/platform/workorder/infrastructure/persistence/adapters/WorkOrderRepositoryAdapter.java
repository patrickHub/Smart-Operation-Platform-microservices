package ch.smart.operations.platform.workorder.infrastructure.persistence.adapters;

import ch.smart.operations.platform.workorder.application.ports.WorkOrderRepository;
import ch.smart.operations.platform.workorder.domain.entities.WorkOrder;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderStatus;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.WorkOrderJpaEntity;
import ch.smart.operations.platform.workorder.infrastructure.persistence.repositories.WorkOrderJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Repository
public class WorkOrderRepositoryAdapter implements WorkOrderRepository {

    private final WorkOrderJpaRepository repository;
    private final Logger logger = LoggerFactory.getLogger(WorkOrderRepositoryAdapter.class);

    public WorkOrderRepositoryAdapter(WorkOrderJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public WorkOrder save(WorkOrder workOrder) {
        try{
        
            logger.info("Saving work order: {}", workOrder.getWorkOrderNumber());
            WorkOrderJpaEntity entity = toJpa(workOrder);
            logger.info("Converted work order to JPA entity: {}", entity.getWorkOrderNumber());
            WorkOrderJpaEntity savedEntity = repository.save(entity);
            logger.info("Work order saved with ID: {}", savedEntity.getId());
            WorkOrder savedWorkOrder = toDomain(savedEntity);
            logger.info("Converted saved entity to domain object: {}", savedWorkOrder.getWorkOrderNumber());
            return savedWorkOrder;
        } catch (Exception e) {
            logger.error("Error occurred while saving work order: {}", workOrder.getWorkOrderNumber(), e);
            throw e;
        }
    }

    @Override
    public Optional<WorkOrder> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<WorkOrder> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public List<WorkOrder> findByStatus(WorkOrderStatus status) {
        return repository.findByStatus(status).stream().map(this::toDomain).toList();
    }

    @Override
    public List<WorkOrder> findByPriority(WorkOrderPriority priority) {
        return repository.findByPriority(priority).stream().map(this::toDomain).toList();
    }

    @Override
    public List<WorkOrder> findByAssignedTechnicianUserId(UUID technicianUserId) {
        return repository.findByAssignedTechnicianUserId(technicianUserId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<WorkOrder> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream().map(this::toDomain).toList();
    }

    private WorkOrder toDomain(WorkOrderJpaEntity entity) {
        return new WorkOrder(
                entity.getId(),
                entity.getWorkOrderNumber(),
                entity.getAssetId(),
                entity.getCustomerId(),
                entity.getSiteId(),
                entity.getCreatedByUserId(),
                entity.getAssignedTechnicianUserId(),
                entity.getType(),
                entity.getPriority(),
                entity.getStatus(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getRequestedDate(),
                entity.getScheduledStart(),
                entity.getScheduledEnd(),
                entity.getCompletedAt(),
                entity.getCancellationReason(),
                entity.getVersion(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }

    private WorkOrderJpaEntity toJpa(WorkOrder workOrder) {
        WorkOrderJpaEntity entity = new WorkOrderJpaEntity();

        entity.setId(workOrder.getId());
        entity.setWorkOrderNumber(workOrder.getWorkOrderNumber());
        entity.setAssetId(workOrder.getAssetId());
        entity.setCustomerId(workOrder.getCustomerId());
        entity.setSiteId(workOrder.getSiteId());
        entity.setCreatedByUserId(workOrder.getCreatedByUserId());
        entity.setAssignedTechnicianUserId(workOrder.getAssignedTechnicianUserId());
        entity.setType(workOrder.getType());
        entity.setPriority(workOrder.getPriority());
        entity.setStatus(workOrder.getStatus());
        entity.setTitle(workOrder.getTitle());
        entity.setDescription(workOrder.getDescription());
        entity.setRequestedDate(workOrder.getRequestedDate());
        entity.setScheduledStart(workOrder.getScheduledStart());
        entity.setScheduledEnd(workOrder.getScheduledEnd());
        entity.setCompletedAt(workOrder.getCompletedAt());
        entity.setCancellationReason(workOrder.getCancellationReason());
        entity.setVersion(workOrder.getVersion());
        entity.setCreatedAt(workOrder.getCreatedAt());
        entity.setCreatedBy(workOrder.getCreatedBy());
        entity.setUpdatedAt(workOrder.getUpdatedAt());
        entity.setUpdatedBy(workOrder.getUpdatedBy());

        return entity;
    }
}