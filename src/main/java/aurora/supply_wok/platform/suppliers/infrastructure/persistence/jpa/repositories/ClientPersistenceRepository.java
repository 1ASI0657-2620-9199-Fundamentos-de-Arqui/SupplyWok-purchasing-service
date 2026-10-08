package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.ClientPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for client persistence entities.
 */
@Repository
public interface ClientPersistenceRepository extends JpaRepository<ClientPersistenceEntity, Long> {
    Optional<ClientPersistenceEntity> findFirstByNameIgnoreCase(String name);

    @Query("""
            select client
            from SupplierClientPersistenceEntity supplierClient
            join supplierClient.client client
            where supplierClient.supplier.id = :supplierId
            order by client.id
            """)
    List<ClientPersistenceEntity> findAllBySupplierId(@Param("supplierId") Long supplierId);
}
