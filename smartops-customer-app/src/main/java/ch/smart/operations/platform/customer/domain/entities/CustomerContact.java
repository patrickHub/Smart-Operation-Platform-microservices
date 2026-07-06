package ch.smart.operations.platform.customer.domain.entities;

import ch.smart.operations.platform.customer.domain.enums.CustomerContactStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

public class CustomerContact {

    private final UUID id;
    private final UUID customerId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String contactRole;
    private final boolean primary;
    private final CustomerContactStatus status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    public CustomerContact(
            UUID id,
            UUID customerId,
            String firstName,
            String lastName,
            String email,
            String phone,
            String contactRole,
            boolean primary,
            CustomerContactStatus status,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.contactRole = contactRole;
        this.primary = primary;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getContactRole() {
        return contactRole;
    }

    public boolean isPrimary() {
        return primary;
    }

    public CustomerContactStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}