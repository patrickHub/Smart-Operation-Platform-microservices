package ch.smart.operations.platform.billing.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BillingOutboxEventStatus {
    PENDING("Pending"),
    PUBLISHED("Published"),
    FAILED("Failed");

    private final String value;

    BillingOutboxEventStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static BillingOutboxEventStatus fromValue(String value) {
        for (BillingOutboxEventStatus type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
