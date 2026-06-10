package ch.smart.operations.platform.asset.api.controllers;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.smart.operations.platform.asset.application.dtos.AssetSummaryDto;
import ch.smart.operations.platform.asset.application.services.AssetApplicationService;


@RestController
@RequestMapping("/internal/v1/assets")
public class InternalAssetController {

    private final AssetApplicationService service;

    public InternalAssetController(AssetApplicationService service) {
        this.service = service;
    }

    @GetMapping("/{assetId}/summary")
    public AssetSummaryDto getAssetSummary(@PathVariable UUID assetId) {
        return service.getAssetSummary(assetId);
    }
    
}
