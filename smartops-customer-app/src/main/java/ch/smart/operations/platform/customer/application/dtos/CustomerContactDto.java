package ch.smart.operations.platform.customer.application.dtos;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CustomerContactDto(
        UUID id,
        UUID customerId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String contactRole,
        boolean isPrimary,
        String status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}