package ch.smart.operations.platform.customer.api.controllers;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.smart.operations.platform.customer.api.contracts.CreateCustomerRequest;
import ch.smart.operations.platform.customer.application.commands.CreateCustomerCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerDto;
import ch.smart.operations.platform.customer.application.services.CustomerApplicationService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/v1/customers")

public class CustomerController {

    private final CustomerApplicationService customerApplicationService;


    public CustomerController(CustomerApplicationService customerApplicationService) {
        this.customerApplicationService = customerApplicationService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomer() {
        return ResponseEntity.ok(customerApplicationService.getAllCustomers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(customerApplicationService.getCustomerById(id));
    }

    @PostMapping
    public ResponseEntity<Map<String,UUID>> createCustome(@Valid @RequestBody CreateCustomerRequest request) {

        UUID id = customerApplicationService.createCustomer(
            new CreateCustomerCommand(request.legalName(),
            request.displayName(), 
            request.taxIdentifier(), 
            request.industry(), 
            request.notes())
        );

        return ResponseEntity.created(URI.create("/api/v1/customer/" + id))
            .body((Map.of("id", id)));
        
    }
    
    
    

    
}
