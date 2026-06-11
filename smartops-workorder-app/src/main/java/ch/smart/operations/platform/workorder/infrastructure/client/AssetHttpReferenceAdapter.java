package ch.smart.operations.platform.workorder.infrastructure.client;

import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import ch.smart.operations.platform.workorder.application.dtos.AssetSummaryDto;
import ch.smart.operations.platform.workorder.application.ports.AssetReferencePort;

@Component
public class AssetHttpReferenceAdapter implements AssetReferencePort {

    private static final Logger logger = LoggerFactory.getLogger(AssetHttpReferenceAdapter.class);
    private final RestClient restClient;


    public AssetHttpReferenceAdapter(RestClient.Builder restClientBuilder,
        @Value("${smartops.services.asset.base-url:http://localhost:8082}") String assetServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(assetServiceBaseUrl)
                .build();

    }

    @Override
    public Optional<AssetSummaryDto> findAssetSummary(UUID assetId) {

        try{
            AssetSummaryResponse response = restClient.get()
                    .uri("/internal/v1/assets/{assetId}/summary", assetId)
                    .retrieve()
                    .body(AssetSummaryResponse.class);
            
            if(response == null){
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
        }catch (RestClientResponseException ex){
            if(ex.getStatusCode().value() == 404){
                return Optional.empty();
            }
            logger.error("Failed to retrieve asset summary for asset {} from Asset Service", assetId, ex);
            throw ex;
        }
            
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
