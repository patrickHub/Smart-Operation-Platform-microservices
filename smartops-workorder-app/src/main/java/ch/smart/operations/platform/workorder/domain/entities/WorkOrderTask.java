package ch.smart.operations.platform.workorder.domain.entities;

import ch.smart.operations.platform.workorder.domain.enums.WorkOrderTaskStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class WorkOrderTask {

    private final UUID id;
    private final UUID workOrderId;
    private final String title;
    private final String description;
    private final Integer taskOrder;
    private final WorkOrderTaskStatus status;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final OffsetDateTime updatedAt;
    private final String updatedBy;

    public WorkOrderTask(
            UUID id,
            UUID workOrderId,
            String title,
            String description,
            Integer taskOrder,
            WorkOrderTaskStatus status,
            OffsetDateTime createdAt,
            String createdBy,
            OffsetDateTime updatedAt,
            String updatedBy
    ) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.title = title;
        this.description = description;
        this.taskOrder = taskOrder;
        this.status = status;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static WorkOrderTask create(UUID workOrderId, String title, String description, Integer taskOrder, String createdBy) {
        OffsetDateTime now = OffsetDateTime.now();

        return new WorkOrderTask(
                UUID.randomUUID(),
                workOrderId,
                title,
                description,
                taskOrder,
                WorkOrderTaskStatus.PENDING,
                now,
                createdBy,
                now,
                createdBy
        );
    }

    public UUID getId() { return id; }
    public UUID getWorkOrderId() { return workOrderId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Integer getTaskOrder() { return taskOrder; }
    public WorkOrderTaskStatus getStatus() { return status; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}