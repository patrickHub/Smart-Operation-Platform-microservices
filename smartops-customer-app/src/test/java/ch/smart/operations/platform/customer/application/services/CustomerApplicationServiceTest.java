package ch.smart.operations.platform.customer.application.services;
import ch.smart.operations.platform.customer.application.commands.CreateCustomerCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerDto;
import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.application.services.CustomerApplicationService;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerApplicationServiceTest {

    private CustomerRepository customerRepository;
    private CustomerApplicationService customerApplicationService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        customerApplicationService = new CustomerApplicationService(customerRepository);
    }

    @Test
    void getAllCustomers_shouldReturnCustomerDtos() {
        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Important hospital customer"
        );

        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerDto> response = customerApplicationService.getAllCustomers();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().id()).isEqualTo(customer.getId());
        assertThat(response.getFirst().customerNumber()).isEqualTo("CUST-001");
        assertThat(response.getFirst().legalName()).isEqualTo("Clinic Edelweiss SA");
        assertThat(response.getFirst().displayName()).isEqualTo("Clinic Edelweiss");
        assertThat(response.getFirst().taxIdentifier()).isEqualTo("CHE-123456789");
        assertThat(response.getFirst().industry()).isEqualTo("Healthcare");
        assertThat(response.getFirst().notes()).isEqualTo("Important hospital customer");

        verify(customerRepository).findAll();
    }

    @Test
    void getCustomerById_shouldReturnCustomer_whenCustomerExists() {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Important hospital customer"
        );

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        CustomerDto response = customerApplicationService.getCustomerById(customerId);

        assertThat(response.id()).isEqualTo(customer.getId());
        assertThat(response.legalName()).isEqualTo("Clinic Edelweiss SA");
        assertThat(response.displayName()).isEqualTo("Clinic Edelweiss");

        verify(customerRepository).findById(customerId);
    }

    @Test
    void getCustomerById_shouldThrowNotFoundException_whenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerApplicationService.getCustomerById(customerId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer not found with id: " + customerId);

        verify(customerRepository).findById(customerId);
    }

    @Test
    void createCustomer_shouldSaveCustomerAndReturnId_whenCommandIsValid() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Important hospital customer"
        );

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID createdCustomerId = customerApplicationService.createCustomer(command);

        assertThat(createdCustomerId).isNotNull();

        verify(customerRepository).save(argThat(customer ->
                customer.getCustomerNumber().startsWith("CUST-")
                        && customer.getLegalName().equals("Clinic Edelweiss SA")
                        && customer.getDisplayName().equals("Clinic Edelweiss")
                        && customer.getTaxIdentifier().equals("CHE-123456789")
                        && customer.getIndustry().equals("Healthcare")
                        && customer.getNotes().equals("Important hospital customer")
        ));
    }

    @Test
    void createCustomer_shouldThrowValidationException_whenDisplayNameEqualsLegalName() {
        CreateCustomerCommand command = new CreateCustomerCommand(
                "Clinic Edelweiss SA",
                "Clinic Edelweiss SA",
                "CHE-123456789",
                "Healthcare",
                "Invalid because names are same"
        );

        assertThatThrownBy(() -> customerApplicationService.createCustomer(command))
                .isInstanceOf(ValidationException.class);

        verify(customerRepository, never()).save(any());
    }
}