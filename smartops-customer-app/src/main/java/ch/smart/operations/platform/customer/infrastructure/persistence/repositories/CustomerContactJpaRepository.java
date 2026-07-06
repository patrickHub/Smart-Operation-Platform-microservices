package ch.smart.operations.platform.customer.infrastructure.persistence.repositories;

import ch.smart.operations.platform.customer.infrastructure.persistence.entities.CustomerContactJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CustomerContactJpaRepository extends JpaRepository<CustomerContactJpaEntity, UUID> {
    boolean existsByCustomerIdAndEmailIgnoreCase(UUID customerId, String email);
    boolean existsByCustomerIdAndIsPrimaryTrue(UUID customerId);
    List<CustomerContactJpaEntity> findByCustomerId(UUID customerId);
    @Query("""
        select c from CustomerContactJpaEntity c
        where c.customerId = :customerId
    """)
    List<CustomerContactJpaEntity> findAllByCustomerId(UUID customerId);
}