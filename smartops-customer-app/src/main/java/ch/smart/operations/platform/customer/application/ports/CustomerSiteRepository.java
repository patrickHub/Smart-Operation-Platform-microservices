package ch.smart.operations.platform.customer.application.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.smart.operations.platform.customer.domain.entities.CustomerSite;

public interface CustomerSiteRepository {

    List<CustomerSite> findAll();
    List<CustomerSite> findAllByCustomerId(UUID customerId);
    Optional<CustomerSite> findById(UUID id);
    CustomerSite save(CustomerSite customerSite);

}
