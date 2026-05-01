package ch.smart.operations.platform.asset.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.smart.operations.platform.asset.api.contracts.CreateAssetTypeRequest;
import ch.smart.operations.platform.asset.application.commands.CreateAssetTypeCommand;
import ch.smart.operations.platform.asset.application.dtos.AssetTypeDto;
import ch.smart.operations.platform.asset.application.services.AssetTypeApplicationService;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/v1/asset-types")
public class AssetTypeController {

    private final AssetTypeApplicationService assetTypeApplicationService;

    public AssetTypeController(AssetTypeApplicationService assetTypeApplicationService) {
        this.assetTypeApplicationService = assetTypeApplicationService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> createAssetType(@RequestBody @Valid CreateAssetTypeRequest request) {
        CreateAssetTypeCommand command = new CreateAssetTypeCommand(
            request.code(),
            request.name(),
            request.manufacturer(),
            request.model(),
            request.description()
        );

        UUID id = assetTypeApplicationService.createAssetType(command);
        return ResponseEntity.created(URI.create("/api/v1/asset-types/" + id)).body(Map.of("id", id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetTypeDto> getAssetTypeById(@PathVariable UUID id) {
        AssetTypeDto assetType = assetTypeApplicationService.getAssetTypeById(id);
        return ResponseEntity.ok(assetType);
    }

        @GetMapping
    public ResponseEntity<List<AssetTypeDto>> getAllAssetTypes() {
        var assetTypes = assetTypeApplicationService.getAllAssetTypes();
        return ResponseEntity.ok(assetTypes);
    }   
    

    
}
