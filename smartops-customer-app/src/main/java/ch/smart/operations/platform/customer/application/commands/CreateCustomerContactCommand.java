package ch.smart.operations.platform.customer.application.commands;

import java.util.UUID;

public record CreateCustomerContactCommand(
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String contactRole,
        boolean isPrimary
) {
}