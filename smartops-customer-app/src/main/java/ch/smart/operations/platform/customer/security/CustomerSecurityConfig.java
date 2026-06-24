package ch.smart.operations.platform.customer.security;

import ch.smart.operations.platform.shared.security.JwtDecoderFactory;
import ch.smart.operations.platform.shared.security.JwtRoleConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class CustomerSecurityConfig {

    @Bean
    public SecurityFilterChain customerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/customers")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        .requestMatchers(HttpMethod.PUT, "/api/v1/customers/**")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        .requestMatchers(HttpMethod.POST, "/api/v1/customers/*/sites")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        .requestMatchers(HttpMethod.POST, "/api/v1/customers/*/contacts")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        .requestMatchers(HttpMethod.GET, "/api/v1/customers/**")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER", "BILLING_MANAGER")

                        // For now: internal endpoints require authentication.
                        // Later we will replace this with service-to-service tokens.
                        .requestMatchers("/internal/v1/**")
                        .authenticated()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                )
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${smartops.security.jwt.secret}") String secret
    ) {
        return JwtDecoderFactory.hmacSha256Decoder(secret);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new JwtRoleConverter());
        return converter;
    }
}