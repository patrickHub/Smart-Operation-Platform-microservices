package ch.smart.operations.platform.billing.infrastructure.persistence.adapters;

import ch.smart.operations.platform.billing.application.ports.PricingPolicyRepository;
import ch.smart.operations.platform.billing.domain.entities.PricingPolicy;
import ch.smart.operations.platform.billing.infrastructure.persistence.entities.PricingPolicyJpaEntity;
import ch.smart.operations.platform.billing.infrastructure.persistence.repositories.PricingPolicyJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public class PricingPolicyRepositoryAdapter implements PricingPolicyRepository {

    private final PricingPolicyJpaRepository repository;

    public PricingPolicyRepositoryAdapter(PricingPolicyJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<PricingPolicy> findActiveByWorkType(String workType, LocalDate date) {
        Optional<PricingPolicyJpaEntity> openEnded =
                repository.findFirstByWorkTypeAndActiveTrueAndValidFromLessThanEqualAndValidToIsNullOrderByValidFromDesc(
                        workType,
                        date
                );

        if (openEnded.isPresent()) {
            return openEnded.map(this::toDomain);
        }

        return repository
                .findFirstByWorkTypeAndActiveTrueAndValidFromLessThanEqualAndValidToGreaterThanEqualOrderByValidFromDesc(
                        workType,
                        date,
                        date
                )
                .map(this::toDomain);
    }

    private PricingPolicy toDomain(PricingPolicyJpaEntity entity) {
        return new PricingPolicy(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getWorkType(),
                entity.getLaborRate(),
                entity.getDefaultCurrency(),
                entity.isActive(),
                entity.getValidFrom(),
                entity.getValidTo(),
                entity.getRulesJson(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getUpdatedAt(),
                entity.getUpdatedBy()
        );
    }
}