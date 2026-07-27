package ch.smart.operations.platform.customer.application.services;

import ch.smart.operations.platform.customer.application.commands.CreateCustomerContactCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerContactDto;
import ch.smart.operations.platform.customer.application.ports.CustomerContactRepository;
import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.customer.domain.entities.CustomerContact;
import ch.smart.operations.platform.customer.domain.enums.CustomerContactStatus;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerContactApplicationServiceTest {

    private CustomerRepository customerRepository;
    private CustomerContactRepository customerContactRepository;
    private CustomerContactApplicationService customerContactApplicationService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerContactRepository = mock(CustomerContactRepository.class);

        customerContactApplicationService = new CustomerContactApplicationService(
                customerRepository,
                customerContactRepository
        );
    }

    @Test
    void createCustomerContact_shouldCreateContact_whenCommandIsValid() {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Active customer"
        );

        CreateCustomerContactCommand command = command(customerId, "doctor@clinic.local", true);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerContactRepository.existsByCustomerIdAndEmail(customerId, "doctor@clinic.local")).thenReturn(false);
        when(customerContactRepository.existsPrimaryContact(customerId)).thenReturn(false);
        when(customerContactRepository.save(any(CustomerContact.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID contactId = customerContactApplicationService.createCustomerContact(command);

        assertThat(contactId).isNotNull();

        verify(customerRepository).findById(customerId);
        verify(customerContactRepository).existsByCustomerIdAndEmail(customerId, "doctor@clinic.local");
        verify(customerContactRepository).existsPrimaryContact(customerId);
        verify(customerContactRepository).save(argThat(contact ->
                contact.getCustomerId().equals(customerId)
                        && contact.getFirstName().equals("John")
                        && contact.getLastName().equals("Doctor")
                        && contact.getEmail().equals("doctor@clinic.local")
                        && contact.isPrimary()
        ));
    }

    @Test
    void createCustomerContact_shouldThrowNotFoundException_whenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        CreateCustomerContactCommand command = command(customerId, "doctor@clinic.local", false);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerContactApplicationService.createCustomerContact(command))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found with id " + customerId);

        verify(customerContactRepository, never()).save(any());
    }

    @Test
    void createCustomerContact_shouldThrowValidationException_whenEmailAlreadyExistsForCustomer() {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Active customer"
        );

        CreateCustomerContactCommand command = command(customerId, "doctor@clinic.local", false);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerContactRepository.existsByCustomerIdAndEmail(customerId, "doctor@clinic.local")).thenReturn(true);

        assertThatThrownBy(() -> customerContactApplicationService.createCustomerContact(command))
                .isInstanceOf(ValidationException.class);

        verify(customerContactRepository, never()).save(any());
    }

    @Test
    void createCustomerContact_shouldThrowValidationException_whenPrimaryContactAlreadyExists() {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Active customer"
        );

        CreateCustomerContactCommand command = command(customerId, "doctor@clinic.local", true);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerContactRepository.existsByCustomerIdAndEmail(customerId, "doctor@clinic.local")).thenReturn(false);
        when(customerContactRepository.existsPrimaryContact(customerId)).thenReturn(true);

        assertThatThrownBy(() -> customerContactApplicationService.createCustomerContact(command))
                .isInstanceOf(ValidationException.class);

        verify(customerContactRepository, never()).save(any());
    }

    @Test
    void getContactById_shouldReturnContact_whenContactExists() {
        UUID customerId = UUID.randomUUID();
        UUID contactId = UUID.randomUUID();

        CustomerContact contact = contact(contactId, customerId, "doctor@clinic.local", true);

        when(customerContactRepository.findById(contactId)).thenReturn(Optional.of(contact));

        CustomerContactDto response = customerContactApplicationService.getContactById(contactId);

        assertThat(response.id()).isEqualTo(contactId);
        assertThat(response.customerId()).isEqualTo(customerId);
        assertThat(response.email()).isEqualTo("doctor@clinic.local");
        assertThat(response.isPrimary()).isTrue();

        verify(customerContactRepository).findById(contactId);
    }

    @Test
    void getContactById_shouldThrowNotFoundException_whenContactDoesNotExist() {
        UUID contactId = UUID.randomUUID();

        when(customerContactRepository.findById(contactId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerContactApplicationService.getContactById(contactId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer contact not found with id " + contactId);
    }

    @Test
    void getContactsByCustomerId_shouldReturnContactsForCustomer() {
        UUID customerId = UUID.randomUUID();

        CustomerContact contact = contact(UUID.randomUUID(), customerId, "doctor@clinic.local", true);

        when(customerContactRepository.findAllByCustomerId(customerId)).thenReturn(List.of(contact));

        List<CustomerContactDto> response = customerContactApplicationService.getContactsByCustomerId(customerId);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().customerId()).isEqualTo(customerId);
        assertThat(response.getFirst().email()).isEqualTo("doctor@clinic.local");

        verify(customerContactRepository).findAllByCustomerId(customerId);
    }

    private CreateCustomerContactCommand command(UUID customerId, String email, boolean isPrimary) {
        return new CreateCustomerContactCommand(
                customerId,
                "John",
                "Doctor",
                email,
                "+41 26 000 00 00",
                "Medical Director",
                isPrimary
        );
    }

    private CustomerContact contact(UUID contactId, UUID customerId, String email, boolean isPrimary) {
        OffsetDateTime now = OffsetDateTime.now();

        return new CustomerContact(
                contactId,
                customerId,
                "John",
                "Doctor",
                email,
                "+41 26 000 00 00",
                "Medical Director",
                isPrimary,
                CustomerContactStatus.ACTIVE,
                now,
                now
        );
    }
}