package ch.smart.operations.platform.workorder.infrastructure.persistence.entities;

import ch.smart.operations.platform.workorder.domain.enums.AssignmentStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "work_order_assignments", schema = "workorder")
public class WorkOrderAssignmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "work_order_id", nullable = false)
    private UUID workOrderId;

    @Column(name = "technician_user_id", nullable = false)
    private UUID technicianUserId;

    @Column(name = "assigned_by_user_id", nullable = false)
    private UUID assignedByUserId;

    @Column(name = "assigned_at", nullable = false)
    private OffsetDateTime assignedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_status", nullable = false)
    private AssignmentStatus assignmentStatus;

    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;


    public UUID getId() {
        return this.id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getWorkOrderId() {
        return this.workOrderId;
    }

    public void setWorkOrderId(UUID workOrderId) {
        this.workOrderId = workOrderId;
    }

    public UUID getTechnicianUserId() {
        return this.technicianUserId;
    }

    public void setTechnicianUserId(UUID technicianUserId) {
        this.technicianUserId = technicianUserId;
    }

    public UUID getAssignedByUserId() {
        return this.assignedByUserId;
    }

    public void setAssignedByUserId(UUID assignedByUserId) {
        this.assignedByUserId = assignedByUserId;
    }

    public OffsetDateTime getAssignedAt() {
        return this.assignedAt;
    }

    public void setAssignedAt(OffsetDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public AssignmentStatus getAssignmentStatus() {
        return this.assignmentStatus;
    }

    public void setAssignmentStatus(AssignmentStatus assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
}