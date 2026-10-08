package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.CatalogItemPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for catalog item persistence entities.
 */
@Repository
public interface CatalogItemPersistenceRepository extends JpaRepository<CatalogItemPersistenceEntity, Long> {
    List<CatalogItemPersistenceEntity> findAllBySupplier_IdOrderByIdAsc(Long supplierId);

    Optional<CatalogItemPersistenceEntity> findByIdAndSupplier_Id(Long catalogItemId, Long supplierId);
}
