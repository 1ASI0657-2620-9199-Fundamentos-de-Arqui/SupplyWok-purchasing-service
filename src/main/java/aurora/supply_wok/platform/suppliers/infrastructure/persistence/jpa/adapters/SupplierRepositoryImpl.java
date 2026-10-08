package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.adapters;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.Supplier;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.assemblers.SupplierPersistenceAssembler;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories.SupplierPersistenceRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the supplier domain repository port with Spring Data JPA.
 */
@Repository
public class SupplierRepositoryImpl implements SupplierRepository {

    private final SupplierPersistenceRepository supplierPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public SupplierRepositoryImpl(SupplierPersistenceRepository supplierPersistenceRepository,
                                  DomainEventPublisher domainEventPublisher) {
        this.supplierPersistenceRepository = supplierPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Supplier save(Supplier supplier) {
        var isNew = supplier.getId() == null;
        var persisted = supplierPersistenceRepository.save(SupplierPersistenceAssembler.toPersistenceFromDomain(supplier));
        var savedSupplier = SupplierPersistenceAssembler.toDomainFromPersistence(persisted);
        if (isNew) {
            savedSupplier.onCreated();
            domainEventPublisher.publishAndClear(savedSupplier);
        } else {
            domainEventPublisher.publishAndClear(supplier);
        }
        return savedSupplier;
    }

    @Override
    public List<Supplier> findAll() {
        return supplierPersistenceRepository.findAll().stream()
                .map(SupplierPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<Supplier> findById(Long id) {
        return supplierPersistenceRepository.findById(id).map(SupplierPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Supplier> findByEmailIgnoreCase(String email) {
        return supplierPersistenceRepository.findFirstByEmailIgnoreCase(email)
                .map(SupplierPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsById(Long id) {
        return supplierPersistenceRepository.existsById(id);
    }
}
