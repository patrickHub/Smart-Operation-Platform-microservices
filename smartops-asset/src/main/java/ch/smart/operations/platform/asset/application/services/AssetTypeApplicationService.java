package ch.smart.operations.platform.asset.application.services;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ch.smart.operations.platform.asset.application.commands.CreateAssetTypeCommand;
import ch.smart.operations.platform.asset.application.dtos.AssetTypeDto;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import ch.smart.operations.platform.asset.application.ports.AssetTypeRepository;
import ch.smart.operations.platform.asset.domain.entities.AssetType;

@Service
public class AssetTypeApplicationService {

    private final AssetTypeRepository assetTypeRepository;

    public AssetTypeApplicationService(AssetTypeRepository assetTypeRepository) {
        this.assetTypeRepository = assetTypeRepository;
    }


    public UUID createAssetType(CreateAssetTypeCommand command) {

        validate(command);

        AssetType assetType = AssetType.create(
            command.code(),
            command.name(),
            command.manufacturer(),
            command.model(),
            command.description()
        );

        AssetType saved = assetTypeRepository.save(assetType);
        return saved.getId();
    }

    public List<AssetTypeDto> getAllAssetTypes() {
        return assetTypeRepository.findAll().stream().map(this::toDto).toList();
    } 

    public AssetTypeDto getAssetTypeById(UUID id) {
        return assetTypeRepository.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ValidationException(Map.of(
                "id", new String[]{"Asset type not found with id: " + id}
            )));
    }

    private AssetTypeDto toDto(AssetType assetType) {

        return new AssetTypeDto(assetType.getId(), assetType.getCode(), assetType.getName(), assetType.getManufacturer(),
         assetType.getModel(), assetType.getDescription(), assetType.getCreatedAt(), assetType.getUpdatedAt());
    }

    private void validate(CreateAssetTypeCommand command){
        assetTypeRepository.findAll().stream()
            .filter(t->t.getCode().equalsIgnoreCase(command.code()))
            .findAny()
            .ifPresent(
                a -> {
                    throw new ValidationException(Map.of(
                        "code", new String[]{"Code must be unique."}
                    ));
                }
        );
    }

    
}
