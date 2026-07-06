package ch.smart.operations.platform.notification.application.ports;

import ch.smart.operations.platform.notification.domain.entities.NotificationTemplate;
import ch.smart.operations.platform.notification.domain.enums.NotificationChannel;

import java.util.Optional;

public interface NotificationTemplateRepository {
    Optional<NotificationTemplate> findActiveByEventTypeAndChannel(
            String eventType,
            NotificationChannel channel
    );
}