package aurora.supply_wok.platform.purchasing.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.purchasing.infrastructure.persistence.jpa.entities.PurchaseOrderPersistenceEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for purchase order persistence entities.
 */
@Repository
public interface PurchaseOrderPersistenceRepository extends JpaRepository<PurchaseOrderPersistenceEntity, Long> {

    @EntityGraph(attributePaths = "items")
    List<PurchaseOrderPersistenceEntity> findAllByOrderByIdAsc();

    @EntityGraph(attributePaths = "items")
    List<PurchaseOrderPersistenceEntity> findAllBySupplierIdOrderByIdAsc(Long supplierId);

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<PurchaseOrderPersistenceEntity> findById(Long purchaseOrderId);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
