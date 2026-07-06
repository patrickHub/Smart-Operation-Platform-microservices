package ch.smart.operations.platform.billing.security;

import ch.smart.operations.platform.shared.security.InternalServiceTokenProvider;
import ch.smart.operations.platform.shared.security.JwtEncoderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;

@Configuration
public class InternalServiceSecurityConfig {

    @Bean
    public JwtEncoder internalJwtEncoder(
            @Value("${smartops.security.jwt.secret}") String secret
    ) {
        return JwtEncoderFactory.hmacSha256Encoder(secret);
    }

    @Bean
    public InternalServiceTokenProvider internalServiceTokenProvider(
            JwtEncoder internalJwtEncoder,
            @Value("${smartops.security.jwt.issuer}") String issuer,
            @Value("${smartops.security.internal.service-name}") String serviceName,
            @Value("${smartops.security.internal.token-validity-seconds:300}") long tokenValiditySeconds
    ) {
        return new InternalServiceTokenProvider(
                internalJwtEncoder,
                issuer,
                serviceName,
                tokenValiditySeconds
        );
    }
}