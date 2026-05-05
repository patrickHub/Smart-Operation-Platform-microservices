package ch.smart.operations.platform.billing.api.controllers;

import ch.smart.operations.platform.billing.api.contracts.InvoiceActionRequest;
import ch.smart.operations.platform.billing.application.commands.InvoiceActionCommand;
import ch.smart.operations.platform.billing.application.dtos.InvoiceDto;
import ch.smart.operations.platform.billing.application.services.BillingApplicationService;
import ch.smart.operations.platform.billing.domain.enums.InvoiceStatus;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final BillingApplicationService billingApplicationService;

    public InvoiceController(BillingApplicationService billingApplicationService) {
        this.billingApplicationService = billingApplicationService;
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDto> getInvoice(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(billingApplicationService.getInvoice(invoiceId));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceDto>> searchInvoices(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID workOrderId,
            @RequestParam(required = false) InvoiceStatus status
    ) {
        return ResponseEntity.ok(billingApplicationService.searchInvoices(customerId, workOrderId, status));
    }

    @PostMapping("/{invoiceId}/mark-sent")
    public ResponseEntity<Void> markSent(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody InvoiceActionRequest request
    ) {
        billingApplicationService.markInvoiceAsSent(
                new InvoiceActionCommand(invoiceId, request.updatedBy())
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{invoiceId}/cancel")
    public ResponseEntity<Void> cancel(
            @PathVariable UUID invoiceId,
            @Valid @RequestBody InvoiceActionRequest request
    ) {
        billingApplicationService.cancelInvoice(
                new InvoiceActionCommand(invoiceId, request.updatedBy())
        );

        return ResponseEntity.noContent().build();
    }
}