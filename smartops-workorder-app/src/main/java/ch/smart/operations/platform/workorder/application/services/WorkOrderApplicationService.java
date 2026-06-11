package ch.smart.operations.platform.workorder.application.services;

import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import ch.smart.operations.platform.workorder.application.commands.AddWorkOrderTaskCommand;
import ch.smart.operations.platform.workorder.application.commands.AssignTechnicianCommand;
import ch.smart.operations.platform.workorder.application.commands.CancelWorkOrderCommand;
import ch.smart.operations.platform.workorder.application.commands.CompleteWorkOrderCommand;
import ch.smart.operations.platform.workorder.application.commands.CreateWorkOrderCommand;
import ch.smart.operations.platform.workorder.application.commands.TechnicianActionCommand;
import ch.smart.operations.platform.workorder.application.dtos.AssetSummaryDto;
import ch.smart.operations.platform.workorder.application.dtos.InterventionReportDto;
import ch.smart.operations.platform.workorder.application.dtos.UsedPartDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderAssignmentDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderTaskDto;
import ch.smart.operations.platform.workorder.application.ports.AssetReferencePort;
import ch.smart.operations.platform.workorder.application.ports.InterventionReportRepository;
import ch.smart.operations.platform.workorder.application.ports.OutboxEventRepository;
import ch.smart.operations.platform.workorder.application.ports.UsedPartRepository;
import ch.smart.operations.platform.workorder.application.ports.WorkOrderAssignmentRepository;
import ch.smart.operations.platform.workorder.application.ports.WorkOrderRepository;
import ch.smart.operations.platform.workorder.application.ports.WorkOrderTaskRepository;
import ch.smart.operations.platform.workorder.domain.entities.InterventionReport;
import ch.smart.operations.platform.workorder.domain.entities.OutboxEvent;
import ch.smart.operations.platform.workorder.domain.entities.UsedPart;
import ch.smart.operations.platform.workorder.domain.entities.WorkOrder;
import ch.smart.operations.platform.workorder.domain.entities.WorkOrderAssignment;
import ch.smart.operations.platform.workorder.domain.entities.WorkOrderTask;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class WorkOrderApplicationService {

    private final WorkOrderRepository workOrderRepository;
    private final AssetReferencePort assetReferencePort;
    private final WorkOrderAssignmentRepository assignmentRepository;
    private final InterventionReportRepository interventionReportRepository;
    private final UsedPartRepository usedPartRepository;
    private final WorkOrderTaskRepository taskRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(WorkOrderApplicationService.class);

    public WorkOrderApplicationService(
            WorkOrderRepository workOrderRepository,
            AssetReferencePort assetReferencePort,
            WorkOrderAssignmentRepository assignmentRepository,
            InterventionReportRepository interventionReportRepository,
            UsedPartRepository usedPartRepository,
            WorkOrderTaskRepository taskRepository,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.workOrderRepository = workOrderRepository;
        this.assetReferencePort = assetReferencePort;
        this.assignmentRepository = assignmentRepository;
        this.interventionReportRepository = interventionReportRepository;
        this.usedPartRepository = usedPartRepository;
        this.taskRepository = taskRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    public UUID createWorkOrder(CreateWorkOrderCommand command) {
        validateCreateCommand(command);

        AssetSummaryDto asset = assetReferencePort.findAssetSummary(command.assetId())
                .orElseThrow(() -> new NotFoundException("Asset not found with id " + command.assetId()));

        if (!"Active".equalsIgnoreCase(asset.status())) {
            throw new BusinessRuleException("Cannot create a work order for an asset that is not ACTIVE.");
        }

        logger.info("Attempting to create work order for asset {} ({}), customer {}, site {}",
                asset.name(), asset.id(), asset.customerId(), asset.siteId());

        WorkOrder workOrder = WorkOrder.create(
                generateWorkOrderNumber(),
                asset.id(),
                asset.customerId(),
                asset.siteId(),
                command.createdByUserId(),
                command.type(),
                command.priority(),
                command.title().trim(),
                command.description().trim(),
                command.requestedDate(),
                command.scheduledStart(),
                command.scheduledEnd(),
                command.createdBy().trim()
        );

        logger.info("Work order {} created with status {}", workOrder.getWorkOrderNumber(), workOrder.getStatus());

        saveOutboxEvent(workOrder, "WORK_ORDER_CREATED");   
        
        logger.info("Work order attempt creation for work order {}", workOrder.getWorkOrderNumber());
        UUID workOrderId = workOrderRepository.save(workOrder).getId();
        logger.info("Work order {} created successfully with id {}", workOrder.getWorkOrderNumber(), workOrderId);
        return workOrderId;
    }

    @Transactional(readOnly = true)
    public WorkOrderDto getWorkOrderById(UUID workOrderId) {
        return workOrderRepository.findById(workOrderId)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + workOrderId));
    }

    @Transactional(readOnly = true)
    public List<WorkOrderDto> searchWorkOrders(
            WorkOrderStatus status,
            WorkOrderPriority priority,
            UUID assignedTechnicianUserId,
            UUID customerId
    ) {
        if (status != null) {
            return workOrderRepository.findByStatus(status).stream().map(this::toDto).toList();
        }

        if (priority != null) {
            return workOrderRepository.findByPriority(priority).stream().map(this::toDto).toList();
        }

        if (assignedTechnicianUserId != null) {
            return workOrderRepository.findByAssignedTechnicianUserId(assignedTechnicianUserId).stream().map(this::toDto).toList();
        }

        if (customerId != null) {
            return workOrderRepository.findByCustomerId(customerId).stream().map(this::toDto).toList();
        }

        return workOrderRepository.findAll().stream().map(this::toDto).toList();
    }

    public void assignTechnician(AssignTechnicianCommand command) {
        WorkOrder existing = workOrderRepository.findById(command.workOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + command.workOrderId()));

        WorkOrder updated = existing.assignTo(command.technicianUserId(), command.updatedBy());

        workOrderRepository.save(updated);

        assignmentRepository.save(
                WorkOrderAssignment.assigned(
                        existing.getId(),
                        command.technicianUserId(),
                        command.assignedByUserId()
                )
        );
        saveOutboxEvent(updated, "WORK_ORDER_ASSIGNED");
    }

    public void acceptAssignment(TechnicianActionCommand command) {
        WorkOrder existing = workOrderRepository.findById(command.workOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + command.workOrderId()));

        WorkOrder updated = existing.accept(command.technicianUserId(), command.updatedBy());
        workOrderRepository.save(updated);
        saveOutboxEvent(updated, "WORK_ORDER_ACCEPTED");
    }

    public void startIntervention(TechnicianActionCommand command) {
        WorkOrder existing = workOrderRepository.findById(command.workOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + command.workOrderId()));

        WorkOrder updated = existing.start(command.technicianUserId(), command.updatedBy());
        workOrderRepository.save(updated);
        saveOutboxEvent(updated, "WORK_ORDER_STARTED");
    }

    public void completeWorkOrder(CompleteWorkOrderCommand command) {
        WorkOrder existing = workOrderRepository.findById(command.workOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + command.workOrderId()));

        if (interventionReportRepository.existsByWorkOrderId(command.workOrderId())) {
            throw new BusinessRuleException("An intervention report already exists for this work order.");
        }

        WorkOrder completed = existing.complete(command.technicianUserId(), command.updatedBy());
        workOrderRepository.save(completed);
        saveOutboxEvent(completed, "WORK_ORDER_COMPLETED");

        OffsetDateTime now = OffsetDateTime.now();

        InterventionReport report = new InterventionReport(
                UUID.randomUUID(),
                command.workOrderId(),
                command.technicianUserId(),
                command.startedAt(),
                command.completedAt() != null ? command.completedAt() : now,
                command.laborDurationMinutes(),
                command.summary().trim(),
                normalize(command.actionsPerformed()),
                command.resultStatus(),
                command.followUpRequired(),
                normalize(command.followUpNotes()),
                now,
                command.updatedBy().trim(),
                now,
                command.updatedBy().trim()
        );

        InterventionReport savedReport = interventionReportRepository.save(report);

        if (command.usedParts() != null) {
            command.usedParts().forEach(part ->
                    usedPartRepository.save(new UsedPart(
                            UUID.randomUUID(),
                            savedReport.getId(),
                            part.partNumber().trim(),
                            part.partName().trim(),
                            part.quantity(),
                            part.unitPrice(),
                            normalize(part.currency())
                    ))
            );
        }
    }

    public void cancelWorkOrder(CancelWorkOrderCommand command) {
        validateCancelCommand(command);

        WorkOrder existing = workOrderRepository.findById(command.workOrderId())
                .orElseThrow(() -> new NotFoundException(
                        "Work order not found with id " + command.workOrderId()
                ));

        WorkOrder cancelled = existing.cancel(
                command.reason().trim(),
                command.updatedBy().trim()
        );

        WorkOrder saved = workOrderRepository.save(cancelled);

        saveOutboxEvent(saved, "WORK_ORDER_CANCELLED");
    }

    private void validateCancelCommand(CancelWorkOrderCommand command) {
        Map<String, String[]> errors = new LinkedHashMap<>();

        if (command.workOrderId() == null) {
            errors.put("workOrderId", new String[]{"workOrderId is required."});
        }

        if (command.reason() == null || command.reason().isBlank()) {
            errors.put("reason", new String[]{"reason is required."});
        }

        if (command.updatedBy() == null || command.updatedBy().isBlank()) {
            errors.put("updatedBy", new String[]{"updatedBy is required."});
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    public UUID addTask(AddWorkOrderTaskCommand command) {
        WorkOrder workOrder = workOrderRepository.findById(command.workOrderId())
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + command.workOrderId()));

        if (workOrder.getStatus() == WorkOrderStatus.COMPLETED || workOrder.getStatus() == WorkOrderStatus.CANCELLED) {
            throw new BusinessRuleException("Cannot add tasks to completed or cancelled work orders.");
        }

        WorkOrderTask task = WorkOrderTask.create(
                command.workOrderId(),
                command.title().trim(),
                normalize(command.description()),
                command.taskOrder(),
                command.createdBy().trim()
        );

        return taskRepository.save(task).getId();
    }

    @Transactional(readOnly = true)
    public List<WorkOrderTaskDto> listTasks(UUID workOrderId) {
        workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + workOrderId));

        return taskRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(this::toTaskDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkOrderAssignmentDto> listAssignments(UUID workOrderId) {
        workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + workOrderId));

        return assignmentRepository.findByWorkOrderId(workOrderId)
                .stream()
                .map(this::toAssignmentDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public InterventionReportDto getInterventionReport(UUID workOrderId) {
        workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException("Work order not found with id " + workOrderId));

        InterventionReport report = interventionReportRepository.findByWorkOrderId(workOrderId)
                .orElseThrow(() -> new NotFoundException("Intervention report not found for work order " + workOrderId));

        List<UsedPartDto> usedParts = usedPartRepository.findByInterventionReportId(report.getId())
                .stream()
                .map(this::toUsedPartDto)
                .toList();

        return toInterventionReportDto(report, usedParts);
    }

    /**
     * Call this method after any state change including create, assign, accept, start, and complete,
     * to create a corresponding outbox event for integration with other systems.
     */
    private void saveOutboxEvent(WorkOrder workOrder, String eventType) {
        try {
            logger.info("Creating outbox event for work order {}: {}", workOrder.getId(), eventType);
            String payload = objectMapper.writeValueAsString(Map.of(
                    "workOrderId", workOrder.getId(),
                    "workOrderNumber", workOrder.getWorkOrderNumber(),
                    "assetId", workOrder.getAssetId(),
                    "customerId", workOrder.getCustomerId(),
                    "siteId", workOrder.getSiteId(),
                    "status", workOrder.getStatus().name(),
                    "eventType", eventType,
                    "occurredAt", OffsetDateTime.now().toString()
            ));

            logger.info("Outbox event payload for work order {}: {}", workOrder.getId(), payload);
            outboxEventRepository.save(
                    OutboxEvent.pending(workOrder.getId(), eventType, payload)
            );

            logger.info("Outbox event for work order {} saved successfully", workOrder.getId());

        } catch (Exception e) {
            logger.error("Failed to create outbox event for work order {}: {}", workOrder.getId(), e.getMessage(), e);
            throw new BusinessRuleException("Failed to create work order outbox event.");
        }
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void validateCreateCommand(CreateWorkOrderCommand command) {
        Map<String, String[]> errors = new LinkedHashMap<>();

        if (command.assetId() == null) {
            errors.put("assetId", new String[]{"assetId is required."});
        }

        if (command.createdByUserId() == null) {
            errors.put("createdByUserId", new String[]{"createdByUserId is required."});
        }

        if (command.type() == null) {
            errors.put("type", new String[]{"type is required."});
        }

        if (command.priority() == null) {
            errors.put("priority", new String[]{"priority is required."});
        }

        if (command.title() == null || command.title().isBlank()) {
            errors.put("title", new String[]{"title is required."});
        }

        if (command.description() == null || command.description().isBlank()) {
            errors.put("description", new String[]{"description is required."});
        }

        if (command.createdBy() == null || command.createdBy().isBlank()) {
            errors.put("createdBy", new String[]{"createdBy is required."});
        }

        if (command.scheduledStart() != null
                && command.scheduledEnd() != null
                && command.scheduledStart().isAfter(command.scheduledEnd())) {
            errors.put("scheduledStart", new String[]{"scheduledStart must be before scheduledEnd."});
        }

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
        
    }

    private WorkOrderDto toDto(WorkOrder workOrder) {
        return new WorkOrderDto(
                workOrder.getId(),
                workOrder.getWorkOrderNumber(),
                workOrder.getAssetId(),
                workOrder.getCustomerId(),
                workOrder.getSiteId(),
                workOrder.getCreatedByUserId(),
                workOrder.getAssignedTechnicianUserId(),
                workOrder.getType().name(),
                workOrder.getPriority().name(),
                workOrder.getStatus().name(),
                workOrder.getTitle(),
                workOrder.getDescription(),
                workOrder.getRequestedDate(),
                workOrder.getScheduledStart(),
                workOrder.getScheduledEnd(),
                workOrder.getCompletedAt(),
                workOrder.getCancellationReason(),
                workOrder.getCreatedAt(),
                workOrder.getCreatedBy(),
                workOrder.getUpdatedAt(),
                workOrder.getUpdatedBy()
        );
    }

    private String generateWorkOrderNumber() {
        return "WO-" + System.currentTimeMillis();
    }

    private WorkOrderAssignmentDto toAssignmentDto(WorkOrderAssignment assignment) {
        return new WorkOrderAssignmentDto(
                assignment.getId(),
                assignment.getWorkOrderId(),
                assignment.getTechnicianUserId(),
                assignment.getAssignedByUserId(),
                assignment.getAssignedAt(),
                assignment.getAssignmentStatus().name(),
                assignment.getReason()
        );
    }

    private WorkOrderTaskDto toTaskDto(WorkOrderTask task) {
        return new WorkOrderTaskDto(
                task.getId(),
                task.getWorkOrderId(),
                task.getTitle(),
                task.getDescription(),
                task.getTaskOrder(),
                task.getStatus().name(),
                task.getCreatedAt(),
                task.getCreatedBy(),
                task.getUpdatedAt(),
                task.getUpdatedBy()
        );
    }

    private UsedPartDto toUsedPartDto(UsedPart part) {
        return new UsedPartDto(
                part.getId(),
                part.getInterventionReportId(),
                part.getPartNumber(),
                part.getPartName(),
                part.getQuantity(),
                part.getUnitPrice(),
                part.getCurrency()
        );
    }

    private InterventionReportDto toInterventionReportDto(InterventionReport report, List<UsedPartDto> usedParts) {
        return new InterventionReportDto(
                report.getId(),
                report.getWorkOrderId(),
                report.getTechnicianUserId(),
                report.getStartedAt(),
                report.getCompletedAt(),
                report.getLaborDurationMinutes(),
                report.getSummary(),
                report.getActionsPerformed(),
                report.getResultStatus().name(),
                report.isFollowUpRequired(),
                report.getFollowUpNotes(),
                report.getCreatedAt(),
                report.getCreatedBy(),
                report.getUpdatedAt(),
                report.getUpdatedBy(),
                usedParts
        );
    }
}