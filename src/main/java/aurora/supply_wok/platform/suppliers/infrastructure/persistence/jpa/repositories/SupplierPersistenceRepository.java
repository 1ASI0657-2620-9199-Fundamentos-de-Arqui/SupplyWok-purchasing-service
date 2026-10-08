package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.SupplierPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data repository for supplier persistence entities.
 */
@Repository
public interface SupplierPersistenceRepository extends JpaRepository<SupplierPersistenceEntity, Long> {
    Optional<SupplierPersistenceEntity> findFirstByEmailIgnoreCase(String email);
}
