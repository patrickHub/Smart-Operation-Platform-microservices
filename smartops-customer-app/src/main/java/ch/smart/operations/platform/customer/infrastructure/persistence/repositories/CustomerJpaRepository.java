package ch.smart.operations.platform.customer.infrastructure.persistence.repositories;

import java.util.UUID;
import ch.smart.operations.platform.customer.infrastructure.persistence.entities.CustomerJpaEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    
}
