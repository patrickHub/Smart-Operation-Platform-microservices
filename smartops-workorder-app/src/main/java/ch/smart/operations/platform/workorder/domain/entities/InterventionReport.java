package ch.smart.operations.platform.workorder.domain.entities;

import ch.smart.operations.platform.workorder.domain.enums.InterventionResultStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class InterventionReport {

    private final UUID id;
    private final UUID workOrderId;
    private final UUID technicianUserId;
    private final OffsetDateTime startedAt;
    private final OffsetDateTime completedAt;
    private final Integer laborDurationMinutes;
    private final String summary;
    private final String actionsPerformed;
    private final InterventionResultStatus resultStatus;
    private final boolean followUpRequired;
    private final String followUpNotes;
    private final OffsetDateTime createdAt;
    private final String createdBy;
    private final OffsetDateTime updatedAt;
    private final String updatedBy;

    public InterventionReport(
            UUID id,
            UUID workOrderId,
            UUID technicianUserId,
            OffsetDateTime startedAt,
            OffsetDateTime completedAt,
            Integer laborDurationMinutes,
            String summary,
            String actionsPerformed,
            InterventionResultStatus resultStatus,
            boolean followUpRequired,
            String followUpNotes,
            OffsetDateTime createdAt,
            String createdBy,
            OffsetDateTime updatedAt,
            String updatedBy
    ) {
        this.id = id;
        this.workOrderId = workOrderId;
        this.technicianUserId = technicianUserId;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.laborDurationMinutes = laborDurationMinutes;
        this.summary = summary;
        this.actionsPerformed = actionsPerformed;
        this.resultStatus = resultStatus;
        this.followUpRequired = followUpRequired;
        this.followUpNotes = followUpNotes;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public UUID getId() { return id; }
    public UUID getWorkOrderId() { return workOrderId; }
    public UUID getTechnicianUserId() { return technicianUserId; }
    public OffsetDateTime getStartedAt() { return startedAt; }
    public OffsetDateTime getCompletedAt() { return completedAt; }
    public Integer getLaborDurationMinutes() { return laborDurationMinutes; }
    public String getSummary() { return summary; }
    public String getActionsPerformed() { return actionsPerformed; }
    public InterventionResultStatus getResultStatus() { return resultStatus; }
    public boolean isFollowUpRequired() { return followUpRequired; }
    public String getFollowUpNotes() { return followUpNotes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public String getCreatedBy() { return createdBy; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public String getUpdatedBy() { return updatedBy; }
}