package ch.smart.operations.platform.workorder.infrastructure.persistence.adapters;

import ch.smart.operations.platform.workorder.application.ports.UsedPartRepository;
import ch.smart.operations.platform.workorder.domain.entities.UsedPart;
import ch.smart.operations.platform.workorder.infrastructure.persistence.entities.UsedPartJpaEntity;
import ch.smart.operations.platform.workorder.infrastructure.persistence.repositories.UsedPartJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class UsedPartRepositoryAdapter implements UsedPartRepository {

    private final UsedPartJpaRepository repository;

    public UsedPartRepositoryAdapter(UsedPartJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public UsedPart save(UsedPart usedPart) {
        return toDomain(repository.save(toJpa(usedPart)));
    }

    @Override
    public List<UsedPart> findByInterventionReportId(UUID interventionReportId) {
        return repository.findByInterventionReportId(interventionReportId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private UsedPart toDomain(UsedPartJpaEntity entity) {
        return new UsedPart(
                entity.getId(),
                entity.getInterventionReportId(),
                entity.getPartNumber(),
                entity.getPartName(),
                entity.getQuantity(),
                entity.getUnitPrice(),
                entity.getCurrency()
        );
    }

    private UsedPartJpaEntity toJpa(UsedPart usedPart) {
        UsedPartJpaEntity entity = new UsedPartJpaEntity();

        entity.setId(usedPart.getId());
        entity.setInterventionReportId(usedPart.getInterventionReportId());
        entity.setPartNumber(usedPart.getPartNumber());
        entity.setPartName(usedPart.getPartName());
        entity.setQuantity(usedPart.getQuantity());
        entity.setUnitPrice(usedPart.getUnitPrice());
        entity.setCurrency(usedPart.getCurrency());

        return entity;
    }
}
