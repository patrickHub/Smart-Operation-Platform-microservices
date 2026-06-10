package ch.smart.operations.platform.asset.api.controllers;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.smart.operations.platform.asset.api.contracts.ChangeAssetStatusRequest;
import ch.smart.operations.platform.asset.api.contracts.CreateAssetRequest;
import ch.smart.operations.platform.asset.api.contracts.UpdateAssetRequest;
import ch.smart.operations.platform.asset.application.commands.ChangeAssetStatusCommand;
import ch.smart.operations.platform.asset.application.commands.CreateAssetCommand;
import ch.smart.operations.platform.asset.application.commands.UpdateAssetCommand;
import ch.smart.operations.platform.asset.application.dtos.AssetDto;
import ch.smart.operations.platform.asset.application.dtos.AssetHistoryDto;
import ch.smart.operations.platform.asset.application.services.AssetApplicationService;
import ch.smart.operations.platform.asset.domain.enums.AssetStatus;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/v1/assets")
public class AssetController {

    private static final Logger log = LoggerFactory.getLogger(AssetController.class);
    private final AssetApplicationService assetApplicationService;

    public AssetController(AssetApplicationService assetApplicationService) {
        this.assetApplicationService = assetApplicationService;
    }

    // -----------------------------
    // CREATE
    // -----------------------------
    @PostMapping
    public ResponseEntity<Map<String, UUID>> createAsset(@Valid @RequestBody CreateAssetRequest request) {

        log.info("Asset Attempt Created: serialNumber={}, assetTypeId={}", request.serialNumber(), request.assetTypeId());

        UUID id = assetApplicationService.createAsset(
                new CreateAssetCommand(
                        request.serialNumber(),
                        request.assetTypeId(),
                        request.siteId(),
                        request.customerId(),
                        request.name(),
                        request.description(),
                        request.installationDate(),
                        request.warrantyEndDate(),
                        request.criticality(),
                        request.createdBy()
                )
        );

        return ResponseEntity
                .created(URI.create("/api/v1/assets/" + id))
                .body(Map.of("id", id));
    }

    // -----------------------------
    // GET BY ID
    // -----------------------------
    @GetMapping("/{assetId}")
    public ResponseEntity<AssetDto> getAsset(@PathVariable UUID assetId) {
        return ResponseEntity.ok(assetApplicationService.getAssetById(assetId));
    }

    // -----------------------------
    // SEARCH
    // -----------------------------
    @GetMapping
    public ResponseEntity<List<AssetDto>> searchAssets(
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) UUID siteId,
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) AssetStatus status
    ) {
        return ResponseEntity.ok(
                assetApplicationService.searchAssets(serialNumber, siteId, customerId, status)
        );
    }

    // -----------------------------
    // UPDATE
    // -----------------------------
    @PutMapping("/{assetId}")
    public ResponseEntity<Void> updateAsset(
            @PathVariable UUID assetId,
            @Valid @RequestBody UpdateAssetRequest request
    ) {
        assetApplicationService.updateAsset(
                new UpdateAssetCommand(
                        assetId,
                        request.name(),
                        request.description(),
                        request.installationDate(),
                        request.warrantyEndDate(),
                        request.criticality(),
                        request.updatedBy()
                )
        );

        return ResponseEntity.noContent().build();
    }

    // -----------------------------
    // CHANGE STATUS
    // -----------------------------
    @PatchMapping("/{assetId}/status")
    public ResponseEntity<Void> changeStatus(
            @PathVariable UUID assetId,
            @Valid @RequestBody ChangeAssetStatusRequest request
    ) {
        assetApplicationService.changeAssetStatus(
                new ChangeAssetStatusCommand(
                        assetId,
                        request.status(),
                        request.updatedBy()
                )
        );

        return ResponseEntity.noContent().build();
    }

    // -----------------------------
    // HISTORY
    // -----------------------------
    @GetMapping("/{assetId}/history")
    public ResponseEntity<List<AssetHistoryDto>> getHistory(@PathVariable UUID assetId) {
        return ResponseEntity.ok(assetApplicationService.getAssetHistory(assetId));
    }
    
}
