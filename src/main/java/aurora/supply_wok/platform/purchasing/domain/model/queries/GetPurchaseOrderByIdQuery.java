package aurora.supply_wok.platform.purchasing.domain.model.queries;

/**
 * Query to retrieve a purchase order by id.
 */
public record GetPurchaseOrderByIdQuery(
        Long purchaseOrderId
) {
}
