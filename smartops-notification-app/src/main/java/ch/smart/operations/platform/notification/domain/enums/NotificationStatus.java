package ch.smart.operations.platform.notification.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum NotificationStatus {
    PENDING("Pending"),
    SENT("Sent"),
    FAILED("Failed"),
    CANCELLED("Cancelled");

    private final String value;

    NotificationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static NotificationStatus fromValue(String value) {
        for (NotificationStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidNotificationStatus: " + value);
    }
}