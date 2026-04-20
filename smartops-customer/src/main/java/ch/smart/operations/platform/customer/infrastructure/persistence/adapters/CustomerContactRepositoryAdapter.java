package ch.smart.operations.platform.customer.infrastructure.persistence.adapters;

import ch.smart.operations.platform.customer.application.ports.CustomerContactRepository;
import ch.smart.operations.platform.customer.domain.entities.CustomerContact;
import ch.smart.operations.platform.customer.infrastructure.persistence.entities.CustomerContactJpaEntity;
import ch.smart.operations.platform.customer.infrastructure.persistence.repositories.CustomerContactJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CustomerContactRepositoryAdapter implements CustomerContactRepository {

    private final CustomerContactJpaRepository repository;

    public CustomerContactRepositoryAdapter(CustomerContactJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public CustomerContact save(CustomerContact contact) {
        CustomerContactJpaEntity entity = toJpa(contact);
        CustomerContactJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existsByCustomerIdAndEmail(UUID customerId, String email) {
        return repository.existsByCustomerIdAndEmailIgnoreCase(customerId, email);
    }

    @Override
    public boolean existsPrimaryContact(UUID customerId) {
        return repository.existsByCustomerIdAndIsPrimaryTrue(customerId);
    }

    @Override
    public List<CustomerContact> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<CustomerContact> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    private CustomerContact toDomain(CustomerContactJpaEntity entity) {
        return new CustomerContact(
                entity.getId(),
                entity.getCustomerId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getContactRole(),
                entity.isPrimary(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CustomerContactJpaEntity toJpa(CustomerContact contact) {
        CustomerContactJpaEntity entity = new CustomerContactJpaEntity();
        entity.setId(contact.getId());
        entity.setCustomerId(contact.getCustomerId());
        entity.setFirstName(contact.getFirstName());
        entity.setLastName(contact.getLastName());
        entity.setEmail(contact.getEmail());
        entity.setPhone(contact.getPhone());
        entity.setContactRole(contact.getContactRole());
        entity.setPrimary(contact.isPrimary());
        entity.setStatus(contact.getStatus());
        entity.setCreatedAt(contact.getCreatedAt());
        entity.setUpdatedAt(contact.getUpdatedAt());
        return entity;
    }
}