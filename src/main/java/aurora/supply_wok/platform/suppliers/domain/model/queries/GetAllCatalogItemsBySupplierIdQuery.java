package aurora.supply_wok.platform.suppliers.domain.model.queries;

/**
 * Query to retrieve all catalog items linked to a supplier.
 *
 * @param supplierId supplier unique identifier
 */
public record GetAllCatalogItemsBySupplierIdQuery(Long supplierId) {
}
