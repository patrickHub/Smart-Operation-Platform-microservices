package ch.smart.operations.platform.billing.application.services;

import ch.smart.operations.platform.billing.application.ports.BillingOutboxEventRepository;
import ch.smart.operations.platform.billing.domain.entities.BillingOutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingOutboxEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(BillingOutboxEventPublisher.class);

    private final BillingOutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final int maxAttempts;
    private final boolean enabled;

    public BillingOutboxEventPublisher(
            BillingOutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${smartops.kafka.topics.billing-events:smartops.billing.events}") String topic,
            @Value("${smartops.outbox.max-attempts:5}") int maxAttempts,
            @Value("${smartops.outbox.enabled:true}") boolean enabled
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.maxAttempts = maxAttempts;
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${smartops.outbox.polling-rate-ms:600000}")
    @Transactional
    public void publishPendingEvents() {
        if (!enabled) {
            return;
        }

        var events = outboxEventRepository.findPendingEvents(maxAttempts);

        if (events.isEmpty()) {
            logger.info("No pending billing outbox events found");
            return;
        }

        logger.info("Publishing {} billing outbox event(s)", events.size());

        for (BillingOutboxEvent event : events) {
            publishOne(event);
        }
    }

    private void publishOne(BillingOutboxEvent event) {
        try {
            kafkaTemplate.send(
                    topic,
                    event.getEventKey(),
                    event.getEventPayload()
            ).get();

            outboxEventRepository.save(event.published());

            logger.info(
                    "Published billing outbox event id={}, type={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId()
            );

        } catch (Exception ex) {
            logger.error(
                    "Failed to publish billing outbox event id={}, type={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId(),
                    ex
            );

            if (event.getRetryCount() + 1 >= maxAttempts) {
                outboxEventRepository.save(event.failed());
            } else {
                outboxEventRepository.save(event.retryPending());
            }
        }
    }
}