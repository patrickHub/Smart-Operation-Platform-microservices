package ch.smart.operations.platform.customer.api.controllers;

import ch.smart.operations.platform.customer.application.services.CustomerApplicationService;
import ch.smart.operations.platform.customer.application.services.CustomerSiteApplicationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/customers")
public class InternalCustomerController {

    private final CustomerApplicationService customerApplicationService;
    private final CustomerSiteApplicationService customerSiteApplicationService;

    public InternalCustomerController(CustomerApplicationService customerApplicationService, CustomerSiteApplicationService customerSiteApplicationService) {
        this.customerApplicationService = customerApplicationService;
        this.customerSiteApplicationService = customerSiteApplicationService;
    }


    @GetMapping("/{customerId}/exists")
    public ResponseEntity<Void> customerExists(@PathVariable UUID customerId) {
        customerApplicationService.getCustomerById(customerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{customerId}/sites/{siteId}/exists")
    public ResponseEntity<Void> customerSiteExists(
            @PathVariable UUID customerId,
            @PathVariable UUID siteId
    ) {
        customerSiteApplicationService.getCustomerSiteByCustomerIdAndSiteId(customerId, siteId);
        return ResponseEntity.noContent().build();
    }
}
