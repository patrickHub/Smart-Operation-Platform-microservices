package ch.smart.operations.platform.workorder.infrastructure.persistence.adapters;

import ch.smart.operations.platform.workorder.application.ports.InterventionReportRepository;
import ch.smart.operations.platform.workorder.domain.entities.InterventionReport;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.InterventionReportJpaEntity;
import ch.smart.operations.platform.workorder.infrastructure.persistence.repositories.InterventionReportJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class InterventionReportRepositoryAdapter implements InterventionReportRepository {

    private final InterventionReportJpaRepository repository;

    public InterventionReportRepositoryAdapter(InterventionReportJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public InterventionReport save(InterventionReport report) {
        return toDomain(repository.save(toJpa(report)));
    }

    @Override
    public Optional<InterventionReport> findByWorkOrderId(UUID workOrderId) {
        return repository.findByWorkOrderId(workOrderId).map(this::toDomain);
    }

    @Override
    public boolean existsByWorkOrderId(UUID workOrderId) {
        return repository.existsByWorkOrderId(workOrderId);
    }

    private InterventionReport toDomain(InterventionReportJpaEntity entity) {
        return new InterventionReport(
                entity.getId(),
                entity.getWorkOrderId(),
                entity.getTechnicianUserId(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                entity.getLaborDurationMinutes(),
                entity.getSummary(),
                entity.getActionsPerformed(),
                entity.getResultStatus(),
                entity.isFollowUpRequired(),
                entity.getFollowUpNotes(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }

    private InterventionReportJpaEntity toJpa(InterventionReport report) {
        InterventionReportJpaEntity entity = new InterventionReportJpaEntity();

        entity.setId(report.getId());
        entity.setWorkOrderId(report.getWorkOrderId());
        entity.setTechnicianUserId(report.getTechnicianUserId());
        entity.setStartedAt(report.getStartedAt());
        entity.setCompletedAt(report.getCompletedAt());
        entity.setLaborDurationMinutes(report.getLaborDurationMinutes());
        entity.setSummary(report.getSummary());
        entity.setActionsPerformed(report.getActionsPerformed());
        entity.setResultStatus(report.getResultStatus());
        entity.setFollowUpRequired(report.isFollowUpRequired());
        entity.setFollowUpNotes(report.getFollowUpNotes());
        entity.setCreatedAt(report.getCreatedAt());
        entity.setCreatedBy(report.getCreatedBy());
        entity.setUpdatedAt(report.getUpdatedAt());
        entity.setUpdatedBy(report.getUpdatedBy());

        return entity;
    }
}