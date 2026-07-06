package ch.smart.operations.platform.identity.application.dtos;

import java.util.List;

public record LoginResponseDto(
        String accessToken,
        String tokenType,
        long expiresIn,
        String username,
        List<String> roles
) {
}