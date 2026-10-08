package aurora.supply_wok.platform.suppliers.domain.repositories;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;

import java.util.List;
import java.util.Optional;

/**
 * Catalog item repository port.
 */
public interface CatalogItemRepository {
    List<CatalogItem> findAllBySupplierId(Long supplierId);

    Optional<CatalogItem> findByIdAndSupplierId(Long catalogItemId, Long supplierId);

    CatalogItem save(CatalogItem catalogItem);

    void delete(CatalogItem catalogItem);
}
