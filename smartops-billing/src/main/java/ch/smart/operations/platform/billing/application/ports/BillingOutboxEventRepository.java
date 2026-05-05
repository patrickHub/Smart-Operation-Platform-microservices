package ch.smart.operations.platform.billing.application.ports;

import ch.smart.operations.platform.billing.domain.entities.BillingOutboxEvent;

public interface BillingOutboxEventRepository {
    BillingOutboxEvent save(BillingOutboxEvent event);
}