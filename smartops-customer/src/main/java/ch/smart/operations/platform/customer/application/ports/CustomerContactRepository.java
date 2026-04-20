package ch.smart.operations.platform.customer.application.ports;

import ch.smart.operations.platform.customer.domain.entities.CustomerContact;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerContactRepository {
    CustomerContact save(CustomerContact contact);
    boolean existsByCustomerIdAndEmail(UUID customerId, String email);
    boolean existsPrimaryContact(UUID customerId);
    List<CustomerContact> findByCustomerId(UUID customerId);
    Optional<CustomerContact> findById(UUID id);
}