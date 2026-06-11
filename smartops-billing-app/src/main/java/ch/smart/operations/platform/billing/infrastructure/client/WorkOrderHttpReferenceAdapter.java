package ch.smart.operations.platform.billing.infrastructure.client;

import ch.smart.operations.platform.billing.application.dtos.UsedPartBillingDto;
import ch.smart.operations.platform.billing.application.dtos.WorkOrderBillingSummaryDto;
import ch.smart.operations.platform.billing.application.ports.WorkOrderReferencePort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class WorkOrderHttpReferenceAdapter implements WorkOrderReferencePort {

    private final Logger logger = LoggerFactory.getLogger(WorkOrderHttpReferenceAdapter.class);
    private final RestClient restClient;

    public WorkOrderHttpReferenceAdapter(RestClient.Builder restClientBuilder,
        @Value("${smartops.services.workorder.base-url:http://localhost:8083}") String workorderServiceBaseUrl
    ){
        this.restClient = restClientBuilder
            .baseUrl(workorderServiceBaseUrl)
            .build();

    }

    @Override
    public Optional<WorkOrderBillingSummaryDto> findBillingSummary(UUID workOrderId) {
        try {
           
            WorkOrderBillingSummaryResponse response = restClient.get()
                .uri("/internal/v1/work-orders/{workOrderId}/billing-summary", workOrderId)
                .retrieve()
                .body(WorkOrderBillingSummaryResponse.class);

            if(response == null){
                return Optional.empty();
            }
            return Optional.of(new WorkOrderBillingSummaryDto(
                    response.id(),
                    response.workOrderNumber(),
                    response.customerId(),
                    response.assetId(),
                    response.siteId(),
                    response.type(),
                    response.status(),
                    response.completedAt(),
                    response.laborDurationMinutes(),
                    response.resultStatus(),
                    response.usedParts() == null ? List.of() :
                            response.usedParts
                                .stream()
                                .map(part -> new UsedPartBillingDto(
                                        part.partNumber(),
                                        part.partName(),
                                        part.quantity(),
                                        part.unitPrice(),
                                        part.currency()
                                ))
                                .toList()
            ));
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            logger.error(
                    "Failed to retrieve work order billing summary from WorkOrder Service for workOrderId={}",
                    workOrderId,
                    ex
            );

            throw ex;
        }
    }

    private record WorkOrderBillingSummaryResponse(
            UUID id,
            String workOrderNumber,
            UUID customerId,
            UUID assetId,
            UUID siteId,
            String type,
            String status,
            OffsetDateTime completedAt,
            Integer laborDurationMinutes,
            String resultStatus,
            List<UsedPartBillingResponse> usedParts
    ) {
    }

    private record UsedPartBillingResponse(
            String partNumber,
            String partName,
            java.math.BigDecimal quantity,
            java.math.BigDecimal unitPrice,
            String currency
    ) {
    }




}