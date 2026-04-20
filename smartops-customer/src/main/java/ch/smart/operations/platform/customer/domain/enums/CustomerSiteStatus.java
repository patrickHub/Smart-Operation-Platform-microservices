package ch.smart.operations.platform.customer.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CustomerSiteStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String value;

    CustomerSiteStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CustomerSiteStatus fromValue(String value) {
        for (CustomerSiteStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}
