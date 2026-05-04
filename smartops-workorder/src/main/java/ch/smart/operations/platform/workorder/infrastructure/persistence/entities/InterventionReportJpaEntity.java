package ch.smart.operations.platform.workorder.infrastructure.persistence.entities;

import ch.smart.operations.platform.workorder.domain.enums.InterventionResultStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "intervention_reports", schema = "workorder")
public class InterventionReportJpaEntity {

    @Id
    private UUID id;

    @Column(name = "work_order_id", nullable = false, unique = true)
    private UUID workOrderId;

    @Column(name = "technician_user_id", nullable = false)
    private UUID technicianUserId;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "labor_duration_minutes")
    private Integer laborDurationMinutes;

    @Column(name = "summary", nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "actions_performed", columnDefinition = "TEXT")
    private String actionsPerformed;

    @Enumerated(EnumType.STRING)
    @Column(name = "result_status", nullable = false)
    private InterventionResultStatus resultStatus;

    @Column(name = "follow_up_required", nullable = false)
    private boolean followUpRequired;

    @Column(name = "follow_up_notes", columnDefinition = "TEXT")
    private String followUpNotes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;


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

    public OffsetDateTime getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getCompletedAt() {
        return this.completedAt;
    }

    public void setCompletedAt(OffsetDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getLaborDurationMinutes() {
        return this.laborDurationMinutes;
    }

    public void setLaborDurationMinutes(Integer laborDurationMinutes) {
        this.laborDurationMinutes = laborDurationMinutes;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getActionsPerformed() {
        return this.actionsPerformed;
    }

    public void setActionsPerformed(String actionsPerformed) {
        this.actionsPerformed = actionsPerformed;
    }

    public InterventionResultStatus getResultStatus() {
        return this.resultStatus;
    }

    public void setResultStatus(InterventionResultStatus resultStatus) {
        this.resultStatus = resultStatus;
    }

    public boolean isFollowUpRequired() {
        return this.followUpRequired;
    }

    public boolean getFollowUpRequired() {
        return this.followUpRequired;
    }

    public void setFollowUpRequired(boolean followUpRequired) {
        this.followUpRequired = followUpRequired;
    }

    public String getFollowUpNotes() {
        return this.followUpNotes;
    }

    public void setFollowUpNotes(String followUpNotes) {
        this.followUpNotes = followUpNotes;
    }

    public OffsetDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return this.updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }
    
}