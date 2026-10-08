package aurora.supply_wok.platform.purchasing.domain.model.queries;

/**
 * Query to retrieve all purchase orders belonging to a specific restaurant tenant.
 *
 * @param restaurantId the restaurant tenant ID
 */
public record GetPurchaseOrdersByRestaurantIdQuery(Long restaurantId) {
    public GetPurchaseOrdersByRestaurantIdQuery {
        if (restaurantId == null || restaurantId <= 0) {
            throw new IllegalArgumentException("Restaurant id must be greater than zero.");
        }
    }
}
