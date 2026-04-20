package ch.smart.operations.platform.customer.api.controllers;

import ch.smart.operations.platform.customer.api.contracts.CreateCustomerContactRequest;
import ch.smart.operations.platform.customer.application.commands.CreateCustomerContactCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerContactDto;
import ch.smart.operations.platform.customer.application.services.CustomerContactApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.UUID;


@RestController
public class CustomerContactController {

    private final CustomerContactApplicationService customerContactApplicationService;

    public CustomerContactController(CustomerContactApplicationService customerContactApplicationService) {
        this.customerContactApplicationService = customerContactApplicationService;
    }

    @PostMapping("/api/v1/customers/{customerId}/contacts")
    public ResponseEntity<Map<String, UUID>> createCustomerContact(
            @PathVariable UUID customerId,
            @Valid @RequestBody CreateCustomerContactRequest request
    ) {
        UUID id = customerContactApplicationService.createCustomerContact(
                new CreateCustomerContactCommand(
                        customerId,
                        request.firstName(),
                        request.lastName(),
                        request.email(),
                        request.phone(),
                        request.contactRole(),
                        request.isPrimary()
                )
        );

        return ResponseEntity
                .created(URI.create("/api/v1/contacts/" + id))
                .body(Map.of("id", id));
    }

    @GetMapping("/api/v1/contacts/{contactId}")
    public ResponseEntity<CustomerContactDto> get(@PathVariable UUID contactId) {
        CustomerContactDto contact = customerContactApplicationService.getContactById(contactId);
        return ResponseEntity.ok(contact);

    }
    
}