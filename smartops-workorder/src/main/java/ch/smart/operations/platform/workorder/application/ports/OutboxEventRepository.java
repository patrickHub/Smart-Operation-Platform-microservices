package ch.smart.operations.platform.workorder.application.ports;

import ch.smart.operations.platform.workorder.domain.entities.OutboxEvent;

public interface OutboxEventRepository {
    OutboxEvent save(OutboxEvent event);
}