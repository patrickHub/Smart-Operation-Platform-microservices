package ch.smart.operations.platform.customer.application.ports;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import ch.smart.operations.platform.customer.domain.entities.Customer;

public interface CustomerRepository {
    List<Customer> findAll();
    Optional<Customer> findById(UUID id);
    Customer save(Customer customer);
}
