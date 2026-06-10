package ch.smart.operations.platform.customer.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CustomerContactStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String value;

    CustomerContactStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CustomerContactStatus fromValue(String value) {
        for (CustomerContactStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
