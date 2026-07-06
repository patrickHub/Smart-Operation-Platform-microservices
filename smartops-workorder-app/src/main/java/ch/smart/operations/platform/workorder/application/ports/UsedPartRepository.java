package ch.smart.operations.platform.workorder.application.ports;

import java.util.List;
import java.util.UUID;

import ch.smart.operations.platform.workorder.domain.entities.UsedPart;

public interface UsedPartRepository {
    UsedPart save(UsedPart usedPart);
    List<UsedPart> findByInterventionReportId(UUID interventionReportId);
}
