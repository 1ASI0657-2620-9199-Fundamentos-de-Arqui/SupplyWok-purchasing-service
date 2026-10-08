package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.adapters;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.SupplierClient;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierClientRepository;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.entities.SupplierClientPersistenceEntity;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories.ClientPersistenceRepository;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories.SupplierClientPersistenceRepository;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories.SupplierPersistenceRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository adapter that bridges supplier-client link domain operations with Spring Data JPA.
 */
@Repository
public class SupplierClientRepositoryImpl implements SupplierClientRepository {

    private final SupplierClientPersistenceRepository supplierClientPersistenceRepository;
    private final SupplierPersistenceRepository supplierPersistenceRepository;
    private final ClientPersistenceRepository clientPersistenceRepository;

    public SupplierClientRepositoryImpl(SupplierClientPersistenceRepository supplierClientPersistenceRepository,
                                        SupplierPersistenceRepository supplierPersistenceRepository,
                                        ClientPersistenceRepository clientPersistenceRepository) {
        this.supplierClientPersistenceRepository = supplierClientPersistenceRepository;
        this.supplierPersistenceRepository = supplierPersistenceRepository;
        this.clientPersistenceRepository = clientPersistenceRepository;
    }

    @Override
    public SupplierClient save(SupplierClient supplierClient) {
        var entity = new SupplierClientPersistenceEntity();
        if (supplierClient.getId() != null) {
            entity.setId(supplierClient.getId());
        }
        entity.setSupplier(supplierPersistenceRepository.getReferenceById(supplierClient.getSupplierId()));
        entity.setClient(clientPersistenceRepository.getReferenceById(supplierClient.getClientId()));

        var persisted = supplierClientPersistenceRepository.save(entity);
        var savedSupplierClient = new SupplierClient(
                persisted.getSupplier().getId(),
                persisted.getClient().getId()
        );
        savedSupplierClient.setId(persisted.getId());
        return savedSupplierClient;
    }

    @Override
    public boolean existsBySupplierIdAndClientId(Long supplierId, Long clientId) {
        return supplierClientPersistenceRepository.existsBySupplier_IdAndClient_Id(supplierId, clientId);
    }
}
