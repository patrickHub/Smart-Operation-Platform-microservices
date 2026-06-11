package ch.smart.operations.platform.billing.application.ports;

import ch.smart.operations.platform.billing.application.dtos.WorkOrderBillingSummaryDto;

import java.util.Optional;
import java.util.UUID;

public interface WorkOrderReferencePort {
    Optional<WorkOrderBillingSummaryDto> findBillingSummary(UUID workOrderId);
}
