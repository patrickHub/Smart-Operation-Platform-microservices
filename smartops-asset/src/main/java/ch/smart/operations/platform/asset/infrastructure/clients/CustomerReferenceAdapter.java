package ch.smart.operations.platform.asset.infrastructure.clients;

import java.util.UUID;

import org.springframework.stereotype.Component;

import ch.smart.operations.platform.asset.application.ports.CustomerReferencePort;
import ch.smart.operations.platform.customer.infrastructure.persistence.repositories.CustomerJpaRepository;
import ch.smart.operations.platform.customer.infrastructure.persistence.repositories.CustomerSiteJpaRepository;

@Component
public class CustomerReferenceAdapter implements CustomerReferencePort {
    
    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerSiteJpaRepository customerSiteJpaRepository;

    public CustomerReferenceAdapter(
            CustomerJpaRepository customerJpaRepository,
            CustomerSiteJpaRepository customerSiteJpaRepository
    ) {
        this.customerJpaRepository = customerJpaRepository;
        this.customerSiteJpaRepository = customerSiteJpaRepository;
    }

    @Override
    public boolean customerExists(UUID customerId) {
        return customerJpaRepository.existsById(customerId);
    }

    @Override
    public boolean siteExists(UUID siteId) {
        return customerSiteJpaRepository.existsById(siteId);
    }
}
