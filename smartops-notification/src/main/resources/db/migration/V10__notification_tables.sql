CREATE SCHEMA IF NOT EXISTS notification;

CREATE TABLE IF NOT EXISTS notification.notification_templates (
    id UUID PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    subject_template VARCHAR(255) NULL,
    body_template TEXT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS notification.notifications (
    id UUID PRIMARY KEY,
    source_event_id UUID NULL,
    event_type VARCHAR(100) NOT NULL,
    recipient_type VARCHAR(50) NOT NULL,
    recipient_reference VARCHAR(255) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    subject VARCHAR(255) NULL,
    body TEXT NOT NULL,
    deduplication_key VARCHAR(255) NULL UNIQUE,
    scheduled_at TIMESTAMPTZ NULL,
    sent_at TIMESTAMPTZ NULL,
    failure_reason TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE IF NOT EXISTS notification.notification_delivery_attempts (
    id UUID PRIMARY KEY,
    notification_id UUID NOT NULL,
    attempted_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(50) NOT NULL,
    provider_response TEXT NULL,
    error_message TEXT NULL,
    CONSTRAINT fk_delivery_attempt_notification
        FOREIGN KEY (notification_id) REFERENCES notification.notifications(id)
);

CREATE INDEX IF NOT EXISTS idx_notifications_recipient_reference
    ON notification.notifications(recipient_reference);

CREATE INDEX IF NOT EXISTS idx_notifications_status
    ON notification.notifications(status);

CREATE INDEX IF NOT EXISTS idx_notifications_event_type
    ON notification.notifications(event_type);

CREATE INDEX IF NOT EXISTS idx_delivery_attempts_notification_id
    ON notification.notification_delivery_attempts(notification_id);