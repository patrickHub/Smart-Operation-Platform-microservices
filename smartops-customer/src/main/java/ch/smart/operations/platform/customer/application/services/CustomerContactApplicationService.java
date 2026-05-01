package ch.smart.operations.platform.customer.application.services;

import ch.smart.operations.platform.customer.application.commands.CreateCustomerContactCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerContactDto;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import ch.smart.operations.platform.customer.application.ports.CustomerContactRepository;
import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.customer.domain.entities.CustomerContact;
import ch.smart.operations.platform.customer.domain.enums.CustomerContactStatus;
import ch.smart.operations.platform.customer.domain.enums.CustomerStatus;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerContactApplicationService {

    private final CustomerRepository customerRepository;
    private final CustomerContactRepository customerContactRepository;

    public CustomerContactApplicationService(
            CustomerRepository customerRepository,
            CustomerContactRepository customerContactRepository
    ) {
        this.customerRepository = customerRepository;
        this.customerContactRepository = customerContactRepository;
    }

    public UUID createCustomerContact(CreateCustomerContactCommand command) {
        Customer customer = customerRepository.findById(command.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id " + command.customerId()));

        if (customer.getStatus() != CustomerStatus.ACTIVE) {
            throw new ValidationException(Map.of(
                    "customerId", new String[]{"Cannot add contact to an inactive customer."}  
            ));
        }

        if (customerContactRepository.existsByCustomerIdAndEmail(command.customerId(), command.email())) {
            throw new ValidationException(Map.of(
                    "email", new String[]{"A contact with the same email already exists for this customer."}
            ));
        }

        if (command.isPrimary() && customerContactRepository.existsPrimaryContact(command.customerId())) {
            throw new ValidationException(Map.of(
                    "isPrimary", new String[]{"A primary contact already exists for this customer."}
            ));
        }

        OffsetDateTime now = OffsetDateTime.now();

        CustomerContact contact = new CustomerContact(
                UUID.randomUUID(),
                command.customerId(),
                command.firstName(),
                command.lastName(),
                command.email(),
                command.phone(),
                command.contactRole(),
                command.isPrimary(),
                CustomerContactStatus.ACTIVE,
                now,
                now
        );

        return customerContactRepository.save(contact).getId();
    }

    public CustomerContactDto getContactById(UUID contactId) {
        CustomerContact contact = customerContactRepository.findById(contactId)
                .orElseThrow(() -> new NotFoundException("Customer contact not found with id " + contactId));

        return toDto(contact);
    }

    private CustomerContactDto toDto(CustomerContact contact) {
        return new CustomerContactDto(
                contact.getId(),
                contact.getCustomerId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getContactRole(),
                contact.isPrimary(),
                contact.getStatus().name(),
                contact.getCreatedAt(),
                contact.getUpdatedAt()
        );
    }
}