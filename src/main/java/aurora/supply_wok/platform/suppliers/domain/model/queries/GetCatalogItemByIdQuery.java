package aurora.supply_wok.platform.suppliers.domain.model.queries;

/**
 * Query to retrieve one catalog item linked to a supplier.
 *
 * @param supplierId supplier unique identifier
 * @param catalogItemId catalog item unique identifier
 */
public record GetCatalogItemByIdQuery(Long supplierId, Long catalogItemId) {
}
