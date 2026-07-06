package ch.smart.operations.platform.identity.application.services;

import ch.smart.operations.platform.identity.infrastructure.persistence.entities.IdentityUserJpaEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long accessTokenValiditySeconds;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${smartops.security.jwt.issuer}") String issuer,
            @Value("${smartops.security.jwt.access-token-validity-seconds}") long accessTokenValiditySeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public String generateAccessToken(IdentityUserJpaEntity user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(accessTokenValiditySeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("preferred_username", user.getUsername())
                .claim("roles", user.getRoles()
                        .stream()
                        .map(Enum::name)
                        .toList())
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public long getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }
}
