package ch.smart.operations.platform.notification.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DeliveryAttemptStatus {
    SUCCESS("Success"),
    FAILED("Failed");

    private final String value;

    DeliveryAttemptStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DeliveryAttemptStatus fromValue(String value) {
        for (DeliveryAttemptStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidDeliveryAttemptStatus: " + value);
    }
}