package ch.smart.operations.platform.customer.application.services;

import ch.smart.operations.platform.customer.application.commands.CreateCustomerSiteCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerSiteDto;
import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.application.ports.CustomerSiteRepository;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.customer.domain.entities.CustomerSite;
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

class CustomerSiteApplicationServiceTest {

    private CustomerSiteRepository customerSiteRepository;
    private CustomerRepository customerRepository;
    private CustomerSiteApplicationService customerSiteApplicationService;

    @BeforeEach
    void setUp() {
        customerSiteRepository = mock(CustomerSiteRepository.class);
        customerRepository = mock(CustomerRepository.class);

        customerSiteApplicationService = new CustomerSiteApplicationService(
                customerSiteRepository,
                customerRepository
        );
    }

    @Test
    void getAllCustomerSites_shouldReturnSiteDtos() {
        UUID customerId = UUID.randomUUID();

        CustomerSite site = site(customerId, "Main Hospital");

        when(customerSiteRepository.findAll()).thenReturn(List.of(site));

        List<CustomerSiteDto> response = customerSiteApplicationService.getAllCustomerSites();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().id()).isEqualTo(site.getId());
        assertThat(response.getFirst().customerId()).isEqualTo(customerId);
        assertThat(response.getFirst().siteName()).isEqualTo("Main Hospital");

        verify(customerSiteRepository).findAll();
    }

    @Test
    void getCustomerSitesByCustomerId_shouldReturnSitesForCustomer() {
        UUID customerId = UUID.randomUUID();

        CustomerSite site = site(customerId, "Main Hospital");

        when(customerSiteRepository.findAllByCustomerId(customerId)).thenReturn(List.of(site));

        List<CustomerSiteDto> response = customerSiteApplicationService.getCustomerSitesByCustomerId(customerId);

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().customerId()).isEqualTo(customerId);
        assertThat(response.getFirst().siteName()).isEqualTo("Main Hospital");

        verify(customerSiteRepository).findAllByCustomerId(customerId);
    }

    @Test
    void getCustomerSiteById_shouldReturnSite_whenSiteExists() {
        UUID siteId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        CustomerSite site = site(customerId, "Main Hospital");

        when(customerSiteRepository.findById(siteId)).thenReturn(Optional.of(site));

        CustomerSiteDto response = customerSiteApplicationService.getCustomerSiteById(siteId);

        assertThat(response.id()).isEqualTo(site.getId());
        assertThat(response.siteName()).isEqualTo("Main Hospital");

        verify(customerSiteRepository).findById(siteId);
    }

    @Test
    void getCustomerSiteById_shouldThrowNotFoundException_whenSiteDoesNotExist() {
        UUID siteId = UUID.randomUUID();

        when(customerSiteRepository.findById(siteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerSiteApplicationService.getCustomerSiteById(siteId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Customer site not found with id: " + siteId);

        verify(customerSiteRepository).findById(siteId);
    }

    @Test
    void createCustomerSite_shouldCreateSite_whenCustomerIsActiveAndSiteNameIsUnique() {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Active customer"
        );

        CreateCustomerSiteCommand command = command(customerId, "Main Hospital");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerSiteRepository.findAllByCustomerId(customerId)).thenReturn(List.of());
        when(customerSiteRepository.save(any(CustomerSite.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UUID createdSiteId = customerSiteApplicationService.createCustomerSite(command);

        assertThat(createdSiteId).isNotNull();

        verify(customerRepository).findById(customerId);
        verify(customerSiteRepository).findAllByCustomerId(customerId);
        verify(customerSiteRepository).save(argThat(site ->
                site.getSiteNumber().startsWith("SITE-")
                        && site.getCustomerId().equals(customerId)
                        && site.getSiteName().equals("Main Hospital")
                        && site.getCity().equals("Fribourg")
                        && site.getCountryCode().equals("CH")
        ));
    }

    @Test
    void createCustomerSite_shouldThrowValidationException_whenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();

        CreateCustomerSiteCommand command = command(customerId, "Main Hospital");

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerSiteApplicationService.createCustomerSite(command))
                .isInstanceOf(ValidationException.class);

        verify(customerSiteRepository, never()).save(any());
    }

    @Test
    void createCustomerSite_shouldThrowValidationException_whenSiteNameAlreadyExistsForCustomer() {
        UUID customerId = UUID.randomUUID();

        Customer customer = Customer.create(
                "CUST-001",
                "Clinic Edelweiss SA",
                "Clinic Edelweiss",
                "CHE-123456789",
                "Healthcare",
                "Active customer"
        );

        CustomerSite existingSite = site(customerId, "Main Hospital");

        CreateCustomerSiteCommand command = command(customerId, "main hospital");

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(customerSiteRepository.findAllByCustomerId(customerId)).thenReturn(List.of(existingSite));

        assertThatThrownBy(() -> customerSiteApplicationService.createCustomerSite(command))
                .isInstanceOf(ValidationException.class);

        verify(customerSiteRepository, never()).save(any());
    }

    private CreateCustomerSiteCommand command(UUID customerId, String siteName) {
        return new CreateCustomerSiteCommand(
                customerId,
                siteName,
                "Rue de Lausanne 10",
                "Building A",
                "Fribourg",
                "FR",
                "1700",
                "CH",
                "Europe/Zurich",
                "Use main entrance"
        );
    }

    private CustomerSite site(UUID customerId, String siteName) {
        return CustomerSite.create(
                "SITE-001",
                customerId,
                siteName,
                "Rue de Lausanne 10",
                "Building A",
                "Fribourg",
                "FR",
                "1700",
                "CH",
                "Europe/Zurich",
                "Use main entrance"
        );
    }
}