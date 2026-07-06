package ch.smart.operations.platform.notification.application.services;

import ch.smart.operations.platform.notification.application.dtos.NotificationDeliveryAttemptDto;
import ch.smart.operations.platform.notification.application.dtos.NotificationDto;
import ch.smart.operations.platform.notification.application.ports.NotificationDeliveryAttemptRepository;
import ch.smart.operations.platform.notification.application.ports.NotificationRepository;
import ch.smart.operations.platform.notification.application.ports.NotificationTemplateRepository;
import ch.smart.operations.platform.notification.domain.entities.Notification;
import ch.smart.operations.platform.notification.domain.entities.NotificationDeliveryAttempt;
import ch.smart.operations.platform.notification.domain.entities.NotificationTemplate;
import ch.smart.operations.platform.notification.domain.enums.NotificationChannel;
import ch.smart.operations.platform.notification.domain.enums.NotificationStatus;
import ch.smart.operations.platform.notification.domain.enums.RecipientType;
import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class NotificationApplicationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final NotificationDeliveryAttemptRepository attemptRepository;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(NotificationApplicationService.class);

    public NotificationApplicationService(
            NotificationRepository notificationRepository,
            NotificationTemplateRepository templateRepository,
            NotificationDeliveryAttemptRepository attemptRepository,
            ObjectMapper objectMapper
    ) {
        this.notificationRepository = notificationRepository;
        this.templateRepository = templateRepository;
        this.attemptRepository = attemptRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<UUID> handleEvent(String eventPayload) {
        try {
            JsonNode root = objectMapper.readTree(eventPayload);

            String eventType = text(root, "eventType");
            if (eventType == null || eventType.isBlank()) {
                throw new BusinessRuleException("Event payload does not contain eventType.");
            }

            NotificationTemplate template = templateRepository
                    .findActiveByEventTypeAndChannel(eventType, NotificationChannel.INTERNAL)
                    .orElse(null);

            if (template == null) {
                return Optional.empty();
            }

            String recipientReference = resolveRecipientReference(eventType, root);
            RecipientType recipientType = resolveRecipientType(eventType);

            String deduplicationKey = eventType + ":" + sourceAggregateId(root);

            if (notificationRepository.existsByDeduplicationKey(deduplicationKey)) {
                return Optional.empty();
            }

            String subject = render(template.getSubjectTemplate(), root);
            String body = render(template.getBodyTemplate(), root);

            UUID sourceEventId = null;

            Notification notification = Notification.pending(
                    sourceEventId,
                    eventType,
                    recipientType,
                    recipientReference,
                    template.getChannel(),
                    subject,
                    body,
                    deduplicationKey
            );

            Notification saved = notificationRepository.save(notification);

            dispatch(saved);

            return Optional.of(saved.getId());

        } catch (Exception ex) {
            throw new BusinessRuleException("Failed to handle notification event: " + ex.getMessage());
        }
    }

    public void retryFailedNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id " + notificationId));

        Notification retry = notification.retry();
        Notification saved = notificationRepository.save(retry);

        dispatch(saved);
    }

    @Transactional(readOnly = true)
    public NotificationDto getNotification(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Notification not found with id " + notificationId));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> searchNotifications(
            String recipientReference,
            NotificationStatus status,
            String eventType
    ) {
        List<Notification> notifications;

        if (recipientReference != null && !recipientReference.isBlank()) {
            notifications = notificationRepository.findByRecipientReference(recipientReference);
        } else if (status != null) {
            notifications = notificationRepository.findByStatus(status);
        } else if (eventType != null && !eventType.isBlank()) {
            notifications = notificationRepository.findByEventType(eventType);
        } else {
            notifications = notificationRepository.findAll();
        }

        return notifications.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationDeliveryAttemptDto> listDeliveryAttempts(UUID notificationId) {
        notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found with id " + notificationId));

        return attemptRepository.findByNotificationId(notificationId)
                .stream()
                .map(this::toAttemptDto)
                .toList();
    }

    private void dispatch(Notification notification) {
        try {
            /*
             * Simulated provider.
             * Later we can replace this with EmailProviderPort, SmsProviderPort, etc.
             */
            String providerResponse = "Simulated " + notification.getChannel() + " delivery successful";

            attemptRepository.save(
                    NotificationDeliveryAttempt.success(notification.getId(), providerResponse)
            );

            notificationRepository.save(notification.sent());

            logger.info("Notification {} sent successfully. Provider response: {}", notification.getId(), providerResponse);

        } catch (Exception ex) {
            attemptRepository.save(
                    NotificationDeliveryAttempt.failed(notification.getId(), ex.getMessage())
            );

            notificationRepository.save(notification.failed(ex.getMessage()));
        }
    }

    private String resolveRecipientReference(String eventType, JsonNode root) {
        return switch (eventType) {
            case "WORK_ORDER_ASSIGNED",
                 "WORK_ORDER_ACCEPTED",
                 "WORK_ORDER_STARTED",
                 "WORK_ORDER_COMPLETED" -> text(root, "assignedTechnicianUserId") != null
                    ? text(root, "assignedTechnicianUserId")
                    : "internal-operations-team";

            case "INVOICE_GENERATED" -> text(root, "customerId") != null
                    ? text(root, "customerId")
                    : "billing-team";

            default -> "internal-operations-team";
        };
    }

    private RecipientType resolveRecipientType(String eventType) {
        if ("INVOICE_GENERATED".equals(eventType)) {
            return RecipientType.CUSTOMER_CONTACT;
        }
        return RecipientType.USER;
    }

    private String sourceAggregateId(JsonNode root) {
        String workOrderId = text(root, "workOrderId");
        if (workOrderId != null) {
            return workOrderId;
        }

        String invoiceId = text(root, "invoiceId");
        if (invoiceId != null) {
            return invoiceId;
        }

        return UUID.randomUUID().toString();
    }

    private String render(String template, JsonNode payload) {
        if (template == null) {
            return null;
        }

        String rendered = template;
        Iterator<String> fieldNames = payload.fieldNames();

        while (fieldNames.hasNext()) {
            String field = fieldNames.next();
            String value = payload.get(field).isNull() ? "" : payload.get(field).asText();
            rendered = rendered.replace("{{" + field + "}}", value);
        }

        return rendered;
    }

    private String text(JsonNode node, String field) {
        if (node == null || !node.has(field) || node.get(field).isNull()) {
            return null;
        }
        return node.get(field).asText();
    }

    private NotificationDto toDto(Notification n) {
        return new NotificationDto(
                n.getId(),
                n.getSourceEventId(),
                n.getEventType(),
                n.getRecipientType().name(),
                n.getRecipientReference(),
                n.getChannel().name(),
                n.getStatus().name(),
                n.getSubject(),
                n.getBody(),
                n.getDeduplicationKey(),
                n.getScheduledAt(),
                n.getSentAt(),
                n.getFailureReason(),
                n.getCreatedAt(),
                n.getUpdatedAt()
        );
    }

    private NotificationDeliveryAttemptDto toAttemptDto(NotificationDeliveryAttempt a) {
        return new NotificationDeliveryAttemptDto(
                a.getId(),
                a.getNotificationId(),
                a.getAttemptedAt(),
                a.getStatus().name(),
                a.getProviderResponse(),
                a.getErrorMessage()
        );
    }
}