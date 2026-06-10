package ch.smart.operations.platform.asset.infrastructure.clients;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.smart.operations.platform.asset.application.ports.CustomerReferencePort;

@Component
public class CustomerHttpReferenceAdapter implements CustomerReferencePort {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerHttpReferenceAdapter.class);

    private final RestClient restClient;

    public CustomerHttpReferenceAdapter(
            RestClient.Builder restClientBuilder,
            @Value("${smartops.services.customer.base-url:http://localhost:8081}") String customerServiceBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(customerServiceBaseUrl)
                .build();
    }

    @Override
    public boolean customerExists(UUID customerId) {
        try{
            restClient.get()
                .uri("/internal/v1/customers/{customerId}/exists", customerId)
                .retrieve()
                .toBodilessEntity();

            return true;
        }catch(RestClientResponseException ex){
            if(ex.getStatusCode().value() == 404){
                return false;
            }
            logger.error("Failed to verify customer {} from Customer Service", customerId, ex);
            throw ex;
        }
    }

    @Override
    public boolean siteExists(UUID customerId, UUID siteId) {
        try{
            restClient.get()
                .uri("/internal/v1/customers/{customerId}/sites/{siteId}/exists", customerId, siteId)
                .retrieve()
                .toBodilessEntity();

            return true;

        }catch(RestClientResponseException ex){
            if(ex.getStatusCode().value() == 404){
                return false;
            }
            logger.error("Failed to verify  customer site {} for customerId {}, siteId {} from Customer Service", siteId, customerId, ex);
            throw ex;
        }
    }
}
