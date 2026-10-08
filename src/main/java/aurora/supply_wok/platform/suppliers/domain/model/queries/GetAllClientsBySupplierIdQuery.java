package aurora.supply_wok.platform.suppliers.domain.model.queries;

/**
 * Query to retrieve all clients linked to a supplier.
 *
 * @param supplierId supplier unique identifier
 */
public record GetAllClientsBySupplierIdQuery(Long supplierId) {
}
