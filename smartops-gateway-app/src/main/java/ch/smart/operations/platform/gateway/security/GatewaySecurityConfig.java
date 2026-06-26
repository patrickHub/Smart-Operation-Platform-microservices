package ch.smart.operations.platform.gateway.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMethodSecurity
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain gatewaySecurityFilterChain(
        ServerHttpSecurity http,
        Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter,
        GatewayAuthenticationEntryPoint authenticationEntryPoint,
        GatewayAccessDeniedHandler accessDeniedHandler
    ) {
    return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults())
            .exceptionHandling(exceptionHandling -> exceptionHandling
                    .authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeExchange(exchange -> exchange
                    // CORS preflight
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // Swagger public paths
                    .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                    // Public identity endpoints
                    .pathMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()

                    // For now, user creation is ADMIN only
                    .pathMatchers(HttpMethod.POST, "/api/v1/auth/users")
                    .hasRole("ADMIN")

                    // Public actuator endpoints
                    .pathMatchers("/actuator/health", "/actuator/info").permitAll()

                    // CUSTOMER
                    .pathMatchers(HttpMethod.POST, "/api/v1/customers")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.PUT, "/api/v1/customers/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.POST, "/api/v1/customers/*/sites")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.GET, "/api/v1/customers/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER", "BILLING_MANAGER")

                    // ASSET
                    .pathMatchers(HttpMethod.POST, "/api/v1/assets/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.PUT, "/api/v1/assets/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.PATCH, "/api/v1/assets/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.GET, "/api/v1/assets/**", "/api/v1/asset-types/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER", "TECHNICIAN", "BILLING_MANAGER")

                    // WORK ORDER
                    .pathMatchers(HttpMethod.POST, "/api/v1/work-orders")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                    .pathMatchers(HttpMethod.POST, "/api/v1/work-orders/*/assignments")
                    .hasAnyRole("ADMIN", "DISPATCHER")

                    .pathMatchers(HttpMethod.POST, "/api/v1/work-orders/*/accept")
                    .hasRole("TECHNICIAN")

                    .pathMatchers(HttpMethod.POST, "/api/v1/work-orders/*/start")
                    .hasRole("TECHNICIAN")

                    .pathMatchers(HttpMethod.POST, "/api/v1/work-orders/*/complete")
                    .hasRole("TECHNICIAN")

                    .pathMatchers(HttpMethod.POST, "/api/v1/work-orders/*/cancel")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER")

                    .pathMatchers(HttpMethod.GET, "/api/v1/work-orders/**")
                    .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER", "TECHNICIAN", "BILLING_MANAGER")

                    // BILLING
                    .pathMatchers(HttpMethod.GET, "/api/v1/invoices/**")
                    .hasAnyRole("ADMIN", "BILLING_MANAGER")

                    .pathMatchers(HttpMethod.POST, "/api/v1/invoices/*/mark-sent")
                    .hasAnyRole("ADMIN", "BILLING_MANAGER")

                    .pathMatchers(HttpMethod.POST, "/api/v1/invoices/*/cancel")
                    .hasAnyRole("ADMIN", "BILLING_MANAGER")

                    .pathMatchers(HttpMethod.GET, "/api/v1/pricing-policies/**")
                    .hasAnyRole("ADMIN", "BILLING_MANAGER")

                    // NOTIFICATION
                    .pathMatchers(HttpMethod.GET, "/api/v1/notifications/**")
                    .hasRole("ADMIN")

                    .pathMatchers(HttpMethod.POST, "/api/v1/notifications/*/retry")
                    .hasRole("ADMIN")

                    // Internal endpoints should not be open from the frontend.
                    // For now, we should allow only ADMIN until we implement service-to-service tokens.
                    .pathMatchers("/internal/v1/**")
                    .hasRole("ADMIN")

                    // Anything else still requires authentication
                    .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                            .jwtAuthenticationConverter(jwtAuthenticationConverter)
                    )
            )
            .build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${smartops.security.jwt.secret}") String secret
    ) {
        SecretKey key = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );

        return NimbusReactiveJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Object rolesClaim = jwt.getClaims().get("roles");

            if (!(rolesClaim instanceof Collection<?> roles)) {
                return List.of();
            }

            return roles.stream()
                    .map(String::valueOf)
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        });

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}