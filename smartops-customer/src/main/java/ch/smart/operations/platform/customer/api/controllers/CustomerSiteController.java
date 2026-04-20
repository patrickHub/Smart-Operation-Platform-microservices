package ch.smart.operations.platform.customer.api.controllers;


import org.springframework.web.bind.annotation.RestController;

import ch.smart.operations.platform.customer.api.contracts.CreateCustomerSiteRequest;
import ch.smart.operations.platform.customer.application.commands.CreateCustomerSiteCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerSiteDto;
import ch.smart.operations.platform.customer.application.services.CustomerSiteApplicationService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class CustomerSiteController {


    private final CustomerSiteApplicationService customerSiteApplicationService;

    public CustomerSiteController(CustomerSiteApplicationService customerSiteApplicationService) {
        this.customerSiteApplicationService = customerSiteApplicationService;
    }

    @GetMapping("/api/v1/sites")
    public ResponseEntity<List<CustomerSiteDto>> getAllCustomerSites() {
        return ResponseEntity.ok(customerSiteApplicationService.getAllCustomerSites());
    }

    @GetMapping("/api/v1/customers/{customerId}/sites")
    public ResponseEntity<List<CustomerSiteDto>> getCustomerSitesByCustomerId(@RequestParam("customerId") UUID customerId) {
        return ResponseEntity.ok(customerSiteApplicationService.getCustomerSitesByCustomerId(customerId));
    }

    @GetMapping("/api/v1/sites/{id}")
    public ResponseEntity<CustomerSiteDto> getCustomerSiteById(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(customerSiteApplicationService.getCustomerSiteById(id));
    }

    @PostMapping("/api/v1/customers/{customerId}/sites") 
    public ResponseEntity<Map<String, UUID>> createCustomerSite( @RequestParam("customerId") UUID customerId, @Valid @RequestBody CreateCustomerSiteRequest request) {
        UUID id = customerSiteApplicationService.createCustomerSite(new CreateCustomerSiteCommand(
            customerId,
            request.siteName(),
            request.addressLine1(),
            request.addressLine2(),
            request.city(),
            request.stateRegion(),
            request.postalCode(),
            request.countryCode(),
            request.timezone(),
            request.accessInstructions()
        ));
        return ResponseEntity.created(URI.create("/api/v1/sites/" + id))
            .body((Map.of("id", id)));
    }

}