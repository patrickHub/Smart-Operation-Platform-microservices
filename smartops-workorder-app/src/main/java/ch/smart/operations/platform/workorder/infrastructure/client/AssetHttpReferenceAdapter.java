package ch.smart.operations.platform.workorder.infrastructure.client;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import ch.smart.operations.platform.shared.exceptions.DownstreamClientException;
import ch.smart.operations.platform.shared.exceptions.DownstreamServiceUnavailableException;
import ch.smart.operations.platform.shared.security.InternalServiceTokenProvider;
import ch.smart.operations.platform.workorder.application.dtos.AssetSummaryDto;
import ch.smart.operations.platform.workorder.application.ports.AssetReferencePort;

@Component
public class AssetHttpReferenceAdapter implements AssetReferencePort {

    private static final Logger logger = LoggerFactory.getLogger(AssetHttpReferenceAdapter.class);
    private final RestClient restClient;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final InternalServiceTokenProvider internalServiceTokenProvider;


    public AssetHttpReferenceAdapter(RestClient.Builder restClientBuilder,
        @Value("${smartops.services.asset.base-url:http://localhost:8082}") String assetServiceBaseUrl,
        CircuitBreakerFactory<?, ?> circuitBreakerFactory,
        InternalServiceTokenProvider internalServiceTokenProvider
    ) {
        this.restClient = restClientBuilder
                .baseUrl(assetServiceBaseUrl)
                .build();
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.internalServiceTokenProvider = internalServiceTokenProvider;

    }

    @Override
    public Optional<AssetSummaryDto> findAssetSummary(UUID assetId) {
        return circuitBreakerFactory.create("assetService")
                .run(
                        () -> retrieveAssetSummary(assetId),
                        throwable -> handleAssetServiceFallback(assetId, throwable)
                );
    }

    private Optional<AssetSummaryDto> retrieveAssetSummary(UUID assetId) {
        try {
            AssetSummaryResponse response = restClient.get()
                    .uri("/internal/v1/assets/{assetId}/summary", assetId)
                    .header(HttpHeaders.AUTHORIZATION, internalServiceTokenProvider.authorizationHeader())
                    .retrieve()
                    .body(AssetSummaryResponse.class);

            if (response == null) {
                return Optional.empty();
            }

            return Optional.of(new AssetSummaryDto(
                    response.id(),
                    response.assetNumber(),
                    response.serialNumber(),
                    response.customerId(),
                    response.siteId(),
                    response.name(),
                    response.status(),
                    response.criticality()
            ));

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }

            if (ex.getStatusCode().is4xxClientError()) {
                throw new DownstreamClientException(
                        "asset-service",
                        ex.getStatusCode(),
                        ex.getResponseBodyAsString()
                );
            }

            if (ex.getStatusCode().is5xxServerError()) {
                throw new DownstreamServiceUnavailableException("asset-service", ex);
            }

            logger.error("Failed to retrieve asset summary for asset {} from Asset Service", assetId, ex);
            throw ex;
        }
    }

    private Optional<AssetSummaryDto> handleAssetServiceFallback(UUID assetId, Throwable throwable) {
        if (throwable instanceof DownstreamClientException downstreamClientException) {
            throw downstreamClientException;
        }

        logger.error("Asset Service is unavailable while retrieving asset summary for asset {}", assetId, throwable);

        throw new DownstreamServiceUnavailableException("asset-service", throwable);
    }

    private record AssetSummaryResponse(
            UUID id,
            String assetNumber,
            String serialNumber,
            UUID customerId,
            UUID siteId,
            String name,
            String status,
            String criticality
    ) {
    }
}
