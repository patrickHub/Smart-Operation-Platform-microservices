package ch.smart.operations.platform.workorder.application.services;

import ch.smart.operations.platform.workorder.application.ports.OutboxEventRepository;
import ch.smart.operations.platform.workorder.domain.entities.OutboxEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkOrderOutboxEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(WorkOrderOutboxEventPublisher.class);

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate; // Used to communicate with Kafka cluster. The key is String (eventKey) and the value is String (eventPayload).
    private final String topic;
    private final int maxAttempts;
    private final boolean enabled;

    public WorkOrderOutboxEventPublisher(
            OutboxEventRepository outboxEventRepository,
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${smartops.kafka.topics.workorder-events:smartops.workorder.events}") String topic,
            @Value("${smartops.outbox.max-attempts:5}") int maxAttempts,
            @Value("${smartops.outbox.enabled:true}") boolean enabled
    ) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.maxAttempts = maxAttempts;
        this.enabled = enabled;
    }

    @Scheduled(fixedDelayString = "${smartops.outbox.polling-rate-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        if (!enabled) {
            return;
        }

        var events = outboxEventRepository.findPendingEvents(maxAttempts);

        if (events.isEmpty()) {
            logger.info("No pending workorder outbox events found");
            return;
        }

        logger.info("Publishing {} workorder outbox event(s)", events.size());

        for (OutboxEvent event : events) {
            publishOne(event);
        }
    }

    private void publishOne(OutboxEvent event) {
        try {
            kafkaTemplate.send(
                    topic, // Using topic-level partitioning. All events for the same aggregateId will go to the same partition, preserving order.
                    event.getEventKey(), // Using eventKey as the Kafka message key ensures that all events for the same aggregateId go to the same partition, preserving order.
                    event.getEventPayload()
            ).get(); // This is a synchronous call. It forces the code to wait until Kafka confirms it received the message. If Kafka is down, this will throw an exception, triggering our retry logic.

            outboxEventRepository.save(event.published());

            logger.info(
                    "Published workorder outbox event id={}, type={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateId()
            );

        } catch (Exception ex) {
            logger.error(
                    "Failed to publish workorder outbox event id={}, type={}, aggregateId={}",
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