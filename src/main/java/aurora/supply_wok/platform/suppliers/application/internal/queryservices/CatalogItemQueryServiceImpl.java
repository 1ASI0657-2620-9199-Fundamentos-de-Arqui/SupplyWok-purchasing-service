package aurora.supply_wok.platform.suppliers.application.internal.queryservices;

import aurora.supply_wok.platform.suppliers.application.queryservices.CatalogItemQueryService;
import aurora.supply_wok.platform.suppliers.domain.model.aggregates.CatalogItem;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetAllCatalogItemsBySupplierIdQuery;
import aurora.supply_wok.platform.suppliers.domain.model.queries.GetCatalogItemByIdQuery;
import aurora.supply_wok.platform.suppliers.domain.repositories.CatalogItemRepository;
import aurora.supply_wok.platform.suppliers.domain.repositories.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the CatalogItemQueryService interface.
 */
@Service
public class CatalogItemQueryServiceImpl implements CatalogItemQueryService {

    private final CatalogItemRepository catalogItemRepository;
    private final SupplierRepository supplierRepository;

    public CatalogItemQueryServiceImpl(CatalogItemRepository catalogItemRepository, SupplierRepository supplierRepository) {
        this.catalogItemRepository = catalogItemRepository;
        this.supplierRepository = supplierRepository;
    }

    @Override
    public Optional<List<CatalogItem>> handle(GetAllCatalogItemsBySupplierIdQuery query) {
        if (!supplierRepository.existsById(query.supplierId())) {
            return Optional.empty();
        }

        return Optional.of(catalogItemRepository.findAllBySupplierId(query.supplierId()));
    }

    @Override
    public Optional<CatalogItem> handle(GetCatalogItemByIdQuery query) {
        if (!supplierRepository.existsById(query.supplierId())) {
            return Optional.empty();
        }

        return catalogItemRepository.findByIdAndSupplierId(query.catalogItemId(), query.supplierId());
    }
}
