package ch.smart.operations.platform.workorder.application.ports;

import java.util.Optional;
import java.util.UUID;

import ch.smart.operations.platform.workorder.domain.entities.InterventionReport;

public interface InterventionReportRepository {
    InterventionReport save(InterventionReport report);
    Optional<InterventionReport> findByWorkOrderId(UUID workOrderId);
    boolean existsByWorkOrderId(UUID workOrderId);
}