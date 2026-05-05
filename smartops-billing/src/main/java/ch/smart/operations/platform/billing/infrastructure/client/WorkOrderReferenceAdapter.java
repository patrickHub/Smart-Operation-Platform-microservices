package ch.smart.operations.platform.billing.infrastructure.client;

import ch.smart.operations.platform.billing.application.dtos.UsedPartBillingDto;
import ch.smart.operations.platform.billing.application.dtos.WorkOrderBillingSummaryDto;
import ch.smart.operations.platform.billing.application.ports.WorkOrderReferencePort;
import ch.smart.operations.platform.workorder.application.dtos.InterventionReportDto;
import ch.smart.operations.platform.workorder.application.dtos.WorkOrderDto;
import ch.smart.operations.platform.workorder.application.services.WorkOrderApplicationService;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class WorkOrderReferenceAdapter implements WorkOrderReferencePort {

    private final WorkOrderApplicationService workOrderApplicationService;

    public WorkOrderReferenceAdapter(WorkOrderApplicationService workOrderApplicationService) {
        this.workOrderApplicationService = workOrderApplicationService;
    }

    @Override
    public Optional<WorkOrderBillingSummaryDto> findBillingSummary(UUID workOrderId) {
        try {
            WorkOrderDto workOrder = workOrderApplicationService.getWorkOrderById(workOrderId);

            InterventionReportDto report = workOrderApplicationService.getInterventionReport(workOrderId);

            return Optional.of(new WorkOrderBillingSummaryDto(
                    workOrder.id(),
                    workOrder.workOrderNumber(),
                    workOrder.customerId(),
                    workOrder.assetId(),
                    workOrder.siteId(),
                    workOrder.type(),
                    workOrder.status(),
                    workOrder.completedAt(),
                    report.laborDurationMinutes(),
                    report.resultStatus(),
                    report.usedParts()
                            .stream()
                            .map(part -> new UsedPartBillingDto(
                                    part.partNumber(),
                                    part.partName(),
                                    part.quantity(),
                                    part.unitPrice(),
                                    part.currency()
                            ))
                            .toList()
            ));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}