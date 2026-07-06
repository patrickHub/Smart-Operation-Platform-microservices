package ch.smart.operations.platform.customer.infrastructure.persistence.adapters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.smart.operations.platform.customer.application.ports.CustomerRepository;
import ch.smart.operations.platform.customer.domain.entities.Customer;
import ch.smart.operations.platform.customer.infrastructure.persistence.entities.CustomerJpaEntity;
import ch.smart.operations.platform.customer.infrastructure.persistence.repositories.CustomerJpaRepository;


@Repository
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;


    public CustomerRepositoryAdapter(CustomerJpaRepository customerJpaRepository) {
        this.customerJpaRepository = customerJpaRepository;
    }


    @Override
    public List<Customer> findAll() {
        return customerJpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Customer> findById(UUID id) {

        return customerJpaRepository.findById(id).map(this::toDomain);
        
    }

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity saved = customerJpaRepository.save(toJpa(customer));
        return toDomain(saved);
    }


    private Customer toDomain(CustomerJpaEntity entity) {
        return new Customer(entity.getId(),
                    entity.getCustomerNumber(), 
                    entity.getLegalName(), 
                    entity.getDisplayName(),
                    entity.getStatus(),
                    entity.getTaxIdentifier(),
                    entity.getIndustry(),
                    entity.getNotes()
                );
    }

    private CustomerJpaEntity toJpa(Customer customer) {

        CustomerJpaEntity entity = new CustomerJpaEntity();
        entity.setId(customer.getId());
        entity.setCustomerNumber(customer.getCustomerNumber());
        entity.setLegalName(customer.getLegalName());
        entity.setDisplayName(customer.getDisplayName());
        entity.setStatus(customer.getStatus());
        entity.setTaxIdentifier(customer.getTaxIdentifier());
        entity.setIndustry(customer.getIndustry());
        entity.setNotes(customer.getNotes());

        return entity;
    
    }
    
}
