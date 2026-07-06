package ch.smart.operations.platform.billing.infrastructure.persistence.repositories;

import ch.smart.operations.platform.billing.infrastructure.persistence.entities.PricingPolicyJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface PricingPolicyJpaRepository extends JpaRepository<PricingPolicyJpaEntity, UUID> {

    Optional<PricingPolicyJpaEntity>
    findFirstByWorkTypeAndActiveTrueAndValidFromLessThanEqualAndValidToIsNullOrderByValidFromDesc(
            String workType,
            LocalDate date
    );

    Optional<PricingPolicyJpaEntity>
    findFirstByWorkTypeAndActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqualOrderByValidFromDesc(
            String workType,
            LocalDate date,
            LocalDate sameDate
    );
}