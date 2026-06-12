package ch.smart.operations.platform.notification.api.controllers;

import ch.smart.operations.platform.notification.application.dtos.NotificationDeliveryAttemptDto;
import ch.smart.operations.platform.notification.application.dtos.NotificationDto;
import ch.smart.operations.platform.notification.application.services.NotificationApplicationService;
import ch.smart.operations.platform.notification.domain.enums.NotificationStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationApplicationService service;

    public NotificationController(NotificationApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<NotificationDto> getNotification(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(service.getNotification(notificationId));
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> searchNotifications(
            @RequestParam(required = false) String recipientReference,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) String eventType
    ) {
        return ResponseEntity.ok(
                service.searchNotifications(recipientReference, status, eventType)
        );
    }

    @PostMapping("/{notificationId}/retry")
    public ResponseEntity<Void> retry(@PathVariable UUID notificationId) {
        service.retryFailedNotification(notificationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{notificationId}/delivery-attempts")
    public ResponseEntity<List<NotificationDeliveryAttemptDto>> listAttempts(
            @PathVariable UUID notificationId
    ) {
        return ResponseEntity.ok(service.listDeliveryAttempts(notificationId));
    }
}