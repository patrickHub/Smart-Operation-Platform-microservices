package ch.smart.operations.platform.billing.api.controllers;

import ch.smart.operations.platform.billing.api.contracts.GenerateInvoiceFromWorkOrderRequest;
import ch.smart.operations.platform.billing.application.commands.GenerateInvoiceFromWorkOrderCommand;
import ch.smart.operations.platform.billing.application.services.BillingApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/invoices")
public class InternalInvoiceController {

    private final BillingApplicationService billingApplicationService;

    public InternalInvoiceController(BillingApplicationService billingApplicationService) {
        this.billingApplicationService = billingApplicationService;
    }

    @PostMapping("/generate-from-work-order/{workOrderId}")
    public ResponseEntity<Map<String, UUID>> generateFromWorkOrder(
            @PathVariable UUID workOrderId,
            @Valid @RequestBody GenerateInvoiceFromWorkOrderRequest request
    ) {
        UUID id = billingApplicationService.generateInvoiceFromWorkOrder(
                new GenerateInvoiceFromWorkOrderCommand(
                        workOrderId,
                        request.dueDate(),
                        request.createdBy()
                )
        );

        return ResponseEntity
                .created(URI.create("/api/v1/invoices/" + id))
                .body(Map.of("id", id));
    }
}