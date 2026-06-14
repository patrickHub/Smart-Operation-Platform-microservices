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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
public class CustomerSiteController {


    private final CustomerSiteApplicationService customerSiteApplicationService;
    private final Logger logger = LoggerFactory.getLogger(CustomerSiteController.class);

    public CustomerSiteController(CustomerSiteApplicationService customerSiteApplicationService) {
        this.customerSiteApplicationService = customerSiteApplicationService;
    }

    @GetMapping("/api/v1/sites")
    public ResponseEntity<List<CustomerSiteDto>> getAllCustomerSites() {
        return ResponseEntity.ok(customerSiteApplicationService.getAllCustomerSites());
    }

    @GetMapping("/api/v1/customers/{customerId}/sites")
    public ResponseEntity<List<CustomerSiteDto>> getCustomerSitesByCustomerId(@PathVariable("customerId") UUID customerId) {
        return ResponseEntity.ok(customerSiteApplicationService.getCustomerSitesByCustomerId(customerId));
    }

    @GetMapping("/api/v1/sites/{id}")
    public ResponseEntity<CustomerSiteDto> getCustomerSiteById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(customerSiteApplicationService.getCustomerSiteById(id));
    }

    @PostMapping("/api/v1/customers/{customerId}/sites") 
    public ResponseEntity<Map<String, UUID>> createCustomerSite( @PathVariable("customerId") UUID customerId, @Valid @RequestBody CreateCustomerSiteRequest request) {
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