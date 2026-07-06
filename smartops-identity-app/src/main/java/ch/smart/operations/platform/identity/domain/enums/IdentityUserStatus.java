package ch.smart.operations.platform.identity.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum IdentityUserStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String value;

    IdentityUserStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static IdentityUserStatus fromValue(String value) {
        for (IdentityUserStatus status : values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidStatus: " + value);
    }
}