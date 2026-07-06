package ch.smart.operations.platform.notification.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RecipientType {
    USER("User"),
    CUSTOMER_CONTACT("Customer_Contact");

    private final String value;

    RecipientType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RecipientType fromValue(String value) {
        for (RecipientType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid InvalidRecipientType: " + value);
    }
}