package ch.smart.operations.platform.workorder.domain.entities;

import ch.smart.operations.platform.workorder.domain.enums.AssignmentStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class WorkOrderAssignment {

    private final UUID id;
    private final UUID workOrderId;
    private final UUID technicianUserId;
    private final UUID assignedByUserId;
    private final OffsetDateTime assignedAt;
    private final AssignmentStatus assignmentStatus;
    private final String reason;

    public WorkOrderAssignment(
            UUID id,
            UUID workOrderId,
            UUID technicianUserId,
            UUID assignedByUserId,
            OffsetDateTime assignedAt,
            AssignmentStatus assignmentStatus,
            String reason
    ) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.technicianUserId = technicianUserId;
        this.assignedByUserId = assignedByUserId;
        this.assignedAt = assignedAt;
        this.assignmentStatus = assignmentStatus;
        this.reason = reason;
    }

    public static WorkOrderAssignment assigned(
            UUID workOrderId,
            UUID technicianUserId,
            UUID assignedByUserId
    ) {
        return new WorkOrderAssignment(
                UUID.randomUUID(),
                workOrderId,
                technicianUserId,
                assignedByUserId,
                OffsetDateTime.now(),
                AssignmentStatus.ASSIGNED,
                null
        );
    }

    public UUID getId() { return id; }
    public UUID getWorkOrderId() { return workOrderId; }
    public UUID getTechnicianUserId() { return technicianUserId; }
    public UUID getAssignedByUserId() { return assignedByUserId; }
    public OffsetDateTime getAssignedAt() { return assignedAt; }
    public AssignmentStatus getAssignmentStatus() { return assignmentStatus; }
    public String getReason() { return reason; }
}