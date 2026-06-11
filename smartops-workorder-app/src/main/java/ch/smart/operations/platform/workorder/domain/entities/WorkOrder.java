package ch.smart.operations.platform.workorder.domain.entities;

import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderPriority;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderStatus;
import ch.smart.operations.platform.workorder.domain.enums.WorkOrderType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class WorkOrder {

    private final UUID id;
    private final String workOrderNumber;
    private final UUID assetId;
    private final UUID customerId;
    private final UUID siteId;
    private final UUID createdByUserId;
    private final UUID assignedTechnicianUserId;
    private final WorkOrderType type;
    private final WorkOrderPriority priority;
    private final WorkOrderStatus status;
    private final String title;
    private final String description;
    private final OffsetDateTime requestedDate;
    private final OffsetDateTime scheduledStart;
    private final OffsetDateTime scheduledEnd;
    private final OffsetDateTime completedAt;
    private final String cancellationReason;
    private final Long version;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final OffsetDateTime updatedAt;
    private final String updatedBy;

    public WorkOrder(
            UUID id,
            String workOrderNumber,
            UUID assetId,
            UUID customerId,
            UUID siteId,
            UUID createdByUserId,
            UUID assignedTechnicianUserId,
            WorkOrderType type,
            WorkOrderPriority priority,
            WorkOrderStatus status,
            String title,
            String description,
            OffsetDateTime requestedDate,
            OffsetDateTime scheduledStart,
            OffsetDateTime scheduledEnd,
            OffsetDateTime completedAt,
            String cancellationReason,
            Long version,
            OffsetDateTime createdAt,
            String createdBy,
            OffsetDateTime updatedAt,
            String updatedBy
    ) {
        this.id = id;
        this.workOrderNumber = workOrderNumber;
        this.assetId = assetId;
        this.customerId = customerId;
        this.siteId = siteId;
        this.createdByUserId = createdByUserId;
        this.assignedTechnicianUserId = assignedTechnicianUserId;
        this.type = type;
        this.priority = priority;
        this.status = status;
        this.title = title;
        this.description = description;
        this.requestedDate = requestedDate;
        this.scheduledStart = scheduledStart;
        this.scheduledEnd = scheduledEnd;
        this.completedAt = completedAt;
        this.cancellationReason = cancellationReason;
        this.version = version;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static WorkOrder create(
            String workOrderNumber,
            UUID assetId,
            UUID customerId,
            UUID siteId,
            UUID createdByUserId,
            WorkOrderType type,
            WorkOrderPriority priority,
            String title,
            String description,
            OffsetDateTime requestedDate,
            OffsetDateTime scheduledStart,
            OffsetDateTime scheduledEnd,
            String createdBy
    ) {
        OffsetDateTime now = OffsetDateTime.now();

        return new WorkOrder(
                UUID.randomUUID(),
                workOrderNumber,
                assetId,
                customerId,
                siteId,
                createdByUserId,
                null,
                type,
                priority,
                WorkOrderStatus.CREATED,
                title,
                description,
                requestedDate,
                scheduledStart,
                scheduledEnd,
                null,
                null,
                null,
                now,
                createdBy,
                now,
                createdBy
        );
    }

    public WorkOrder assignTo(UUID technicianUserId, String updatedBy) {
        if (this.status != WorkOrderStatus.CREATED) {
            throw new BusinessRuleException("Only CREATED work orders can be assigned.");
        }

        return new WorkOrder(
                id,
                workOrderNumber,
                assetId,
                customerId,
                siteId,
                createdByUserId,
                technicianUserId,
                type,
                priority,
                WorkOrderStatus.ASSIGNED,
                title,
                description,
                requestedDate,
                scheduledStart,
                scheduledEnd,
                completedAt,
                cancellationReason,
                version,
                createdAt,
                createdBy,
                OffsetDateTime.now(),
                updatedBy
        );
    }

    public WorkOrder accept(UUID technicianUserId, String updatedBy) {
        if (this.status != WorkOrderStatus.ASSIGNED) {
            throw new BusinessRuleException("Only ASSIGNED work orders can be accepted.");
        }

        if (!technicianUserId.equals(this.assignedTechnicianUserId)) {
            throw new BusinessRuleException("Only the assigned technician can accept this work order.");
        }

        return copyWithStatus(WorkOrderStatus.ACCEPTED, updatedBy, null);
    }

    public WorkOrder start(UUID technicianUserId, String updatedBy) {
        if (this.status != WorkOrderStatus.ACCEPTED) {
            throw new BusinessRuleException("Only ACCEPTED work orders can be started.");
        }

        if (!technicianUserId.equals(this.assignedTechnicianUserId)) {
            throw new BusinessRuleException("Only the assigned technician can start this work order.");
        }

        return copyWithStatus(WorkOrderStatus.IN_PROGRESS, updatedBy, null);
    }

    public WorkOrder complete(UUID technicianUserId, String updatedBy) {
        if (this.status != WorkOrderStatus.IN_PROGRESS) {
            throw new BusinessRuleException("Only IN_PROGRESS work orders can be completed.");
        }

        if (!technicianUserId.equals(this.assignedTechnicianUserId)) {
            throw new BusinessRuleException("Only the assigned technician can complete this work order.");
        }

        return copyWithStatus(WorkOrderStatus.COMPLETED, updatedBy, OffsetDateTime.now());
    }

    public WorkOrder cancel(String reason, String updatedBy) {
        if (this.status == WorkOrderStatus.COMPLETED) {
            throw new BusinessRuleException("Completed work orders cannot be cancelled.");
        }

        if (this.status == WorkOrderStatus.CANCELLED) {
            throw new BusinessRuleException("Work order is already cancelled.");
        }

        return new WorkOrder(
                id,
                workOrderNumber,
                assetId,
                customerId,
                siteId,
                createdByUserId,
                assignedTechnicianUserId,
                type,
                priority,
                WorkOrderStatus.CANCELLED,
                title,
                description,
                requestedDate,
                scheduledStart,
                scheduledEnd,
                completedAt,
                reason,
                version,
                createdAt,
                createdBy,
                OffsetDateTime.now(),
                updatedBy
        );
    }

    private WorkOrder copyWithStatus(
        WorkOrderStatus newStatus,
        String updatedBy,
        OffsetDateTime completedAt
    ) {
        return new WorkOrder(
                id,
                workOrderNumber,
                assetId,
                customerId,
                siteId,
                createdByUserId,
                assignedTechnicianUserId,
                type,
                priority,
                newStatus,
                title,
                description,
                requestedDate,
                scheduledStart,
                scheduledEnd,
                completedAt != null ? completedAt : this.completedAt,
                cancellationReason,
                version,
                createdAt,
                createdBy,
                OffsetDateTime.now(),
                updatedBy
        );
    }

    public UUID getId() { return id; }
    public String getWorkOrderNumber() { return workOrderNumber; }
    public UUID getAssetId() { return assetId; }
    public UUID getCustomerId() { return customerId; }
    public UUID getSiteId() { return siteId; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public UUID getAssignedTechnicianUserId() { return assignedTechnicianUserId; }
    public WorkOrderType getType() { return type; }
    public WorkOrderPriority getPriority() { return priority; }
    public WorkOrderStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public OffsetDateTime getRequestedDate() { return requestedDate; }
    public OffsetDateTime getScheduledStart() { return scheduledStart; }
    public OffsetDateTime getScheduledEnd() { return scheduledEnd; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public String getCancellationReason() { return cancellationReason; }
    public Long getVersion() { return version; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}