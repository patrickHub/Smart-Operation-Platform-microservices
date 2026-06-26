package ch.smart.operations.platform.workorder.security;

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
public class WorkOrderSecurityConfig {

    @Bean
    public SecurityFilterChain workorderSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                        .requestMatchers("/internal/v1/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/v1/work-orders")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT")

                        .requestMatchers(HttpMethod.POST, "/api/v1/work-orders/*/assignments")
                        .hasAnyRole("ADMIN", "DISPATCHER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/work-orders/*/accept")
                        .hasRole("TECHNICIAN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/work-orders/*/start")
                        .hasRole("TECHNICIAN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/work-orders/*/complete")
                        .hasRole("TECHNICIAN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/work-orders/*/cancel")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER")

                        .requestMatchers(HttpMethod.GET, "/api/v1/work-orders/**")
                        .hasAnyRole("ADMIN", "SUPPORT_AGENT", "DISPATCHER", "TECHNICIAN", "BILLING_MANAGER")

                        .requestMatchers("/internal/v1/**").hasAnyRole("SERVICE", "ADMIN")

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