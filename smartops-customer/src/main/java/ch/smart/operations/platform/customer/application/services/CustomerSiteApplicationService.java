package ch.smart.operations.platform.customer.application.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ch.smart.operations.platform.customer.application.commands.CreateCustomerSiteCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerSiteDto;
import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.application.ports.CustomerSiteRepository;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.customer.domain.entities.CustomerSite;
import ch.smart.operations.platform.customer.domain.enums.CustomerStatus;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;

@Service
public class CustomerSiteApplicationService {

    private final CustomerSiteRepository customerSiteRepository;
    private final CustomerRepository customerRepository;

    public CustomerSiteApplicationService(CustomerSiteRepository customerSiteRepository, CustomerRepository
         customerRepository) {
        this.customerSiteRepository = customerSiteRepository;
        this.customerRepository = customerRepository;   
    }

    public List<CustomerSiteDto> getAllCustomerSites() {
        return customerSiteRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public List<CustomerSiteDto> getCustomerSitesByCustomerId(UUID customerId) {
        return customerSiteRepository.findAllByCustomerId(customerId).stream()
                .map(this::toDto)
                .toList();
    }

    public CustomerSiteDto getCustomerSiteById(UUID id) {
        return customerSiteRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Customer site not found with id: " + id));
    }

    public UUID createCustomerSite(CreateCustomerSiteCommand  command) {
        validate(command);
        CustomerSite customerSite = CustomerSite.create(
            generateSiteNumber(),
            command.customerId(),
            command.siteName(),
            command.addressLine1(),
            command.addressLine2(),
            command.city(),
            command.stateRegion(),
            command.postalCode(),
            command.countryCode(),
            command.timezone(),
            command.accessInstructions()
        );
        CustomerSite saved = customerSiteRepository.save(customerSite);
        return saved.getId();
    }

    private CustomerSiteDto toDto(CustomerSite customerSite) {
        return new CustomerSiteDto(
            customerSite.getId(),
            customerSite.getSiteNumber(),
            customerSite.getCustomerId(),
            customerSite.getSiteName(),
            customerSite.getStatus(),
            customerSite.getAddressLine1(),
            customerSite.getAddressLine2(),
            customerSite.getCity(),
            customerSite.getStateRegion(),
            customerSite.getPostalCode(),
            customerSite.getCountryCode(),
            customerSite.getTimezone(),
            customerSite.getAccessInstructions(),
            customerSite.getVersion(),
            customerSite.getCreatedAt().toString()
        );
    }

    private void validate(CreateCustomerSiteCommand command) {

            Customer customer = customerRepository.findById(command.customerId()).orElse(null);
            if (customer == null) {
                throw new ValidationException(Map.of(
                    "customerId", new String[]{"Customer with id " + command.customerId() + " does not exist."}
                )  );
            }
            
            if(customer.getStatus() != CustomerStatus.ACTIVE){
                throw new ValidationException(Map.of(
                    "customerId", new String[]{"Customer with id " + command.customerId() + " is not active."}
                )  );
            }
            
            customerSiteRepository.findAllByCustomerId(command.customerId()).stream()
                .filter(site -> site.getSiteName().equalsIgnoreCase(command.siteName()))
                .findAny()
                .ifPresent(site -> {
                    throw new ValidationException(Map.of(
                        "siteName", new String[]{"Site name must be unique for the customer."}
                    ));
                });


    }


    private String generateSiteNumber() {
        return "SITE-" + System.currentTimeMillis();
    }


    
    
}
