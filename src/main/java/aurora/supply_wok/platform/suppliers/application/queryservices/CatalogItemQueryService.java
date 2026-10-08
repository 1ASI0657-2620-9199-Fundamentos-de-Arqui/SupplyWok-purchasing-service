package aurora.supply_wok.platform.suppliers.application.queryservices;

import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllCatalogItemsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetCatalogItemByIdQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service for supplier catalog items.
 */
public interface CatalogItemQueryService {
    Optional<List<CatalogItem>> handle(GetAllCatalogItemsBySupplierIdQuery query);

    Optional<CatalogItem> handle(GetCatalogItemByIdQuery query);
}
