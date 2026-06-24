package ch.smart.operations.platform.asset.infrastructure.clients;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.smart.operations.platform.asset.application.ports.CustomerReferencePort;
import ch.smart.operations.platform.shared.exceptions.DownstreamClientException;
import ch.smart.operations.platform.shared.exceptions.DownstreamServiceUnavailableException;
import ch.smart.operations.platform.shared.security.InternalServiceTokenProvider;

@Component
public class CustomerHttpReferenceAdapter implements CustomerReferencePort {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerHttpReferenceAdapter.class);

    private final RestClient restClient;
    private final CircuitBreakerFactory<?,?> circuitBreakerFactory;
    private final InternalServiceTokenProvider internalServiceTokenProvider;

    public CustomerHttpReferenceAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${smartops.services.customer.base-url:http://localhost:8081}") String customerServiceBaseUrl,
            CircuitBreakerFactory<?,?> circuitBreakerFactory,
            InternalServiceTokenProvider internalServiceTokenProvider
    ) {
        this.restClient = restClientBuilder
                .baseUrl(customerServiceBaseUrl)
                .build();
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.internalServiceTokenProvider = internalServiceTokenProvider;
    }


    @Override
    public boolean customerExists(UUID customerId) {
        return circuitBreakerFactory.create("customerService") // wrappe the entire HTTP call inside the spring cloud circuit breaker calle customerService
            .run(
                    () -> {
                        restClient.get()
                                .uri("/internal/v1/customers/{customerId}/exists", customerId)
                                .header(HttpHeaders.AUTHORIZATION, internalServiceTokenProvider.authorizationHeader())
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                    String body = new String(
                                            response.getBody().readAllBytes(),
                                            StandardCharsets.UTF_8
                                    );

                                    throw new DownstreamClientException(
                                            "customer-service",
                                            response.getStatusCode(),
                                            body
                                    );
                                })
                                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                    throw new DownstreamServiceUnavailableException("customer-service");
                                })
                                .toBodilessEntity();

                        return true;
                    },
                    this::handleCustomerServiceFallback // this will runs if the network call times out, if a 4xx/5xx HTTP exception is thrown, if the circuit breaker is already open, or if the above .onStatus() interceptor fires
            );
    }


    @Override
    public boolean siteExists(UUID customerId, UUID siteId) {
        return circuitBreakerFactory.create("customerService")
            .run(
                    () -> {
                        restClient.get()
                                .uri("/internal/v1/customers/{customerId}/sites/{siteId}/exists", customerId, siteId)
                                .header(HttpHeaders.AUTHORIZATION, internalServiceTokenProvider.authorizationHeader())
                                .retrieve()
                                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                    String body = new String(
                                            response.getBody().readAllBytes(),
                                            StandardCharsets.UTF_8
                                    );

                                    throw new DownstreamClientException(
                                            "customer-service",
                                            response.getStatusCode(),
                                            body
                                    );
                                })
                                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                                    throw new DownstreamServiceUnavailableException("customer-service");
                                })
                                .toBodilessEntity();

                        return true;
                    },
                    this::handleCustomerServiceFallback
            );
    }


    private Boolean handleCustomerServiceFallback(Throwable throwable) {
        if (throwable instanceof DownstreamClientException downstreamClientException) {
            throw downstreamClientException;
        }

        throw new DownstreamServiceUnavailableException("customer-service", throwable);
    }
}
