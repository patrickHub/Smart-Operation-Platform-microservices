package ch.smart.operations.platform.workorder.api.controllers;

import ch.smart.operations.platform.workorder.application.dtos.WorkOrderBillingSummaryDto;
import ch.smart.operations.platform.workorder.application.services.WorkOrderApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/work-orders")
public class InternalWorkOrderController {

    private final WorkOrderApplicationService workOrderApplicationService;

    public InternalWorkOrderController(WorkOrderApplicationService workOrderApplicationService) {
        this.workOrderApplicationService = workOrderApplicationService;
    }

    @GetMapping("/{workOrderId}/billing-summary")
    public ResponseEntity<WorkOrderBillingSummaryDto> getBillingSummary(
            @PathVariable UUID workOrderId
    ) {
        return ResponseEntity.ok(workOrderApplicationService.getBillingSummary(workOrderId));
    }
}
