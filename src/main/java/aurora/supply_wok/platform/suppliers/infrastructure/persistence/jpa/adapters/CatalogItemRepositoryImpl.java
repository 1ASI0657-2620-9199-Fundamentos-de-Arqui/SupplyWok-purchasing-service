package aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.adapters;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.assemblers.CatalogItemPersistenceAssembler;
import aurora.supply_wok.platform.suppliers.infrastructure.persistence.jpa.repositories.CatalogItemPersistenceRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the catalog item domain repository port with Spring Data JPA.
 */
@Repository
public class CatalogItemRepositoryImpl implements CatalogItemRepository {

    private final CatalogItemPersistenceRepository catalogItemPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CatalogItemRepositoryImpl(CatalogItemPersistenceRepository catalogItemPersistenceRepository,
                                     DomainEventPublisher domainEventPublisher) {
        this.catalogItemPersistenceRepository = catalogItemPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public List<CatalogItem> findAllBySupplierId(Long supplierId) {
        return catalogItemPersistenceRepository.findAllBySupplier_IdOrderByIdAsc(supplierId)
                .stream()
                .map(CatalogItemPersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Optional<CatalogItem> findByIdAndSupplierId(Long catalogItemId, Long supplierId) {
        return catalogItemPersistenceRepository.findByIdAndSupplier_Id(catalogItemId, supplierId)
                .map(CatalogItemPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public CatalogItem save(CatalogItem catalogItem) {
        var isNew = catalogItem.getId() == null;
        var saved = catalogItemPersistenceRepository.save(CatalogItemPersistenceAssembler.toPersistenceFromDomain(catalogItem));
        if (!isNew) {
            domainEventPublisher.publishAndClear(catalogItem);
        }

        var savedCatalogItem = CatalogItemPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedCatalogItem.onCreated();
            domainEventPublisher.publishAndClear(savedCatalogItem);
        }
        return savedCatalogItem;
    }

    @Override
    public void delete(CatalogItem catalogItem) {
        catalogItemPersistenceRepository.delete(CatalogItemPersistenceAssembler.toPersistenceFromDomain(catalogItem));
    }
}
