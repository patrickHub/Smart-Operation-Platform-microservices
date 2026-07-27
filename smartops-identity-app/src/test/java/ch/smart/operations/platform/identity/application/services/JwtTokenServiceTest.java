package ch.smart.operations.platform.identity.application.services;

import ch.smart.operations.platform.identity.domain.enums.IdentityUserStatus;
import ch.smart.operations.platform.identity.domain.enums.UserRole;
import ch.smart.operations.platform.identity.infrastructure.persistence.entities.IdentityUserJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

    private static final String ISSUER = "smartops-identity";
    private static final String SECRET = "local-dev-secret-change-me-local-dev-secret-change-me";
    private static final long ACCESS_TOKEN_VALIDITY_SECONDS = 3600L;

    private JwtTokenService jwtTokenService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        SecretKey key = new SecretKeySpec(
            SECRET.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );

        JwtEncoder jwtEncoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));

        jwtTokenService = new JwtTokenService(
                jwtEncoder,
                ISSUER,
                ACCESS_TOKEN_VALIDITY_SECONDS
        );

        jwtDecoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Test
    void generateAccessToken_shouldGenerateValidJwtToken() {
        // Arrange
        IdentityUserJpaEntity user = user(
                "admin",
                Set.of(UserRole.ADMIN)
        );

        // Act
        String token = jwtTokenService.generateAccessToken(user);

        // Assert
        assertThat(token).isNotBlank();

        Jwt decodedToken = jwtDecoder.decode(token);

        assertThat(decodedToken.getClaimAsString("iss")).isEqualTo(ISSUER);
        assertThat(decodedToken.getSubject()).isEqualTo(user.getId().toString());
        assertThat(decodedToken.getClaimAsString("preferred_username")).isEqualTo("admin");

        List<String> roles = decodedToken.getClaimAsStringList("roles");
        assertThat(roles).containsExactly("ADMIN");

        assertThat(decodedToken.getIssuedAt()).isNotNull();
        assertThat(decodedToken.getExpiresAt()).isNotNull();
        assertThat(decodedToken.getExpiresAt()).isAfter(decodedToken.getIssuedAt());
    }

    @Test
    void generateAccessToken_shouldIncludeAllUserRoles() {
        // Arrange
        IdentityUserJpaEntity user = user(
                "multi.role.user",
                Set.of(UserRole.ADMIN, UserRole.SUPPORT_AGENT)
        );

        // Act
        String token = jwtTokenService.generateAccessToken(user);

        // Assert
        Jwt decodedToken = jwtDecoder.decode(token);

        List<String> roles = decodedToken.getClaimAsStringList("roles");

        assertThat(roles)
                .containsExactlyInAnyOrder("ADMIN", "SUPPORT_AGENT");
    }

    @Test
    void getAccessTokenValiditySeconds_shouldReturnConfiguredValidity() {
        // Act / Assert
        assertThat(jwtTokenService.getAccessTokenValiditySeconds())
                .isEqualTo(ACCESS_TOKEN_VALIDITY_SECONDS);
    }

    private IdentityUserJpaEntity user(String username, Set<UserRole> roles) {
        OffsetDateTime now = OffsetDateTime.now();

        return new IdentityUserJpaEntity(
                UUID.randomUUID(),
                username,
                "encoded-password",
                "Test",
                "User",
                "Test User",
                username + "@smartops.local",
                "Platform User",
                IdentityUserStatus.ACTIVE,
                new LinkedHashSet<>(roles),
                now,
                now
        );
    }
}