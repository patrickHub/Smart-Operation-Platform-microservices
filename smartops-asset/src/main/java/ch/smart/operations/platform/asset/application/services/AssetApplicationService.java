package ch.smart.operations.platform.asset.application.services;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.smart.operations.platform.asset.application.commands.ChangeAssetStatusCommand;
import ch.smart.operations.platform.asset.application.commands.CreateAssetCommand;
import ch.smart.operations.platform.asset.application.commands.UpdateAssetCommand;
import ch.smart.operations.platform.asset.application.dtos.AssetDto;
import ch.smart.operations.platform.asset.application.dtos.AssetHistoryDto;
import ch.smart.operations.platform.asset.application.dtos.AssetSummaryDto;
import ch.smart.operations.platform.shared.exceptions.BusinessRuleException;
import ch.smart.operations.platform.shared.exceptions.NotFoundException;
import ch.smart.operations.platform.shared.exceptions.ValidationException;
import ch.smart.operations.platform.asset.application.ports.AssetHistoryRepository;
import ch.smart.operations.platform.asset.application.ports.AssetRepository;
import ch.smart.operations.platform.asset.application.ports.AssetTypeRepository;
import ch.smart.operations.platform.asset.application.ports.CustomerReferencePort;
import ch.smart.operations.platform.asset.domain.entities.Asset;
import ch.smart.operations.platform.asset.domain.entities.AssetHistory;
import ch.smart.operations.platform.asset.domain.enums.AssetHistoryEventType;
import ch.smart.operations.platform.asset.domain.enums.AssetStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class AssetApplicationService {

    private final AssetRepository assetRepository;
    private final AssetHistoryRepository assetHistoryRepository;
    private final CustomerReferencePort customerReferencePort;
    private final AssetTypeRepository assetTypeRepository;
    private final ObjectMapper objectMapper;

    private final Logger logger = LoggerFactory.getLogger(AssetApplicationService.class);

    public AssetApplicationService(
            AssetRepository assetRepository,
            AssetHistoryRepository assetHistoryRepository,
            CustomerReferencePort customerReferencePort,
            AssetTypeRepository assetTypeRepository,
            ObjectMapper objectMapper
    ) {
        this.assetRepository = assetRepository;
        this.assetHistoryRepository = assetHistoryRepository;
        this.customerReferencePort = customerReferencePort;
        this.assetTypeRepository = assetTypeRepository;
        this.objectMapper = objectMapper;
    }

    public UUID createAsset(CreateAssetCommand command) {
        validateCreateCommand(command);

        Asset asset = Asset.create(
            generateAssetNumber(),
            command.serialNumber().trim(),
            command.assetTypeId(),
            command.siteId(),
            command.customerId(),
            command.name().trim(),
            normalize(command.description()),
            command.installationDate(),
            command.warrantyEndDate(),
            command.criticality(),
            command.createdBy().trim(),
            command.createdBy().trim()
        );

        Asset saved = assetRepository.save(asset);

        createHistory(
                saved.getId(),
                AssetHistoryEventType.REGISTERED,
                "Asset registered",
                null
        );

        return saved.getId();
    }

    @Transactional(readOnly = true)
    public AssetDto getAssetById(UUID assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NotFoundException("Asset not found with id " + assetId));

        return toDto(asset);
    }

    @Transactional(readOnly = true)
    public List<AssetDto> searchAssets(String serialNumber, UUID siteId, UUID customerId, AssetStatus status) {
        if (serialNumber != null && !serialNumber.isBlank()) {
            return assetRepository.findBySerialNumber(serialNumber.trim())
                    .stream()
                    .map(this::toDto)
                    .toList();
        }

        if (siteId != null) {
            return assetRepository.findBySiteId(siteId)
                    .stream()
                    .map(this::toDto)
                    .toList();
        }

        if (customerId != null) {
            return assetRepository.findByCustomerId(customerId)
                    .stream()
                    .map(this::toDto)
                    .toList();
        }

        if (status != null) {
            return assetRepository.findByStatus(status)
                    .stream()
                    .map(this::toDto)
                    .toList();
        }

        return assetRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public void updateAsset(UpdateAssetCommand command) {
        validateUpdateCommand(command);

        Asset existing = assetRepository.findById(command.assetId())
                .orElseThrow(() -> new NotFoundException("Asset not found with id " + command.assetId()));

        Asset updated = new Asset(
            existing.getId(),
            existing.getAssetNumber(),
            existing.getSerialNumber(),
            existing.getAssetTypeId(),
            existing.getSiteId(),
            existing.getCustomerId(),
            command.name().trim(),
            normalize(command.description()),
            command.installationDate(),
            command.warrantyEndDate(),
            existing.getStatus(),
            command.criticality(),
            existing.getVersion(),
            existing.getCreatedAt(),
            existing.getCreatedBy(),
            OffsetDateTime.now(),
            command.updatedBy().trim()
        );

        logger.info("Updating asset with ID: {}", updated.getId());
        logger.info("Updated asset details: {}", updated);


        assetRepository.save(updated);

        String payloadJson = buildUpdatePayload(existing, updated);

        logger.info("Asset update payload: {}", payloadJson);

        createHistory(
                updated.getId(),
                AssetHistoryEventType.UPDATED,
                "Asset updated",
                payloadJson
        );
    }

    public void changeAssetStatus(ChangeAssetStatusCommand command) {
        if (command.assetId() == null) {
            throw new ValidationException(Map.of(
                "assetId", new String[]{"assetId is required."}));
        }
        if (command.status() == null) {
            throw new ValidationException(Map.of(
                "status", new String[]{"status is required."}));
        }
        if (command.updatedBy() == null || command.updatedBy().isBlank()) {
            throw new ValidationException(Map.of(
                "updatedBy", new String[]{"updatedBy is required."}));
        }

        Asset existing = assetRepository.findById(command.assetId())
                .orElseThrow(() -> new NotFoundException("Asset not found with id " + command.assetId()));

        if (existing.getStatus() == command.status()) {
            throw new ValidationException(Map.of(
                    "status", new String[]{"Asset already has status " + command.status()}));
        }

        if (existing.getStatus() == AssetStatus.RETIRED) {
            throw new BusinessRuleException("Cannot change status of a retired asset.");
        }

        Asset updated = new Asset(
                existing.getId(),
                existing.getAssetNumber(),
                existing.getSerialNumber(),
                existing.getAssetTypeId(),
                existing.getSiteId(),
                existing.getCustomerId(),
                existing.getName(),
                existing.getDescription(),
                existing.getInstallationDate(),
                existing.getWarrantyEndDate(),
                command.status(),
                existing.getCriticality(),
                existing.getVersion(),
                existing.getCreatedAt(),
                existing.getCreatedBy(),
                OffsetDateTime.now(),
                command.updatedBy().trim()
        );

        assetRepository.save(updated);

        String payloadJson = buildUpdatePayload(existing, updated);

        createHistory(
                updated.getId(),
                AssetHistoryEventType.STATUS_CHANGED,
                "Asset status changed from " + existing.getStatus() + " to " + command.status(),
                payloadJson
        );
    }

    @Transactional(readOnly = true)
    public List<AssetHistoryDto> getAssetHistory(UUID assetId) {
        assetRepository.findById(assetId)
                .orElseThrow(() -> new NotFoundException("Asset not found with id " + assetId));

        return assetHistoryRepository.findByAssetId(assetId)
                .stream()
                .map(this::toHistoryDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssetSummaryDto getAssetSummary(UUID assetId) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new NotFoundException("Asset not found with id " + assetId));

        return new AssetSummaryDto(
                asset.getId(),
                asset.getAssetNumber(),
                asset.getSerialNumber(),
                asset.getCustomerId(),
                asset.getSiteId(),
                asset.getName(),
                asset.getStatus().name(),
                asset.getCriticality() != null ? asset.getCriticality().name() : null
        );
    }

    private void validateCreateCommand(CreateAssetCommand command) {
        if (command.serialNumber() == null || command.serialNumber().isBlank()) {
            throw new ValidationException(Map.of(
                "serialNumber", new String[]{"serialNumber 1 is required."}));
        }
        if (command.assetTypeId() == null) {
            throw new ValidationException(Map.of(
                "assetTypeId", new String[]{"assetTypeId is required."}));
        }
        if (command.siteId() == null) {
            throw new ValidationException(Map.of(
                "siteId", new String[]{"siteId is required."}));
        }
        if (command.customerId() == null) {
            throw new ValidationException(Map.of(
                "customerId", new String[]{"customerId is required."}));
        }
        if (command.name() == null || command.name().isBlank()) {
            throw new ValidationException(Map.of(
                "name", new String[]{"name is required."}));
        }
        if (command.createdBy() == null || command.createdBy().isBlank()) {
            throw new ValidationException(Map.of(
                "createdBy", new String[]{"createdBy is required."}));
        }

        if(assetTypeRepository.findById(command.assetTypeId()).isEmpty()) {
            throw new ValidationException(Map.of(
                "assetTypeId", new String[]{"Asset type does not exist for id " + command.assetTypeId()}));
        }

        if (assetRepository.existsBySerialNumber(command.serialNumber().trim())) {
            throw new ValidationException(Map.of(
                "serialNumber", new String[]{"An asset with the same serial number already exists."}));
        }

        if (!customerReferencePort.customerExists(command.customerId())) {
            throw new ValidationException(Map.of(
                "customerId", new String[]{"Customer does not exist for id " + command.customerId()}));
        }

        if (!customerReferencePort.siteExists(command.siteId())) {
            throw new ValidationException(Map.of(
                "siteId", new String[]{"Site does not exist for id " + command.siteId()}));
        }

        if (command.installationDate() != null
                && command.warrantyEndDate() != null
                && command.installationDate().isAfter(command.warrantyEndDate())) {
            throw new ValidationException(Map.of(
                "installationDate", new String[]{"installationDate must be before or equal to warrantyEndDate."}));
        }
    }

    private void validateUpdateCommand(UpdateAssetCommand command) {
        if (command.assetId() == null) {
            throw new ValidationException(Map.of(
                "assetId", new String[]{"assetId is required."}));
        }
        if (command.name() == null || command.name().isBlank()) {
            throw new ValidationException(Map.of(
                "name", new String[]{"name is required."}));
        }
        if (command.updatedBy() == null || command.updatedBy().isBlank()) {
            throw new ValidationException(Map.of(
                "updatedBy", new String[]{"updatedBy is required."}));
        }

        if (command.installationDate() != null
                && command.warrantyEndDate() != null
                && command.installationDate().isAfter(command.warrantyEndDate())) {
            throw new ValidationException(Map.of(
                "installationDate", new String[]{"installationDate must be before or equal to warrantyEndDate."}));
        }
    }

    private void createHistory(UUID assetId, AssetHistoryEventType eventType, String summary, String payloadJson) {
        logger.info("Attempting to create asset history record: assetId={}, eventType={}, summary={}", assetId, eventType, summary);
        AssetHistory history = new AssetHistory(
                UUID.randomUUID(),
                assetId,
                eventType,
                OffsetDateTime.now(),
                null,
                summary,
                payloadJson,
                OffsetDateTime.now()
        );

        logger.info("Creating asset history record: assetId={}, eventType={}, summary={}", assetId, eventType, summary);    

        assetHistoryRepository.save(history);

        logger.info("Asset history record created with ID: {}", history.getId());
    }

    private String buildUpdatePayload(Asset before, Asset after) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();

            logger.info("Building update payload. Before: {}, After: {}", before, after);

            payload.put("before", toSimpleMap(before));
            payload.put("after", toSimpleMap(after));
            payload.put("changes", computeChanges(before, after));

            logger.info("Update payload map: {}", payload);

            return objectMapper.writeValueAsString(payload);

        } catch (Exception e) {
            logger.error("Failed to build payloadJson", e);
            throw new BusinessRuleException("Failed to build asset update history payload.");
        }
    }

    private Map<String, Object> toSimpleMap(Asset asset) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("name", asset.getName());
        map.put("installationDate", asset.getInstallationDate() != null ? asset.getInstallationDate().toString() : null);
        map.put("warrantyEndDate", asset.getWarrantyEndDate() != null ? asset.getWarrantyEndDate().toString() : null);
        map.put("criticality", asset.getCriticality() != null ? asset.getCriticality().name() : null);
        map.put("status", asset.getStatus() != null ? asset.getStatus().name() : null);

        return map;
    }

    private Map<String, Object> computeChanges(Asset before, Asset after) {
        Map<String, Object> changes = new LinkedHashMap<>();

        compare(changes, "name", before.getName(), after.getName());
        compare(changes, "description", before.getDescription(), after.getDescription());
        compare(changes, "installationDate", before.getInstallationDate() != null ? before.getInstallationDate().toString() : null, after.getInstallationDate() != null ? after.getInstallationDate().toString() : null);
        compare(changes, "warrantyEndDate", before.getWarrantyEndDate() != null ? before.getWarrantyEndDate().toString() : null, after.getWarrantyEndDate() != null ? after.getWarrantyEndDate().toString() : null);
        compare(changes, "criticality", before.getCriticality() != null ? before.getCriticality().name() : null, after.getCriticality() != null ? after.getCriticality().name() : null);
        compare(changes, "status", before.getStatus() != null ? before.getStatus().name() : null, after.getStatus() != null ? after.getStatus().name() : null);

        return changes;
    }

    private void compare(Map<String, Object> changes, String field, Object oldVal, Object newVal) {
        if (!Objects.equals(oldVal, newVal)) {
            Map<String, Object> change = new LinkedHashMap<>();
            change.put("old", oldVal);
            change.put("new", newVal);
            changes.put(field, change);
        }
    }

    private AssetDto toDto(Asset asset) {
        return new AssetDto(
                asset.getId(),
                asset.getAssetNumber(),
                asset.getSerialNumber(),
                asset.getAssetTypeId(),
                asset.getSiteId(),
                asset.getCustomerId(),
                asset.getName(),
                asset.getDescription(),
                asset.getInstallationDate(),
                asset.getWarrantyEndDate(),
                asset.getStatus().name(),
                asset.getCriticality() != null ? asset.getCriticality().name() : null,
                asset.getCreatedAt(),
                asset.getCreatedBy(),
                asset.getUpdatedAt(),
                asset.getUpdatedBy()
        );
    }

    private AssetHistoryDto toHistoryDto(AssetHistory history) {
        return new AssetHistoryDto(
                history.getId(),
                history.getAssetId(),
                history.getEventType().name(),
                history.getEventTimestamp(),
                history.getActorId(),
                history.getSummary(),
                history.getPayloadJson(),
                history.getCreatedAt()
        );
    }

    private String generateAssetNumber() {
        return "AST-" + System.currentTimeMillis();
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
    
}
