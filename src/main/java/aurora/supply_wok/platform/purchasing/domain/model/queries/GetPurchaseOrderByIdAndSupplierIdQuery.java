package aurora.supply_wok.platform.purchasing.domain.model.queries;

/**
 * Query to retrieve a purchase order by ID scoped to a supplier.
 *
 * @param purchaseOrderId the purchase order ID
 * @param supplierId      the supplier ID
 */
public record GetPurchaseOrderByIdAndSupplierIdQuery(Long purchaseOrderId, Long supplierId) {
    public GetPurchaseOrderByIdAndSupplierIdQuery {
        if (purchaseOrderId == null || purchaseOrderId <= 0) {
            throw new IllegalArgumentException("Purchase order id must be greater than zero.");
        }
        if (supplierId == null || supplierId <= 0) {
            throw new IllegalArgumentException("Supplier id must be greater than zero.");
        }
    }
}
