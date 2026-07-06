package ch.smart.operations.platform.shared.security;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Instant;
import java.util.List;

public class InternalServiceTokenProvider {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final String serviceName;
    private final long tokenValiditySeconds;

    public InternalServiceTokenProvider(
            JwtEncoder jwtEncoder,
            String issuer,
            String serviceName,
            long tokenValiditySeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.serviceName = serviceName;
        this.tokenValiditySeconds = tokenValiditySeconds;
    }

    public String createAccessToken() {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(tokenValiditySeconds);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(serviceName)
                .claim("preferred_username", serviceName)
                .claim("roles", List.of("SERVICE"))
                .claim("token_type", "service")
                .claim("service_name", serviceName)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public String authorizationHeader() {
        return "Bearer " + createAccessToken();
    }
}