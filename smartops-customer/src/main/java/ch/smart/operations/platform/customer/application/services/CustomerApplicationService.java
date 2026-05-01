package ch.smart.operations.platform.customer.application.services;


import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ch.smart.operations.platform.customer.application.commands.CreateCustomerCommand;
import ch.smart.operations.platform.customer.application.dtos.CustomerDto;
import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;


@Service
public class CustomerApplicationService {

    private final CustomerRepository customerRepository;


    public CustomerApplicationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<CustomerDto> getAllCustomers(){
        return customerRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .toList();
    }

    public CustomerDto getCustomerById(UUID id){
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Customer not found with id: " + id));
        return toDto(customer);
    }

    public UUID createCustomer(CreateCustomerCommand command){

        validate(command);

        Customer customer = Customer.create(
            generateCustomerNumber(),
            command.legalName(), 
            command.displayName(),
            command.taxIdentifier(), 
            command.industry(),
            command.notes()
        );

        Customer saved = customerRepository.save(customer);
        return saved.getId();
       
    }

    private CustomerDto toDto(Customer customer){
        return new CustomerDto(customer.getId(), 
                    customer.getCustomerNumber(),
                    customer.getLegalName(), 
                    customer.getDisplayName(), 
                    customer.getStatus(), 
                    customer.getTaxIdentifier(), 
                    customer.getIndustry(), 
                    customer.getNotes()
                );
    }

    private void validate(CreateCustomerCommand command) {
         
        if(command.legalName().equalsIgnoreCase(command.displayName())){
            throw new ValidationException(
                Map.of("displayName", new String[]{"Display name must be different from legal name"})
            );
        }   
    }


     private String generateCustomerNumber() {
        return "CUST-" + System.currentTimeMillis();
    }

    
}
