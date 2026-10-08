package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.SupplierClientPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data repository for supplier-client persistence entities.
 */
@Repository
public interface SupplierClientPersistenceRepository extends JpaRepository<SupplierClientPersistenceEntity, Long> {
    boolean existsBySupplier_IdAndClient_Id(Long supplierId, Long clientId);
}
