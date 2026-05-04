package ch.smart.operations.platform.workorder.infrastructure.client;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import ch.smart.operations.platform.asset.application.services.AssetApplicationService;
import ch.smart.operations.platform.workorder.application.dtos.AssetSummaryDto;
import ch.smart.operations.platform.workorder.application.ports.AssetReferencePort;

@Component
public class AssetReferenceAdapter implements AssetReferencePort {

    private final AssetApplicationService assetApplicationService;

    public AssetReferenceAdapter(AssetApplicationService assetApplicationService) {
        this.assetApplicationService = assetApplicationService;
    }

    @Override
    public Optional<AssetSummaryDto> findAssetSummary(UUID assetId) {

        try{
            ch.smart.operations.platform.asset.application.dtos.AssetSummaryDto asset = assetApplicationService.getAssetSummary(assetId);

            return Optional.of(new AssetSummaryDto(
                    asset.id(),
                    asset.assetNumber(),
                    asset.serialNumber(),
                    asset.customerId(),
                    asset.siteId(),
                    asset.name(),
                    asset.status(),
                    asset.criticality()
            ));
        }catch (Exception e){
            return Optional.empty();
        }


        
            
    }
}
