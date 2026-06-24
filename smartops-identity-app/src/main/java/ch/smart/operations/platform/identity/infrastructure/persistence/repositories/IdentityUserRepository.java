package ch.smart.operations.platform.identity.infrastructure.persistence.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ch.smart.operations.platform.identity.infrastructure.persistence.entities.IdentityUserJpaEntity;

import java.util.Optional;
import java.util.UUID;

public interface IdentityUserRepository extends JpaRepository<IdentityUserJpaEntity, UUID> {

    Optional<IdentityUserJpaEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
