package ch.smart.operations.platform.notification.infrastructure.messaging;

import ch.smart.operations.platform.notification.application.services.NotificationApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class SmartOpsEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(SmartOpsEventConsumer.class);

    private final NotificationApplicationService notificationApplicationService;

    public SmartOpsEventConsumer(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }

    @KafkaListener(
            topics = "${smartops.kafka.topics.workorder-events:smartops.workorder.events}",
            groupId = "${smartops.kafka.consumer.notification-group:smartops-notification}"
    )
    public void consumeWorkOrderEvent(String payload) {
        logger.info("Received workorder event: {}", payload);
        notificationApplicationService.handleEvent(payload);
    }

    @KafkaListener(
            topics = "${smartops.kafka.topics.billing-events:smartops.billing.events}",
            groupId = "${smartops.kafka.consumer.notification-group:smartops-notification}"
    )
    public void consumeBillingEvent(String payload) {
        logger.info("Received billing event: {}", payload);
        notificationApplicationService.handleEvent(payload);
    }
}
