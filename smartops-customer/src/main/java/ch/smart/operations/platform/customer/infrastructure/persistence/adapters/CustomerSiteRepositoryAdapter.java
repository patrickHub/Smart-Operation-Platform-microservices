package ch.smart.operations.platform.customer.infrastructure.persistence.adapters;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import ch.smart.operations.platform.customer.application.ports.CustomerSiteRepository;
import ch.smart.operations.platform.customer.domain.entities.CustomerSite;
import ch.smart.operations.platform.customer.infrastructure.persistence.entities.CustomerSiteJpaEntity;
import ch.smart.operations.platform.customer.infrastructure.persistence.repositories.CustomerSiteJpaRepository;

@Repository
public class CustomerSiteRepositoryAdapter implements CustomerSiteRepository {

    private final CustomerSiteJpaRepository customerSiteJpaRepository;


    public CustomerSiteRepositoryAdapter(CustomerSiteJpaRepository customerSiteJpaRepository) {
        this.customerSiteJpaRepository = customerSiteJpaRepository;
    }


    @Override
    public List<CustomerSite> findAll() {

        List<CustomerSiteJpaEntity> entities = customerSiteJpaRepository.findAll();
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }
        

    @Override
    public List<CustomerSite> findAllByCustomerId(UUID customerId) {
        return customerSiteJpaRepository.findAllByCustomerId(customerId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<CustomerSite> findById(UUID id) {
        return  customerSiteJpaRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public CustomerSite save(CustomerSite customerSite) {
    
        CustomerSiteJpaEntity saved = customerSiteJpaRepository.save(toJpa(customerSite));
        return toDomain(saved);
    }

    private CustomerSite toDomain(CustomerSiteJpaEntity entity) {
        
        CustomerSite customerSite = new CustomerSite(
            entity.getId(),
            entity.getSiteNumber(),
            entity.getCustomerId(),
            entity.getSiteName(),
            entity.getStatus(),
            entity.getAddressLine1(),
            entity.getAddressLine2(),
            entity.getCity(),
            entity.getStateRegion(),
            entity.getPostalCode(),
            entity.getCountryCode(),
            entity.getTimezone(),
            entity.getAccessInstructions(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
        customerSite.setVersion(entity.getVersion());
        return customerSite;

    }

    private CustomerSiteJpaEntity toJpa(CustomerSite customerSite){
        CustomerSiteJpaEntity entity = new CustomerSiteJpaEntity();
        entity.setId(customerSite.getId());
        entity.setSiteNumber(customerSite.getSiteNumber());
        entity.setCustomerId(customerSite.getCustomerId());
        entity.setSiteName(customerSite.getSiteName());
        entity.setStatus(customerSite.getStatus());
        entity.setAddressLine1(customerSite.getAddressLine1());
        entity.setAddressLine2(customerSite.getAddressLine2());
        entity.setCity(customerSite.getCity());
        entity.setStateRegion(customerSite.getStateRegion());
        entity.setPostalCode(customerSite.getPostalCode());
        entity.setCountryCode(customerSite.getCountryCode());
        entity.setTimezone(customerSite.getTimezone());
        entity.setAccessInstructions(customerSite.getAccessInstructions());
        entity.setCreatedAt(customerSite.getCreatedAt());
        entity.setUpdatedAt(customerSite.getUpdatedAt());

        return entity;
    }

    
    
}
