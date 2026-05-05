package ch.smart.operations.platform.billing.application.ports;

import ch.smart.operations.platform.billing.domain.entities.PricingPolicy;

import java.time.LocalDate;
import java.util.Optional;

public interface PricingPolicyRepository {
    Optional<PricingPolicy> findActiveByWorkType(String workType, LocalDate date);
}