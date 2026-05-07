package ch.smart.operations.platform.notification.domain.entities;

import ch.smart.operations.platform.notification.domain.enums.NotificationChannel;

import java.time.OffsetDateTime;
import java.util.UUID;

public class NotificationTemplate {

    private final UUID id;
    private final String code;
    private final String eventType;
    private final NotificationChannel channel;
    private final String subjectTemplate;
    private final String bodyTemplate;
    private final boolean active;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public NotificationTemplate(
            UUID id,
            String code,
            String eventType,
            NotificationChannel channel,
            String subjectTemplate,
            String bodyTemplate,
            boolean active,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.code = code;
        this.eventType = eventType;
        this.channel = channel;
        this.subjectTemplate = subjectTemplate;
        this.bodyTemplate = bodyTemplate;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public String getCode() { return code; }
    public String getEventType() { return eventType; }
    public NotificationChannel getChannel() { return channel; }
    public String getSubjectTemplate() { return subjectTemplate; }
    public String getBodyTemplate() { return bodyTemplate; }
    public boolean isActive() { return active; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}