package ch.smart.operations.platform.identity.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserRole {
    ADMIN("Admin"),
    SUPPORT_AGENT("Support_agent"),
    DISPATCHER("Dispatcher"),
    TECHNICIAN("Technician"),
    BILLING_MANAGER("Billing_manager");

    private final String value;

    UserRole(String value){
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static UserRole fromValue(String value) {
        for (UserRole role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidRole: " + value);
    }

}
