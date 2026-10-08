package aurora.supply_wok.platform.purchasing.domain.model.queries;

/**
 * Query to retrieve purchase orders owned by a supplier.
 */
public record GetPurchaseOrdersBySupplierIdQuery(
        Long supplierId
) {
}
