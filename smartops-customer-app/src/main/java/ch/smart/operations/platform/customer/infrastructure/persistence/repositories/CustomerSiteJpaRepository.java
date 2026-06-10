package ch.smart.operations.platform.customer.infrastructure.persistence.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ch.smart.operations.platform.customer.infrastructure.persistence.entities.CustomerSiteJpaEntity;

public interface CustomerSiteJpaRepository extends JpaRepository<CustomerSiteJpaEntity, UUID> 
{   
    @Query("""
        select c from CustomerSiteJpaEntity c
        where c.customerId = :customerId
    """)
    List<CustomerSiteJpaEntity> findAllByCustomerId(UUID customerId);
}
