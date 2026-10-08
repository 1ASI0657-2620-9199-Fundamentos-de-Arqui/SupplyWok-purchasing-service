package aurora.supply_wok.platform.purchasing.domain.model.queries;

/**
 * Query to retrieve a purchase order by ID scoped to a restaurant tenant.
 *
 * @param purchaseOrderId the purchase order ID
 * @param restaurantId    the restaurant tenant ID
 */
public record GetPurchaseOrderByIdAndRestaurantIdQuery(Long purchaseOrderId, Long restaurantId) {
    public GetPurchaseOrderByIdAndRestaurantIdQuery {
        if (purchaseOrderId == null || purchaseOrderId <= 0) {
            throw new IllegalArgumentException("Purchase order id must be greater than zero.");
        }
        if (restaurantId == null || restaurantId <= 0) {
            throw new IllegalArgumentException("Restaurant id must be greater than zero.");
        }
    }
}
